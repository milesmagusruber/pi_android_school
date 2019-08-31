package com.milesmagusruber.secretserviceflickrsearch.fs;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileHelper implements IFileHandler {


    private final String USER_PHOTOS_FILE_PATH = "photos/"; //photos made by user
    private final String FLICKR_PHOTOS_FILE_PATH="flickr_photos/"; //photos download from flickr

    public final String TAG_FILE_HELPER="FILE_HELPER";

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
        }else{
            Log.e(TAG_FILE_HELPER,"User photos directory is created");
        }

        //Creating public directory where user saves photos from Flickr
        flickrPhotosDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), flickrPhotosDirectoryName);
        //File storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), directoryName);
        if (!flickrPhotosDirectory.mkdirs()) {
            Log.e(TAG_FILE_HELPER, "Flickr photos directory not created");
        }else{
            Log.e(TAG_FILE_HELPER,"Flickr photos directory is created");
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
        File file = new File(userPhotosDirectory, imageFileName);
        return file;
    }

    //returns all user files from
    @Override
    public ArrayList<File> getAllUserPhotos(){
        ArrayList<File> userPhotos = new ArrayList<File>();
        try {
            for (File photo : userPhotosDirectory.listFiles()) {
                userPhotos.add(photo);
            }
            return userPhotos;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public ArrayList<File> getAllFlickrPhotos(){
        ArrayList<File> flickrPhotos = new ArrayList<File>();
        try {
            for (File photo : flickrPhotosDirectory.listFiles()) {
                flickrPhotos.add(photo);
            }
            return flickrPhotos;
        }catch (Exception e){
            return null;
        }
    }


}
