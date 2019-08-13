package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.FavoritesAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.model.Favorite;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    ArrayList<Favorite> favorites;
    RecyclerView rvFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rvFavorites = (RecyclerView) findViewById(R.id.rv_favorites);
        // Initialize Favorites
        favorites = new ArrayList<Favorite>();
        favorites.add(new Favorite(1,1,"cat","Cat Garfield","garfield.com"));
        favorites.add(new Favorite(2,2,"dog","PES TV Series","pes.tv"));
        favorites.add(new Favorite(3,1,"mouse","Mickey Mouse","disney.com"));
        favorites.add(new Favorite(4,2,"rat","Rattatui","rattatui.pixar.com"));

        ;
        // Create adapter passing in the sample user data
        FavoritesAdapter adapter = new FavoritesAdapter(favorites,this);
        // Attach the adapter to the recyclerview to populate items
        rvFavorites.setAdapter(adapter);
        // Set layout manager to position the items
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
    }
}
