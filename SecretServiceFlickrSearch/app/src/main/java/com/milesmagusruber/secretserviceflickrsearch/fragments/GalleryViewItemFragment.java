package com.milesmagusruber.secretserviceflickrsearch.fragments;

import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.milesmagusruber.secretserviceflickrsearch.BuildConfig;
import com.milesmagusruber.secretserviceflickrsearch.R;

import java.io.File;

public class GalleryViewItemFragment extends Fragment {

    public static final String EXTRA_GALLERY_ITEM = BuildConfig.APPLICATION_ID + ".extra.gallery.item";

    private ImageView galleryViewItem;
    private File photoFile;
    private Bitmap fileBitmap;
    private String filePath;

    //asyncTask to work with files
    private AsyncTask<Void,Void,Boolean> fileWorkAsyncTask;

    public GalleryViewItemFragment() {
    }

    public static GalleryViewItemFragment newInstance(String galleryItemFilePath) {
        GalleryViewItemFragment fragment = new GalleryViewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_GALLERY_ITEM, galleryItemFilePath);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ){
        View view = inflater.inflate(R.layout.fragment_gallery_view_item, container, false);
        galleryViewItem = view.findViewById(R.id.gallery_view_item_imageview);
        fileWorkAsyncTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                try{
                    photoFile = new File(filePath);
                    fileBitmap = BitmapFactory.decodeFile(photoFile.getPath());
                    return true;
                }catch (Exception e){
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result) {
                    galleryViewItem.setImageBitmap(fileBitmap);
                }
            }
        };
        if (getArguments() != null) {
            filePath=getArguments().getString(EXTRA_GALLERY_ITEM);
            if ( filePath != null) {
                fileWorkAsyncTask.execute();
            }
        }
        return view;
    }
}
