package com.donnfelker.android.bootstrap;

import android.accounts.AccountsException;
import android.app.Activity;

import com.donnfelker.android.bootstrap.core.BootstrapService;

import java.io.IOException;

/**
 * Created by donnfelker on 5/4/15.
 */
public interface BootstrapServiceProvider {
    BootstrapService getService(Activity activity)
            throws IOException, AccountsException;
}
