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
import com.milesmagusruber.secretserviceflickrsearch.network.model.Photo;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

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

        public void bind(final Photo photo, final PhotosAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(photo);
                }
            });
        }
    }

    private List<Photo> photos;
    private final PhotosAdapter.OnItemClickListener listener;

    private String searchRequest;
    // Pass in the favorites array into the constructor
    public PhotosAdapter(List<Photo> photos,String searchRequest, PhotosAdapter.OnItemClickListener listener) {
        this.photos = photos;
        this.searchRequest = searchRequest;
        this.listener=listener;
    }
    public interface OnItemClickListener {
        void onItemClick(Photo photo);
    }

    @NonNull
    @Override
    public PhotosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View photoView = inflater.inflate(R.layout.item_card_photo, parent, false);
        // Return a new holder instance
        PhotosAdapter.ViewHolder viewHolder = new PhotosAdapter.ViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosAdapter.ViewHolder holder, int position) {
        // Get the data entities based on position
        Photo photo = photos.get(position);
        // Set item views based on your views and data entities
        holder.itemPhotoSearchRequest.setText(searchRequest);
        holder.itemPhotoTitle.setText(photo.getTitle());

        Glide.with(holder.itemView.getContext()).
                load(photo.getPhotoUrl()).
                into(holder.itemPhotoImage);
        holder.bind(photo, listener);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    //to get a photo at a given position
    public Photo getPhotoAtPosition (int position) {
        return photos.get(position);
    }

    //this method is used to remove photo
    public void removePhoto(int position) {
        photos.remove(position);
    }

    public void addNewPhotos(List<Photo> newPhotos){
        photos.addAll(newPhotos);
        notifyDataSetChanged();
    }

}
