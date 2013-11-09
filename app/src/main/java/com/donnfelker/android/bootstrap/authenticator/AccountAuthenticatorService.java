
package com.donnfelker.android.bootstrap.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import static android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT;

/**
 * Authenticator service that returns a subclass of AbstractAccountAuthenticator in onBind()
 */
public class AccountAuthenticatorService extends Service {

    private static BootstrapAccountAuthenticator AUTHENTICATOR = null;

    @Override
    public IBinder onBind(Intent intent) {
        return ACTION_AUTHENTICATOR_INTENT.equals(intent.getAction()) ? getAuthenticator().getIBinder() : null;
    }

    private BootstrapAccountAuthenticator getAuthenticator() {
        if (AUTHENTICATOR == null)
            AUTHENTICATOR = new BootstrapAccountAuthenticator(this);
        return AUTHENTICATOR;
    }
}