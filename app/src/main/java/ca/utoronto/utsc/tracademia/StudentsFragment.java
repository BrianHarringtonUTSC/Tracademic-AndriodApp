package ca.utoronto.utsc.tracademia;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;


public class StudentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "RecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected SwipeRefreshLayout mSwipeLayout;
    protected StudentsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        rootView.setTag(TAG);

        mAdapter = ((OnStudentSelectedListener) getActivity()).getStudentsAdapter();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        // LinearLayoutManager is used here, this will layout th\e elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_container);
        mSwipeLayout.setOnRefreshListener(this);

        GetStudentsRequestTask getStudentsRequestTask = new GetStudentsRequestTask();
        getStudentsRequestTask.execute(MainActivity.BASE_URL +  "api/users");

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRefresh() {
        GetStudentsRequestTask getStudentsRequestTask = new GetStudentsRequestTask();
        getStudentsRequestTask.execute(MainActivity.BASE_URL + "api/users");
    }

    class GetStudentsRequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return HTTPClient.getWebpage(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Student[] studentArray = new Gson().fromJson(result, Student[].class);
            mAdapter.addItemsToList(studentArray);
            mSwipeLayout.setRefreshing(false);
        }
    }
}

