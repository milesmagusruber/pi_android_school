package com.milesmagusruber.secretserviceflickrsearch.fs;

import android.content.Context;

import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;

import java.io.File;

public class FileHelper implements IFileHandler {

    private final String FILE_PATH="images/img_";
    //place where private files of current user are saved
    private File userPrivateRepository;

    //FileHelper instance
    private static FileHelper instance;

    //private constructor
    private FileHelper(Context context) {
        String directoryName=FILE_PATH + CurrentUser.getInstance().getUser().getLogin();
        userPrivateRepository=context.getDir(directoryName,Context.MODE_PRIVATE);
    }

    //Singleton implementation
    public static synchronized FileHelper getInstance(Context context) {

        if (instance == null) {
            instance = new FileHelper(context.getApplicationContext());
        }
        return instance;
    }


}
