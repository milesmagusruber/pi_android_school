package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;

import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.SearchRequestsAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.SearchRequest;

import java.util.ArrayList;


public class LastSearchRequestsActivity extends AppCompatActivity {
    private ArrayList<SearchRequest> searchRequests;
    private RecyclerView rvSearchRequests;
    private DatabaseHelper db;

    //Controlling AsyncTask that gets search requests from db
    private SearchRequestsTask searchRequestsTask;

    //Current user
    private CurrentUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_search_requests);
        currentUser = CurrentUser.getInstance();
        rvSearchRequests = (RecyclerView) findViewById(R.id.rv_search_requests);
        searchRequestsTask = new SearchRequestsTask();

        if (searchRequestsTask.getStatus() != AsyncTask.Status.RUNNING) {
            searchRequestsTask.execute();
        }
    }

    private class SearchRequestsTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... data) {
            //Initialize SearchRequests
            db = DatabaseHelper.getInstance(LastSearchRequestsActivity.this);
            searchRequests = db.getAllSearchRequests(currentUser.getUser().getId());
            db.close();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer a) {
            // Create adapter passing in the sample user data
            SearchRequestsAdapter adapter = new SearchRequestsAdapter(searchRequests);
            // Attach the adapter to the recyclerview to populate items
            rvSearchRequests.setAdapter(adapter);
            // Set layout manager to position the items
            rvSearchRequests.setLayoutManager(new LinearLayoutManager(LastSearchRequestsActivity.this));
        }
    }
}
