package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.SearchRequestsAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.SearchRequest;

import java.util.ArrayList;

public class LastSearchRequestsActivity extends AppCompatActivity {
    ArrayList<SearchRequest> searchRequests;
    RecyclerView rvSearchRequests;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_search_requests);
        rvSearchRequests = (RecyclerView) findViewById(R.id.rv_search_requests);
        // Initialize SearchRequests
        db = new DatabaseHelper(this);
        searchRequests=db.getAllSearchRequests(1);
        db.close();
        /*searchRequests = new ArrayList<SearchRequest>();
        searchRequests.add(new SearchRequest(1,1,"cat","today"));
        searchRequests.add(new SearchRequest(2,2,"dog","today"));
        searchRequests.add(new SearchRequest(3,1,"mouse","today"));
        searchRequests.add(new SearchRequest(4,2,"rat","today"));
        */

        // Create adapter passing in the sample user data
        SearchRequestsAdapter adapter = new SearchRequestsAdapter(searchRequests);
        // Attach the adapter to the recyclerview to populate items
        rvSearchRequests.setAdapter(adapter);
        // Set layout manager to position the items
        rvSearchRequests.setLayoutManager(new LinearLayoutManager(this));
    }
}
