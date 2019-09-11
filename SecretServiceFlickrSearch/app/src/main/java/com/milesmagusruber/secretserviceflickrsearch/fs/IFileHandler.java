package com.milesmagusruber.secretserviceflickrsearch.fs;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;

public interface IFileHandler {

    //adds photo into private directory that will be captured by user's camera
    public File createUserPhotoFile();

    //returns a list of camera user photo files
    public ArrayList<File> getAllUserPhotos();

    //return a list of flickr photo files
    public ArrayList<File> getAllFlickrPhotos();


    //add FlickrPhoto
    public Boolean addFlickrPhoto(String filename, Bitmap bitmap);

    //delete FlickrPhoto
    public boolean deleteFlickrPhoto(String filename);

    //checks if Flick Photo saved
    public boolean isFlickrPhotoSaved(String filename);

    //deletes any photo from device User or Flickr
    public boolean deletePhotoFile(File file);


}
