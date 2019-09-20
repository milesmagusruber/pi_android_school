package com.milesmagusruber.secretserviceflickrsearch.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.milesmagusruber.secretserviceflickrsearch.db.entities.Favorite;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.SearchRequest;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.User;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper implements IDatabaseHandler {

    private static final String LOG_DB = "DatabaseHelper";
    //database instance
    private static DatabaseHelper instance;
    //database information
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "secretserviceflickrsearch.db";
    private static final String KEY_ID = "id";
    private static final String KEY_USER = "user";
    private static final String KEY_SEARCH_REQUEST = "search_request";


    //table users information
    private static final String TABLE_USERS = "users";
    private static final String KEY_USER_LOGIN = "login";

    //table favorites information
    private static final String TABLE_FAVORITES = "favorites";
    private static final String KEY_FAVORITE_TITLE = "title";
    private static final String KEY_FAVORITE_WEB_LINK = "web_link";

    //table search_requests information
    private static final String TABLE_SEARCH_REQUESTS = "search_requests";
    private static final String KEY_SEARCH_REQUEST_SDATETIME = "sdatetime";

    public static synchronized DatabaseHelper getInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //creating table users
        String createUsers = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_LOGIN + " TEXT UNIQUE"
                + ");";
        db.execSQL(createUsers);

        //creating table favorites
        String createFavorites = "CREATE TABLE " + TABLE_FAVORITES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER + " INTEGER,"
                + KEY_SEARCH_REQUEST + " TEXT," + KEY_FAVORITE_TITLE + " TEXT,"
                + KEY_FAVORITE_WEB_LINK + " TEXT UNIQUE);";
        db.execSQL(createFavorites);

        //creating table search requests
        String createSearchRequests = "CREATE TABLE " + TABLE_SEARCH_REQUESTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER + " INTEGER,"
                + KEY_SEARCH_REQUEST + " TEXT," + KEY_SEARCH_REQUEST_SDATETIME + " TEXT); ";
        db.execSQL(createSearchRequests);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        StringBuilder dropTablesQueryBuiler = new StringBuilder();
        dropTablesQueryBuiler.append("DROP TABLE IF EXISTS " + TABLE_USERS + ";");
        dropTablesQueryBuiler.append("DROP TABLE IF EXISTS " + TABLE_FAVORITES + ";");
        dropTablesQueryBuiler.append("DROP TABLE IF EXISTS " + TABLE_SEARCH_REQUESTS + ";");
        db.execSQL(dropTablesQueryBuiler.toString());
        onCreate(db);
    }

    //Working with users

    //add user
    @Override
    public void addUser(User user) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_USER_LOGIN, user.getLogin());
            db.insert(TABLE_USERS, null, values);
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }
    }


    @Override
    public User getUser(String login) {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID,
                            KEY_USER_LOGIN}, KEY_USER_LOGIN + "=?",
                    new String[]{String.valueOf(login)}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    User user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
                    cursor.close();
                    db.close();
                    return user;
                } else {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return null;
    }

    //Working with favorites
    @Override
    public void addFavorite(Favorite favorite) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_USER, favorite.getUser());
            values.put(KEY_SEARCH_REQUEST, favorite.getSearchRequest());
            values.put(KEY_FAVORITE_TITLE, favorite.getTitle());
            values.put(KEY_FAVORITE_WEB_LINK, favorite.getWebLink());
            db.insert(TABLE_FAVORITES, null, values);
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }
    }

    @Override
    public Favorite getFavorite(int id) {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID,
                            KEY_USER_LOGIN}, KEY_USER_LOGIN + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    Favorite favorite = new Favorite(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
                            cursor.getString(2), cursor.getString(3), cursor.getString(4));
                    cursor.close();
                    db.close();
                    return favorite;
                } else {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return null;
    }

    @Override
    public Favorite getFavorite(int user, String url) {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_USER
                    + " = ?" + " AND " + KEY_FAVORITE_WEB_LINK + " = ?", new String[]{Integer.toString(user), url});
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    Favorite favorite = new Favorite(Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(1)), cursor.getString(2),
                            cursor.getString(3), cursor.getString(4));
                    cursor.close();
                    db.close();
                    return favorite;
                } else {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return null;
    }

    @Override
    public ArrayList<Favorite> getAllFavorites(int user, String searchRequest) {
        ArrayList<Favorite> favoritesList = new ArrayList<Favorite>();
        String selectQuery = null;
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            Cursor cursor = null;
            if (searchRequest == null) {
                selectQuery = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_USER
                        + " = ? ORDER BY "+KEY_SEARCH_REQUEST+" ASC";
                cursor = db.rawQuery(selectQuery, new String[]{Integer.toString(user)});

            } else {
                selectQuery = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_USER
                        + " = ?" + " AND " + KEY_SEARCH_REQUEST + "= ? ORDER BY "+KEY_SEARCH_REQUEST+" ASC";
                cursor = db.rawQuery(selectQuery, new String[]{Integer.toString(user), searchRequest});
            }

            String cunningSearchString=""; //for FavoritesAdapter to have different cards

            if (cursor.moveToFirst()) {
                do {
                    String searchReq=cursor.getString(2);
                    if(!cunningSearchString.equals(searchReq)){
                        cunningSearchString=searchReq;
                        favoritesList.add(new Favorite(user,cunningSearchString,"",""));
                    }
                    Favorite favorite = new Favorite();
                    favorite.setId(Integer.parseInt(cursor.getString(0)));
                    favorite.setUser(Integer.parseInt(cursor.getString(1)));
                    favorite.setSearchRequest(cursor.getString(2));
                    favorite.setTitle(cursor.getString(3));
                    favorite.setWebLink(cursor.getString(4));
                    favoritesList.add(favorite);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }

        return favoritesList;
    }

    @Override
    public int updateFavorite(Favorite favorite) {
        SQLiteDatabase db = null;
        int result = -1;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_SEARCH_REQUEST, favorite.getSearchRequest());
            result = db.update(TABLE_FAVORITES, values, KEY_FAVORITE_WEB_LINK + " = ?",
                    new String[]{String.valueOf(favorite.getWebLink())});
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return result;
    }

    @Override
    public void deleteFavorite(Favorite favorite) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            if(!favorite.getWebLink().equals("")) {
                db.delete(TABLE_FAVORITES, KEY_FAVORITE_WEB_LINK + " = ?", new String[]{String.valueOf(favorite.getWebLink())});
            }else{
                db.delete(TABLE_FAVORITES,KEY_SEARCH_REQUEST +" = ?", new String[]{String.valueOf(favorite.getSearchRequest())});
            }
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }
    }

    //Working with search requests
    @Override
    public void addSearchRequest(SearchRequest searchRequest) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_USER, searchRequest.getUser());
            values.put(KEY_SEARCH_REQUEST, searchRequest.getSearchRequest());
            values.put(KEY_SEARCH_REQUEST_SDATETIME, System.currentTimeMillis());
            db.insert(TABLE_SEARCH_REQUESTS, null, values);
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }
    }

    @Override
    public SearchRequest getLastTextSearchRequest(int user) {
        SQLiteDatabase db = null;
        try {
            db = this.getReadableDatabase();
            String pattern="Geo Request%Latitude%Longitude%";
            String selectQuery = "SELECT * FROM " + TABLE_SEARCH_REQUESTS + " WHERE " + KEY_USER
                    + "= ? AND "+KEY_SEARCH_REQUEST+" NOT LIKE ?" + " ORDER BY " + KEY_SEARCH_REQUEST_SDATETIME + " DESC LIMIT 1";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{Integer.toString(user),pattern});
            if (cursor != null) {
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    SearchRequest searchRequest = new SearchRequest(Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(1)), cursor.getString(2),
                            Long.parseLong(cursor.getString(3)));
                    cursor.close();
                    db.close();
                    return searchRequest;
                } else {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return new SearchRequest(0, user, "", 0);
    }

    @Override
    public ArrayList<SearchRequest> getAllSearchRequests(int user) {
        ArrayList<SearchRequest> searchRequestsList = new ArrayList<SearchRequest>();
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            String selectQuery = "SELECT * FROM " + TABLE_SEARCH_REQUESTS + " WHERE " + KEY_USER
                    + "= ?" + " ORDER BY " + KEY_SEARCH_REQUEST_SDATETIME + " DESC LIMIT 20";

            Cursor cursor = db.rawQuery(selectQuery, new String[]{Integer.toString(user)});

            if (cursor.moveToFirst()) {
                do {
                    SearchRequest searchRequest = new SearchRequest();
                    searchRequest.setId(Integer.parseInt(cursor.getString(0)));
                    searchRequest.setUser(Integer.parseInt(cursor.getString(1)));
                    searchRequest.setSearchRequest(cursor.getString(2));
                    searchRequest.setDate(Long.parseLong(cursor.getString(3)));
                    searchRequestsList.add(searchRequest);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }
        return searchRequestsList;
    }

    @Override
    public void deleteSearchRequest(SearchRequest searchRequest) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.delete(TABLE_SEARCH_REQUESTS, KEY_ID + " = ?", new String[]{String.valueOf(searchRequest.getId())});
        } catch (Exception e) {
            Log.e(LOG_DB, e.toString());
        } finally {
            if (db != null) db.close();
        }
    }
}
