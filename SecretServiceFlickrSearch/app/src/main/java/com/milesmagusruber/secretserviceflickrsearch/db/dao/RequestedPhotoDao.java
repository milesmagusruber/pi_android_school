package com.milesmagusruber.secretserviceflickrsearch.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.milesmagusruber.secretserviceflickrsearch.db.entities.RequestedPhoto;

import java.util.List;

@Dao
public interface RequestedPhotoDao {

    @Query("SELECT * FROM requested_photos")
    List<RequestedPhoto> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RequestedPhoto requestedPhoto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RequestedPhoto> requestedPhotos);

    @Delete
    void delete(RequestedPhoto requestedPhoto);

    @Query("DELETE FROM requested_photos")
    void deleteAll();
}
