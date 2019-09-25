package com.milesmagusruber.secretserviceflickrsearch.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.milesmagusruber.secretserviceflickrsearch.db.entities.Favorite;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM favorites WHERE id = :id")
    Favorite getById(int id);

    @Query("SELECT * FROM favorites WHERE user= :user AND web_link= :webLink")
    Favorite getByWebLinkForUser(int user, String webLink);

    @Query("SELECT * FROM favorites WHERE user = :user ORDER BY search_request")
    List<Favorite> getAllForUser(int user);

    @Query("SELECT * FROM favorites WHERE user = :user AND search_request LIKE :filterSearchRequest ORDER BY search_request")
    List<Favorite> getAllFilteredBySearchRequestForUser(int user, String filterSearchRequest);

    @Insert
    void insert(Favorite favorite);

    @Update
    void update(Favorite favorite);

    @Delete
    void delete(Favorite favorite);

    @Query("DELETE FROM favorites WHERE user = :user AND search_request=:searchRequest")
    void deleteAllBySearchRequestForUser(int user, String searchRequest);


}
