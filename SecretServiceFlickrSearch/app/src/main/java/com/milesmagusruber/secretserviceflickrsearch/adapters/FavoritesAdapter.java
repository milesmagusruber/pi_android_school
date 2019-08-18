package com.milesmagusruber.secretserviceflickrsearch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.model.Favorite;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemFavoriteSearchRequest;
        public TextView itemFavoriteWeblink;


        public ViewHolder(View itemView) {
            super(itemView);

            itemFavoriteSearchRequest = (TextView) itemView.findViewById(R.id.item_favorite_search_request);
            itemFavoriteWeblink = (TextView) itemView.findViewById(R.id.item_favorite_weblink);
        }

        public void bind(final Favorite favorite, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(favorite);
                }
            });
        }
    }

    // Store a member variable for the favorites
    private List<Favorite> favorites;
    private final OnItemClickListener listener;
    private Context context;
    // Pass in the favorites array into the constructor
    public FavoritesAdapter(List<Favorite> favorites,Context context,OnItemClickListener listener) {
        this.favorites = favorites;
        this.context=context;
        this.listener=listener;
    }
    public interface OnItemClickListener {
        void onItemClick(Favorite favorite);
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View favoriteView = inflater.inflate(R.layout.item_favorite, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(favoriteView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(FavoritesAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Favorite favorite = favorites.get(position);
        // Set item views based on your views and data model
        viewHolder.itemFavoriteSearchRequest.setText(context.getString(R.string.favorites_adapter_search_request,favorite.getSearchRequest()));
        viewHolder.itemFavoriteWeblink.setText(context.getString(R.string.favorites_adapter_weblink,favorite.getWebLink()));
        viewHolder.bind(favorite, listener);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return favorites.size();
    }
}
