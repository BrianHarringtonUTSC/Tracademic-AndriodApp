package ca.utoronto.utsc.tracademia;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentInfoFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "StudentInfoFragment";
    public static final String ARG_POSITION = "position";

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
        int position = getArguments().getInt(ARG_POSITION);
        student = mAdapter.getStudents().get(position);

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
//            PointType pointType = typePicker.getValue();

            GivePointsRequestTask requestTask = new GivePointsRequestTask(this);
            requestTask.execute(MainActivity.BASE_URL, "api/users/" + student.get_id() + "/give", "experiencePoints", "1");

//            new AlertDialog.Builder(getActivity())
//                    .setIcon(android.R.drawable.ic_dialog_info)
//                    .setTitle(R.string.confirm_title)
//                    .setMessage("Awarding "+ student.getDisplayName() +" "+ num_points + " " + pointType + (num_points == 1 ? " point" : " points"))
//                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //TODO:: Make API call to give point to student
//                            Log.d("abc", "hereee");
//
//                            requestTask.execute(MainActivity.BASE_URL, "api/users");
//                        }
//                    }).setNegativeButton(R.string.cancel, null)
//                    .show();
        }
    }

    public enum PointType {
        Experience,
        Teaching,
        Challenge
    }
}


class GivePointsRequestTask extends AsyncTask<String, String, String> {

    private final String TAG = "RequestTask";
    private Fragment fragment;

    public GivePointsRequestTask(Fragment fragment) {
        this.fragment = fragment;
    }

    private String getQuery(List<AbstractMap.SimpleEntry<String, String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (AbstractMap.SimpleEntry<String, String> pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    @Override
    protected String doInBackground(String... params) {
        String responseBody = "";

        try {
            URL url = new URL(params[0] + params[1]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("x-no-csrf", "1");

            List<AbstractMap.SimpleEntry<String, String>> cookie_params = new ArrayList<>();
            cookie_params.add(new AbstractMap.SimpleEntry<>("type", params[2]));
            cookie_params.add(new AbstractMap.SimpleEntry<>("amount", params[3]));

            urlConnection.setRequestProperty("type", params[2]);
            urlConnection.setRequestProperty("amount", params[3]);

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(cookie_params));
            writer.flush();
            writer.close();
            os.close();

            String cookie = "";
            if (LoginActivity.mCookieManager.getCookieStore().getCookies().size() > 0) {
                cookie = TextUtils.join(";", LoginActivity.mCookieManager.getCookieStore().getCookies());
            }

            urlConnection.setRequestProperty("Cookie", cookie);
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
        Toast.makeText(fragment.getActivity().getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
    }
}
