package com.milesmagusruber.secretserviceflickrsearch.network.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import static android.content.ContentValues.TAG;

public class Photo {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("url_s")
    private String photoUrl;


    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {this.title = title; }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {return title; }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void getInfo(){
        Log.d(TAG, "getInfo: "+
                "id: "+id +" url "+ photoUrl +" title "+title);
    }
}
