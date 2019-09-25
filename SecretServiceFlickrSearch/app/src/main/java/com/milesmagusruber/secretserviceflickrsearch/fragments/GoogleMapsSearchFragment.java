package com.milesmagusruber.secretserviceflickrsearch.fragments;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.milesmagusruber.secretserviceflickrsearch.R;

import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class GoogleMapsSearchFragment extends Fragment implements OnMapReadyCallback {

    //constants
    public static final float INIT_ZOOM = 13f;
    private static final int REQUEST_LOCATION_PERMISSION = 5;
    private static final double DEFAULT_LATITUDE=46.468018;
    private static final double DEFAULT_LONGITUDE=30.734358;


    //map variables
    private GoogleMap googleMap;
    private Marker marker;


    //Button that returns result coordinates
    private MaterialButton buttonGeoSearch;


    //interface to container Activity
    public interface MapFragmentListener {
        void onPlaceSelected(double latitude, double longitude);
    }

    private MapFragmentListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapFragmentListener) {
            listener = (MapFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement methods of MapFragmentListener");
        }
    }

    @Override
    public void onDetach(){
        listener=null;
        super.onDetach();
    }


    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().setTitle(R.string.title_geo_search);
    }

    //constructor
    public GoogleMapsSearchFragment() {
    }

    public static GoogleMapsSearchFragment newInstance() {
        return new GoogleMapsSearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_google_maps_search, container, false);
        buttonGeoSearch = view.findViewById(R.id.button_geo_result);
        buttonGeoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marker == null) {
                    Toast.makeText(getActivity(), R.string.geo_search_no_markers, Toast.LENGTH_LONG).show();
                } else {
                    LatLng latLng = marker.getPosition();
                    listener.onPlaceSelected(latLng.latitude,latLng.longitude);
                }
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
        return view;
    }




    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                setMarker(latLng);
            }
        });
        getLocation();

    }

    //setting marker in a point we need
    private void setMarker(LatLng latLng){
        if (marker != null) {
            marker.remove();
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, INIT_ZOOM));

        String snippet = String.format(Locale.getDefault(),
                getString(R.string.geo_snippet),
                latLng.latitude,
                latLng.longitude);

        marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.marker_title))
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_VIOLET)));
    }

    /*This method is used to get our location
     * If we can't get it then we have a default one in Odessa
     * */
    private void getLocation(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            // Get last known recent location using Google Play Services SDK
            FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getActivity());
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                setMarker(latLng);
                            }else{
                                setDefaultLocation(R.string.geo_location_problem);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            setDefaultLocation(R.string.geo_location_problem);
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }else{
                    setDefaultLocation(R.string.geo_location_permission_not_granted);
                }
            }
        }
    }

    public void setDefaultLocation(int stringResId){
        setMarker(new LatLng(DEFAULT_LATITUDE,DEFAULT_LONGITUDE));
        Toast.makeText(getActivity(), stringResId, Toast.LENGTH_LONG).show();
    }

}
