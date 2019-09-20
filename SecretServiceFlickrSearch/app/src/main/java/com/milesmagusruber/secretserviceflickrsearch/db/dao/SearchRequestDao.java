package com.milesmagusruber.secretserviceflickrsearch.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.milesmagusruber.secretserviceflickrsearch.db.entities.SearchRequest;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SearchRequestDao {

    @Query("SELECT * FROM search_requests WHERE user = :user AND search_request " +
            "NOT LIKE 'Geo Request%Latitude%Longitude%' ORDER BY sdatetime DESC LIMIT 1")
    SearchRequest getLastForUser(int user);

    @Query("SELECT * FROM search_requests WHERE user = :user ORDER BY sdatetime DESC LIMIT 20")
    List<SearchRequest> getLast20ForUser(int user);

    @Insert
    void insert(SearchRequest searchRequest);

    @Delete
    void delete(SearchRequest searchRequest);
}
