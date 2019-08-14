package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.FavoritesAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.Favorite;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    private ArrayList<Favorite> favorites;
    private RecyclerView rvFavorites;
    private EditText editTextFavoritesFilter;
    private Button buttonFavoritesFilter;
    private DatabaseHelper db;
    private FavoritesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rvFavorites = (RecyclerView) findViewById(R.id.rv_favorites);
        editTextFavoritesFilter = (EditText) findViewById(R.id.edittext_favorites_filter);
        buttonFavoritesFilter = (Button) findViewById(R.id.button_favorites_filter);
        // Initialize Favorites from database data
        showFavorites(null);

        buttonFavoritesFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterText=editTextFavoritesFilter.getText().toString();
                if (filterText.equals("")){
                    showFavorites(null);
                }else{
                    showFavorites(filterText);
                }
            }
        });

    }

    //Getting favorites from db if we have filter or not
    private void showFavorites(final String searchRequest){


        new AsyncTask<Void,Void,Integer>(){
            @Override
            protected Integer doInBackground(Void... data) {
                db = DatabaseHelper.getInstance(FavoritesActivity.this);
                favorites=db.getAllFavorites(1,searchRequest);
                db.close();
                return 0;
            }

            @Override
            protected void onPostExecute(Integer a){

                // Create adapter passing in the sample user data
                adapter = new FavoritesAdapter(favorites,FavoritesActivity.this);
                // Attach the adapter to the recyclerview to populate items
                rvFavorites.setAdapter(adapter);
                // Set layout manager to position the items
                rvFavorites.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));
            }
        }.execute();

    }
}
