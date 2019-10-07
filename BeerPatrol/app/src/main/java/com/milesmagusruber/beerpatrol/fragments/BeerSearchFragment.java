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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.milesmagusruber.beerpatrol.R;
import com.milesmagusruber.beerpatrol.adapters.BeersAdapter;
import com.milesmagusruber.beerpatrol.listeners.OnBeerSelectedListener;
import com.milesmagusruber.beerpatrol.network.NetworkHelper;
import com.milesmagusruber.beerpatrol.network.model.Beer;
import com.milesmagusruber.beerpatrol.network.model.BreweryDBResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class BeerSearchFragment extends Fragment {

    //Declaring UI elements
    private ProgressBar beerDownloadProgressBar;
    private ProgressBar beerScrollProgressBar;
    private TextView textViewBeerSearchError;
    private RecyclerView rvBeers;
    private Toolbar toolbar;
    private MaterialSearchView materialSearchView;

    //search request
    private String searchQuery;

    //adapter for BreweryDB beers
    private BeersAdapter beersAdapter;
    private int beersPage; //controlling infinite search
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
    private Call<BreweryDBResponse<Beer>> call;

    //Listener to transfer from BeerSearchFragment to BeerInfoFragment
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

    private BeerSearchFragment() {
    }

    public static BeerSearchFragment newInstance() {
        BeerSearchFragment fragment = new BeerSearchFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beer_search, container, false);

        //working with network
        networkHelper = NetworkHelper.getInstance(getActivity());

        //Initialising UI elements
        toolbar = view.findViewById(R.id.toolbar);
        materialSearchView = view.findViewById(R.id.beer_search_view);
        beerDownloadProgressBar = view.findViewById(R.id.beer_download_progressbar);
        rvBeers = view.findViewById(R.id.rv_beers);
        textViewBeerSearchError = view.findViewById(R.id.textview_beer_search_error);
        beerScrollProgressBar = view.findViewById(R.id.beer_scroll_progressbar);

        //To work with infinite search
        isLoading = false;
        beersPage = 1;//number of pages is 1
        totalPages = 0;
        pagesEndReached = false;

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
                        removeBeer(position);
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
                                nextPageBeerSearchLoad();
                            }
                        }
                    }
                }
            }
        };

        setToolbar();

        return view;
    }

    //load first page of beer search query
    private void firstPageBeerSearchLoad() {

        //getting search query from edittext
        //searchQuery = editTextBeerSearch.getText().toString();

        //setting invisible error textview and recyclerview
        textViewBeerSearchError.setVisibility(View.INVISIBLE);
        rvBeers.setVisibility(View.INVISIBLE);

        //working with pagination
        beersPage = 1;
        pagesEndReached = false;

        //At first check network connection
        if (!networkHelper.haveNetworkConnection(getActivity())) {
            //if we haven't connection then we show error text
            textViewBeerSearchError.setVisibility(TextView.VISIBLE);
            textViewBeerSearchError.setText(R.string.error_turn_on_internet);
        } else if (searchQuery.equals("")) {
            //if user inputs nothing then we show error text
            textViewBeerSearchError.setVisibility(TextView.VISIBLE);
            textViewBeerSearchError.setText(R.string.beer_search_empty_search_query);

        } else {

            //Making download process visible to user
            beerDownloadProgressBar.setVisibility(ProgressBar.VISIBLE);

            //initialize call
            call = networkHelper.getBeersBySearchQuery(searchQuery, beersPage);

            //asynchronous call from BreweryDB
            call.enqueue(new Callback<BreweryDBResponse<Beer>>() {
                @Override
                public void onResponse(Call<BreweryDBResponse<Beer>> call, Response<BreweryDBResponse<Beer>> response) {
                    BreweryDBResponse<Beer> breweryDBResponse = response.body();
                    List<Beer> beers = null;
                    //If Response is not null making a result list of beers
                    if (breweryDBResponse != null) {
                        //get total number of result pages
                        totalPages = breweryDBResponse.getNumberOfPages();
                        beers = breweryDBResponse.getData();

                    }
                    //If beers not null show them
                    if (beers != null && !beers.isEmpty()) {
                        showBeers(beers);
                        beersPage++;
                        if (beersPage > totalPages) {
                            pagesEndReached = true;
                        }
                    } else {
                        textViewBeerSearchError.setVisibility(View.VISIBLE);
                        textViewBeerSearchError.setText(R.string.beer_search_found_nothing);
                    }

                    //disabling download bar
                    beerDownloadProgressBar.setVisibility(View.INVISIBLE);

                }

                //If we fail then set an error string to textview
                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(Call<BreweryDBResponse<Beer>> call, Throwable t) {
                    Log.e(TAG, "onFailure: Error");
                    Log.e(TAG, t.toString());
                    beerDownloadProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    textViewBeerSearchError.setVisibility(TextView.VISIBLE);
                    textViewBeerSearchError.setText(R.string.error_request);
                }
            });
        }
    }

    //load more pages of the same beer search query
    private void nextPageBeerSearchLoad() {

        //setting visibility of beer scroll progress bar
        beerScrollProgressBar.setVisibility(View.VISIBLE);

        //initialize call for next page
        call = networkHelper.getBeersBySearchQuery(searchQuery, beersPage);

        //asynchronous BreweryDB call
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
                    beersAdapter.addNewBeers(beers);
                    beersPage++;
                    if (beersPage > totalPages) pagesEndReached = true;
                } else {
                    pagesEndReached = true;
                }

                //disabling download bar
                beerScrollProgressBar.setVisibility(View.INVISIBLE);
                isLoading = false;
            }

            //If we fail then set an error string to textview
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<BreweryDBResponse<Beer>> call, Throwable t) {
                Log.e(TAG, "onFailure: Error");
                Log.e(TAG, t.toString());
                beerScrollProgressBar.setVisibility(ProgressBar.INVISIBLE);
                isLoading = false;
            }
        });
    }

    //Use to show list of beers in our view
    private void showBeers(final List<Beer> beers) {

        beersAdapter = new BeersAdapter(beers, new BeersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Beer beer) {
                //get to BeerInfoFragment
                listener.onBeerSelected(beer);
            }

        });
        // Attach the adapter to the recyclerview to populate items
        rvBeers.setAdapter(beersAdapter);

        // Set layout manager to position the items
        rvBeers.setLayoutManager(layoutManager);

        // Add the functionality to swipe beers
        itemTouchHelper.attachToRecyclerView(rvBeers);

        //Add infinity scroll functionality to RecyclerView
        rvBeers.addOnScrollListener(onScrollListener);

        //show result
        rvBeers.setVisibility(View.VISIBLE);
    }

    //This method is used with swipe to remove beer list item from view
    private void removeBeer(int position) {
        beersAdapter.removeBeer(position);
        beersAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.beer_search_menu_item, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(menuItem);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
        toolbar.setTitle(R.string.title_beer_search);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(getContext().getResources().getColor(R.color.colorElement));
        setHasOptionsMenu(true);

        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                firstPageBeerSearchLoad();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });
    }


}
