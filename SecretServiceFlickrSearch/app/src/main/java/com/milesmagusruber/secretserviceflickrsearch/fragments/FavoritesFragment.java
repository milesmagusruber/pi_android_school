package com.milesmagusruber.secretserviceflickrsearch.fragments;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.FavoritesAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.SSFSDatabase;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.Favorite;
import com.milesmagusruber.secretserviceflickrsearch.listeners.OnPhotoSelectedListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


//importing rx libraries
import com.jakewharton.rxbinding3.widget.RxTextView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;


public class FavoritesFragment extends Fragment {

    //Current user
    private CurrentUser currentUser;

    //Favorite that is deleted
    private Favorite favoriteForDelete;

    //Controlling asynctasks
    private AsyncTask<Void, Void, Integer> asyncTask;

    private ArrayList<Favorite> favorites;
    private RecyclerView rvFavorites;
    private TextInputEditText editTextFavoritesFilter;
    private SSFSDatabase db;
    private FavoritesAdapter adapter;
    private ItemTouchHelper helper;


    //constructor
    public FavoritesFragment() {
    }

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    private OnPhotoSelectedListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoSelectedListener) {
            listener = (OnPhotoSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement methods of OnPhotoSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().setTitle(R.string.title_favorites);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        currentUser = CurrentUser.getInstance();
        rvFavorites = view.findViewById(R.id.rv_favorites);

        editTextFavoritesFilter = view.findViewById(R.id.edittext_favorites_filter);

        //Using rxJava to react on text changes in edittext

        //Our Observable EditText field for editTextFavoritesFilter
        Observable<String> rxEditTextFavoritesFilterObservable = RxTextView.textChanges(editTextFavoritesFilter)
                .debounce(500, TimeUnit.MILLISECONDS).skip(1)
                .observeOn(AndroidSchedulers.mainThread()).map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(CharSequence charSequence) throws Exception {
                        return charSequence.toString();
                    }
                });

        //Subscribing an Observer that will process input of characters
        rxEditTextFavoritesFilterObservable.subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String filterSearchRequest) {
                showFavorites(filterSearchRequest);
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
                        favoriteForDelete = adapter.getFavoriteAtPosition(position);
                        if ((favoriteForDelete != null) && ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING))) {
                            asyncTask = new AsyncTask<Void, Void, Integer>() {
                                private String deleteSearchRequest;

                                @Override
                                protected void onPreExecute() {
                                    deleteSearchRequest = favoriteForDelete.getSearchRequest();
                                }

                                @Override
                                protected Integer doInBackground(Void... voids) {
                                    db = db.getInstance(getActivity());
                                    if (!favoriteForDelete.getWebLink().equals("")) {
                                        db.favoriteDao().delete(favoriteForDelete);
                                    } else {
                                        db.favoriteDao().deleteAllBySearchRequestForUser(favoriteForDelete.getUser(), deleteSearchRequest);
                                    }
                                    return 0;
                                }

                                @Override
                                protected void onPostExecute(Integer a) {
                                    if (!favoriteForDelete.getWebLink().equals("")) {
                                        adapter.removeFavorite(position);
                                        Favorite prevFav = adapter.getFavoriteAtPosition(position - 1);
                                        Favorite nextFav = adapter.getFavoriteAtPosition(position);
                                        if (prevFav.getWebLink().equals("") && (
                                                nextFav == null || nextFav.getWebLink().equals("")
                                        )) {
                                            adapter.removeFavorite(position - 1);
                                        }
                                    } else {
                                        //delete all favorites with header search after swipe of header
                                        int currentPosition = position;
                                        boolean isNotEnd = true;
                                        adapter.removeFavorite(currentPosition);
                                        Favorite fav = null;
                                        do {
                                            fav = adapter.getFavoriteAtPosition(currentPosition);
                                            isNotEnd = (fav != null) && (fav.getSearchRequest().equals(deleteSearchRequest));
                                            if (isNotEnd) {
                                                adapter.removeFavorite(currentPosition);
                                            }
                                        } while (isNotEnd);
                                    }
                                }
                            };
                            asyncTask.execute();
                        }
                    }
                });

        // Initialize Favorites from database data
        showFavorites(null);

        return view;
    }

    //Getting favorites from db if we have filter or not
    private void showFavorites(final String filterSearchRequest) {

        if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            asyncTask = new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... data) {
                    db = db.getInstance(getActivity());
                    if (filterSearchRequest == null || filterSearchRequest.equals("")) {
                        favorites = new ArrayList<>(db.favoriteDao().getAllForUser(currentUser.getUser().getId()));
                        if (favorites != null && favorites.size() > 0)
                            saturateFavoritesWithSearchRequestsObjects();
                    } else {
                        favorites = new ArrayList<>(db.favoriteDao().getAllFilteredBySearchRequestForUser(currentUser.getUser().getId(), "%" + filterSearchRequest.trim() + "%"));
                        if (favorites != null && favorites.size() > 0)
                            saturateFavoritesWithSearchRequestsObjects();
                    }
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {
                    // Create adapter passing in the sample user data
                    adapter = new FavoritesAdapter(favorites, getActivity(), new FavoritesAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Favorite favorite) {
                            //get to Favorite Flick Photo from FavoritesFragment
                            listener.onFlickrPhotoSelected(favorite.getSearchRequest(), favorite.getWebLink(), favorite.getTitle());
                        }

                    }, new FavoritesAdapter.OnRemoveFavoriteClickListener() {
                        @Override
                        public void onClick(final int position) {
                            favoriteForDelete = adapter.getFavoriteAtPosition(position);
                            if ((favoriteForDelete != null) && ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING))) {
                                asyncTask = new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... voids) {
                                        db = db.getInstance(getActivity());
                                        db.favoriteDao().delete(favoriteForDelete);
                                        return 0;
                                    }

                                    @Override
                                    protected void onPostExecute(Integer a) {
                                        adapter.removeFavorite(position);
                                        Favorite prevFav = adapter.getFavoriteAtPosition(position - 1);
                                        Favorite nextFav = adapter.getFavoriteAtPosition(position);
                                        if (prevFav.getWebLink().equals("") && (
                                                nextFav == null || nextFav.getWebLink().equals("")
                                        )) {
                                            adapter.removeFavorite(position - 1);
                                        }
                                    }
                                };
                                asyncTask.execute();
                            }
                        }
                    });
                    // Attach the adapter to the recyclerview to populate items
                    rvFavorites.setAdapter(adapter);
                    // Set layout manager to position the items
                    rvFavorites.setLayoutManager(new LinearLayoutManager(getActivity()));

                    helper.attachToRecyclerView(rvFavorites);

                }
            };
            asyncTask.execute();
        }

    }

    private void saturateFavoritesWithSearchRequestsObjects() {
        String cunningSearchString = favorites.get(0).getSearchRequest(); //for FavoritesAdapter to have different cards
        String searchReq;
        int userId = currentUser.getUser().getId();
        favorites.add(0, new Favorite(userId, cunningSearchString, "", ""));
        int index = 1;
        int sizeOfFavorites = favorites.size();
        while (index < sizeOfFavorites) {
            searchReq = favorites.get(index).getSearchRequest();
            if (!searchReq.equals(cunningSearchString)) {
                cunningSearchString = searchReq;
                favorites.add(index, new Favorite(userId, cunningSearchString, "", ""));
                index++;
                sizeOfFavorites++;
            }
            index++;
        }
    }


}
