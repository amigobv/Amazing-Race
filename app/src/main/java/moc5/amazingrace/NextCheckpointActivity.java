package moc5.amazingrace;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

        route = ((AmazingRace)getApplication()).getSelectedRoute();
        updateGui();
    }

    private void updateGui() {
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.fragmentMap);
        GoogleMap map = mapFragment.getMap();

        if (route != null) {
            final Checkpoint checkpoint = route.getNextCheckpoint();

            if (checkpoint != null)
            {
                setTitle(checkpoint.getNumber() + ". " + checkpoint.getName());

                TextView txvHint = (TextView) findViewById(R.id.txvCheckpointHint);
                txvHint.setText(checkpoint.getHint());

                Button btnCheck = (Button) findViewById(R.id.btnCheck);
                if (btnCheck != null ) {

                    btnCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String answer = ((EditText)findViewById(R.id.edtHint)).getText().toString();
                            if (answer.length() > 0)
                                submitCheckpoint(checkpoint.getId(), answer);
                            else
                                Toast.makeText(NextCheckpointActivity.this, R.string.ProvideAnswer, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                if (map != null) {
                    LatLng location = new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude());
                    map.addMarker(new MarkerOptions().position(location).title(checkpoint.getName()));

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
                }
            }
        }
    }

    private void submitCheckpoint(String id, String answer) {
        CheckpointRequest checkpoint = new CheckpointRequest();
        Request auth = ((AmazingRace)getApplication()).getAuthentification();

        checkpoint.setUserName(auth.getUserName());
        checkpoint.setPassword(auth.getPassword());
        checkpoint.setCheckpointId(id);
        checkpoint.setSecret(answer);

        new AsyncTask<Request, Objects, Boolean>() {
            @Override
            protected  Boolean doInBackground(Request... req) {
                try {
                    return new ServiceProxy().checkVisitedCheckpoint(req[0]);
                } catch(ServiceCallException e) {
                    Log.e(NextCheckpointActivity.this.toString(), "Failed to load route list");
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean answer) {
                if (answer) {
                    Toast.makeText(NextCheckpointActivity.this, R.string.SuccessfulSubmit, Toast.LENGTH_LONG).show();
                    updateRoutes();
                } else {
                    Toast.makeText(NextCheckpointActivity.this, R.string.InvalidSubmit, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(checkpoint);
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

                            if (r.getNextCheckpoint() == null) {
                                DialogHelper.showAlert(NextCheckpointActivity.this, R.string.DialogTitle,
                                        R.string.RouteDone, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            } else
                                finish();
                        }
                    }
                } else {
                    Toast.makeText(NextCheckpointActivity.this, R.string.couldNotLoadRoutes, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(((AmazingRace) getApplication()).getAuthentification());
    }
}
