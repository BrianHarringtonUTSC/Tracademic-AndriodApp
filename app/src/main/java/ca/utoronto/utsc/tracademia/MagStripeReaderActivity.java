package ca.utoronto.utsc.tracademia;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.idtechproducts.acom.Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import IDTech.MSR.XMLManager.StructConfigParameters;
import IDTech.MSR.uniMag.uniMagReader;
import IDTech.MSR.uniMag.uniMagReaderMsg;


public class MagStripeReaderActivity extends Activity implements uniMagReaderMsg, OnClickListener {
    private static final String TAG = "MagStripeReaderActivity";

    private uniMagReader myUniMagReader;
    private LoadXMLConfigurationTask loadXMLConfigurationTask;
    private EditText sdntUserName;
    protected StudentsAdapter mAdapter;

    StudentInfoFragment.PointType pointType;
    NumberPicker typePicker, pointsPicker;
    Student student;
    String displayName, _id;
    int num_points;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.award_points);
        if(myUniMagReader == null) {
            myUniMagReader = new uniMagReader(this,this);
            myUniMagReader.setSaveLogEnable(false);
            loadXMLConfigurationTask = new LoadXMLConfigurationTask();
            loadXMLConfigurationTask.execute();
        }

        //getStudent data
        Intent intent = getIntent();
        mAdapter = (StudentsAdapter) intent.getSerializableExtra("StudentsAdapter");

        //Set Activity data
        typePicker = (NumberPicker)findViewById(R.id.pointTypePicker);
        StudentInfoFragment.PointType[] pt = StudentInfoFragment.PointType.values();
        String[] types = new String[pt.length];
        for (int i = 0; i < pt.length; i++) {
            types[i] = pt[i].name();
        }
        typePicker.setMinValue(0);
        typePicker.setMaxValue(pt.length - 1);
        typePicker.setDisplayedValues(types);

        pointsPicker = (NumberPicker)findViewById(R.id.pointsPicker);
        pointsPicker.setMinValue(1);
        pointsPicker.setMaxValue(5);

        sdntUserName = (EditText)findViewById(R.id.pointsStudentName);

        findViewById(R.id.give_point).setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        myUniMagReader.stopSwipeCard();
        myUniMagReader.unregisterListen();
        myUniMagReader.release();
        super.onDestroy();
    }

    @Override
    public boolean getUserGrant(int arg0, String arg1) {
        Log.d(TAG, "getUserGrant -- " + arg1);
        return true;
    }

    @Override
    public void onReceiveMsgAutoConfigProgress(int arg0) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onReceiveMsgAutoConfigProgress");
    }

    @Override
    public void onReceiveMsgAutoConfigProgress(int i, double v, String s) {
        Log.d(TAG, "onReceiveMsgAutoConfigProgress");
    }

    @Override
    public void onReceiveMsgCardData(byte arg0, byte[] arg1) {
        Log.d(TAG, "onReceiveMsgCardData");
        Log.d(TAG, "Successful swipe!");

        String strData = new String(arg1);
        if(myUniMagReader.isSwipeCardRunning()) {
            myUniMagReader.stopSwipeCard();
        }

        // Match library card data. %FirstName^LastName^StudentNumber^DateOfCardRelease;LibraryNumber?
        String pattern = "\\%[a-zA-Z^]+(\\d)+\\^\\d{2}\\/\\d{2}\\/\\d{4}\\?\\;\\d+\\?";
        Log.d(TAG, "Card data: " + strData);
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(strData);
        String name = "";
        String studentNumber = "";
        if(m.find()) {
            String[] cardValues = strData.split(";")[0].split("\\^");
            for(String cardValue : cardValues) {
                if (cardValue.matches("^?\\d+$") ){
                    studentNumber = cardValue;
                    break;
                }
            }

            Student student = mAdapter.getStudentByStudentNumber(studentNumber);
            if (student != null){
                _id = student.get_id();
                displayName = student.getDisplayName();
                sdntUserName.setText(displayName);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Student isn't registered. Please register student.",  Toast.LENGTH_SHORT);
                toast.show();
            }

        }else {
            Toast toast = Toast.makeText(getApplicationContext(), "Invalid card data.",  Toast.LENGTH_SHORT);
            toast.show();
        }

        myUniMagReader.startSwipeCard();
    }

    @Override
    public void onReceiveMsgProcessingCardData() {
        Log.d(TAG, "onReceiveMsgProcessingCardData");

    }

    @Override
    public void onReceiveMsgToCalibrateReader() {
        Log.d(TAG, "onReceiveMsgToCalibrateReader");
    }

