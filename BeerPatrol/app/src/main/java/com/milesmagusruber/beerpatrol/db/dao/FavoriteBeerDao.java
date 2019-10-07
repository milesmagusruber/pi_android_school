package com.milesmagusruber.beerpatrol.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.milesmagusruber.beerpatrol.db.entities.FavoriteBeer;

import java.util.List;

@Dao
public interface FavoriteBeerDao {

    //get beer by id
    @Query("SELECT * FROM favorite_beers WHERE beerId=:beerId")
    FavoriteBeer getByBeerId(String beerId);

    //get all favorite beers
    @Query("SELECT * FROM favorite_beers ORDER BY name")
    List<FavoriteBeer> getAll();

    //get all beers filtered by name filter
    @Query("SELECT * FROM favorite_beers WHERE name LIKE :nameFilter ORDER BY name")
    List<FavoriteBeer> getAllFilteredByName(String nameFilter);

    @Insert
    long insert(FavoriteBeer favoriteBeer);

    @Update
    int update(FavoriteBeer favoriteBeer);

    @Delete
    int delete(FavoriteBeer favoriteBeer);
}
