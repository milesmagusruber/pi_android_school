package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.milesmagusruber.secretserviceflickrsearch.R;

import java.util.Locale;

import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_LATITUDE;
import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_LONGITUDE;

public class GoogleMapsSearchActivity extends FragmentActivity implements OnMapReadyCallback {

    //constants
    public static final float INIT_ZOOM = 10f;
    private static final int REQUEST_LOCATION_PERMISSION = 5;

    //map variables
    private GoogleMap googleMap;
    private Marker marker;

    //Button that returns result coordinates
    private Button buttonGeoSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps_search);
        buttonGeoSearch = findViewById(R.id.button_geo_result);
        buttonGeoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marker == null) {
                    Toast.makeText(GoogleMapsSearchActivity.this, R.string.geo_search_no_markers, Toast.LENGTH_LONG).show();
                } else {
                    LatLng latLng = marker.getPosition();
                    Intent intent = new Intent(GoogleMapsSearchActivity.this, FlickrSearchActivity.class);
                    intent.putExtra(EXTRA_LATITUDE, latLng.latitude);
                    intent.putExtra(EXTRA_LONGITUDE, latLng.longitude);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        setNewMarker(googleMap);
        getLocation(googleMap);
    }


    //This method is used to set a marker on new location and delete the old one
    private void setNewMarker(final GoogleMap gMap) {
        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (marker != null) {
                    marker.remove();
                }

                String snippet = String.format(Locale.getDefault(),
                        getString(R.string.geo_snippet),
                        latLng.latitude,
                        latLng.longitude);

                marker = gMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.marker_title))
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_VIOLET)));
            }
        });
    }

    /*This method is used to get our location
     * If we can't get it then we have a default one in Odessa
     * */
    private void getLocation(GoogleMap gMap) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
            try {
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setCostAllowed(false);
                String provider = locationManager.getBestProvider(criteria, false);
                Location location = locationManager.getLastKnownLocation(provider);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, INIT_ZOOM));
            }catch(Exception e){
                Toast.makeText(GoogleMapsSearchActivity.this, R.string.geo_location_problem, Toast.LENGTH_LONG).show();
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.468018, 30.734358), INIT_ZOOM));
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.468018, 30.734358), INIT_ZOOM));
        }
    }

}
