package com.milesmagusruber.secretserviceflickrsearch.fs;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;

import java.io.File;
import java.io.IOException;

public class FileHelper implements IFileHandler {

    private final String FILE_PATH = "photos/";
    private String login;

    private String directoryName;
    //place where private files of current user are saved
    private File userPrivateRepository;

    //FileHelper instance
    private static FileHelper instance;

    //private constructor
    private FileHelper() {
    }

    public void checkLogin() {
        login = CurrentUser.getInstance().getUser().getLogin();
        directoryName = FILE_PATH + login;
    }

    //Singleton implementation
    public static synchronized FileHelper getInstance() {

        if (instance == null) {
            instance = new FileHelper();
        }
        return instance;
    }

    @Override
    public File createUserPhotoFile(Context context) {
        String imageFileName = "userphoto_" + System.currentTimeMillis() + ".jpg";
        /*File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );*/
        File storageDir = new File(context.getFilesDir(), directoryName);
        //File storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), directoryName);
        if (!storageDir.mkdirs()) {
            Log.e("FILET", "Directory not created");
        }else{
            Log.e("FILET","Directory is created");
        }


        File file = new File(storageDir, imageFileName);
        //Log.d("FILETT","file "+file.toString());
        return file;

    }
}
