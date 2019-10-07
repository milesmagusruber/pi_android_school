package com.milesmagusruber.beerpatrol.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.milesmagusruber.beerpatrol.db.dao.FavoriteBeerDao;
import com.milesmagusruber.beerpatrol.db.entities.FavoriteBeer;

@Database(entities = {FavoriteBeer.class}, version = 1)
public abstract class BeerPatrolDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "beerpatrol.db";

    private static BeerPatrolDatabase INSTANCE;

    public abstract FavoriteBeerDao favoriteBeerDao();

    private static final Object sLock = new Object();

    public static BeerPatrolDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        BeerPatrolDatabase.class, DATABASE_NAME).build();
            }
            return INSTANCE;
        }
    }
}
