package ca.utoronto.utsc.tracademia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/*
    Authors: Umair Idris and Markus Friesen
 */
public class PointsActivity extends AppCompatActivity implements OnClickListener{

    //The + button responsible for opening the barcode scanning app.
    private ImageButton scanBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        scanBtn = (ImageButton)findViewById(R.id.scanBarcode);
        scanBtn.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_points, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        Responsible for dealing with any view that is clicked.
        Currently supports: Opening the Barcode scanning app. If an app doesn't exit, the user
            will be prompted to download one.
     */
    public void onClick(View v) {
        //respond to clicks
        if(v.getId()==R.id.scanBarcode){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,
                intent);

        if (scanningResult != null) {
            String libraryNumber = scanningResult.getContents();

            Toast toast = Toast.makeText(getApplicationContext(), libraryNumber, Toast.LENGTH_SHORT);
            toast.show();

            Intent awardIntent = new Intent(this, AwardPointsActivity.class);
            awardIntent.putExtra(getString(R.string.libraryNumber), libraryNumber);
            startActivity(awardIntent);

        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
