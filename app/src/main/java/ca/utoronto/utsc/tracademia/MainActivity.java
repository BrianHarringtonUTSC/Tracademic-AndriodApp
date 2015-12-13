package ca.utoronto.utsc.tracademia;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

/*
 *  Authors: Umair Idris and Markus Friesen
 */
public class MainActivity extends AppCompatActivity implements StudentListener, View.OnClickListener, FragmentManager.OnBackStackChangedListener {

    public static final String BASE_URL = "https://track-point.cloudapp.net/"; // "https://tracademic.utsc.utoronto.ca/"; // DEV:
    public static final int GET_STUDENT_NUMBER_REQUEST = 1;
    public static final String ARG_STUDENT_NUMBER = "studentNumber";

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

        FloatingActionButton cardReaderLauncherButton = (FloatingActionButton) findViewById(R.id.card_reader_launcher_button);
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
        if (mAdapter.getStudentByStudentNumber(studentNumber) != null) {
            StudentInfoFragment studentInfoFragment = new StudentInfoFragment();
            Bundle args = new Bundle();
            args.putString(ARG_STUDENT_NUMBER, studentNumber);
            studentInfoFragment.setArguments(args);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, studentInfoFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Student Number not given or found", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onStudentInfoSubmitted() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.card_reader_launcher_button) {
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
}
