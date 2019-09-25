package com.milesmagusruber.secretserviceflickrsearch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.SearchRequest;

import java.util.List;

public class SearchRequestsAdapter extends RecyclerView.Adapter<SearchRequestsAdapter.ViewHolder>{
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemSearchRequest;


        public ViewHolder(View itemView) {
            super(itemView);

            itemSearchRequest = (TextView) itemView.findViewById(R.id.item_card_search_request_value);
        }
    }

    // Store a member variable for the contacts
    private List<SearchRequest> searchRequests;

    // Pass in the contact array into the constructor
    public SearchRequestsAdapter(List<SearchRequest> favorites) {
        this.searchRequests = favorites;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public SearchRequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_card_search_request, parent, false);
        // Return a new holder instance
        SearchRequestsAdapter.ViewHolder viewHolder = new SearchRequestsAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(SearchRequestsAdapter.ViewHolder viewHolder, int position) {
        // Get the data entities based on position
        SearchRequest searchRequest = searchRequests.get(position);
        // Set item views based on your views and data entities
        viewHolder.itemSearchRequest.setText(searchRequest.getSearchRequest());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return searchRequests.size();
    }
}
