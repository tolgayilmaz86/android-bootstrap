package com.donnfelker.android.bootstrap.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import com.donnfelker.android.bootstrap.core.Constants;
import com.donnfelker.android.bootstrap.util.Ln;
import com.donnfelker.android.bootstrap.util.SafeAsyncTask;

import javax.inject.Inject;


/**
 * Class used for logging a user out.
 */
public class LogoutService {

    protected final Context mContext;
    protected final AccountManager mAccountManager;

    @Inject
    public LogoutService(final Context context, final AccountManager accountManager) {
        mContext = context;
        mAccountManager = accountManager;
    }

    public void logout(final Runnable onSuccess) {
        new LogoutTask(mContext, onSuccess).execute();
    }

    private static class LogoutTask extends SafeAsyncTask<Boolean> {

        private final Context mTaskContext;
        private final Runnable mOnSuccess;

        protected LogoutTask(final Context context, final Runnable onSuccess) {
            this.mTaskContext = context;
            this.mOnSuccess = onSuccess;
        }

        @Override
        public Boolean call() throws Exception {

            final Account[] accounts = AccountManager.get(mTaskContext)
                    .getAccountsByType(Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
            if(accounts.length > 0) {
                final AccountManagerFuture<Boolean> removeAccountFuture
                        = AccountManager.get(mTaskContext)
                        .removeAccount(accounts[0], null, null);

                return removeAccountFuture.getResult();
            }

            return false;
        }

        @Override
        protected void onSuccess(final Boolean accountWasRemoved) throws Exception {
            super.onSuccess(accountWasRemoved);

            Ln.d("Logout succeeded: %s", accountWasRemoved);
            mOnSuccess.run();

        }

        @Override
        protected void onException(final Exception e) throws RuntimeException {
            super.onException(e);

            Ln.e(e.getCause(), "Logout failed.");
        }
    }
}
