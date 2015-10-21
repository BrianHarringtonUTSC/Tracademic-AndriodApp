package ca.utoronto.utsc.tracademia;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import IDTech.MSR.XMLManager.StructConfigParameters;
import IDTech.MSR.uniMag.UniMagTools.uniMagReaderToolsMsg;
import IDTech.MSR.uniMag.UniMagTools.uniMagSDKTools;
import IDTech.MSR.uniMag.uniMagReader;
import IDTech.MSR.uniMag.uniMagReaderMsg;

public class MagStripeReaderActivity extends AppCompatActivity implements uniMagReaderMsg, uniMagReaderToolsMsg {

    // declaring the instance of the uniMagReader;
    private uniMagReader myUniMagReader = null;
    //TODO: REMOVE
    private uniMagSDKTools firmwareUpdateTool = null;

    private static final String TAG = "MagStripeReader";
    private StructConfigParameters profile = null;
    private ProfileDatabase profileDatabase = null;
    private Handler handler = new Handler();

    //update the powerup status
    private int percent = 0;
    private long beginTime = 0;
    private long beginTimeOfAutoConfig = 0;
    private byte[] challengeResponse = null;
    private boolean isUseAutoConfigProfileChecked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mag_strip_reader);

        profileDatabase = new ProfileDatabase(this);
        profileDatabase.initializeDB();
        isUseAutoConfigProfileChecked = profileDatabase.getIsUseAutoConfigProfile();

        initializeReader();

        if (myUniMagReader!=null)
        {
            beginTime = System.currentTimeMillis();

            if(myUniMagReader.startSwipeCard())
            {
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, "Starting read", Toast.LENGTH_SHORT);
                toast.show();
                Log.d("Demo Info >>>>>","to startSwipeCard");
            }
            else
                Log.d("Demo Info >>>>>","cannot startSwipeCard");
        }
    }

    private void initializeReader()
    {
        if(myUniMagReader!=null){
            myUniMagReader.unregisterListen();
            myUniMagReader.release();
            myUniMagReader = null;
        }
        myUniMagReader = new uniMagReader(this,this,uniMagReader.ReaderType.SHUTTLE);

        if (myUniMagReader == null)
            return;

        if (isUseAutoConfigProfileChecked) {
            if (profileDatabase.updateProfileFromDB()) {
                this.profile = profileDatabase.getProfile();
                Toast.makeText(this, "AutoConfig profile has been loaded.", Toast.LENGTH_LONG).show();
                handler.post(doConnectUsingProfile);
            }
            else {
                Toast.makeText(this, "No profile found. Please run AutoConfig first.", Toast.LENGTH_LONG).show();
            }
        } else {
            /////////////////////////////////////////////////////////////////////////////////
            // Network operation is prohibited in the UI Thread if target API is 11 or above.
            // If target API is 11 or above, please use AsyncTask to avoid errors.
            myUniMagReader.setXMLFileNameWithPath(null);
            myUniMagReader.loadingConfigurationXMLFile(true);
            /////////////////////////////////////////////////////////////////////////////////
        }

        /////////////////////////////////////////////////////////////////////////////////
        // Network operation is prohibited in the UI Thread if target API is 11 or above.
        // If target API is 11 or above, please use AsyncTask to avoid errors.

//        myUniMagReader.loadingConfigurationXMLFile(true);
        /////////////////////////////////////////////////////////////////////////////////

//        //Initializing SDKTool for firmware update
//        firmwareUpdateTool = new uniMagSDKTools(this,this);
//        firmwareUpdateTool.setUniMagReader(myUniMagReader);
//        myUniMagReader.setSDKToolProxy(firmwareUpdateTool.getSDKToolProxy());

    }
    @Override
    protected void onDestroy() {
        if (myUniMagReader != null) {
            myUniMagReader.unregisterListen();
            myUniMagReader.stopSwipeCard();
            myUniMagReader.release();
        }
        profileDatabase.closeDB();
        super.onDestroy();

    }

    @Override
    public void onReceiveMsgToConnect() {
        Log.d(TAG, "1");
    }

    @Override
    public void onReceiveMsgConnected() {
        Log.d(TAG, "2");
    }

    @Override
    public void onReceiveMsgDisconnected() {
        Log.d(TAG, "3");
    }

    @Override
    public void onReceiveMsgTimeout(String s) {
        Log.d(TAG, "4");
    }

    @Override
    public void onReceiveMsgToSwipeCard() {
        Log.d(TAG, "5");
    }

    @Override
    public void onReceiveMsgCommandResult(int i, byte[] bytes) {
        Log.d(TAG, "6");
    }

    @Override
    public void onReceiveMsgCardData(byte b, byte[] bytes) {
        Log.d(TAG, "7");
    }

    @Override
    public void onReceiveMsgProcessingCardData() {
        Log.d(TAG, "8");
    }

    @Override
    public void onReceiveMsgToCalibrateReader() {
        Log.d(TAG, "9");
    }

    @Override
    public void onReceiveMsgSDCardDFailed(String s) {
        Log.d(TAG, "10");
    }

    @Override
    public void onReceiveMsgFailureInfo(int i, String s) {
        Log.d(TAG, "11");
    }

    @Override
    public void onReceiveMsgAutoConfigProgress(int i) {
        Log.d(TAG, "12");
    }

    @Override
    public void onReceiveMsgAutoConfigProgress(int i, double v, String s) {
        Log.d(TAG, "13");
    }

    @Override
    public void onReceiveMsgAutoConfigCompleted(StructConfigParameters structConfigParameters) {
        Log.d(TAG, "14");
    }

    @Override
    public boolean getUserGrant(int i, String s) {
        Log.d(TAG, "15");
        return false;
    }

    @Override
    public void onReceiveMsgUpdateFirmwareProgress(int i) {
        Log.d(TAG, "16");
    }

    @Override
    public void onReceiveMsgUpdateFirmwareResult(int i) {
        Log.d(TAG, "17");
    }

    @Override
    public void onReceiveMsgChallengeResult(int i, byte[] bytes) {
        Log.d(TAG, "18");
    }
    private Runnable doConnectUsingProfile = new Runnable()
    {
        public void run() {
            if (myUniMagReader != null)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myUniMagReader.connectWithProfile(profile);
            }
        }
    };

}
