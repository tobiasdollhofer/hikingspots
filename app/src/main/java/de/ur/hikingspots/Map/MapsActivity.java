package de.ur.hikingspots.Map;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.ur.hikingspots.AddActivity;
import de.ur.hikingspots.Authentication.LoginActivity;
import de.ur.hikingspots.R;
import de.ur.hikingspots.Singleton;
import de.ur.hikingspots.Spot;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Vibrator vib;
    private FirebaseAuth mAuth;
    private String lat, lgt;
    private LocationManager locationManager;
    private Location lastLocation;
    private int ms = 100, amplitude = 1;
    private final int PERMISSION_REQUEST_CODE = 1;
    ArrayList<Spot> spots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        vib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        locationManager = (LocationManager) getSystemService(MapsActivity.LOCATION_SERVICE);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        getLocation();

        double latitude = Double.parseDouble(lat);
        double longitude = Double.parseDouble(lgt);

        LatLng currentLocation = new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18));

        int sync = getIntent().getIntExtra("bigdata:synccode", -1);
        final ArrayList<Spot> spots = Singleton.get().getLargeData(sync);
        spots.size();
        for(Spot spot : spots){
            double lat = spot.getSpotLocation().getLatitude();
            double lgt = spot.getSpotLocation().getLongitude();
            LatLng marker = new LatLng(lat, lgt);
            mMap.addMarker(new MarkerOptions().position(marker).title(spot.getSpotName()).snippet(spot.getSpotDescription()));
        }

        addNewMarker();

        setOnMarkerClickListener();
    }

    private void addNewMarker(){
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng location) {
                mMap.addMarker(new MarkerOptions().position(location).title(""));
                Intent addNewSpot = new Intent(MapsActivity.this, AddActivity.class);
                startActivity(addNewSpot);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vib.vibrate(VibrationEffect.createOneShot(ms, amplitude));
                }
            }
        });
    }

    private void setOnMarkerClickListener(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void getLocation() {
        checkPermission();
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lat = Double.toString(lastLocation.getLatitude());
        lgt = Double.toString(lastLocation.getLongitude());
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    lastLocation = null;
                }
                return;
            }
        }
    }
}
