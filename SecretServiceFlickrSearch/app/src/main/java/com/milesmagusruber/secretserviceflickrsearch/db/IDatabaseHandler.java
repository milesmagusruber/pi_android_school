package com.milesmagusruber.secretserviceflickrsearch.db;

import com.milesmagusruber.secretserviceflickrsearch.db.entities.Favorite;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.SearchRequest;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.User;

import java.util.ArrayList;

public interface IDatabaseHandler {
    //working with Users
    public void addUser(User user);
    public User getUser(String login);

    //working with Favorites
    public void addFavorite(Favorite favorite);
    public Favorite getFavorite(int id);
    public Favorite getFavorite(int user, String url);
    public ArrayList<Favorite> getAllFavorites(int user, String searchRequest);
    public int updateFavorite(Favorite favorite);
    public void deleteFavorite(Favorite favorite);

    //working with SearchRequests
    public void addSearchRequest(SearchRequest searchRequest);
    public SearchRequest getLastTextSearchRequest(int user);
    public ArrayList<SearchRequest> getAllSearchRequests(int user);
    public void deleteSearchRequest(SearchRequest searchRequest);


}
