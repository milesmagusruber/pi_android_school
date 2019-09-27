package com.milesmagusruber.secretserviceflickrsearch.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.milesmagusruber.secretserviceflickrsearch.db.entities.TempPhoto;

import java.util.List;

@Dao
public interface TempPhotoDao {

    @Query("SELECT * FROM temp_photos")
    List<TempPhoto> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TempPhoto tempPhoto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TempPhoto> tempPhotos);

    @Delete
    void delete(TempPhoto tempPhoto);

    @Query("DELETE FROM temp_photos")
    void deleteAll();
}
