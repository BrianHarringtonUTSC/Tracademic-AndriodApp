package ca.utoronto.utsc.tracademia;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;


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

        GetStudentsRequestTask getStudentsRequestTask = new GetStudentsRequestTask(mAdapter);
        getStudentsRequestTask.execute(MainActivity.BASE_URL +  "api/users");

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }
}

class GetStudentsRequestTask extends AsyncTask<String, String, String> {

    private StudentsAdapter mAdapter;

    public GetStudentsRequestTask(StudentsAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    protected String doInBackground(String... params) {
        return HTTPClient.getWebpage(params[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        Student[] studentArray = new Gson().fromJson(result, Student[].class);
        mAdapter.getStudents().clear();
        mAdapter.addItemsToList(studentArray);
    }
}