package ca.utoronto.utsc.tracademia;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class PointsActivityFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";
    private static final String BASE_URL = "https://track-point.cloudapp.net/";

    protected PointsAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

        mAdapter = new PointsAdapter(new StudentPoints[]{});
        mRecyclerView.setAdapter(mAdapter);

        RequestTask requestTask = new RequestTask(mAdapter);
        requestTask.execute(BASE_URL + "api/users");

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }
}

class RequestTask extends AsyncTask<String, String, String> {

    private final String TAG = "RequestTask";

    private PointsAdapter mAdapter;

    public RequestTask(PointsAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    protected String doInBackground(String... params) {
        String responseBody = "";

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
//        StudentPoints[] studentPoints = new Gson().fromJson(result, StudentPoints[].class)

    }
}