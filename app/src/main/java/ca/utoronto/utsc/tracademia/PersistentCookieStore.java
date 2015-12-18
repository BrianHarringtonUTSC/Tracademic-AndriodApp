package ca.utoronto.utsc.tracademia;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * Repository for cookies. CookieManager will store cookies of every incoming HTTP response into
 * CookieStore, and retrieve cookies for every outgoing HTTP request.
 * Cookies are stored in {@link android.content.SharedPreferences} and will persist on the
 * user's device between application session. {@link com.google.gson.Gson} is used to serialize
 * the cookies into a json string in order to be able to save the cookie to
 * {@link android.content.SharedPreferences}
 * SOURCE: https://gist.github.com/lezorich/8f3f3a54f07515881581
 */
public class PersistentCookieStore implements CookieStore {
    private final static String PREF_DEFAULT_STRING = "";
    private final static String PREFS_NAME = PersistentCookieStore.class.getName();
    private final static String PREF_SESSION_COOKIE = "session_cookie";

    private CookieStore mStore;
    private Context mContext;

    public PersistentCookieStore(Context context) {
        // prevent context leaking by getting the application context
        mContext = context.getApplicationContext();

        // get the default in memory store and if there is a cookie stored in shared preferences,
        // we added it to the cookie store
        mStore = new CookieManager().getCookieStore();
        String jsonSessionCookie = getJsonSessionCookieString();
        if (!jsonSessionCookie.equals(PREF_DEFAULT_STRING)) {
            Gson gson = new Gson();
            HttpCookie cookie = gson.fromJson(jsonSessionCookie, HttpCookie.class);
            if (!cookie.hasExpired()) {
                mStore.add(URI.create(cookie.getDomain()), cookie);
            }
        }
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        remove(URI.create(cookie.getDomain()), cookie);
        saveSessionCookie(cookie);
        mStore.add(URI.create(cookie.getDomain()), cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return mStore.get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return mStore.getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return mStore.getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        boolean removed = mStore.remove(uri, cookie);
        if (removed) {
            deleteSharedPreferences();
        }
        return removed;
    }

    @Override
    public boolean removeAll() {
        deleteSharedPreferences();
        return mStore.removeAll();
    }

    private String getJsonSessionCookieString() {
        return getSharedPreferences().getString(PREF_SESSION_COOKIE, PREF_DEFAULT_STRING);
    }

    private void saveSessionCookie(HttpCookie cookie) {
        Gson gson = new Gson();
        String jsonSessionCookieString = gson.toJson(cookie);
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_SESSION_COOKIE, jsonSessionCookieString);
        editor.apply();
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private void deleteSharedPreferences() {
        getSharedPreferences().edit().clear().apply();
    }
}
