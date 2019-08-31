package com.milesmagusruber.secretserviceflickrsearch.fs;

import android.content.Context;
import android.os.Environment;

import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;

import java.io.File;
import java.io.IOException;

public class FileHelper implements IFileHandler {

    private final String FILE_PATH="images/img_";
    //place where private files of current user are saved
    private File userPrivateRepository;

    //FileHelper instance
    private static FileHelper instance;

    //private constructor
    private FileHelper(Context context) {
        //String directoryName=FILE_PATH + CurrentUser.getInstance().getUser().getLogin();
        //userPrivateRepository=context.getDir(directoryName,Context.MODE_PRIVATE);
    }

    //Singleton implementation
    public static synchronized FileHelper getInstance(Context context) {

        if (instance == null) {
            instance = new FileHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public File createUserPhotoFile() {
        String imageFileName = "userphoto_" + System.currentTimeMillis();
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        try {
            File file = File.createTempFile(
                    imageFileName, ".jpg", storageDir
            );
            return file;
        }catch (IOException e){
            return null;
        }
    }
}
