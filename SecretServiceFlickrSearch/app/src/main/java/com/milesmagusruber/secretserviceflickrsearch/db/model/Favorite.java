package com.milesmagusruber.secretserviceflickrsearch.db.model;

public class Favorite {
    int id;
    int user;
    String searchRequest;
    String title;
    String webLink;


    public Favorite(){}

    public Favorite(int id, int user, String searchRequest, String title, String webLink){
        this.id=id;
        this.user = user;
        this.searchRequest = searchRequest;
        this.title = title;
        this.webLink = webLink;
    }

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
        this.title = webLink;
    }
}
