package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.FavoritesAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.Favorite;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    ArrayList<Favorite> favorites;
    RecyclerView rvFavorites;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rvFavorites = (RecyclerView) findViewById(R.id.rv_favorites);
        // Initialize Favorites from database data

        db = new DatabaseHelper(this);
        favorites=db.getAllFavorites(1,null);
        db.close();

        // Create adapter passing in the sample user data
        FavoritesAdapter adapter = new FavoritesAdapter(favorites,this);
        // Attach the adapter to the recyclerview to populate items
        rvFavorites.setAdapter(adapter);
        // Set layout manager to position the items
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));

    }
}
