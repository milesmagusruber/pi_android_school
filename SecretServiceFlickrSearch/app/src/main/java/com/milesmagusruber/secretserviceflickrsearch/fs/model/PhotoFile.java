package com.milesmagusruber.secretserviceflickrsearch.fs.model;

import android.net.Uri;

public class PhotoFile {
    private String title;
    private Uri fileURI;

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public Uri getFileURI(){
        return fileURI;
    }

    public void setFileURI(Uri fileURI){
        this.fileURI=fileURI;
    }
}
