package moc5.amazingrace;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

public class NextCheckpointActivity extends AppCompatActivity {
    private Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_checkpoint);

        ActionBar actionbar = getActionBar();

        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        route = ((AmazingRaceApp)getApplication()).getSelectedRoute();
        updateGui();
    }

    private void updateGui() {

        if (route != null) {
            Checkpoint checkpoint = route.getNextCheckpoint();

            if (checkpoint != null)
            {
                setTitle(checkpoint.getNumber() + ". " + checkpoint.getName());

                TextView txvHint = (TextView) findViewById(R.id.txvCheckpointHint);
                if (txvHint != null)
                    txvHint.setText(checkpoint.getHint());
            }
        }
    }
}
