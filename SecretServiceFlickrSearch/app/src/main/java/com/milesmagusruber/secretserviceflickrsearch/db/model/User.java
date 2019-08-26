package com.milesmagusruber.secretserviceflickrsearch.db.model;

public class User {
    int id;
    String login;

    public User(int id, String login){
        this.id=id;
        this.login = login;
    }

    public User(String login){
        this.login = login;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getLogin(){
        return this.login;
    }

    public void setLogin(String login){
        this.login = login;
    }
}
