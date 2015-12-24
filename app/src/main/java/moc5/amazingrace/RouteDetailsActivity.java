package moc5.amazingrace;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Format;
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
        updateGui();
    }

    @Override
    protected void onResume() {
        super.onResume();
        route = ((AmazingRace)getApplication()).getSelectedRoute();
    }

    private void updateRoutes() {
        new AsyncTask<Request, Objects, Route[]>() {
            @Override
            protected  Route[] doInBackground(Request... req) {
                try {
                    return new ServiceProxy().getRoutes(req[0]);
                } catch(ServiceCallException e) {
                    Log.e("Console", "Failed to load route list");
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
        }.execute(((AmazingRace) getApplication()).getAuthentification());
    }

    private void updateGui() {
        if (route != null) {
            setTitle(route.getName());
            TextView txvNextCheckpoint = (TextView)findViewById(R.id.txvNextCheckpoint);
            TextView txvVisitedCheckpoints = (TextView)findViewById(R.id.txvVisitedCheckpoint);

            txvNextCheckpoint.setText(R.string.nextCheckpoint);
            txvVisitedCheckpoints.setText(R.string.visitedCheckpoints);

            if (route.getNextCheckpoint() == null) {
                txvNextCheckpoint.setTextColor(ContextCompat.getColor(RouteDetailsActivity.this, R.color.colorUnavailable));
            } else {
                txvNextCheckpoint.setTextColor(ContextCompat.getColor(RouteDetailsActivity.this, android.R.color.tertiary_text_light));
            }

            if (route.getVisitedCheckpoints() == null || route.getVisitedCheckpoints().length == 0) {
                txvVisitedCheckpoints.setTextColor(ContextCompat.getColor(RouteDetailsActivity.this, R.color.colorUnavailable));
            } else {
                txvVisitedCheckpoints.setTextColor(ContextCompat.getColor(RouteDetailsActivity.this, android.R.color.tertiary_text_light));
            }

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
                    if (route.getVisitedCheckpoints() != null && route.getVisitedCheckpoints().length != 0) {
                        Intent intent = new Intent(RouteDetailsActivity.this, PreviousCheckpointsActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.route_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.mnuResetRoute).setVisible(route != null);
        menu.findItem(R.id.mnuResetAllRoutes).setVisible(route != null);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: finish();   return true;
            case R.id.mnuResetRoute:
                if (route != null) {
                    resetRoute();
                }
                return true;

            case R.id.mnuResetAllRoutes:
                resetAllRoutes();
                return true;

            default: return super.onOptionsItemSelected(item);

        }
    }

    private void resetRoute() {
        RouteRequest routeReq = new RouteRequest();
        Request auth = ((AmazingRace) getApplication()).getAuthentification();

        routeReq.setUserName(auth.getUserName());
        routeReq.setPassword(auth.getPassword());
        routeReq.setRouteId(route.getId());

        new AsyncTask<Request, Objects, Boolean>() {
            @Override
            protected  Boolean doInBackground(Request... req) {
                try {
                    return new ServiceProxy().resetRoute(req[0]);
                } catch(ServiceCallException e) {
                    Log.e(RouteDetailsActivity.this.toString(), "Failed to load route list");
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean answer) {
                if (answer) {
                    finish();
                } else {
                    Toast.makeText(RouteDetailsActivity.this, R.string.couldNotLoadRoutes, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(routeReq);
    }

    private void resetAllRoutes() {
        new AsyncTask<Request, Objects, Boolean>() {
            @Override
            protected  Boolean doInBackground(Request... req) {
                try {
                    return new ServiceProxy().resetAllRoute(req[0]);
                } catch(ServiceCallException e) {
                    Log.e(RouteDetailsActivity.this.toString(), "Failed to load route list");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Boolean answer) {
                if (answer) {
                    finish();
                } else {
                    Toast.makeText(RouteDetailsActivity.this, R.string.couldNotLoadRoutes, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(((AmazingRace) getApplication()).getAuthentification());
    }
}
