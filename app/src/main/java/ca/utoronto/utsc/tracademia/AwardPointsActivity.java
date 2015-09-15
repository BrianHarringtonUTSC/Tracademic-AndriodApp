package ca.utoronto.utsc.tracademia;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Created by markus on 9/15/15.
 */
public class AwardPointsActivity  extends AppCompatActivity implements OnClickListener{
    int num_points = 5;
    String name = "Bob";
    PointType pointType = PointType.Challenge;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.award_points);

        Intent intent = getIntent();
        String libraryNumber = intent.getStringExtra(getString(R.string.libraryNumber));

        submitButton = (Button)findViewById(R.id.give_point);
        submitButton.setOnClickListener(this);
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

    public void onClick(View v) {
        if(v.getId()==R.id.give_point){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.confirm_title)
                    .setMessage("You are giving " + name +" "+ num_points + " " + pointType.toString() +" points!")
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Stop the activity
                            AwardPointsActivity.this.finish();
                        }
                    }).setNegativeButton(R.string.cancel, null)
                            .show();
        }
    }
}