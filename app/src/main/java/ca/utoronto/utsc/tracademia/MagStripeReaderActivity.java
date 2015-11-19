package ca.utoronto.utsc.tracademia;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import IDTech.MSR.XMLManager.StructConfigParameters;
import IDTech.MSR.uniMag.uniMagReader;
import IDTech.MSR.uniMag.uniMagReaderMsg;


public class MagStripeReaderActivity extends AppCompatActivity implements uniMagReaderMsg {
    private static final String TAG = "MagStripeReaderActivity";
    private final String[] REQUIRED_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private uniMagReader uniMagReader;
    private TextView readerStatus;
    private AudioManager mAudioManager;
    private int originalVolume;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mag_stripe_reader);
        readerStatus = (TextView) findViewById(R.id.reader_status_text_view);
        getPermissions();
    }

    private void init() {
        setReaderStatusNotReady();

        if(uniMagReader == null) {
            uniMagReader = new uniMagReader(this,this);
            uniMagReader.setSaveLogEnable(false);
            LoadXMLConfigurationTask loadXMLConfigurationTask = new LoadXMLConfigurationTask();
            loadXMLConfigurationTask.execute();
        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this,  permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void getPermissions() {
        // only need to check for permissions on SDK 23 and above
        if (Build.VERSION.SDK_INT > 22) {
            List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : REQUIRED_PERMISSIONS) {
                if (!hasPermission(permission)) {
                    permissionsToRequest.add(permission);
                }
            }
            if (permissionsToRequest.size() == 0) {
                init();
            } else {
                readerStatus.setText("Waiting for Permissions");
                ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), 0);
            }
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        boolean allRequiredPermissionsGranted = true;
        if (grantResults.length == permissions.length) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allRequiredPermissionsGranted = false;
                }
            }
        } else {
            allRequiredPermissionsGranted = false;
        }

        if (allRequiredPermissionsGranted) {
            init();
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Did not get required permissions to scan cards", Snackbar.LENGTH_LONG)
                    .show();
        }

    }

    private void setReaderStatusNotReady(){
        readerStatus.setText("Reader not ready");
        readerStatus.setTextColor(Color.RED);
    }

    @Override
    public void onDestroy() {
        if (uniMagReader != null) {
            uniMagReader.stopSwipeCard();
            uniMagReader.unregisterListen();
            uniMagReader.release();
        }
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
        }

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


        String data = new String(arg1);
        Log.d(TAG, "Successful swipe: " + data);

        if(uniMagReader.isSwipeCardRunning()) {
            uniMagReader.stopSwipeCard();
        }

        // Match library card data. %FirstName^LastName^StudentNumber^DateOfCardRelease;LibraryNumber?
        String[] dataSplit = data.split("\\^");

       if (dataSplit.length == 4 &&  dataSplit[2].matches("[0-9]{9,10}")) {
           String studentNumber = dataSplit[2];
           Intent resultIntent = new Intent();
           resultIntent.putExtra(MainActivity.ARG_STUDENT_NUMBER, studentNumber);
           setResult(Activity.RESULT_OK, resultIntent);
           finish();
       } else {
            Snackbar.make(findViewById(android.R.id.content), "Invalid card data: " + data, Snackbar.LENGTH_LONG)
                    .show();
            uniMagReader.startSwipeCard();
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
        readerStatus.setTextColor(Color.parseColor("#339933"));
        uniMagReader.startSwipeCard();
        readerStatus.setText("Ready to Scan card");
    }

    @Override
    public void onReceiveMsgDisconnected() {
        Log.d(TAG, "onReceiveMsgDisconnected");
        if(uniMagReader.isSwipeCardRunning()) {
            uniMagReader.stopSwipeCard();
        }
        setReaderStatusNotReady();
        Snackbar.make(findViewById(android.R.id.content), "Reader Disconnected", Snackbar.LENGTH_LONG)
                .show();
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
        Log.d(TAG, "Timed out!");
        uniMagReader.startSwipeCard();
    }

    @Override
    public void onReceiveMsgToConnect() {
        Log.d(TAG, "Reader Powered Up");
    }

    @Override
    public void onReceiveMsgToSwipeCard() {
        Log.d(TAG, "onReceiveMsgToSwipeCard");
    }

    @Override
    public void onReceiveMsgAutoConfigCompleted(StructConfigParameters arg0) {
        Log.d(TAG, "onReceiveMsgAutoConfigCompleted");
    }

    public class LoadXMLConfigurationTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, "Attempting to load xml config");
            uniMagReader.setXMLFileNameWithPath("android.resource://" + getPackageName() + "/" + R.raw.idt_unimagcfg);
            uniMagReader.loadingConfigurationXMLFile(true);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d(TAG, "Loaded xml config");
            if (uniMagReader != null) {
                Log.d(TAG, "uniMagReader ready to listen");
                uniMagReader.registerListen();
            }

        }
    }
}