package com.milesmagusruber.secretserviceflickrsearch.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.milesmagusruber.secretserviceflickrsearch.db.entities.User;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users WHERE login = :login")
    User getUser(String login);

    @Insert
    void insert(User user);

    @Delete
    void delete(User user);
}
