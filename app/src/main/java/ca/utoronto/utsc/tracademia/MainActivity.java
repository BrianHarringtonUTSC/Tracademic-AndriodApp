package ca.utoronto.utsc.tracademia;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

/*
 *  Authors: Umair Idris and Markus Friesen
 */
public class MainActivity extends AppCompatActivity implements OnStudentSelectedListener {

    public static final String BASE_URL = "https://track-point.cloudapp.net/";
    protected StudentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If being restored don't create new frag
        if (savedInstanceState != null) {
            return;
        }

        mAdapter = new StudentsAdapter(new ArrayList<Student>(), this);

        StudentsFragment studentsFragment = new StudentsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, studentsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_points, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public StudentsAdapter getStudentsAdapter() {
        return mAdapter;
    }

    @Override
    public void onStudentSelected(int position) {
        StudentInfoFragment studentInfoFragment = new StudentInfoFragment();
        Bundle args = new Bundle();
        args.putInt(StudentInfoFragment.ARG_POSITION, position);
        studentInfoFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, studentInfoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onStudentSelected(String studentNumber) {

    }
}
