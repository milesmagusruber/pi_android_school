package com.milesmagusruber.secretserviceflickrsearch.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_requests")
public class SearchRequest {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "user")
    private int user;

    @ColumnInfo(name = "search_request")
    private String searchRequest;

    @ColumnInfo(name = "sdatetime")
    private long sDateTime;

    @Ignore
    public SearchRequest(){

    }

    public SearchRequest(int id, int user, String searchRequest, long dateTime){
        this.id=id;
        this.user = user;
        this.searchRequest = searchRequest;
        this.sDateTime = dateTime;
    }

    @Ignore
    public SearchRequest(int user, String searchRequest){
        this.user = user;
        this.searchRequest = searchRequest;
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

    public long getDateTime(){
        return this.sDateTime;
    }

    public void setDate(long sDateTime){
        this.sDateTime = sDateTime;
    }
}
