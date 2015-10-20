package ca.utoronto.utsc.tracademia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import IDTech.MSR.XMLManager.StructConfigParameters;
import IDTech.MSR.uniMag.UniMagTools.uniMagReaderToolsMsg;
import IDTech.MSR.uniMag.uniMagReaderMsg;

public class MagStripReaderActivity extends AppCompatActivity implements uniMagReaderMsg, uniMagReaderToolsMsg {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mag_strip_reader);
    }

    @Override
    public void onReceiveMsgToConnect() {

    }

    @Override
    public void onReceiveMsgConnected() {

    }

    @Override
    public void onReceiveMsgDisconnected() {

    }

    @Override
    public void onReceiveMsgTimeout(String s) {

    }

    @Override
    public void onReceiveMsgToSwipeCard() {

    }

    @Override
    public void onReceiveMsgCommandResult(int i, byte[] bytes) {

    }

    @Override
    public void onReceiveMsgCardData(byte b, byte[] bytes) {

    }

    @Override
    public void onReceiveMsgProcessingCardData() {

    }

    @Override
    public void onReceiveMsgToCalibrateReader() {

    }

    @Override
    public void onReceiveMsgSDCardDFailed(String s) {

    }

    @Override
    public void onReceiveMsgFailureInfo(int i, String s) {

    }

    @Override
    public void onReceiveMsgAutoConfigProgress(int i) {

    }

    @Override
    public void onReceiveMsgAutoConfigProgress(int i, double v, String s) {

    }

    @Override
    public void onReceiveMsgAutoConfigCompleted(StructConfigParameters structConfigParameters) {

    }

    @Override
    public boolean getUserGrant(int i, String s) {
        return false;
    }

    @Override
    public void onReceiveMsgUpdateFirmwareProgress(int i) {

    }

    @Override
    public void onReceiveMsgUpdateFirmwareResult(int i) {

    }

    @Override
    public void onReceiveMsgChallengeResult(int i, byte[] bytes) {

    }
}
