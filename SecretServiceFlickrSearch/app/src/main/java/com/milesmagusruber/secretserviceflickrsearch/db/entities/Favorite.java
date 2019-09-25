package com.milesmagusruber.secretserviceflickrsearch.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class Favorite {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "user")
    private int user;

    @ColumnInfo(name = "search_request")
    private String searchRequest;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "web_link")
    private String webLink;


    @Ignore
    public Favorite(){}

    public Favorite(int id, int user, String searchRequest, String title, String webLink){
        this.id=id;
        this.user = user;
        this.searchRequest = searchRequest;
        this.title = title;
        this.webLink = webLink;
    }

    @Ignore
    public Favorite(int user, String searchRequest, String title, String webLink){
        this.user = user;
        this.searchRequest = searchRequest;
        this.title = title;
        this.webLink = webLink;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getUser(){
        return this.user;
    }

    public void setUser(int user){
        this.user = user;
    }

    public String getSearchRequest(){
        return this.searchRequest;
    }

    public void setSearchRequest(String searchRequest){
        this.searchRequest = searchRequest;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getWebLink(){
        return this.webLink;
    }

    public void setWebLink(String webLink){
        this.webLink = webLink;
    }
}
