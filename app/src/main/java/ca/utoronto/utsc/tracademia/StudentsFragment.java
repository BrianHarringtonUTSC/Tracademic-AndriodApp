package ca.utoronto.utsc.tracademia;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class StudentsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected StudentsAdapter mAdapter;

    //The + button responsible for opening the barcode scanning app.
    private ImageButton scanBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        rootView.setTag(TAG);

        mAdapter = ((OnStudentSelectedListener) getActivity()).getStudentsAdapter();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout th\e elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        RequestTask requestTask = new RequestTask(mAdapter);
        requestTask.execute(MainActivity.BASE_URL, "api/users");

//        scanBtn = (ImageButton)rootView.findViewById(R.id.scanBarcode);
//        scanBtn.setOnClickListener(this);

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
            //TODO:: Make a dual option.
            ////StartBarcodeScan();
            Intent intent = new Intent(getActivity(), MagStripeReaderActivity.class);
            intent.putExtra("StudentsAdapter", mAdapter);
            startActivity(intent);
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

            Student sp =  mAdapter.getStudentByLibraryNumber(libraryNumber);

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
    private StudentsAdapter mAdapter;

    public RequestTask(StudentsAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    protected String doInBackground(String... params) {
        String responseBody = "";

        try {
            URL url = new URL(params[0] + params[1]);
            URLConnection urlConnection = url.openConnection();
            String code = null;
            if (LoginActivity.mCookieManager.getCookieStore().getCookies().size() > 0) {
                code = TextUtils.join(";", LoginActivity.mCookieManager.getCookieStore().getCookies());
            }

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
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return responseBody;
    }

    @Override
    protected void onPostExecute(String result) {
        Student[] studentArray = new Gson().fromJson(result, Student[].class);
        mAdapter.addItemsToList(studentArray);
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
