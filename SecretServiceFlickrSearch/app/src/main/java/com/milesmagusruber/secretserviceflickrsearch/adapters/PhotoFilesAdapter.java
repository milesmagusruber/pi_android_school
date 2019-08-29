package com.milesmagusruber.secretserviceflickrsearch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.fs.model.PhotoFile;

import java.util.List;

public class PhotoFilesAdapter extends RecyclerView.Adapter<PhotoFilesAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemPhotoFileTitle;
        public ImageView itemPhotoFileImage;


        public ViewHolder(View itemView) {
            super(itemView);
            itemPhotoFileTitle = (TextView) itemView.findViewById(R.id.item_card_photo_file_title);
            itemPhotoFileImage = (ImageView) itemView.findViewById(R.id.item_card_photo_file_image);
        }

        public void bind(final PhotoFile photoFile, final PhotoFilesAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(photoFile);
                }
            });
        }
    }

    private List<PhotoFile> photoFiles;
    private final PhotoFilesAdapter.OnItemClickListener listener;

    private String searchRequest;
    // Pass in the favorites array into the constructor
    public PhotoFilesAdapter(List<PhotoFile> photoFiles, PhotoFilesAdapter.OnItemClickListener listener) {
        this.photoFiles = photoFiles;
        this.listener=listener;
    }
    public interface OnItemClickListener {
        void onItemClick(PhotoFile photoFile);
    }

    @NonNull
    @Override
    public PhotoFilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View photoView = inflater.inflate(R.layout.item_card_photo_file, parent, false);
        // Return a new holder instance
        PhotoFilesAdapter.ViewHolder viewHolder = new PhotoFilesAdapter.ViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoFilesAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        PhotoFile photoFile = photoFiles.get(position);
        // Set item views based on your views and data model
        holder.itemPhotoFileTitle.setText(photoFile.getTitle());
        holder.itemPhotoFileImage.setImageURI(photoFile.getFileURI());
        holder.bind(photoFile, listener);
    }

    @Override
    public int getItemCount() {
        return photoFiles.size();
    }

    //to get a photoFile at a given position
    public PhotoFile getPhotoFileAtPosition (int position) {
        return photoFiles.get(position);
    }

    //this method is used to remove photoFile
    public void removePhoto(int position) {
        photoFiles.remove(position);
    }

    //this method is user to add new photoFile
    public void addNewPhoto(PhotoFile newPhotoFile){
        photoFiles.add(newPhotoFile);
        notifyDataSetChanged();
    }

}
