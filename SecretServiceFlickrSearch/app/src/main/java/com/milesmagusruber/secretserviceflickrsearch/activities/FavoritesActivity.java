package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.FavoritesAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.Favorite;

import java.util.ArrayList;

import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_SEARCH_REQUEST;
import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_WEBLINK;

public class FavoritesActivity extends AppCompatActivity {

    //Current user
    private CurrentUser currentUser;

    //Favorite that is deleted
    private Favorite favoriteForDelete;

    //Controlling asynctasks
    private AsyncTask<Void, Void, Integer> asyncTask;

    private ArrayList<Favorite> favorites;
    private RecyclerView rvFavorites;
    private EditText editTextFavoritesFilter;
    private Button buttonFavoritesFilter;
    private DatabaseHelper db;
    private FavoritesAdapter adapter;
    private ItemTouchHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        currentUser = CurrentUser.getInstance();
        rvFavorites = (RecyclerView) findViewById(R.id.rv_favorites);

        editTextFavoritesFilter = (EditText) findViewById(R.id.edittext_favorites_filter);

        buttonFavoritesFilter = (Button) findViewById(R.id.button_favorites_filter);

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
                        if((favoriteForDelete!=null) && ( (asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING))){
                            asyncTask = new AsyncTask<Void, Void, Integer>() {
                                private String deleteSearchRequest;

                                @Override
                                protected void onPreExecute(){
                                    deleteSearchRequest=favoriteForDelete.getSearchRequest();
                                }

                                @Override
                                protected Integer doInBackground(Void... voids) {
                                    db = DatabaseHelper.getInstance(FavoritesActivity.this);
                                    db.deleteFavorite(favoriteForDelete);
                                    db.close();
                                    return 0;
                                }

                                @Override
                                protected void onPostExecute(Integer a){
                                    if(!favoriteForDelete.getWebLink().equals("")) {
                                        adapter.removeFavorite(position);
                                        Favorite prevFav=adapter.getFavoriteAtPosition(position-1);
                                        Favorite nextFav=adapter.getFavoriteAtPosition(position);
                                        if (prevFav.getWebLink().equals("") && (
                                                nextFav == null || nextFav.getWebLink().equals("")
                                                )){
                                            adapter.removeFavorite(position-1);
                                        }
                                    }else{
                                        //delete all favorites with header search after swipe of header
                                        int currentPosition=position;
                                        boolean isNotEnd=true;
                                        adapter.removeFavorite(currentPosition);
                                        Favorite fav=null;
                                        do{
                                            fav = adapter.getFavoriteAtPosition(currentPosition);
                                            isNotEnd=(fav!=null) && (fav.getSearchRequest().equals(deleteSearchRequest));
                                            if(isNotEnd) {
                                                adapter.removeFavorite(currentPosition);
                                            }
                                        }while(isNotEnd);
                                    }
                                }
                            };
                            asyncTask.execute();
                        }
                    }
                });

        // Initialize Favorites from database data
        showFavorites(null);

        buttonFavoritesFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterText = editTextFavoritesFilter.getText().toString();
                if (filterText.equals("")) {
                    showFavorites(null);
                } else {
                    showFavorites(filterText);
                }
            }
        });


    }

    //Getting favorites from db if we have filter or not
    private void showFavorites(final String searchRequest) {

        if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            asyncTask = new AsyncTask<Void, Void, Integer>() {
                @Override
                protected void onPreExecute() {
                    buttonFavoritesFilter.setClickable(false);
                }

                @Override
                protected Integer doInBackground(Void... data) {
                    db = DatabaseHelper.getInstance(FavoritesActivity.this);
                    favorites = db.getAllFavorites(currentUser.getUser().getId(), searchRequest);
                    db.close();
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {
                    // Create adapter passing in the sample user data
                    adapter = new FavoritesAdapter(favorites, FavoritesActivity.this, new FavoritesAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Favorite favorite) {
                            //get to Favorite Flick Photo from FavoritesActivity
                            Intent intent = new Intent(FavoritesActivity.this, FlickrViewItemActivity.class);
                            intent.putExtra(EXTRA_WEBLINK, favorite.getWebLink());
                            intent.putExtra(EXTRA_SEARCH_REQUEST, favorite.getSearchRequest());
                            startActivity(intent);
                        }

                    }, new FavoritesAdapter.OnRemoveFavoriteClickListener() {
                        @Override
                        public void onClick(final int position) {
                            favoriteForDelete = adapter.getFavoriteAtPosition(position);
                            if((favoriteForDelete!=null) && ( (asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING))){
                                asyncTask = new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... voids) {
                                        db = DatabaseHelper.getInstance(FavoritesActivity.this);
                                        db.deleteFavorite(favoriteForDelete);
                                        db.close();
                                        return 0;
                                    }

                                    @Override
                                    protected void onPostExecute(Integer a){
                                        adapter.removeFavorite(position);
                                        Favorite prevFav=adapter.getFavoriteAtPosition(position-1);
                                        Favorite nextFav=adapter.getFavoriteAtPosition(position);
                                        if (prevFav.getWebLink().equals("") && (
                                                nextFav == null || nextFav.getWebLink().equals("")
                                        )){
                                            adapter.removeFavorite(position-1);
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
                    rvFavorites.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));

                    helper.attachToRecyclerView(rvFavorites);

                    buttonFavoritesFilter.setClickable(true);
                }
            };
            asyncTask.execute();
        }

    }




}