//    final Handler swipeHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            String text = (String)msg.obj;
//            TextView dataView = (TextView) findViewById(R.id.text_view);
//            dataView.setText(text);
//        }
//    };

    @Override
    public void onReceiveMsgCommandResult(int arg0, byte[] arg1) {
        Log.d(TAG, "onReceiveMsgCommandResult");
    }

    @Override
    public void onReceiveMsgConnected() {
        Log.d(TAG, "onReceiveMsgConnected");
        Log.d(TAG, "Card reader is connected.");
        Toast toast = Toast.makeText(getApplicationContext(), "Ready to Scan card.",  Toast.LENGTH_SHORT);
        toast.show();

        myUniMagReader.startSwipeCard();
    }

    @Override
    public void onReceiveMsgDisconnected() {
        Log.d(TAG, "onReceiveMsgDisconnected");
        if(myUniMagReader.isSwipeCardRunning()) {
            myUniMagReader.stopSwipeCard();
        }
        myUniMagReader.release();

    }

    @Override
    public void onReceiveMsgFailureInfo(int arg0, String arg1) {
        Log.d(TAG, "onReceiveMsgFailureInfo -- " + arg1);
    }

    @Override
    public void onReceiveMsgSDCardDFailed(String arg0) {
        Log.d(TAG, "onReceiveMsgSDCardDFailed -- " + arg0);
    }

    @Override
    public void onReceiveMsgTimeout(String arg0) {
        Log.d(TAG, "onReceiveMsgTimeout -- " + arg0);
        Log.d(TAG,"Timed out!");
        myUniMagReader.startSwipeCard();
    }

    @Override
    public void onReceiveMsgToConnect() {
        Log.d(TAG, "Swiper Powered Up");
    }

    @Override
    public void onReceiveMsgToSwipeCard() {
        Log.d(TAG, "onReceiveMsgToSwipeCard");
    }

    @Override
    public void onReceiveMsgAutoConfigCompleted(StructConfigParameters arg0) {
        Log.d(TAG, "onReceiveMsgAutoConfigCompleted");
    }



    String fromStream(InputStream in) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }

    private void writeToFile(String data) throws IOException {
        File file = new File(Common.getApplicationPath(getApplicationContext()), "IDT_uniMagCfg.xml");
        Log.d(TAG, "writing to" + file.getPath());

        try(FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.give_point){
            pointType = StudentInfoFragment.PointType.values()[typePicker.getValue()];
            num_points = pointsPicker.getValue();

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.confirm_title)
                    .setMessage("Awarding "+ displayName +" "+ num_points + " " + pointType + (num_points == 1 ? " point" : " points"))
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                awardPoints(_id, pointType, num_points);
                            } catch (IOException e) {
                                Log.d(TAG, "An error occured, See Stack Trace for more errors.");
                                e.printStackTrace();
                            }
                            MagStripeReaderActivity.this.finish();
                        }
                    }).setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    //TODO:: Join POST REQUEST CODE
    private void awardPoints(String id, StudentInfoFragment.PointType pointType, int num_points) throws IOException {

        List<AbstractMap.SimpleEntry<String, String>> urlParams = new ArrayList<>();
        urlParams.add(new AbstractMap.SimpleEntry<>("amount", Integer.toString(num_points)));
        urlParams.add(new AbstractMap.SimpleEntry<>("type", pointType.toString()));

        URL url = new URL(MainActivity.BASE_URL + "api/users/" + id + "/give?" + getQuery(urlParams));

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        if (LoginActivity.mCookieManager.getCookieStore().getCookies().size() > 0) {
            urlConnection.setRequestProperty("Cookie", TextUtils.join(";", LoginActivity.mCookieManager.getCookieStore().getCookies()));
        }

        urlConnection.connect();
    }



    //TODO:: Join this and LoginActivity into one class.
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

    public class LoadXMLConfigurationTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d(TAG, "Attempting to load xml config");

            Log.d(TAG, Common.getSDRootFilePath());
            InputStream is = getResources().openRawResource(R.raw.idt_unimagcfg);
            try {
                String res = fromStream(is);
                writeToFile(res);

            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }

            myUniMagReader.setXMLFileNameWithPath(Common.getApplicationPath(getApplicationContext()) + File.separator + "IDT_uniMagCfg.xml");
            myUniMagReader.loadingConfigurationXMLFile(true);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d(TAG, "Loaded xml config");
            if (myUniMagReader != null) {
                Log.d(TAG, "myUniMagReader isn't null!");
                myUniMagReader.registerListen();
            }

        }
    }
}