package moc5.amazingrace;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

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

        /*
        lsvCheckpoints.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Intent intent = new Intent(PreviousCheckpointsActivity.this, PreviousCheckpointsDetailsActivity.class);
                Checkpoint selectedCheckpoint = adapter.getItem(position);
                startActivity(intent);
            }
        });
        */
    }

    @Override
    protected void onStart() {
        super.onStart();

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
            ((TextView) convertView.findViewById(R.id.txvName)).setText(checkpoint.getName());
            return convertView;

        }
    }
}
