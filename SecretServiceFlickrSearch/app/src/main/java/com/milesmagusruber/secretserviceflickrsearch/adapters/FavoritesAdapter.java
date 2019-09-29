package com.milesmagusruber.secretserviceflickrsearch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.Favorite;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    public static final int FAVORITE_BACKGROUND_COLOR=0xFFFFFFFF;
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemFavoriteSearchRequest;
        public ImageView itemFavoriteImage;
        public TextView itemFavoriteTitle;
        public MaterialButton itemFavoriteButtonRemove;
        public TextView itemFavoriteHeaderSearch;


        public ViewHolder(View itemView) {
            super(itemView);

            itemFavoriteSearchRequest = (TextView) itemView.findViewById(R.id.item_card_favorite_search_request);
            itemFavoriteTitle = (TextView) itemView.findViewById(R.id.item_card_favorite_title);
            itemFavoriteImage = (ImageView) itemView.findViewById(R.id.item_card_favorite_image);
            itemFavoriteButtonRemove = itemView.findViewById(R.id.item_card_favorite_button_remove);
            itemFavoriteHeaderSearch = (TextView) itemView.findViewById(R.id.item_card_favorite_header_search);

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
        // Get the data entities based on position
        Favorite favorite = favorites.get(position);
        if(!favorite.getWebLink().equals("")) {
            viewHolder.itemFavoriteHeaderSearch.setVisibility(View.GONE);
            viewHolder.itemFavoriteButtonRemove.setVisibility(View.VISIBLE);
            viewHolder.itemFavoriteTitle.setVisibility(View.VISIBLE);
            viewHolder.itemFavoriteSearchRequest.setVisibility(View.VISIBLE);
            viewHolder.itemFavoriteImage.setVisibility(View.VISIBLE);
            // Set item views based on your views and data entities
            viewHolder.itemFavoriteSearchRequest.setText(favorite.getSearchRequest());
            viewHolder.itemFavoriteTitle.setText(favorite.getTitle());

            Glide.with(viewHolder.itemView.getContext()).
                    load(favorite.getWebLink()).
                    into(viewHolder.itemFavoriteImage);
            final ViewHolder holder = viewHolder;
            holder.itemFavoriteButtonRemove.setOnClickListener(new View.OnClickListener() {

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
            viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorSurface));
        }else{

            viewHolder.itemFavoriteHeaderSearch.setVisibility(View.VISIBLE);
            viewHolder.itemFavoriteHeaderSearch.setText(favorite.getSearchRequest());
            viewHolder.itemFavoriteButtonRemove.setVisibility(View.GONE);
            viewHolder.itemFavoriteTitle.setVisibility(View.GONE);
            viewHolder.itemFavoriteSearchRequest.setVisibility(View.GONE);
            viewHolder.itemFavoriteImage.setVisibility(View.GONE);
            viewHolder.bind(favorite, new OnItemClickListener() {
                @Override
                public void onItemClick(Favorite favorite) {

                }
            });
            viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return favorites.size();
    }

    //to get a favorite at a given position
    public Favorite getFavoriteAtPosition (int position) {
        Favorite fav=null;
        try {
            fav = favorites.get(position);
            return fav;
        }catch (Exception e){

        }
        return null;
    }

    //this method is used to remove favorite
    public void removeFavorite(int position) {
        favorites.remove(position);
        notifyItemRemoved(position);
    }

}
