package ca.utoronto.utsc.tracademia;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;


public class StudentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private static final String TAG = "RecyclerViewFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected SwipeRefreshLayout mSwipeLayout;
    protected StudentsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        rootView.setTag(TAG);

        mAdapter = ((StudentListener) getActivity()).getStudentsAdapter();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == R.id.action_search || super.onOptionsItemSelected(item);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mAdapter.getFilter().filter(query);
        return false;
    }


    class GetStudentsRequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return HTTPClient.getWebpage(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            Student[] studentArray = new Gson().fromJson(result, Student[].class);
            if (mAdapter != null) {
                mAdapter.addItemsToList(studentArray);
            }
            mSwipeLayout.setRefreshing(false);
        }
    }
}

