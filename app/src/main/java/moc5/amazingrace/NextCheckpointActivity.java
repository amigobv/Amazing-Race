package moc5.amazingrace;

import android.app.ActionBar;
import android.app.FragmentManager;
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
        new AsyncTask<String, Objects, Boolean>() {
            @Override
            protected  Boolean doInBackground(String... strings) {
                Log.i("Console", String.format("Check checkpoint %s %s", strings[0], strings[1]));

                try {
                    CheckpointRequest req = new CheckpointRequest();
                    req.setUserName(strings[0]);
                    req.setPassword(strings[1]);
                    req.setCheckpointId(strings[2]);
                    req.setSecret(strings[3]);
                    return new ServiceProxy().checkVisitedCheckpoint(req);
                } catch(ServiceCallException e) {
                    Log.e(NextCheckpointActivity.this.toString(), "Failed to load route list");
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean answer) {
                if (answer) {
                    Toast.makeText(NextCheckpointActivity.this, R.string.SuccessfulSubmit, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(NextCheckpointActivity.this, RouteDetailsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(NextCheckpointActivity.this, R.string.InvalidSubmit, Toast.LENGTH_LONG).show();
                }
            }
        }.execute("s1310307036", "s1310307036", id, answer);
    }
}
