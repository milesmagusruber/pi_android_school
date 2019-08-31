package com.milesmagusruber.secretserviceflickrsearch.fs;

import android.content.Context;
import java.io.File;
import java.util.ArrayList;

public interface IFileHandler {

    //adds photo into private directory that will be captured by user's camera
    public File createUserPhotoFile(Context context);

    //returns a list of camera user photo files
    public ArrayList<File> getAllUserPhotos();

    //return a list of flickr photo files
    public ArrayList<File> getAllFlickrPhotos();

    //add FlickrPhoto
    //public void addFlickrPhoto();

    //deletes any photo from device User or Flickr
    //public void deletePhoto();


}
