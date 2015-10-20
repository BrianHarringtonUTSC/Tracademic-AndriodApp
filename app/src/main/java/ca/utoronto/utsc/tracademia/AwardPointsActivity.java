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
import android.widget.NumberPicker;


public class AwardPointsActivity  extends AppCompatActivity implements OnClickListener{
    int num_points;
    String displayName, _id;
    PointType pointType;
    NumberPicker typePicker, pointsPicker;
    StudentPoints studentPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.award_points);

        Intent intent = getIntent();
        displayName = intent.getStringExtra(getString(R.string.name));
        _id = intent.getStringExtra(getString(R.string._id));

        findViewById(R.id.give_point).setOnClickListener(this);

        typePicker = (NumberPicker)findViewById(R.id.pointTypePicker);
        PointType[] pt = PointType.values();
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
            pointType = PointType.values()[typePicker.getValue()];
            num_points = pointsPicker.getValue();


            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.confirm_title)
                    .setMessage("Awarding "+ displayName +" "+ num_points + " " + pointType + (num_points == 1 ? " point" : " points"))
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO:: Make API call to give point to student
                            AwardPointsActivity.this.finish();
                        }
                    }).setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }
}