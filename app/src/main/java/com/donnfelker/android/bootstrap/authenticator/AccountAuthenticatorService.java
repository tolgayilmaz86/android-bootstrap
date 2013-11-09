
package com.donnfelker.android.bootstrap.authenticator;

import static android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Authenticator service that returns a subclass of AbstractAccountAuthenticator in onBind()
 */
public class AccountAuthenticatorService extends Service {

    private static BootstrapAccountAuthenticator sAuthenticator = null;

    @Override
    public IBinder onBind(final Intent intent) {
        if (intent != null && ACTION_AUTHENTICATOR_INTENT.equals(intent.getAction())) {
            return getAuthenticator().getIBinder();
        }
        return null;
    }

    private BootstrapAccountAuthenticator getAuthenticator() {
        if (sAuthenticator == null) {
            sAuthenticator = new BootstrapAccountAuthenticator(this);
        }
        return sAuthenticator;
    }
}