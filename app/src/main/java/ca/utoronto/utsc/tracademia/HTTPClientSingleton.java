package ca.utoronto.utsc.tracademia;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
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

public class HTTPClientSingleton {
    private static final String SESSION_COOKIE_NAME = "points.sess";
    private static final int REQUEST_TIMEOUT = 10000; // 10 seconds

    private static HTTPClientSingleton mInstance;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private CookieManager mCookieManager;

    private HTTPClientSingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        mCookieManager = getCookieManager();
    }

    public static synchronized HTTPClientSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HTTPClientSingleton(context);
            trustEveryone();
        }
        return mInstance;
    }

    private static void trustEveryone() {
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

    public static Map<String, String> getRequestHeaders() {
        Map<String, String> params = new HashMap<>();
        params.put("x-no-csrf", "1");
        return params;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        setRequestTimeout(req);
        getRequestQueue().add(req);
    }

    public void setRequestTimeout(Request request) {
        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public CookieManager getCookieManager() {
        if (mCookieManager == null) {
            mCookieManager = new CookieManager(new PersistentCookieStore(mContext.getApplicationContext()), CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(mCookieManager);
        }
        return mCookieManager;
    }

    public boolean isLoggedIn() {
        List<HttpCookie> cookies = getCookieManager().getCookieStore().getCookies();
        return cookies.size() == 1 && cookies.get(0).getName().equals(SESSION_COOKIE_NAME);
    }
}
