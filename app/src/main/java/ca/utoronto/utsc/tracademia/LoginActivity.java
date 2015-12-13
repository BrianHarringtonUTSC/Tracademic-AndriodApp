package ca.utoronto.utsc.tracademia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    private static final String COOKIES_HEADER = "Set-Cookie";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            Map<String, String> requestParams = new HashMap<>();
            requestParams.put("login", "true");
            requestParams.put("remember", "0");
            requestParams.put("username", username);
            requestParams.put("password", password);
            mAuthTask = new UserLoginTask(requestParams);
            mAuthTask.execute(MainActivity.BASE_URL + "api/User");
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 0;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, String, Boolean> {


        private final Map<String, String> requestParams;
        private int responseStatus;
        private String errorMsg;

        UserLoginTask(Map<String, String> requestParams) {
            this.requestParams = requestParams;
            responseStatus = 0;
            errorMsg = "";
        }

        @Override
        protected void onPreExecute() {
            showProgress(true);

            // this is to accept intermediate certificates such as comodo used by tracademic
            // and self signed certificates for dev server
            // exposes us to man in the middle attacks
            // TODO: find a way to accept only comodo intermediate certificate when running on prod
            trustEveryone();

        }

        private void trustEveryone() {
            try {
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, new X509TrustManager[]{new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain,
                                                   String authType) throws CertificateException {
                    }

                    public void checkServerTrusted(X509Certificate[] chain,
                                                   String authType) throws CertificateException {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }}, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(
                        context.getSocketFactory());
            } catch (Exception e) { // should never happen
                e.printStackTrace();
            }
        }

        public boolean networkAvailable() {
            return ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
        }
        @Override
        protected Boolean doInBackground(String... params) {

            if (!networkAvailable()) {
                responseStatus = -1;
                errorMsg = "No internet connection";
                return false;
            }
            boolean result = true;

            try {

                HttpURLConnection urlConnection = HTTPClient.getOpenHttpConnection(params[0], "POST");


                String requestBody = HTTPClient.buildRequestBody(requestParams);
                HTTPClient.writeOutputStream(urlConnection, requestBody);

                try {
                    Map<String, List<String>> headerFields = urlConnection.getHeaderFields();

                    responseStatus = urlConnection.getResponseCode();

                    if (responseStatus == 200) {
                        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                        if (cookiesHeader != null) {
                            for (String cookie : cookiesHeader) {
                                HTTPClient.mCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                            }
                        } else {
                            result = false;
                            errorMsg = "No cookie set.";
                        }
                    } else {
                        result = false;
                        errorMsg = "Response Status " + responseStatus;
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (responseStatus == 401) {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
                Snackbar.make(findViewById(android.R.id.content), errorMsg, Snackbar.LENGTH_LONG)
                        .show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

