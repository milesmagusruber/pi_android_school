package com.milesmagusruber.beerpatrol.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.milesmagusruber.beerpatrol.BuildConfig;
import com.milesmagusruber.beerpatrol.R;
import com.milesmagusruber.beerpatrol.adapters.BreweriesAdapter;
import com.milesmagusruber.beerpatrol.listeners.OnBrewerySelectedListener;
import com.milesmagusruber.beerpatrol.listeners.OnMapWorkSelectedListener;
import com.milesmagusruber.beerpatrol.listeners.OnUseMapInBreweries;
import com.milesmagusruber.beerpatrol.network.NetworkHelper;
import com.milesmagusruber.beerpatrol.network.model.BreweryDBResponse;
import com.milesmagusruber.beerpatrol.network.model.BreweryLocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class BreweriesFragment extends Fragment implements OnUseMapInBreweries {


    private final int REQUEST_LOCATION_PERMISSION = 5;

    private final static String EXTRA_LOCATION_LAT = BuildConfig.APPLICATION_ID + ".location.lat";
    private final static String EXTRA_LOCATION_LNG = BuildConfig.APPLICATION_ID + ".location.lng";
    private final static String EXTRA_LOCATION_RADIUS = BuildConfig.APPLICATION_ID + ".location.radius";

    private final double CONST_LAT = 40.6169416;
    private final double CONST_LNG = -106.0018273;
    private final double CONST_RADIUS = 100;

    private double latitude;
    private double longitude;
    private double radius;

    private ProgressBar breweryDownloadProgressBar;
    private ProgressBar breweryScrollProgressBar;
    private TextView textViewBreweriesError;
    private RecyclerView rvBreweries;
    private MaterialButton buttonBreweriesGetByLocation;
    private MaterialButton buttonBreweriesGetByMap;


    //adapter for BreweryDB beers
    private BreweriesAdapter breweriesAdapter;
    private int breweriesPage; //controlling infinite search
    private int totalPages; //total number of pages result
    private boolean pagesEndReached; // to know if we reach an end of pages
    private boolean isLoading; //to know if we still load our pages

    private ItemTouchHelper itemTouchHelper; //For touch swipes
    private RecyclerView.OnScrollListener onScrollListener; //For scrolls
    private LinearLayoutManager layoutManager;

    public static final String TAG = "BeerSearchFragment";

    //Working with Network
    private NetworkHelper networkHelper;

    //Controlling Retrofit calls in this activity
    private Call<BreweryDBResponse<BreweryLocation>> call;

    //Listener to transfer from BeerSearchFragment to BeerInfoFragment
    private OnBrewerySelectedListener listener;

    private OnMapWorkSelectedListener mapListener;

    //listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBrewerySelectedListener) {
            listener = (OnBrewerySelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement methods of OnBrewerySelectedListener");
        }

        if (context instanceof OnMapWorkSelectedListener) {
            mapListener = (OnMapWorkSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement methods of OnMapWorkSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        mapListener = null;
        super.onDetach();
    }


    private BreweriesFragment() {
    }

    public static BreweriesFragment newInstance() {
        BreweriesFragment fragment = new BreweriesFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_breweries, container, false);// Inflate the layout for this fragment

        //working with network
        networkHelper = NetworkHelper.getInstance(getActivity());

        //Initialising UI elements
        breweryDownloadProgressBar = view.findViewById(R.id.brewery_download_progressbar);
        rvBreweries = view.findViewById(R.id.rv_breweries);
        textViewBreweriesError = view.findViewById(R.id.textview_brewery_search_error);
        breweryScrollProgressBar = view.findViewById(R.id.brewery_scroll_progressbar);
        buttonBreweriesGetByLocation = view.findViewById(R.id.button_breweries_get_by_location);
        buttonBreweriesGetByMap = view.findViewById(R.id.button_breweries_get_by_map);

        getLocation();
        radius = CONST_RADIUS;


        //To work with infinite search
        isLoading = false;
        breweriesPage = 1;//number of pages is 1
        pagesEndReached = false;


        buttonBreweriesGetByLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
                firstPageBreweriesLoad();
                Toast.makeText(getContext(), getContext().getString(R.string.geo_snippet, latitude, longitude), Toast.LENGTH_SHORT).show();
            }
        });

        buttonBreweriesGetByMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapListener.onUseMap();
            }
        });


        layoutManager = new LinearLayoutManager(getActivity());

        //Initialize itemTouchHelper that helps us to remove beers from list of items
        itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        removeBrewery(position);
                    }
                });

        //Initialize onScrollListener that helps with infinite scrolls
        onScrollListener = new RecyclerView.OnScrollListener() {
            int visibleItemCount, totalItemCount, pastVisiblesItems;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading) {
                    if (dy > 0) {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if (!pagesEndReached && networkHelper.haveNetworkConnection(getActivity())) {
                                isLoading = true;
                                nextPageBreweriesLoad();
                            }
                        }
                    }
                }
            }
        };

        //load first page of breweries
        firstPageBreweriesLoad();

        return view;
    }


    //load first page of breweries
    private void firstPageBreweriesLoad() {

        //setting invisible error textview and recyclerview
        textViewBreweriesError.setVisibility(View.INVISIBLE);
        rvBreweries.setVisibility(View.INVISIBLE);

        //working with pagination
        breweriesPage = 1;
        pagesEndReached = false;

        //At first check network connection
        if (!networkHelper.haveNetworkConnection(getActivity())) {
            //if we haven't connection then we show error text
            textViewBreweriesError.setVisibility(TextView.VISIBLE);
            textViewBreweriesError.setText(R.string.error_turn_on_internet);
        } else {

            //Making download process visible to user
            breweryDownloadProgressBar.setVisibility(ProgressBar.VISIBLE);

            //initialize call
            call = networkHelper.getBreweriesByLocation(latitude, longitude, radius, breweriesPage);

            //asynchronous call from BreweryDB
            call.enqueue(new Callback<BreweryDBResponse<BreweryLocation>>() {
                @Override
                public void onResponse(Call<BreweryDBResponse<BreweryLocation>> call, Response<BreweryDBResponse<BreweryLocation>> response) {
                    BreweryDBResponse<BreweryLocation> breweryDBResponse = response.body();

                    List<BreweryLocation> breweryLocations = null;
                    //If Response is not null making a result list of breweries
                    if (breweryDBResponse != null) {
                        totalPages = breweryDBResponse.getNumberOfPages();
                        breweryLocations = breweryDBResponse.getData();

                    }
                    //If photos not null show them
                    if (breweryLocations != null && !breweryLocations.isEmpty()) {
                        showBreweries(breweryLocations);
                        breweriesPage++;
                        if (breweriesPage > totalPages) {
                            pagesEndReached = true;
                        }
                    } else {
                        textViewBreweriesError.setVisibility(View.VISIBLE);
                        textViewBreweriesError.setText(R.string.breweries_search_found_nothing);
                    }

                    //disabling download bar
                    breweryDownloadProgressBar.setVisibility(View.INVISIBLE);

                }

                //If we fail then set an error string to textview
                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(Call<BreweryDBResponse<BreweryLocation>> call, Throwable t) {
                    Log.e(TAG, "onFailure: Error");
                    Log.e(TAG, t.toString());
                    breweryDownloadProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    textViewBreweriesError.setVisibility(TextView.VISIBLE);
                    textViewBreweriesError.setText(R.string.error_request);
                }
            });
        }
    }

    //load more pages of breweries
    private void nextPageBreweriesLoad() {

        //setting visibility of beer scroll progress bar
        breweryScrollProgressBar.setVisibility(View.VISIBLE);

        //initialize call for next page
        call = networkHelper.getBreweriesByLocation(CONST_LAT, CONST_LNG, CONST_RADIUS, breweriesPage);

        //asynchronous BreweryDB call
        call.enqueue(new Callback<BreweryDBResponse<BreweryLocation>>() {
            @Override
            public void onResponse(Call<BreweryDBResponse<BreweryLocation>> call, Response<BreweryDBResponse<BreweryLocation>> response) {
                BreweryDBResponse<BreweryLocation> breweryDBResponse = response.body();

                List<BreweryLocation> breweryLocations = null;

                //If Response is not null making a result list of beers
                if (breweryDBResponse != null) {
                    breweryLocations = breweryDBResponse.getData();
                }

                //If beers not null show them
                if (breweryLocations != null && !breweryLocations.isEmpty()) {
                    breweriesAdapter.addNewBreweries(breweryLocations);
                    breweriesPage++;
                    if (breweriesPage > totalPages) {
                        pagesEndReached = true;
                    }
                } else {
                    pagesEndReached = true;
                }

                //disabling download bar
                breweryScrollProgressBar.setVisibility(View.INVISIBLE);
                isLoading = false;
            }

            //If we fail then set an error string to textview
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<BreweryDBResponse<BreweryLocation>> call, Throwable t) {
                Log.e(TAG, "onFailure: Error");
                Log.e(TAG, t.toString());
                breweryScrollProgressBar.setVisibility(ProgressBar.INVISIBLE);
                isLoading = false;
            }
        });
    }

    //Use to show list of breweries in our view
    private void showBreweries(final List<BreweryLocation> breweryLocations) {

        breweriesAdapter = new BreweriesAdapter(breweryLocations, getContext(), new BreweriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BreweryLocation breweryLocation) {
                //get to BreweryInfoFragment
                listener.onBrewerySelected(breweryLocation);
            }

        });
        // Attach the adapter to the recyclerview to populate items
        rvBreweries.setAdapter(breweriesAdapter);

        // Set layout manager to position the items
        rvBreweries.setLayoutManager(layoutManager);

        // Add the functionality to swipe breweries
        itemTouchHelper.attachToRecyclerView(rvBreweries);

        //Add infinity scroll functionality to RecyclerView
        rvBreweries.addOnScrollListener(onScrollListener);

        //show result
        rvBreweries.setVisibility(View.VISIBLE);
    }

    //This method is used with swipe to remove brewery list item from view
    private void removeBrewery(int position) {
        breweriesAdapter.removeBrewery(position);
        breweriesAdapter.notifyItemRemoved(position);
    }

    /*This method is used to get our location
     * If we can't get it then we have a default one
     * */
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Get last known recent location using Google Play Services SDK
            FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getActivity());
            locationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            } else {
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

    //on request location permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    setDefaultLocation(R.string.geo_location_permission_not_granted);
                }
            }
        }
    }

    public void setDefaultLocation(int stringResId) {
        latitude = CONST_LAT;
        longitude = CONST_LNG;
        Toast.makeText(getActivity(), stringResId, Toast.LENGTH_LONG).show();
    }

    public void useMapCoordinates(double lat, double lng) {
        //load list of breweries with map coordinates
        latitude = lat;
        longitude = lng;
        firstPageBreweriesLoad();
        Toast.makeText(getContext(), getContext().getString(R.string.geo_snippet, latitude, longitude), Toast.LENGTH_SHORT).show();
    }

}
