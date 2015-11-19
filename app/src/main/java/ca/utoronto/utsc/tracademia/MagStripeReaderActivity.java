package ca.utoronto.utsc.tracademia;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.idtechproducts.acom.Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import IDTech.MSR.XMLManager.StructConfigParameters;
import IDTech.MSR.uniMag.uniMagReader;
import IDTech.MSR.uniMag.uniMagReaderMsg;


public class MagStripeReaderActivity extends AppCompatActivity implements uniMagReaderMsg {
    private static final String TAG = "MagStripeReaderActivity";

    private uniMagReader myUniMagReader;
    private LoadXMLConfigurationTask loadXMLConfigurationTask;
    private EditText sdntUserName;
    protected StudentsAdapter mAdapter;
    private TextView readerStatus;

    StudentInfoFragment.PointType pointType;
    NumberPicker typePicker, pointsPicker;
    Student student;
    String displayName, _id;
    int num_points;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mag_stripe_reader);
        if(myUniMagReader == null) {
            myUniMagReader = new uniMagReader(this,this);
            myUniMagReader.setSaveLogEnable(false);
            loadXMLConfigurationTask = new LoadXMLConfigurationTask();
            loadXMLConfigurationTask.execute();
        }

        readerStatus = (TextView) (TextView) findViewById(R.id.reader_status_text_view);
        setReaderUnconnected();
    }

    private void setReaderUnconnected(){
        readerStatus.setText("Reader not ready");
        readerStatus.setTextColor(Color.RED);
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
            Intent resultIntent = new Intent();
            resultIntent.putExtra(MainActivity.ARG_STUDENT_NUMBER, studentNumber);
            setResult(Activity.RESULT_OK, resultIntent);
            if(myUniMagReader.isSwipeCardRunning()) {
                myUniMagReader.stopSwipeCard();
            }
            myUniMagReader.release();
            finish();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Invalid card data", Snackbar.LENGTH_LONG)
                    .show();
            myUniMagReader.startSwipeCard();
        }
    }

    @Override
    public void onReceiveMsgProcessingCardData() {
        Log.d(TAG, "onReceiveMsgProcessingCardData");

    }

    @Override
    public void onReceiveMsgToCalibrateReader() {
        Log.d(TAG, "onReceiveMsgToCalibrateReader");
    }

    @Override
    public void onReceiveMsgCommandResult(int arg0, byte[] arg1) {
        Log.d(TAG, "onReceiveMsgCommandResult");
    }

    @Override
    public void onReceiveMsgConnected() {
        Log.d(TAG, "onReceiveMsgConnected");

        readerStatus.setText("Ready to Scan card");
        readerStatus.setTextColor(Color.parseColor("#339933"));
        myUniMagReader.startSwipeCard();
    }

    @Override
    public void onReceiveMsgDisconnected() {
        Log.d(TAG, "onReceiveMsgDisconnected");
        if(myUniMagReader.isSwipeCardRunning()) {
            myUniMagReader.stopSwipeCard();
        }
        setReaderUnconnected();
        myUniMagReader.release();
        setResult(Activity.RESULT_CANCELED);
        finish();
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void writeToFile(String data) throws IOException {
        File file = new File(Common.getApplicationPath(getApplicationContext()), "IDT_uniMagCfg.xml");
        Log.d(TAG, "writing to" + file.getPath());

        try(FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
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