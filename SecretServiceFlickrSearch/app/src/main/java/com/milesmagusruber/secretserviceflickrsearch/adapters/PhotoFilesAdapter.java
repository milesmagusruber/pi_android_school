package com.milesmagusruber.secretserviceflickrsearch.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.milesmagusruber.secretserviceflickrsearch.R;

import java.io.File;
import java.util.ArrayList;

public class PhotoFilesAdapter extends RecyclerView.Adapter<PhotoFilesAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemPhotoFileTitle;
        public ImageView itemPhotoFileImage;


        public ViewHolder(View itemView) {
            super(itemView);
            itemPhotoFileTitle = (TextView) itemView.findViewById(R.id.item_card_photo_file_title);
            itemPhotoFileImage = (ImageView) itemView.findViewById(R.id.item_card_photo_file_image);
        }

        public void bind(final File photoFile, final PhotoFilesAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(photoFile);
                }
            });
        }
    }

    private ArrayList<File> photoFiles;
    private final PhotoFilesAdapter.OnItemClickListener listener;

    private String searchRequest;
    // Pass in the favorites array into the constructor
    public PhotoFilesAdapter(ArrayList<File> photoFiles, PhotoFilesAdapter.OnItemClickListener listener) {
        this.photoFiles = photoFiles;
        this.listener=listener;
    }
    public interface OnItemClickListener {
        void onItemClick(File photoFile);
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
        // Get the data entities based on position
        File photoFile = photoFiles.get(position);
        // Set item views based on your views and data entities
        holder.itemPhotoFileTitle.setText(photoFile.getName());
        Glide.with(holder.itemView.getContext()).load(photoFile).into(holder.itemPhotoFileImage);
        holder.bind(photoFile, listener);
    }

    @Override
    public int getItemCount() {
        return photoFiles.size();
    }

    //to get a photoFile at a given position
    public File getPhotoFileAtPosition (int position) {
        return photoFiles.get(position);
    }

    //this method is used to remove photoFile
    public void removePhotoFile(int position) {
        photoFiles.remove(position);
        notifyItemRemoved(position);
    }

    //this method is user to add new photoFile
    public void addNewPhotoFile(File newPhotoFile){
        photoFiles.add(newPhotoFile);
        notifyDataSetChanged();
    }

}
