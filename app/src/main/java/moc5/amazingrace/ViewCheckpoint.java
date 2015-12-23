package moc5.amazingrace;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewCheckpoint extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_checkpoint);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.viewFragmentMap);
        GoogleMap map = mapFragment.getMap();

        String name = getIntent().getStringExtra("Name");
        int number = getIntent().getIntExtra("Number", 1);

        setTitle(number + ". " + name);
        double lat = getIntent().getDoubleExtra("Lat", 0);
        double lng = getIntent().getDoubleExtra("Long", 0);

        if (map != null) {
            LatLng location = new LatLng(lat, lng);
            map.addMarker(new MarkerOptions().position(location).title(name));

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
        }
    }
}
