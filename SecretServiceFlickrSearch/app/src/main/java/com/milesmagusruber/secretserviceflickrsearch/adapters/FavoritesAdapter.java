package com.milesmagusruber.secretserviceflickrsearch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.model.Favorite;
import com.milesmagusruber.secretserviceflickrsearch.model.Photo;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemFavoriteSearchRequest;
        public ImageView itemFavoriteImage;
        public TextView itemFavoriteTitle;
        public Button itemFavoriteButtonRemove;


        public ViewHolder(View itemView) {
            super(itemView);

            itemFavoriteSearchRequest = (TextView) itemView.findViewById(R.id.item_card_favorite_search_request);
            itemFavoriteTitle = (TextView) itemView.findViewById(R.id.item_card_favorite_title);
            itemFavoriteImage = (ImageView) itemView.findViewById(R.id.item_card_favorite_image);
            itemFavoriteButtonRemove = (Button) itemView.findViewById(R.id.item_card_favorite_button_remove);
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
    private final OnItemClickListener clickListener;
    private final OnRemoveFavoriteClickListener removeFavoriteListener;
    private Context context;
    // Pass in the favorites array into the constructor
    public FavoritesAdapter(List<Favorite> favorites, Context context, OnItemClickListener clickListener,
                            OnRemoveFavoriteClickListener removeFavoriteListener ) {
        this.favorites = favorites;
        this.context=context;
        this.clickListener=clickListener;
        this.removeFavoriteListener = removeFavoriteListener;
    }

    //To control item clicks
    public interface OnItemClickListener {
        void onItemClick(Favorite favorite);
    }

    //To control delete button clicks
    public interface OnRemoveFavoriteClickListener{
        void onClick(final int position);
    }


    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View favoriteView = inflater.inflate(R.layout.item_card_favorite, parent, false);
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
        viewHolder.itemFavoriteSearchRequest.setText(favorite.getSearchRequest());
        viewHolder.itemFavoriteTitle.setText(favorite.getTitle());

        Glide.with(viewHolder.itemView.getContext()).
                load(favorite.getWebLink()).
                into(viewHolder.itemFavoriteImage);
        final ViewHolder holder = viewHolder;
        holder.itemFavoriteButtonRemove.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (removeFavoriteListener != null) {
                    final int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        removeFavoriteListener.onClick(position);
                    }
                }
            }
        });

        viewHolder.bind(favorite, clickListener);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return favorites.size();
    }

    //to get a favorite at a given position
    public Favorite getFavoriteAtPosition (int position) {
        return favorites.get(position);
    }

    //this method is used to remove favorite
    public void removeFavorite(int position) {
        favorites.remove(position);
    }
}
