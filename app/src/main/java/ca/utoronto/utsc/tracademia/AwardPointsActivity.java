package ca.utoronto.utsc.tracademia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

/**
 * Created by markus on 9/15/15.
 */
public class AwardPointsActivity  extends AppCompatActivity {

    int teachingPoints, challengePoints, experiencePoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.award_points);
        
    }


}