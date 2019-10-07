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
import com.milesmagusruber.beerpatrol.network.model.Beer;

import java.util.List;

public class BeersAdapter extends RecyclerView.Adapter<BeersAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemBeerName;
        public ImageView itemBeerImage;


        public ViewHolder(View itemView) {
            super(itemView);

            itemBeerName = (TextView) itemView.findViewById(R.id.item_beer_name);
            itemBeerImage = (ImageView) itemView.findViewById(R.id.item_beer_image);
        }

        public void bind(final Beer beer, final BeersAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(beer);
                }
            });
        }
    }

    private List<Beer> beers;
    private final BeersAdapter.OnItemClickListener listener;

    // Pass in the beers array into the constructor
    public BeersAdapter(List<Beer> beers, BeersAdapter.OnItemClickListener listener) {
        this.beers = beers;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Beer beer);
    }

    @NonNull
    @Override
    public BeersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View beerView = inflater.inflate(R.layout.item_card_beer, parent, false);
        // Return a new holder instance
        BeersAdapter.ViewHolder viewHolder = new BeersAdapter.ViewHolder(beerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BeersAdapter.ViewHolder holder, int position) {
        // Get the data entities based on position
        Beer beer = beers.get(position);
        // Set item views based on your views and data entities
        holder.itemBeerName.setText(beer.name);

        if (beer.labels != null) {
            //if we have beer icon set it
            Glide.with(holder.itemView.getContext()).
                    load(beer.labels.icon).
                    into(holder.itemBeerImage);
        } else {
            //if we haven't beer icon set placeholder
            Glide.with(holder.itemView.getContext()).
                    load(R.drawable.beer_placeholder).
                    into(holder.itemBeerImage);
        }
        //bind onclick listener
        holder.bind(beer, listener);
    }

    @Override
    public int getItemCount() {
        return beers.size();
    }

    //to get a beer at a given position
    public Beer getBeerAtPosition(int position) {
        return beers.get(position);
    }

    //this method is used to remove beer
    public void removeBeer(int position) {
        beers.remove(position);
    }

    //add new beers to list
    public void addNewBeers(List<Beer> newBeers) {
        beers.addAll(newBeers);
        notifyDataSetChanged();
    }
}
