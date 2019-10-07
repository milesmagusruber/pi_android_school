package com.milesmagusruber.beerpatrol.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.milesmagusruber.beerpatrol.R;
import com.milesmagusruber.beerpatrol.adapters.FavoriteBeersAdapter;
import com.milesmagusruber.beerpatrol.db.BeerPatrolDatabase;
import com.milesmagusruber.beerpatrol.db.entities.FavoriteBeer;
import com.milesmagusruber.beerpatrol.listeners.OnBeerSelectedListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;


public class FavoriteBeersFragment extends Fragment {


    //Favorite Beer that is deleted
    private FavoriteBeer favoriteBeerForDelete;

    //Controlling asynctasks
    private AsyncTask<Void, Void, Integer> asyncTask;

    private ArrayList<FavoriteBeer> favoriteBeers;
    private RecyclerView rvFavoriteBeers;
    private ProgressBar favoriteBeerDownloadProgressBar;
    private MaterialTextView textViewFavoriteBeerSearchError;
    private TextInputEditText editTextFavoriteBeerFilter;
    private BeerPatrolDatabase db;
    private FavoriteBeersAdapter adapter;
    private ItemTouchHelper helper;

    private String filter = "";

    //Listener to transfer from FavoriteBeersFragment to BeerInfoFragment
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


    private FavoriteBeersFragment() {
    }

    public static FavoriteBeersFragment newInstance() {
        FavoriteBeersFragment fragment = new FavoriteBeersFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_beers, container, false);

        //Initialize UI elements
        rvFavoriteBeers = view.findViewById(R.id.rv_favorite_beers);
        favoriteBeerDownloadProgressBar = view.findViewById(R.id.favorite_beer_download_progressbar);
        textViewFavoriteBeerSearchError = view.findViewById(R.id.textview_favorite_beer_search_error);
        editTextFavoriteBeerFilter = view.findViewById(R.id.edittext_favorite_beer_search);

        //Using rxJava to react on text changes in edittext

        //Our Observable EditText field for editTextFavoriteBeerFilter
        Observable<String> rxEditTextFavoriteBeersFilterObservable = RxTextView.textChanges(editTextFavoriteBeerFilter)
                .debounce(500, TimeUnit.MILLISECONDS).skip(1)
                .observeOn(AndroidSchedulers.mainThread()).map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(CharSequence charSequence) throws Exception {
                        return charSequence.toString();
                    }
                });

        //Subscribing an Observer that will process input of characters
        rxEditTextFavoriteBeersFilterObservable.subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String filterName) {
                filter = filterName;
                showFavoriteBeers(filterName);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

        // Add the functionality to swipe items in the
        // recycler view to delete that item
        helper = new ItemTouchHelper(
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
                        final int position = viewHolder.getAdapterPosition();
                        favoriteBeerForDelete = adapter.getFavoriteBeerAtPosition(position);
                        if ((favoriteBeerForDelete != null) && ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING))) {
                            asyncTask = new AsyncTask<Void, Void, Integer>() {

                                @Override
                                protected Integer doInBackground(Void... voids) {
                                    db = db.getInstance(getActivity());
                                    db.favoriteBeerDao().delete(favoriteBeerForDelete);
                                    return 0;
                                }

                                @Override
                                protected void onPostExecute(Integer a) {
                                    adapter.removeFavoriteBeer(position);
                                    adapter.notifyItemRemoved(position);
                                }
                            };
                            asyncTask.execute();
                        }
                    }
                });

        // Initialize Favorites from database data
        showFavoriteBeers(null);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        editTextFavoriteBeerFilter.setText(filter);
        showFavoriteBeers(filter);
    }


    //Getting favorite beers from db if we have filter or not
    private void showFavoriteBeers(final String nameFilter) {

        if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            asyncTask = new AsyncTask<Void, Void, Integer>() {

                @Override
                protected void onPreExecute() {
                    favoriteBeerDownloadProgressBar.setVisibility(View.VISIBLE);
                    rvFavoriteBeers.setVisibility(View.INVISIBLE);
                    textViewFavoriteBeerSearchError.setVisibility(View.INVISIBLE);
                }

                @Override
                protected Integer doInBackground(Void... data) {
                    db = db.getInstance(getActivity());
                    if (nameFilter == null || nameFilter.equals("")) {
                        favoriteBeers = new ArrayList<>(db.favoriteBeerDao().getAll());

                    } else {
                        favoriteBeers = new ArrayList<>(db.favoriteBeerDao().getAllFilteredByName("%" + nameFilter.trim() + "%"));
                    }
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {


                    if (favoriteBeers != null && !favoriteBeers.isEmpty()) {
                        // Create adapter passing in the sample user data
                        adapter = new FavoriteBeersAdapter(favoriteBeers, new FavoriteBeersAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(FavoriteBeer favoriteBeer) {
                                //get to BeerInfoFragment from FavoriteBeersFragment
                                listener.onBeerSelected(favoriteBeer);
                            }

                        });
                        // Attach the adapter to the recyclerview to populate items
                        rvFavoriteBeers.setAdapter(adapter);
                        // Set layout manager to position the items
                        rvFavoriteBeers.setLayoutManager(new LinearLayoutManager(getActivity()));

                        helper.attachToRecyclerView(rvFavoriteBeers);
                        rvFavoriteBeers.setVisibility(View.VISIBLE);
                    } else {
                        textViewFavoriteBeerSearchError.setVisibility(View.VISIBLE);
                        textViewFavoriteBeerSearchError.setText(R.string.beer_search_found_nothing);
                    }


                    favoriteBeerDownloadProgressBar.setVisibility(View.INVISIBLE);

                }
            };
            asyncTask.execute();
        }

    }

}
