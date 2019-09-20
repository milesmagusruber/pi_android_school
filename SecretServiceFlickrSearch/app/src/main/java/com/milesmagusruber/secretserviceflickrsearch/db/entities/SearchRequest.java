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

    public SearchRequest(int id, int user, String searchRequest, long sDateTime){
        this.id=id;
        this.user = user;
        this.searchRequest = searchRequest;
        this.sDateTime = sDateTime;
    }

    @Ignore
    public SearchRequest(int user, String searchRequest){
        this.user = user;
        this.searchRequest = searchRequest;
        this.sDateTime = System.currentTimeMillis();
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

    public long getSDateTime(){
        return this.sDateTime;
    }

    public void setSDateTime(long sDateTime){
        this.sDateTime = sDateTime;
    }
}
