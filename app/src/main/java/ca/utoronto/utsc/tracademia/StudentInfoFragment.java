package ca.utoronto.utsc.tracademia;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class StudentInfoFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "StudentInfoFragment";

    protected PointType pointType;
    protected NumberPicker typePicker, pointsPicker;
    protected TextView studentInfoTextView, studentPointInfoTextView;
    private StudentsAdapter mAdapter;
    private Student mStudent;
    private StudentListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.student_info, container, false);
        rootView.setTag(TAG);

        mCallback = (StudentListener) getActivity();
        mAdapter = mCallback.getStudentsAdapter();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        String studentNumber = getArguments().getString(MainActivity.ARG_STUDENT_NUMBER);
        mStudent = mAdapter.getStudentByStudentNumber(studentNumber);

        studentInfoTextView = (TextView) getActivity().findViewById(R.id.student_info_textview);
        studentInfoTextView.setText(mStudent.getDisplayName());

        studentPointInfoTextView = (TextView) getActivity().findViewById(R.id.student_points_info);
        studentPointInfoTextView.setText(mStudent.getPointsInfo());

        typePicker = (NumberPicker)getActivity().findViewById(R.id.point_type_picker);
        StudentInfoFragment.PointType[] pt = StudentInfoFragment.PointType.values();
        String[] types = new String[pt.length];
        for (int i = 0; i < pt.length; i++) {
            types[i] = pt[i].name();
        }
        typePicker.setMinValue(0);
        typePicker.setMaxValue(pt.length - 1);
        typePicker.setDisplayedValues(types);
        typePicker.setValue((int) Math.ceil(pt.length / 2));

        pointsPicker = (NumberPicker)getActivity().findViewById(R.id.points_amount_picker);
        pointsPicker.setMinValue(0);
        pointsPicker.setMaxValue(5);
        pointsPicker.setWrapSelectorWheel(false);

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
                requestTask.execute(MainActivity.BASE_URL + "api/users/" + mStudent.get_id() + "/give");
            }
        }
    }

    public enum PointType {
        Challenge,
        Experience,
        Teaching
    }

    class GivePointsRequestTask extends AsyncTask<String, String, Integer> {
        private final Map<String, String> requestParams;

        public GivePointsRequestTask(Map<String, String> params) {
            this.requestParams = params;
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                return HTTPClient.postWebpage(params[0], requestParams);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer responseStatus) {

            String message;

            if (responseStatus == 200) {
                String type = requestParams.get("type").replace("Points", " points");
                message = mStudent.getUsername() + " awarded " + requestParams.get("amount") + " " + type;
            } else {
                message = "Failed to award points. Response status: "  + responseStatus;
            }

            Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                    .show();
            mCallback.onStudentInfoSubmitted();
        }
    }
}

