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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * @param context context to get singleton from.
     * @return singleton instance.
     */
    public static synchronized HTTPClientSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new HTTPClientSingleton(context);
        }
        return mInstance;
    }

    /**
     * @return request headers that should be used with every request to the server.
     */
    public static Map<String, String> getRequestHeaders() {
        Map<String, String> params = new HashMap<>();
        params.put("x-no-csrf", "1");
        return params;
    }

    /**
     * @return queue used to process http requests.
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Add a new request to the queue.
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        setRequestTimeout(req);
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    /**
     * Cancel all requests with tag in the request queue.
     *
     * @param tag a unique identifier corresponding to an activity or fragment.
     */
    public void cancelAllRequests(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * Sets max time for a request to finish.
     */
    public void setRequestTimeout(Request request) {
        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * @return Cookie Manager that stores session cookies after logging in.
     */
    public CookieManager getCookieManager() {
        if (mCookieManager == null) {
            mCookieManager = new CookieManager(new PersistentCookieStore(mContext.getApplicationContext()), CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(mCookieManager);
        }
        return mCookieManager;
    }

    /**
     * @return true if user is logged in, else false.
     */
    public boolean isLoggedIn() {
        List<HttpCookie> cookies = getCookieManager().getCookieStore().getCookies();
        return cookies.size() == 1 && cookies.get(0).getName().equals(SESSION_COOKIE_NAME);
    }

    /**
     * Removes all cookies.
     */
    public void removeAllCookies() {
        getCookieManager().getCookieStore().removeAll();
    }
}
