package ca.utoronto.utsc.tracademia;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

/*
 *  Authors: Umair Idris and Markus Friesen
 */
public class MainActivity extends AppCompatActivity implements OnStudentSelectedListener, View.OnClickListener, FragmentManager.OnBackStackChangedListener, SearchView.OnQueryTextListener {

    public static final String BASE_URL = "https://track-point.cloudapp.net/";
    public static final int GET_STUDENT_NUMBER_REQUEST = 1;
    public static final String ARG_STUDENT_NUMBER = "studentNumber";
    public static final String ARG_POSITION = "position";

    private static final String TAG = "MainActivity";

    protected StudentsAdapter mAdapter;
    protected FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If being restored don't create new frag
        if (savedInstanceState != null) {
            return;
        }

        ImageButton cardReaderLauncherButton = (ImageButton) findViewById(R.id.card_reader_launcher_button);
        cardReaderLauncherButton.setOnClickListener(this);

        mAdapter = new StudentsAdapter(new ArrayList<Student>(), this);

        fragmentManager = getFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        StudentsFragment studentsFragment = new StudentsFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, studentsFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return item.getItemId() == R.id.action_search || super.onOptionsItemSelected(item);
    }

    @Override
    public StudentsAdapter getStudentsAdapter() {
        return mAdapter;
    }

    @Override
    public void onStudentSelected(int position) {
        Student selectedStudent = mAdapter.getFilteredStudents().get(position);
        onStudentSelected(selectedStudent.getStudentNumber());
    }

    @Override
    public void onStudentSelected(String studentNumber) {
        StudentInfoFragment studentInfoFragment = new StudentInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STUDENT_NUMBER, studentNumber);
        studentInfoFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, studentInfoFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        Log.d(TAG, "Couldn't find student with number " + studentNumber);
    }

    /*
    Responsible for dealing with any view that is clicked.
    Currently supports: Opening the Barcode scanning app. If an app doesn't exit, the user
        will be prompted to download one.
    */
    @Override
    public void onClick(View v) {
        //respond to clicks
        if(v.getId()==R.id.card_reader_launcher_button) {
            //TODO:: Make a dual option.
            Intent intent = new Intent(this, MagStripeReaderActivity.class);
            startActivityForResult(intent, GET_STUDENT_NUMBER_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == GET_STUDENT_NUMBER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String studentNumber = data.getStringExtra(ARG_STUDENT_NUMBER);
                onStudentSelected(studentNumber);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackStackChanged() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(fragmentManager.getBackStackEntryCount() > 0);
        }
    }

    @Override
    public boolean  onSupportNavigateUp() {
        fragmentManager.popBackStack();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, query);
        mAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        Log.d(TAG, query);
        mAdapter.getFilter().filter(query);
        return false;
    }
}
