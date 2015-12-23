package moc5.amazingrace;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        route = ((AmazingRace)getApplication()).getSelectedRoute();
        updateRoutes();
        UpdateGui();
    }

    private void updateRoutes() {
        new AsyncTask<String, Objects, Route[]>() {
            @Override
            protected  Route[] doInBackground(String... strings) {
                Log.i("Console", String.format("Loading route list %s %s", strings[0], strings[1]));

                try {
                    return new ServiceProxy().getRoutes(strings[0], strings[1]);
                } catch(ServiceCallException e) {
                    Log.e(RouteDetailsActivity.this.toString(), "Failed to load route list");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Route[] routes) {
                if (routes != null) {
                    for (Route r : routes) {
                        if (r.getId().equals(route.getId())) {
                            ((AmazingRace) getApplication()).setSelectedRoute(r);
                            route = r;
                        }
                    }
                } else {
                    Toast.makeText(RouteDetailsActivity.this, R.string.couldNotLoadRoutes, Toast.LENGTH_LONG).show();
                }
            }
        }.execute("s1310307036", "s1310307036");
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
                    if (route.getNextCheckpoint() != null) {
                        Intent intent = new Intent(RouteDetailsActivity.this, NextCheckpointActivity.class);
                        startActivity(intent);
                    }
                }
            });

            txvVisitedCheckpoints.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (route.getVisitedCheckpoints() != null) {
                        Intent intent = new Intent(RouteDetailsActivity.this, PreviousCheckpointsActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}
