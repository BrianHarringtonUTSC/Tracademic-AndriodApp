package ca.utoronto.utsc.tracademia;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.idtechproducts.acom.Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import IDTech.MSR.XMLManager.StructConfigParameters;
import IDTech.MSR.uniMag.uniMagReader;
import IDTech.MSR.uniMag.uniMagReaderMsg;


public class MagStripeReaderActivity extends Activity implements uniMagReaderMsg {
    private static final String TAG = "MagStripeReaderActivity";

    private uniMagReader myUniMagReader;
    private LoadXMLConfigurationTask loadXMLConfigurationTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mag_strip_reader);

        if(myUniMagReader == null) {
            myUniMagReader = new uniMagReader(this,this);
            myUniMagReader.setSaveLogEnable(false);
            loadXMLConfigurationTask = new LoadXMLConfigurationTask();
            loadXMLConfigurationTask.execute();

            //myUniMagReader.setVerboseLoggingEnable(true);
//            myUniMagReader.registerListen();
        }

//        myUniMagReader.startSwipeCard();
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
        Log.d(TAG, "SWIPE - " + strData);
        if(myUniMagReader.isSwipeCardRunning()) {
            myUniMagReader.stopSwipeCard();
        }

        // Match the data we want.
//        String pattern = "%B(\\d+)\\^([^\\^]+)\\^(\\d{4})";
//        Log.d(TAG, pattern);
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(strData);
//        String card = "";
//        String name = "";
//        String exp = "";
//        String data = "";
//        if(m.find()) {
//            for(int a = 0; a < m.groupCount(); ++a) {
//                Log.d(TAG, a + " - "+m.group(a));
//            }
//            card = m.group(1);
//            name = m.group(2);
//            exp = m.group(3);
//            data = "Data: " + name + " -- " + card + " -- " + exp;
//            Log.d(TAG, data);
//
//            Message msg = new Message();
//            msg.obj = data;
//            swipeHandler.sendMessage(msg);
//        }

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
    }

    @Override
    public void onReceiveMsgToConnect() {
        Log.d(TAG,"Swiper Powered Up");
    }

    @Override
    public void onReceiveMsgToSwipeCard() {
        Log.d(TAG,"onReceiveMsgToSwipeCard");
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
        FileOutputStream stream = new FileOutputStream(file);
        try {
            stream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
    }

    public class LoadXMLConfigurationTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d(TAG, "Attempting to load xml config");

            File fileDir = getFilesDir();
            Log.d(TAG, fileDir.getPath());

            Log.d(TAG, Common.getSDRootFilePath());
            InputStream is = getResources().openRawResource(R.raw.idt_unimagcfg);
            try {
                String res = fromStream(is);
                writeToFile(res);

            } catch (IOException e) {
                Log.d(TAG, "NOOOOOOOOOOO");
            }

//            File f = new File(fileDir.getPath(), "idt_unimagcfg.xml");
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
                myUniMagReader.startSwipeCard();

            }

        }
    }
}