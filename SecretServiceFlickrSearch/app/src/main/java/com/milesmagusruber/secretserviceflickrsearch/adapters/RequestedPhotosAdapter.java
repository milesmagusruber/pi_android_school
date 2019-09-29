package com.milesmagusruber.secretserviceflickrsearch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.RequestedPhoto;

import java.util.List;

public class RequestedPhotosAdapter extends RecyclerView.Adapter<RequestedPhotosAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemPhotoSearchRequest;
        public TextView itemPhotoTitle;
        public ImageView itemPhotoImage;


        public ViewHolder(View itemView) {
            super(itemView);

            itemPhotoSearchRequest = (TextView) itemView.findViewById(R.id.item_card_photo_search_request);
            itemPhotoTitle = (TextView) itemView.findViewById(R.id.item_card_photo_title);
            itemPhotoImage = (ImageView) itemView.findViewById(R.id.item_card_photo_image);
        }

        public void bind(final RequestedPhoto requestedPhoto, final RequestedPhotosAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(requestedPhoto);
                }
            });
        }
    }

    private List<RequestedPhoto> requestedPhotos;
    private final RequestedPhotosAdapter.OnItemClickListener listener;

    private String searchRequest;
    // Pass in the favorites array into the constructor
    public RequestedPhotosAdapter(List<RequestedPhoto> requestedPhotos, String searchRequest, RequestedPhotosAdapter.OnItemClickListener listener) {
        this.requestedPhotos = requestedPhotos;
        this.searchRequest = searchRequest;
        this.listener=listener;
    }
    public interface OnItemClickListener {
        void onItemClick(RequestedPhoto requestedPhoto);
    }

    @NonNull
    @Override
    public RequestedPhotosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View photoView = inflater.inflate(R.layout.item_card_photo, parent, false);
        // Return a new holder instance
        RequestedPhotosAdapter.ViewHolder viewHolder = new RequestedPhotosAdapter.ViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestedPhotosAdapter.ViewHolder holder, int position) {
        // Get the data entities based on position
        RequestedPhoto requestedPhoto = requestedPhotos.get(position);
        // Set item views based on your views and data entities
        holder.itemPhotoSearchRequest.setText(searchRequest);
        holder.itemPhotoTitle.setText(requestedPhoto.getTitle());

        Glide.with(holder.itemView.getContext()).
                load(requestedPhoto.getUrl()).
                into(holder.itemPhotoImage);
        holder.bind(requestedPhoto, listener);
    }

    @Override
    public int getItemCount() {
        return requestedPhotos.size();
    }

    //to get a photo at a given position
    public RequestedPhoto getRequestedPhotoAtPosition (int position) {
        return requestedPhotos.get(position);
    }
}
