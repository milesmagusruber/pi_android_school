package com.milesmagusruber.beerpatrol.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.milesmagusruber.beerpatrol.R;
import com.milesmagusruber.beerpatrol.db.entities.FavoriteBeer;

import java.util.List;

public class FavoriteBeersAdapter extends RecyclerView.Adapter<FavoriteBeersAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemFavoriteBeerName;
        public ImageView itemFavoriteBeerImage;


        public ViewHolder(View itemView) {
            super(itemView);

            itemFavoriteBeerName = (TextView) itemView.findViewById(R.id.item_favorite_beer_name);
            itemFavoriteBeerImage = (ImageView) itemView.findViewById(R.id.item_favorite_beer_image);
        }

        public void bind(final FavoriteBeer favoriteBeer, final FavoriteBeersAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(favoriteBeer);
                }
            });
        }
    }

    private List<FavoriteBeer> favoriteBeers;
    private final FavoriteBeersAdapter.OnItemClickListener listener;

    // Pass in the favorites beers array into the constructor
    public FavoriteBeersAdapter(List<FavoriteBeer> favoriteBeers, FavoriteBeersAdapter.OnItemClickListener listener) {
        this.favoriteBeers = favoriteBeers;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(FavoriteBeer favoriteBeer);
    }

    @NonNull
    @Override
    public FavoriteBeersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View beerView = inflater.inflate(R.layout.item_card_favorite_beer, parent, false);
        // Return a new holder instance
        FavoriteBeersAdapter.ViewHolder viewHolder = new FavoriteBeersAdapter.ViewHolder(beerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteBeersAdapter.ViewHolder holder, int position) {
        // Get the data entities based on position
        FavoriteBeer favoriteBeer = favoriteBeers.get(position);
        // Set item views based on your views and data entities
        holder.itemFavoriteBeerName.setText(favoriteBeer.getName());

        if (favoriteBeer.getIcon() != null) {
            //if we have icon then set it
            Glide.with(holder.itemView.getContext()).
                    load(favoriteBeer.getIcon()).
                    into(holder.itemFavoriteBeerImage);
        } else {
            //if we haven't icon set placeholder
            Glide.with(holder.itemView.getContext()).
                    load(R.drawable.beer_placeholder).
                    into(holder.itemFavoriteBeerImage);
        }

        holder.bind(favoriteBeer, listener);
    }

    @Override
    public int getItemCount() {
        return favoriteBeers.size();
    }

    //to get a favorite beer at a given position
    public FavoriteBeer getFavoriteBeerAtPosition(int position) {
        return favoriteBeers.get(position);
    }

    //this method is used to remove favorite beer
    public void removeFavoriteBeer(int position) {
        favoriteBeers.remove(position);
    }

}
