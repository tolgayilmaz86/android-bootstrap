

package com.donnfelker.android.bootstrap;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

import javax.inject.Inject;

/**
 * Android Bootstrap application
 */
public class BootstrapApplication extends Application {

    private static BootstrapApplication instance;

    /**
     * Create main application
     */
    public BootstrapApplication() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        // Perform injection
        Injector.init(this, Modules.list());

    }

    public static BootstrapApplication getInstance() {
        return instance;
    }
}
