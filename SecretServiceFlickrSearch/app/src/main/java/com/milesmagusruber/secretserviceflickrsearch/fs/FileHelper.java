package com.milesmagusruber.secretserviceflickrsearch.fs;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FileHelper implements IFileHandler {


    private final String USER_PHOTOS_FILE_PATH = "photos/"; //photos made by user
    private final String FLICKR_PHOTOS_FILE_PATH = "flickr_photos/"; //photos download from flickr

    public final String TAG_FILE_HELPER = "FILE_HELPER";

    private String login;

    private String userPhotosDirectoryName; //user photos directory name
    private String flickrPhotosDirectoryName; //flickr photos directory name

    //place where private files of current user are saved
    private File userPhotosDirectory; //user photos directory
    private File flickrPhotosDirectory; // flickr photos directory

    //FileHelper instance
    private static FileHelper instance;

    //private constructor
    private FileHelper() {
    }

    //method is used to initialize new user and create folders for him
    public void initializeUser(Context context) {
        login = CurrentUser.getInstance().getUser().getLogin();
        userPhotosDirectoryName = USER_PHOTOS_FILE_PATH + login;
        flickrPhotosDirectoryName = FLICKR_PHOTOS_FILE_PATH + login;

        //Creating private directory where user saves his camera photos

        userPhotosDirectory = new File(context.getFilesDir(), userPhotosDirectoryName);
        //File storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), directoryName);
        if (!userPhotosDirectory.mkdirs()) {
            Log.e(TAG_FILE_HELPER, "User photos directory not created");
        } else {
            Log.e(TAG_FILE_HELPER, "User photos directory is created");
        }

        //Creating public directory where user saves photos from Flickr
        flickrPhotosDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), flickrPhotosDirectoryName);
        //File storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), directoryName);
        if (!flickrPhotosDirectory.mkdirs()) {
            Log.e(TAG_FILE_HELPER, "Flickr photos directory not created");
        } else {
            Log.e(TAG_FILE_HELPER, "Flickr photos directory is created");
        }

    }

    //Singleton implementation
    public static synchronized FileHelper getInstance() {

        if (instance == null) {
            instance = new FileHelper();
        }
        return instance;
    }

    //creates new photofile from user camera
    @Override
    public File createUserPhotoFile(Context context) {
        String imageFileName = "userphoto_" + System.currentTimeMillis() + ".jpg";
        return new File(userPhotosDirectory, imageFileName);
    }

    @Override
    public void addFlickrPhoto(Context context, String filename, Bitmap bitmap) {
        FileOutputStream outputStream;
        try {
            File file = new File(flickrPhotosDirectory, filename);
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    @Override
    public boolean deleteFlickrPhoto(String filename) {
        return deletePhotoFile(new File(flickrPhotosDirectory, filename));
    }

    @Override
    public boolean isFlickrPhotoSaved(String filename) {
        try {
            File file = new File(flickrPhotosDirectory, filename);
            return file.exists();
        } catch (Exception e) {
            return false;
        }
    }

    //returns all user files from
    @Override
    public ArrayList<File> getAllUserPhotos() {
        return new ArrayList<>(Arrays.asList(userPhotosDirectory.listFiles()));
    }

    @Override
    public ArrayList<File> getAllFlickrPhotos() {
        return new ArrayList<>(Arrays.asList(flickrPhotosDirectory.listFiles()));
    }

    //deletes file from the device
    @Override
    public boolean deletePhotoFile(File file) {
        return file.delete();
    }


}
