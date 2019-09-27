package com.milesmagusruber.secretserviceflickrsearch.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.milesmagusruber.secretserviceflickrsearch.db.dao.FavoriteDao;
import com.milesmagusruber.secretserviceflickrsearch.db.dao.SearchRequestDao;
import com.milesmagusruber.secretserviceflickrsearch.db.dao.TempPhotoDao;
import com.milesmagusruber.secretserviceflickrsearch.db.dao.UserDao;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.Favorite;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.SearchRequest;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.User;

@Database(entities = {Favorite.class, SearchRequest.class, User.class}, version = 1)
public abstract class SSFSDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "ssfs.db";

    private static SSFSDatabase INSTANCE;

    public abstract FavoriteDao favoriteDao();

    public abstract SearchRequestDao searchRequestDao();

    public abstract UserDao userDao();

    public abstract TempPhotoDao tempPhotoDao();

    private static final Object sLock = new Object();

    public static SSFSDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        SSFSDatabase.class, DATABASE_NAME).build();
            }
            return INSTANCE;
        }
    }

}
