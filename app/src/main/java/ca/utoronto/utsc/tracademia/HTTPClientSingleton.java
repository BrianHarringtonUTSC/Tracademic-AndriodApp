package ca.utoronto.utsc.tracademia;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;

public class HTTPClientSingleton {
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
        }
        return mInstance;
    }

    public static HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> params = new HashMap<>();
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
        getRequestQueue().add(req);
    }

    public CookieManager getCookieManager() {
        if (mCookieManager == null) {
            mCookieManager = new CookieManager();
            CookieHandler.setDefault(mCookieManager);
        }
        return mCookieManager;
    }
}
