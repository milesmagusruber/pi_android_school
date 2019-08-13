package com.milesmagusruber.secretserviceflickrsearch.db.model;

public class SearchRequest {
    int id;
    int user;
    String searchRequest;
    String sDateTime;


    public SearchRequest(){

    }

    public SearchRequest(int id, int user, String searchRequest, String dateTime){
        this.id=id;
        this.user = user;
        this.searchRequest = searchRequest;
        this.sDateTime = dateTime;
    }

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

    public String getDateTime(){
        return this.sDateTime;
    }

    public void setDate(String sDateTime){
        this.sDateTime = sDateTime;
    }
}
