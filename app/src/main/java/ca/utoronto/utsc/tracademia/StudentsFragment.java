package ca.utoronto.utsc.tracademia;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class StudentsFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected StudentsAdapter mAdapter;

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