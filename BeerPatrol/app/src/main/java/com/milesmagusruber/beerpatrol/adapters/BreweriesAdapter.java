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
import com.milesmagusruber.beerpatrol.network.model.BreweryLocation;

import java.util.List;

public class BreweriesAdapter extends RecyclerView.Adapter<BreweriesAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemBreweryName;
        public TextView itemBreweryDistance;
        public ImageView itemBreweryImage;


        public ViewHolder(View itemView) {
            super(itemView);

            itemBreweryName = (TextView) itemView.findViewById(R.id.item_brewery_name);
            itemBreweryDistance = (TextView) itemView.findViewById(R.id.item_brewery_distance);
            itemBreweryImage = (ImageView) itemView.findViewById(R.id.item_brewery_image);
        }

        public void bind(final BreweryLocation breweryLocation, final BreweriesAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(breweryLocation);
                }
            });
        }
    }

    private List<BreweryLocation> breweries;
    private final BreweriesAdapter.OnItemClickListener listener;
    private Context context;

    // Pass in the breweries array into the constructor
    public BreweriesAdapter(List<BreweryLocation> breweries, Context context, BreweriesAdapter.OnItemClickListener listener) {
        this.breweries = breweries;
        this.listener = listener;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(BreweryLocation breweryLocation);
    }

    @NonNull
    @Override
    public BreweriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View breweryView = inflater.inflate(R.layout.item_card_brewery, parent, false);
        // Return a new holder instance
        BreweriesAdapter.ViewHolder viewHolder = new BreweriesAdapter.ViewHolder(breweryView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BreweriesAdapter.ViewHolder holder, int position) {
        // Get the data entities based on position
        BreweryLocation breweryLocation = breweries.get(position);
        // Set item views based on your views and data entities
        holder.itemBreweryName.setText(breweryLocation.name);
        holder.itemBreweryDistance.setText(context.getString(R.string.brewery_item_distance, breweryLocation.distance));

        if (breweryLocation.brewery.images != null) {
            //if we have brewery icon then set it
            Glide.with(holder.itemView.getContext()).
                    load(breweryLocation.brewery.images.icon).
                    into(holder.itemBreweryImage);
        } else {
            //if we haven't brewery icon then set placeholder
            Glide.with(holder.itemView.getContext()).
                    load(R.drawable.brewery_placeholder).
                    into(holder.itemBreweryImage);
        }

        holder.bind(breweryLocation, listener);
    }

    @Override
    public int getItemCount() {
        return breweries.size();
    }

    //to get a brewery at a given position
    public BreweryLocation getBreweryAtPosition(int position) {
        return breweries.get(position);
    }

    //this method is used to remove brewery
    public void removeBrewery(int position) {
        breweries.remove(position);
    }

    //add new breweries to list
    public void addNewBreweries(List<BreweryLocation> newBreweries) {
        breweries.addAll(newBreweries);
        notifyDataSetChanged();
    }
}
