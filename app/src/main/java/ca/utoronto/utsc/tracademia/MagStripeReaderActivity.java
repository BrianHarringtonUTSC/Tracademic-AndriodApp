package ca.utoronto.utsc.tracademia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import IDTech.MSR.XMLManager.StructConfigParameters;
import IDTech.MSR.uniMag.UniMagTools.uniMagReaderToolsMsg;
import IDTech.MSR.uniMag.uniMagReaderMsg;

public class MagStripeReaderActivity extends AppCompatActivity implements uniMagReaderMsg, uniMagReaderToolsMsg {

    private static final String TAG = "MagStripeReader";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mag_strip_reader);
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
}
