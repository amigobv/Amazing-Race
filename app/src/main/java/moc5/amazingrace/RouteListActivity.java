package moc5.amazingrace;

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

public class RouteListActivity extends AppCompatActivity {
    RouteListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((AmazingRaceApp)getApplication()).setSelectedRoute(null);

        ListView lsvRoutes = (ListView)findViewById(R.id.lstRoutes);
        adapter = new RouteListAdapter();
        lsvRoutes.setAdapter(adapter);

        lsvRoutes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Intent intent = new Intent(RouteListActivity.this, RouteDetailsActivity.class);
                Route selectedRoute = adapter.getItem(position);
                ((AmazingRaceApp)getApplication()).setSelectedRoute(selectedRoute);
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

        new AsyncTask<String, Objects, Route[]>() {
            @Override
            protected  Route[] doInBackground(String... strings) {
                Log.i("Console", String.format("Loading route list %s %s", strings[0], strings[1]));

                try {
                    return new ServiceProxy().getRoutes(strings[0], strings[1]);
                } catch(ServiceCallException e) {
                    Log.e(RouteListActivity.this.toString(), "Failed to load route list");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Route[] routes) {
                if (routes != null) {
                    adapter.addAll(routes);
                } else {
                    Toast.makeText(RouteListActivity.this, R.string.couldNotLoadRoutes, Toast.LENGTH_LONG).show();
                }

            }
        }.execute("s1310307036", "s1310307036");
    }


    private class RouteListAdapter extends ArrayAdapter<Route> {

        public RouteListAdapter() {
            super(RouteListActivity.this, 0);
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
