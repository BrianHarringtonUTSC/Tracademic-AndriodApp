package ca.utoronto.utsc.tracademia;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class StudentInfoFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "StudentInfoFragment";

    protected PointType pointType;
    protected NumberPicker typePicker, pointsPicker;
    protected TextView studentInfoTextView;
    private StudentsAdapter mAdapter;
    private Student student;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.student_info, container, false);
        rootView.setTag(TAG);

        mAdapter = ((OnStudentSelectedListener) getActivity()).getStudentsAdapter();


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        String username = getArguments().getString(MainActivity.ARG_USERNAME);
        student = mAdapter.getStudentByUsername(username);

        studentInfoTextView = (TextView) getActivity().findViewById(R.id.student_info_textview);
        studentInfoTextView.setText(student.getDisplayName());

        typePicker = (NumberPicker)getActivity().findViewById(R.id.point_type_picker);
        StudentInfoFragment.PointType[] pt = StudentInfoFragment.PointType.values();
        String[] types = new String[pt.length];
        for (int i = 0; i < pt.length; i++) {
            types[i] = pt[i].name();
        }
        typePicker.setMinValue(0);
        typePicker.setMaxValue(pt.length - 1);
        typePicker.setDisplayedValues(types);

        pointsPicker = (NumberPicker)getActivity().findViewById(R.id.points_amount_picker);
        pointsPicker.setMinValue(0);
        pointsPicker.setMaxValue(3);

        getActivity().findViewById(R.id.give_points_button).setOnClickListener(this);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.give_points_button){
            pointType = StudentInfoFragment.PointType.values()[typePicker.getValue()];
            int num_points = pointsPicker.getValue();
            if (num_points > 0) {
                PointType pointType = PointType.values()[typePicker.getValue()];
                String type = pointType.name().toLowerCase() + "Points";

                Map<String, String> params  = new HashMap<>();
                params.put("type", type);
                params.put("amount", String.valueOf(num_points));

                GivePointsRequestTask requestTask = new GivePointsRequestTask(params);
                requestTask.execute(MainActivity.BASE_URL + "api/users/" + student.get_id() + "/give");
            }
        }
    }

    public enum PointType {
        Experience,
        Teaching,
        Challenge
    }

    class GivePointsRequestTask extends AsyncTask<String, String, String> {
        private final Map<String, String> requestParams;

        public GivePointsRequestTask(Map<String, String> params) {
            this.requestParams = params;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                HTTPClient.postWebpage(params[0], requestParams);
                return "";
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String toastMessage = "Success";
            if (result.length() > 0) {
                toastMessage = "Failed: " + result;
            }
            Toast.makeText(getActivity().getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
        }
    }
}

