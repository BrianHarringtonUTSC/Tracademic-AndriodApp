package ca.utoronto.utsc.tracademia;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.Map;


public class StudentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private static final String TAG = "StudentsFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected SwipeRefreshLayout mSwipeLayout;
    protected MainActivity mCallBack;
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

        mCallBack = (MainActivity) getActivity();
        mAdapter = mCallBack.getStudentsAdapter();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_container);
        mSwipeLayout.setOnRefreshListener(this);

        loadStudents();
        return rootView;
    }

    private void loadStudents() {
        String url = MainActivity.BASE_URL + "api/users";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Student[] studentArray = new Gson().fromJson(response, Student[].class);
                        if (mAdapter != null) {
                            mAdapter.addItemsToList(studentArray);
                    }
                        mSwipeLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Failed to get students: " + error, Snackbar.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return HTTPClientSingleton.getRequestHeaders();
            }
        };

        // Add the request to the RequestQueue.
        HTTPClientSingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
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

        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_logout:
                mCallBack.logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRefresh() {
        loadStudents();
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
}

