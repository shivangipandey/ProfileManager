package yours.appli_pro_man.shivangipandey.profilemanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    int size = 0;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Map Profiles");

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        if (!isOnline())
            Toast.makeText(this, "Internet and GPS connectivity required.", Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fabMap = findViewById(R.id.fabMap);

        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this, EditMaps.class));
                overridePendingTransition(R.anim.bottom_in, R.anim.top_out);

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera

        SimpleDateFormat format1 = new SimpleDateFormat("HH:MM");
        String time = format1.format(Calendar.getInstance().getTime());
        int unit = Integer.parseInt(time.substring(0, 2));

        if (unit > 19 || unit < 5) {
            boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                    .getString(R.string.style_json)));

            if (!success)
                Toast.makeText(this, "Parsing failed", Toast.LENGTH_SHORT).show();
        } else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng sydney = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerClickListener(this);
        addMarkers(mMap);

    }

    private void addMarkers(GoogleMap mMap) {
        ExtractFromFile extractFromFile = new ExtractFromFile();
        ProfileNames profileNames = extractFromFile.deserializedProfileNamesList(this, "profileNames_Maps");
        ArrayList<String> mapNames = null;
        if (profileNames != null)
            mapNames = profileNames.getProfileArrayListMap();

        if (mapNames != null && !mapNames.isEmpty()) {
            Marker marker = null;
            for (String name : mapNames) {
                Profiles profiles = extractFromFile.deserializeProfile(name, this);
                if (profiles == null) {
                    Toast.makeText(this, "App needs LOACATION ACCESS PERMISSION.", Toast.LENGTH_LONG).show();
                    return;
                }
                marker = mMap.addMarker(new MarkerOptions()
                        .position(profiles.getLatLng())
                        .title(name)
                );
                size++;
            }
            if (marker != null)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (size == 0)
            return false;
        String name = marker.getTitle();
        Intent i = new Intent(this, EditMaps.class);
        i.putExtra("name", name);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_switch_basic:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.from_middle, R.anim.to_middle);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

}
