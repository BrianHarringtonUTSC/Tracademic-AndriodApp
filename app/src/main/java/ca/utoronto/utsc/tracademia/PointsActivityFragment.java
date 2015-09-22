package ca.utoronto.utsc.tracademia;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;


public class PointsActivityFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RecyclerViewFragment";
    private static String BASE_URL = "https://track-point.cloudapp.net/";

    protected PointsAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;

    //The + button responsible for opening the barcode scanning app.
    private ImageButton scanBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout th\e elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PointsAdapter(new ArrayList<StudentPoints>());
        mRecyclerView.setAdapter(mAdapter);

        RequestTask requestTask = new RequestTask(mAdapter);
        requestTask.execute(BASE_URL, "api/users");

        scanBtn = (ImageButton)rootView.findViewById(R.id.scanBarcode);
        scanBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }


    /*
    Responsible for dealing with any view that is clicked.
    Currently supports: Opening the Barcode scanning app. If an app doesn't exit, the user
        will be prompted to download one.
    */
    @Override
    public void onClick(View v) {
        //respond to clicks
        if(v.getId()==R.id.scanBarcode){
            StartBarcodeScan();
        }
    }

    private void StartBarcodeScan(){
        FragmentIntentIntegrator scanIntegrator = new FragmentIntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanResult != null) {
            String libraryNumber = scanResult.getContents();

            StudentPoints sp =  mAdapter.getStudentPointsByLibraryNumber(libraryNumber);

            if (sp == null)
            {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(R.string.confirm_title)
                        .setMessage("Sorry, something went wrong. Do you want to try again?")
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StartBarcodeScan();
                            }
                        }).setNegativeButton(R.string.cancel, null)
                        .show();
                return;
            }
            Intent awardIntent = new Intent(getActivity(), AwardPointsActivity.class);
            awardIntent.putExtra(getString(R.string._id), sp.get_id());
            awardIntent.putExtra(getString(R.string.name), sp.getDisplayName());
            startActivity(awardIntent);
        }
        else{
            //TODO:: CRY
            Log.d(TAG, "Umair can't figure out toasts yet. Markus also is none the wiser. Sorry, you're out of luck.");
        }
    }
}

class RequestTask extends AsyncTask<String, String, String> {

    private final String TAG = "RequestTask";

    private PointsAdapter mAdapter;
    static final String COOKIES_HEADER = "Set-Cookie";
    static java.net.CookieManager msCookieManager = new java.net.CookieManager();
    private Boolean hasLoggedin = false;

    public RequestTask(PointsAdapter mAdapter) {
        this.mAdapter = mAdapter;
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

    private void LogIn(String urlPath) {
        if (!hasLoggedin) {
            try {
                URL url = new URL(urlPath + "api/User");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");

                List<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry<>("login", "true"));
                params.add(new AbstractMap.SimpleEntry<>("remember", "0"));
                params.add(new AbstractMap.SimpleEntry<>("password", "a"));
                params.add(new AbstractMap.SimpleEntry<>("username", "a"));

                try {
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getQuery(params));
                    writer.flush();
                    writer.close();
                    os.close();

                    Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
                    //TODO:: IF the first thing in the map is not 201: then an error occured. Add check here.
                    List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                    if (cookiesHeader != null) {
                        for (String cookie : cookiesHeader) {
                            msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                        }
                    }

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }
    private String getQuery(List<AbstractMap.SimpleEntry<String, String>> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (AbstractMap.SimpleEntry<String, String> pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    @Override
    protected String doInBackground(String... params) {
        String responseBody = "";

        trustEveryone();
        LogIn(params[0]);
        try {
            URL url = new URL(params[0]+params[1]);
            URLConnection urlConnection = url.openConnection();
            String code = null;
            if (msCookieManager.getCookieStore().getCookies().size() > 0){
                code =   TextUtils.join(";", msCookieManager.getCookieStore().getCookies());
            }
            try {
                urlConnection.setRequestProperty("Cookie", code);
                urlConnection.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                br.close();
                responseBody = sb.toString();
            } finally {
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return responseBody;
    }

    @Override
    protected void onPostExecute(String result) {
        StudentPoints[] studentPointsArray = new Gson().fromJson(result, StudentPoints[].class);
        mAdapter.addItemsToList(studentPointsArray);
    }
}

final class FragmentIntentIntegrator extends IntentIntegrator {

    private final Fragment fragment;

    public FragmentIntentIntegrator(Fragment fragment) {
        super(fragment.getActivity());
        this.fragment = fragment;
    }

    @Override
    protected void startActivityForResult(Intent intent, int code) {
        fragment.startActivityForResult(intent, code);
    }
}

