package com.milesmagusruber.secretserviceflickrsearch.db;

import android.content.Context;

import com.milesmagusruber.secretserviceflickrsearch.db.entities.User;
import com.milesmagusruber.secretserviceflickrsearch.fs.FileHelper;

public class CurrentUser {

    private static CurrentUser instance;
    private User user;
    private FileHelper fileHelper;

    private CurrentUser() {
    }

    public static CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FileHelper getFileHelper() {
        return fileHelper;
    }

    public void setFileHelper(Context context) {
        if (user != null) {
            fileHelper=new FileHelper(user.getLogin(),context);
        }
    }

}
