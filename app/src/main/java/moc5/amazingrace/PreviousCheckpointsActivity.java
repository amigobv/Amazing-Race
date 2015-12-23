package moc5.amazingrace;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PreviousCheckpointsActivity extends AppCompatActivity {
    CheckpointListAdapter adapter;
    private Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_checkpoints);

        ActionBar actionbar = getActionBar();

        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        ListView lsvCheckpoints = (ListView)findViewById(R.id.lstPrevRoutes);
        adapter = new CheckpointListAdapter();
        lsvCheckpoints.setAdapter(adapter);

        lsvCheckpoints.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Intent intent = new Intent(PreviousCheckpointsActivity.this, ViewCheckpoint.class);
                intent.putExtra("Name", adapter.getItem(position).getName());
                intent.putExtra("Number", adapter.getItem(position).getNumber());
                intent.putExtra("Lat", adapter.getItem(position).getLatitude());
                intent.putExtra("Long", adapter.getItem(position).getLongitude());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        route = ((AmazingRace) getApplication()).getSelectedRoute();
        updateCheckpoints();
    }

    private void updateCheckpoints() {
        adapter.clear();

        if (route != null)
            adapter.addAll(route.getVisitedCheckpoints());
    }

    private class CheckpointListAdapter extends ArrayAdapter<Checkpoint> {

        public CheckpointListAdapter() {
            super(PreviousCheckpointsActivity.this, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.view_prev_checkpoints, null);
            }

            Checkpoint checkpoint = getItem(position);

            if (checkpoint != null) {
                ((TextView) convertView.findViewById(R.id.txvVisited)).setText(checkpoint.getNumber() + ". " + checkpoint.getName());
            }
            return convertView;

        }
    }
}
