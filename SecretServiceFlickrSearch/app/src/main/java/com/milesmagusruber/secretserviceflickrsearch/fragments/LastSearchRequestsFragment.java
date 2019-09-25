package com.milesmagusruber.secretserviceflickrsearch.fragments;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.SearchRequestsAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.SSFSDatabase;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.SearchRequest;

import java.util.ArrayList;


public class LastSearchRequestsFragment extends Fragment {

    private ArrayList<SearchRequest> searchRequests;
    private RecyclerView rvSearchRequests;
    private SSFSDatabase db;

    //Controlling AsyncTask that gets search requests from db
    private SearchRequestsTask searchRequestsTask;

    //Current user
    private CurrentUser currentUser;

    public LastSearchRequestsFragment() {
    }

    public static LastSearchRequestsFragment newInstance() {
        return new LastSearchRequestsFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().setTitle(R.string.title_last_search_requests);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_last_search_requests, container, false);
        currentUser = CurrentUser.getInstance();
        rvSearchRequests = (RecyclerView) view.findViewById(R.id.rv_search_requests);
        searchRequestsTask = new SearchRequestsTask();

        if (searchRequestsTask.getStatus() != AsyncTask.Status.RUNNING) {
            searchRequestsTask.execute();
        }
        return view;
    }

    private class SearchRequestsTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... data) {
            //Initialize SearchRequests
            db = db.getInstance(getActivity());
            searchRequests =new ArrayList<SearchRequest>(db.searchRequestDao().getLast20ForUser(currentUser.getUser().getId()));
            return 0;
        }

        @Override
        protected void onPostExecute(Integer a) {
            // Create adapter passing in the sample user data
            SearchRequestsAdapter adapter = new SearchRequestsAdapter(searchRequests);
            // Attach the adapter to the recyclerview to populate items
            rvSearchRequests.setAdapter(adapter);
            // Set layout manager to position the items
            rvSearchRequests.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }
}
