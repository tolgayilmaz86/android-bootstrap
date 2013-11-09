
package com.donnfelker.android.bootstrap.authenticator;

import static android.R.layout.simple_dropdown_item_1line;
import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static com.donnfelker.android.bootstrap.core.Constants.Http.HEADER_PARSE_APP_ID;
import static com.donnfelker.android.bootstrap.core.Constants.Http.HEADER_PARSE_REST_API_KEY;
import static com.donnfelker.android.bootstrap.core.Constants.Http.PARSE_APP_ID;
import static com.donnfelker.android.bootstrap.core.Constants.Http.PARSE_REST_API_KEY;
import static com.donnfelker.android.bootstrap.core.Constants.Http.URL_AUTH;
import static com.github.kevinsawicki.http.HttpRequest.get;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.donnfelker.android.bootstrap.core.Constants;
import com.donnfelker.android.bootstrap.core.User;
import com.donnfelker.android.bootstrap.util.Ln;
import com.donnfelker.android.bootstrap.util.SafeAsyncTask;
import com.donnfelker.android.bootstrap.util.Strings;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.wishlist.Toaster;
import com.donnfelker.android.bootstrap.R.id;
import com.donnfelker.android.bootstrap.R.layout;
import com.donnfelker.android.bootstrap.R.string;
import com.donnfelker.android.bootstrap.ui.TextWatcherAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Activity to authenticate the user against an API (example API on Parse.com)
 */
public class BootstrapAuthenticatorActivity extends SherlockAccountAuthenticatorActivity {

    /**
     * PARAM_CONFIRM_CREDENTIALS
     */
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

    /**
     * PARAM_PASSWORD
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * PARAM_USERNAME
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * PARAM_AUTHTOKEN_TYPE
     */
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";


    private AccountManager mAccountManager;

    @InjectView(id.et_email) protected AutoCompleteTextView mEmailText;
    @InjectView(id.et_password) protected EditText mPasswordText;
    @InjectView(id.b_signin) protected Button mSignInButton;

    private final TextWatcher mWatcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> mAuthenticationTask;
    private String mAuthToken;
    private String mAuthTokenType;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean mConfirmCredentials = false;

    private String mEmail;

    private String mPassword;


    /**
     * In this instance the token is simply the sessionId returned from Parse.com. This could be a
     * oauth token or some other type of timed token that expires/etc. We're just using the parse.com
     * sessionId to prove the example of how to utilize a token.
     */
    private String mToken;

    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean mRequestNewAccount = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mAccountManager = AccountManager.get(this);

        final Intent intent = getIntent();
        mEmail = intent.getStringExtra(PARAM_USERNAME);
        mAuthTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
        mConfirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false);

        mRequestNewAccount = mEmail == null;

        setContentView(layout.login_activity);

        Views.inject(this);

        mEmailText.setAdapter(new ArrayAdapter<String>(this,
                simple_dropdown_item_1line, userEmailAccounts()));

        mPasswordText.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && mSignInButton.isEnabled()) {
                    handleLogin(mSignInButton);
                    return true;
                }
                return false;
            }
        });

        mPasswordText.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && mSignInButton.isEnabled()) {
                    handleLogin(mSignInButton);
                    return true;
                }
                return false;
            }
        });

        mEmailText.addTextChangedListener(mWatcher);
        mPasswordText.addTextChangedListener(mWatcher);

        final TextView signUpText = (TextView) findViewById(id.tv_signup);
        signUpText.setMovementMethod(LinkMovementMethod.getInstance());
        signUpText.setText(Html.fromHtml(getString(string.signup_link)));
    }

    private List<String> userEmailAccounts() {
        final Account[] accounts = mAccountManager.getAccountsByType("com.google");
        final List<String> emailAddresses = new ArrayList<String>(accounts.length);
        for (final Account account : accounts) {
            emailAddresses.add(account.name);
        }
        return emailAddresses;
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithValidation();
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(mEmailText) && populated(mPasswordText);
        mSignInButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(string.message_signing_in));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {
                if (mAuthenticationTask != null) {
                    mAuthenticationTask.cancel(true);
                }
            }
        });
        return dialog;
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication.
     * <p/>
     * Specified by android:onClick="handleLogin" in the layout xml
     *
     * @param view
     */
    public void handleLogin(final View view) {
        if (mAuthenticationTask != null) {
            return;
        }

        if (mRequestNewAccount) {
            mEmail = mEmailText.getText().toString();
        }

        mPassword = mPasswordText.getText().toString();
        showProgress();

        mAuthenticationTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {

                final String query = String.format("%s=%s&%s=%s",
                        PARAM_USERNAME, mEmail, PARAM_PASSWORD, mPassword);

                final HttpRequest request = get(URL_AUTH + "?" + query)
                        .header(HEADER_PARSE_APP_ID, PARSE_APP_ID)
                        .header(HEADER_PARSE_REST_API_KEY, PARSE_REST_API_KEY);


                Ln.d("Authentication response=%s", request.code());

                if(request.ok()) {
                    final User model = new Gson().fromJson(
                            Strings.toString(request.buffer()),
                            User.class
                    );
                    mToken = model.getSessionToken();
                }

                return request.ok();
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                final Throwable cause = e.getCause() != null ? e.getCause() : e;

                final String message;
                // A 404 is returned as an Exception with this message
                if ("Received authentication challenge is null".equals(cause
                        .getMessage())) {
                    message = getResources().getString(
                            string.message_bad_credentials);
                } else {
                    message = cause.getMessage();
                }

                Toaster.showLong(BootstrapAuthenticatorActivity.this, message);
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                onAuthenticationResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                mAuthenticationTask = null;
            }
        };
        mAuthenticationTask.execute();
    }

    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     *
     * @param result
     */
    protected void finishConfirmCredentials(final boolean result) {
        final Account account = new Account(mEmail, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        mAccountManager.setPassword(account, mPassword);

        final Intent intent = new Intent();
        intent.putExtra(KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. Also sets
     * the authToken in AccountManager for this account.
     */

    protected void finishLogin() {
        final Account account = new Account(mEmail, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        if (mRequestNewAccount) {
            mAccountManager.addAccountExplicitly(account, mPassword, null);
        } else {
            mAccountManager.setPassword(account, mPassword);
        }


        mAuthToken = mToken;

        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, mEmail);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        if (mAuthTokenType != null
                && mAuthTokenType.equals(Constants.Auth.AUTHTOKEN_TYPE)) {
            intent.putExtra(KEY_AUTHTOKEN, mAuthToken);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param result
     */
    public void onAuthenticationResult(final boolean result) {
        if (result) {
            if (!mConfirmCredentials) {
                finishLogin();
            } else {
                finishConfirmCredentials(true);
            }
        } else {
            Ln.d("onAuthenticationResult: failed to authenticate");
            if (mRequestNewAccount) {
                Toaster.showLong(BootstrapAuthenticatorActivity.this,
                        string.message_auth_failed_new_account);
            } else {
                Toaster.showLong(BootstrapAuthenticatorActivity.this,
                        string.message_auth_failed);
            }
        }
    }
}
