

package com.donnfelker.android.bootstrap.ui;

import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.donnfelker.android.bootstrap.BootstrapServiceProvider;
import com.donnfelker.android.bootstrap.R;
import com.donnfelker.android.bootstrap.core.BootstrapService;
import com.donnfelker.android.bootstrap.util.SafeAsyncTask;
import com.viewpagerindicator.TitlePageIndicator;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;


/**
 * Activity to view the carousel and view pager indicator with fragments.
 */
public class CarouselActivity extends BootstrapFragmentActivity {

    @InjectView(R.id.tpi_header) protected TitlePageIndicator indicator;
    @InjectView(R.id.vp_pages) protected ViewPager pager;

    @Inject protected BootstrapServiceProvider serviceProvider;

    private boolean userAuthenticated = false;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CharSequence drawerTitle;
    private CharSequence title;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.carousel_view);

        // View injection with Butterknife
        Views.inject(this);

        // Set up navigation drawer
        title = drawerTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,                    /* Host activity */
                drawerLayout,           /* DrawerLayout object */
                R.drawable.ic_drawer,    /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,    /* "open drawer" description */
                R.string.drawer_close) { /* "close drawer" description */

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(title);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        checkAuth();

    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void initScreen() {
        if (userAuthenticated) {
            pager.setAdapter(new BootstrapPagerAdapter(getResources(),
                    getSupportFragmentManager()));

            indicator.setViewPager(pager);
            pager.setCurrentItem(1);
        }

        setNavListeners();
    }

    private void checkAuth() {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final BootstrapService svc = serviceProvider.getService(CarouselActivity.this);
                return svc != null;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                userAuthenticated = true;
                initScreen();
            }
        }.execute();
    }


    private void setNavListeners() {

        findViewById(R.id.menu_item_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                drawerLayout.closeDrawers();
            }
        });

        findViewById(R.id.menu_item_timer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                drawerLayout.closeDrawers();
                navigateToTimer();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                //menuDrawer.toggleMenu();
                return true;
            case R.id.timer:
                navigateToTimer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToTimer() {
        final Intent i = new Intent(this, BootstrapTimerActivity.class);
        startActivity(i);
    }
}
