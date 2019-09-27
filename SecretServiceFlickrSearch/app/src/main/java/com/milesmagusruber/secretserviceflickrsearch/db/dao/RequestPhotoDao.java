package com.milesmagusruber.secretserviceflickrsearch.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.milesmagusruber.secretserviceflickrsearch.db.entities.RequestPhoto;

import java.util.List;

@Dao
public interface RequestPhotoDao {

    @Query("SELECT * FROM request_photos")
    List<RequestPhoto> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RequestPhoto requestPhoto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RequestPhoto> requestPhotos);

    @Delete
    void delete(RequestPhoto requestPhoto);

    @Query("DELETE FROM request_photos")
    void deleteAll();
}
