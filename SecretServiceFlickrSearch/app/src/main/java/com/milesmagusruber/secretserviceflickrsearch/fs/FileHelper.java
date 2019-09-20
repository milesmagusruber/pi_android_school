package com.milesmagusruber.secretserviceflickrsearch.fs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileHelper implements IFileHandler {


    private final String USER_PHOTOS_FILE_PATH = "photos/"; //photos made by user
    private final String FLICKR_PHOTOS_FILE_PATH = "flickr_photos/"; //photos download from flickr

    private final String TAG_FILE_HELPER = "FILE_HELPER";

    private String userPhotosDirectoryName; //user photos directory name
    private String flickrPhotosDirectoryName; //flickr photos directory name

    //place where private files of current user are saved
    private File userPhotosDirectory; //user photos directory
    private File flickrPhotosDirectory; // flickr photos directory

    //public constructor
    public FileHelper(String login, Context context) {
        userPhotosDirectoryName = USER_PHOTOS_FILE_PATH + login;
        flickrPhotosDirectoryName = FLICKR_PHOTOS_FILE_PATH + login;

        //Creating private directory where user saves his camera photos

        userPhotosDirectory = new File(context.getFilesDir(), userPhotosDirectoryName);

        if (!userPhotosDirectory.mkdirs()) {
            Log.d(TAG_FILE_HELPER, "User photos directory not created");
        } else {
            Log.d(TAG_FILE_HELPER, "User photos directory is created");
        }

        //Creating public directory where user saves photos from Flickr
        flickrPhotosDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), flickrPhotosDirectoryName);
        if (!flickrPhotosDirectory.mkdirs()) {
            Log.d(TAG_FILE_HELPER, "Flickr photos directory not created");
        } else {
            Log.d(TAG_FILE_HELPER, "Flickr photos directory is created");
        }
    }

    //creates new photofile from user camera
    @Override
    public File createUserPhotoFile() {
        String imageFileName = "userphoto_" + System.currentTimeMillis() + ".jpg";
        return new File(userPhotosDirectory, imageFileName);
    }

    //downloads flickr photo to the device
    @Override
    public Boolean addFlickrPhoto(String filename, Bitmap bitmap) {
        FileOutputStream outputStream;
        try {
            File file = new File(flickrPhotosDirectory, filename);
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            return true;
        } catch (Exception error) {
            return false;
        }
    }

    //deletes FlickrPhoto
    @Override
    public boolean deleteFlickrPhoto(String filename) {
        return deletePhotoFile(new File(flickrPhotosDirectory, filename));
    }

    //returns true if Flickr Photo is already saved on the device
    @Override
    public boolean isFlickrPhotoSaved(String filename) {
        try {
            File file = new File(flickrPhotosDirectory, filename);
            return file.exists();
        } catch (Exception e) {
            return false;
        }
    }

    //returns all user files from private directory
    @Override
    public ArrayList<File> getAllUserPhotos() {
        ArrayList<File> photosList = new ArrayList<>();
        File[] photos = userPhotosDirectory.listFiles();
        if (photos != null) {
            photosList.addAll(Arrays.asList(photos));
        }
        return photosList;

    }

    //return all public flickr photos
    @Override
    public ArrayList<File> getAllFlickrPhotos() {
        ArrayList<File> photosList = new ArrayList<>();
        File[] photos = flickrPhotosDirectory.listFiles();
        if (photos != null) {
            photosList.addAll(Arrays.asList(photos));
        }
        return photosList;
    }

    //deletes file from the device
    @Override
    public boolean deletePhotoFile(File file) {
        return file.delete();
    }


}
