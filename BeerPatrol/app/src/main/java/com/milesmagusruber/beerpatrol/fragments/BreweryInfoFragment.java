package com.milesmagusruber.beerpatrol.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.milesmagusruber.beerpatrol.BuildConfig;
import com.milesmagusruber.beerpatrol.R;
import com.milesmagusruber.beerpatrol.adapters.BeersAdapter;
import com.milesmagusruber.beerpatrol.listeners.OnBeerSelectedListener;
import com.milesmagusruber.beerpatrol.network.NetworkHelper;
import com.milesmagusruber.beerpatrol.network.model.Beer;
import com.milesmagusruber.beerpatrol.network.model.BreweryDBResponse;
import com.milesmagusruber.beerpatrol.network.model.BreweryLocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class BreweryInfoFragment extends Fragment {

    private final static String EXTRA_BREWERY_INFO_NAME = BuildConfig.APPLICATION_ID + ".breweryinfo.name";
    private final static String EXTRA_BREWERY_INFO_BREWERYID = BuildConfig.APPLICATION_ID + ".breweryinfo.breweryid";
    private final static String EXTRA_BREWERY_INFO_WEBSITE = BuildConfig.APPLICATION_ID + ".breweryinfo.website";
    private final static String EXTRA_BREWERY_INFO_ESTABLISHED = BuildConfig.APPLICATION_ID + ".breweryinfo.established";
    private final static String EXTRA_BREWERY_INFO_DESCRIPTION = BuildConfig.APPLICATION_ID + ".breweryinfo.description";
    private final static String EXTRA_BREWERY_INFO_ICON = BuildConfig.APPLICATION_ID + ".breweryinfo.icon";
    private final static String EXTRA_BREWERY_INFO_IMAGE = BuildConfig.APPLICATION_ID + ".breweryinfo.image";

    //Brewery parameters
    private String breweryId = null;
    private String name = null;
    private String description = null;
    private String website = null;
    private String established = null;
    private String icon = null;
    private String image = null;

    //UI elements
    private ImageView breweryInfoImage;
    private MaterialTextView breweryInfoDescription;
    private MaterialTextView breweryInfoWebsite;
    private MaterialTextView breweryInfoEstablished;
    private RecyclerView rvBreweryBeers;
    private MaterialTextView breweryInfoBeersError;
    private Toolbar toolbar;

    //adapter for BreweryDB beers
    private BeersAdapter beersAdapter;
    private LinearLayoutManager layoutManager;

    public static final String TAG = "BreweryInfoFragment";

    //Working with Network
    private NetworkHelper networkHelper;

    //Controlling Retrofit calls in this activity
    private Call<BreweryDBResponse<Beer>> call;

    //Listener to transfer from BreweryInfoFragment to BeerInfoFragment
    private OnBeerSelectedListener listener;

    //listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBeerSelectedListener) {
            listener = (OnBeerSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement methods of OnBeerSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    private BreweryInfoFragment() {
    }

    public static BreweryInfoFragment newInstance(BreweryLocation breweryLocation) {
        BreweryInfoFragment fragment = new BreweryInfoFragment();
        Bundle bundle = new Bundle();

        //brewery name
        if (breweryLocation.name != null) {
            bundle.putString(EXTRA_BREWERY_INFO_NAME, breweryLocation.name);
        }

        if (breweryLocation.brewery != null) {
            //brewery breweryId
            bundle.putString(EXTRA_BREWERY_INFO_BREWERYID, breweryLocation.brewery.id);

            //brewery description
            if (breweryLocation.brewery.description != null) {
                bundle.putString(EXTRA_BREWERY_INFO_DESCRIPTION, breweryLocation.brewery.description);
            }

            //brewery website
            if (breweryLocation.brewery.website != null) {
                bundle.putString(EXTRA_BREWERY_INFO_WEBSITE, breweryLocation.brewery.website);
            }

            //brewery established
            if (breweryLocation.brewery.established != null) {
                bundle.putString(EXTRA_BREWERY_INFO_ESTABLISHED, breweryLocation.brewery.established);
            }

            if (breweryLocation.brewery.images != null) {
                //brewery icon
                if (breweryLocation.brewery.images.icon != null) {
                    bundle.putString(EXTRA_BREWERY_INFO_ICON, breweryLocation.brewery.images.icon);
                }

                //brewery image
                if (breweryLocation.brewery.images.large != null) {
                    bundle.putString(EXTRA_BREWERY_INFO_IMAGE, breweryLocation.brewery.images.large);
                }
            }
        }


        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brewery_info, container, false);

        if (getArguments() != null) {
            //getting all brewery parameters
            name = getArguments().getString(EXTRA_BREWERY_INFO_NAME);
            breweryId = getArguments().getString(EXTRA_BREWERY_INFO_BREWERYID);
            website = getArguments().getString(EXTRA_BREWERY_INFO_WEBSITE);
            established = getArguments().getString(EXTRA_BREWERY_INFO_ESTABLISHED);
            description = getArguments().getString(EXTRA_BREWERY_INFO_DESCRIPTION);
            icon = getArguments().getString(EXTRA_BREWERY_INFO_ICON);
            image = getArguments().getString(EXTRA_BREWERY_INFO_IMAGE);
        }

        //setting all UI elements
        breweryInfoImage = view.findViewById(R.id.brewery_info_image);
        breweryInfoDescription = view.findViewById(R.id.brewery_info_description);
        breweryInfoWebsite = view.findViewById(R.id.brewery_info_website);
        breweryInfoEstablished = view.findViewById(R.id.brewery_info_established);
        rvBreweryBeers = view.findViewById(R.id.rv_brewery_beers);
        breweryInfoBeersError = view.findViewById(R.id.brewery_info_beers_error);
        toolbar = view.findViewById(R.id.toolbar);


        //layout manager for beer recyclerview
        layoutManager = new LinearLayoutManager(getActivity());
        //working with network
        networkHelper = NetworkHelper.getInstance(getActivity());

        setToolbar();

        setBreweryInformation();

        return view;
    }

    //setting all brewery information
    private void setBreweryInformation() {

        //setting website
        if (website != null) {
            breweryInfoWebsite.setAutoLinkMask(Linkify.WEB_URLS);
            breweryInfoWebsite.setText(website);
        } else {
            breweryInfoWebsite.setText(getResources().getString(R.string.brewery_info_no_website));
        }

        //setting established
        if (established != null) {
            breweryInfoEstablished.setText(getResources().getString(R.string.brewery_info_established, established));
        } else {
            breweryInfoEstablished.setText(getResources().getString(R.string.brewery_info_no_established));
        }

        //setting description
        if (description != null) {
            breweryInfoDescription.setText(description);
        } else {
            breweryInfoDescription.setText(getResources().getString(R.string.brewery_info_no_description));
        }

        //setting image for brewery
        if (image != null) {
            Glide.with(getActivity()).load(image).into(breweryInfoImage);
        } else {
            Glide.with(getActivity()).load(R.drawable.brewery_placeholder).into(breweryInfoImage);
        }

        loadBreweryBeers();
    }

    //load beers for current brewery
    private void loadBreweryBeers() {

        //setting invisible error textview and recyclerview
        breweryInfoBeersError.setVisibility(View.INVISIBLE);
        rvBreweryBeers.setVisibility(View.INVISIBLE);

        //At first check network connection
        if (!networkHelper.haveNetworkConnection(getActivity())) {
            //if we haven't connection then we show error text
            breweryInfoBeersError.setVisibility(TextView.VISIBLE);
            breweryInfoBeersError.setText(R.string.error_turn_on_internet);
        } else {

            //initialize call
            call = networkHelper.getBeersByBreweryId(breweryId);

            //asynchronous call from BreweryDB
            call.enqueue(new Callback<BreweryDBResponse<Beer>>() {
                @Override
                public void onResponse(Call<BreweryDBResponse<Beer>> call, Response<BreweryDBResponse<Beer>> response) {
                    BreweryDBResponse<Beer> breweryDBResponse = response.body();

                    List<Beer> beers = null;
                    //If Response is not null making a result list of beers
                    if (breweryDBResponse != null) {
                        beers = breweryDBResponse.getData();
                    }
                    //If beers not null show them
                    if (beers != null && !beers.isEmpty()) {
                        showBreweryBeers(beers);
                    } else {
                        breweryInfoBeersError.setVisibility(View.VISIBLE);
                        breweryInfoBeersError.setText(R.string.beer_search_found_nothing);
                    }

                }

                //If we fail then set an error string to textview
                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(Call<BreweryDBResponse<Beer>> call, Throwable t) {
                    Log.e(TAG, "onFailure: Error");
                    Log.e(TAG, t.toString());
                    breweryInfoBeersError.setVisibility(TextView.VISIBLE);
                    breweryInfoBeersError.setText(R.string.error_request);
                }
            });
        }
    }

    //Use to show list of beers in current brewery in our view
    private void showBreweryBeers(final List<Beer> beers) {
        beersAdapter = new BeersAdapter(beers, new BeersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Beer beer) {
                //get to BeerInfoFragment
                listener.onBeerSelected(beer);
            }

        });
        // Attach the adapter to the recyclerview to populate items
        rvBreweryBeers.setAdapter(beersAdapter);

        // Set layout manager to position the items
        rvBreweryBeers.setLayoutManager(layoutManager);

        //show result
        rvBreweryBeers.setVisibility(View.VISIBLE);
    }

    private void setToolbar() {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(name);
            toolbar.setTitleTextColor(getContext().getResources().getColor(R.color.colorElement));
            setHasOptionsMenu(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
