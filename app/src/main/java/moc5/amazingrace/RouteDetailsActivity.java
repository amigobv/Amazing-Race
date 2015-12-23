package moc5.amazingrace;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class RouteDetailsActivity extends AppCompatActivity {
    private Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_details);

        ActionBar actionbar = getActionBar();

        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        route = ((AmazingRaceApp)getApplication()).getSelectedRoute();
        UpdateGui();
    }

    private void UpdateGui() {

        if (route != null) {
            setTitle(route.getName());
            TextView txvNextCheckpoint = (TextView)findViewById(R.id.txvNextCheckpoint);
            TextView txvVisitedCheckpoints = (TextView)findViewById(R.id.txvVisitedCheckpoint);

            txvNextCheckpoint.setText(R.string.nextCheckpoint);
            txvVisitedCheckpoints.setText(R.string.visitedCheckpoints);

            txvNextCheckpoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RouteDetailsActivity.this, NextCheckpointActivity.class);
                    intent.putExtra("routeId", route.getId());
                    startActivity(intent);
                }
            });

            txvVisitedCheckpoints.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RouteDetailsActivity.this, PreviousCheckpointsActivity.class);
                    intent.putExtra("routeId", route.getId());
                    startActivity(intent);
                }
            });
        }
    }
}
