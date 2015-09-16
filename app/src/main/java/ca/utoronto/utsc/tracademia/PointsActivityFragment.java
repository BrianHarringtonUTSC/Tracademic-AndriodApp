package ca.utoronto.utsc.tracademia;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;


public class PointsActivityFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RecyclerViewFragment";
    private static final String BASE_URL = "https://track-point.cloudapp.net/";

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
        requestTask.execute(BASE_URL + "api/users");

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
            FragmentIntentIntegrator scanIntegrator = new FragmentIntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanResult != null) {
            String libraryNumber = scanResult.getContents();

            Intent awardIntent = new Intent(getActivity(), AwardPointsActivity.class);
            awardIntent.putExtra(getString(R.string.libraryNumber), libraryNumber);
            //TODO:: get and send student to next activity
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

    @Override
    protected String doInBackground(String... params) {
        String responseBody = "";

        trustEveryone();
        try {
            URL url = new URL(params[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
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
                urlConnection.disconnect();
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

