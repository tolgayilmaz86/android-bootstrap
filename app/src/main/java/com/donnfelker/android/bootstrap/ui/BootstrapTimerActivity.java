package com.donnfelker.android.bootstrap.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.donnfelker.android.bootstrap.R;
import com.donnfelker.android.bootstrap.core.PauseTimerEvent;
import com.donnfelker.android.bootstrap.core.ResumeTimerEvent;
import com.donnfelker.android.bootstrap.core.StopTimerEvent;
import com.donnfelker.android.bootstrap.core.TimerPausedEvent;
import com.donnfelker.android.bootstrap.core.TimerService;
import com.donnfelker.android.bootstrap.core.TimerTickEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.InjectView;

public class BootstrapTimerActivity extends BootstrapFragmentActivity implements View.OnClickListener {

    @Inject Bus mBus;

    @InjectView(R.id.chronometer) protected TextView mChronometer;
    @InjectView(R.id.start) protected Button mStart;
    @InjectView(R.id.stop) protected Button mStop;
    @InjectView(R.id.pause) protected Button mPause;
    @InjectView(R.id.resume) protected Button mResume;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bootstrap_timer);

        setTitle(R.string.timer);

        mStart.setOnClickListener(this);
        mStop.setOnClickListener(this);
        mPause.setOnClickListener(this);
        mResume.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBus.unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                startTimer();
                break;
            case R.id.stop:
                produceStopEvent();
                break;
            case R.id.pause:
                producePauseEvent();
                break;
            case R.id.resume:
                produceResumeEvent();
                break;
        }
    }

    /**
     * Starts the timer service
     */
    private void startTimer() {
        if(!isTimerServiceRunning()) {
            final Intent i = new Intent(this, TimerService.class);
            startService(i);

            mStart.setVisibility(View.GONE);
            mStop.setVisibility(View.VISIBLE);
            mPause.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Posts a {@link StopTimerEvent} message to the {@link Bus}
     */
    private void produceStopEvent() {
        mBus.post(new StopTimerEvent());
    }

    /**
     * Posts a {@link PauseTimerEvent} message to the {@link Bus}
     */
    private void producePauseEvent() {
        mBus.post(new PauseTimerEvent());
    }

    /**
     * Posts a {@link ResumeTimerEvent} message to the {@link Bus}
     */
    private void produceResumeEvent() {
        mBus.post(new ResumeTimerEvent());
    }

    @Subscribe
    public void onTimerPausedEvent(final TimerPausedEvent event) {
        if(event.isTimerIsPaused()) {
            mResume.setVisibility(View.VISIBLE);
            mStop.setVisibility(View.VISIBLE);
            mPause.setVisibility(View.GONE);
            mStart.setVisibility(View.GONE);
        } else if(isTimerServiceRunning()) {
            mPause.setVisibility(View.VISIBLE);
            mStop.setVisibility(View.VISIBLE);
            mResume.setVisibility(View.GONE);
            mStart.setVisibility(View.GONE);
        }
    }

    /**
     * Called by {@link Bus} when a tick event occurs.
     *
     * @param event The event
     */
    @Subscribe
    public void onTickEvent(final TimerTickEvent event) {
        setFormattedTime(event.getMillis());
    }


    /**
     * Called by {@link Bus} when a tick event occurs.
     *
     * @param event The event
     */
    @Subscribe
    public void onPauseEvent(final PauseTimerEvent event) {
        mResume.setVisibility(View.VISIBLE);
        mPause.setVisibility(View.GONE);
    }

    /**
     * Called by {@link Bus} when a tick event occurs.
     *
     * @param event The event
     */
    @Subscribe
    public void onResumeEvent(final ResumeTimerEvent event) {
        mResume.setVisibility(View.GONE);
        mPause.setVisibility(View.VISIBLE);
    }

    /**
     * Called by {@link Bus} when a tick event occurs.
     *
     * @param event The event
     */
    @Subscribe
    public void onStopEvent(final StopTimerEvent event) {
        mResume.setVisibility(View.GONE);
        mPause.setVisibility(View.GONE);
        mStart.setVisibility(View.VISIBLE);
        mStop.setVisibility(View.GONE);
        setFormattedTime(0); // Since its stopped, zero out the timer.
    }

    /**
     * Checks to see if the timer service is running or not.
     *
     * @return true if the service is running otherwise false.
     */
    private boolean isTimerServiceRunning() {
        final ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TimerService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the formatted time
     *
     * @param millis the elapsed time
     */
    private void setFormattedTime(long millis) {
        final String formattedTime = formatTime(millis);
        mChronometer.setText(formattedTime);
    }

    /**
     * Formats the time to look like "HH:MM:SS"
     *
     * @param millis The number of elapsed milliseconds
     * @return A formatted time value
     */
    public static String formatTime(final long millis) {

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 60;

        String secondsD = String.valueOf(seconds);
        String minutesD = String.valueOf(minutes);
        String hoursD = String.valueOf(hours);

        if (seconds < 10)
            secondsD = "0" + seconds;
        if (minutes < 10)
            minutesD = "0" + minutes;
        if (hours < 10)
            hoursD = "0" + hours;

        // HH:MM:SS
        return String.format("%1$s:%2$s:%3$s", hoursD, minutesD, secondsD);

    }


}
