package com.milesmagusruber.secretserviceflickrsearch.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.milesmagusruber.secretserviceflickrsearch.network.model.Photo;

@Entity(tableName = "requested_photos")
public class RequestedPhoto {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "url")
    private String url;

    @Ignore
    public RequestedPhoto(){}

    public RequestedPhoto(int id, String title, String url){
        this.id=id;
        this.title = title;
        this.url = url;
    }

    @Ignore
    public RequestedPhoto(String title, String url){
        this.title = title;
        this.url = url;
    }

    @Ignore
    public RequestedPhoto(Photo photo){
        this.title=photo.getTitle();
        this.url=photo.getPhotoUrl();
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getUrl(){
        return this.url;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
