package moc5.amazingrace;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class AmazingRaceMainActivity extends AppCompatActivity {
    RouteListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((AmazingRace)getApplication()).setSelectedRoute(null);

        ListView lsvRoutes = (ListView)findViewById(R.id.lstRoutes);
        adapter = new RouteListAdapter();
        lsvRoutes.setAdapter(adapter);

        lsvRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Intent intent = new Intent(AmazingRaceMainActivity.this, RouteDetailsActivity.class);
                Route selectedRoute = adapter.getItem(position);
                ((AmazingRace)getApplication()).setSelectedRoute(selectedRoute);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateRoutes();
    }

    private void updateRoutes() {
        adapter.clear();

        new AsyncTask<Request, Objects, Route[]>() {
            @Override
            protected  Route[] doInBackground(Request... req) {
                try {
                    return new ServiceProxy().getRoutes(req[0]);
                } catch(ServiceCallException e) {
                    Log.e(AmazingRaceMainActivity.this.toString(), "Failed to load route list");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Route[] routes) {
                if (routes != null) {
                    adapter.addAll(routes);
                } else {
                    Toast.makeText(AmazingRaceMainActivity.this, R.string.couldNotLoadRoutes, Toast.LENGTH_LONG).show();
                }

            }
        }.execute(((AmazingRace)getApplication()).getAuthentification());
    }


    private class RouteListAdapter extends ArrayAdapter<Route> {

        public RouteListAdapter() {
            super(AmazingRaceMainActivity.this, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.view_route, null);
            }

            Route route = getItem(position);
            ((TextView) convertView.findViewById(R.id.txvName)).setText(route.getName());
            return convertView;

        }
    }
}
