package com.milesmagusruber.secretserviceflickrsearch.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.milesmagusruber.secretserviceflickrsearch.activities.LoginActivity;
import com.milesmagusruber.secretserviceflickrsearch.db.model.Favorite;
import com.milesmagusruber.secretserviceflickrsearch.db.model.SearchRequest;
import com.milesmagusruber.secretserviceflickrsearch.db.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper implements IDatabaseHandler{

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

    private DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //creating table users
        String createUsers="CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_LOGIN + " TEXT UNIQUE"
                + ");";
        db.execSQL(createUsers);

        //creating table favorites
        String createFavorites="CREATE TABLE " + TABLE_FAVORITES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER + " INTEGER,"
                + KEY_SEARCH_REQUEST + " TEXT,"+ KEY_FAVORITE_TITLE + " TEXT,"
                + KEY_FAVORITE_WEB_LINK + " TEXT UNIQUE);";
                /*+ "FOREIGN KEY("+KEY_USER+") REFERENCES "+TABLE_USERS+"("+KEY_ID + ")"+"); ");*/
        db.execSQL(createFavorites);

        //creating table search requests
        String createSearchRequests="CREATE TABLE " + TABLE_SEARCH_REQUESTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER + " INTEGER,"
                + KEY_SEARCH_REQUEST + " TEXT,"+ KEY_SEARCH_REQUEST_SDATETIME + " TEXT); ";
                /*+ "FOREIGN KEY("+KEY_USER+") REFERENCES "+TABLE_USERS+"("+KEY_ID + ")"+"); ");*/
        db.execSQL(createSearchRequests);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        StringBuilder dropTablesQueryBuiler = new StringBuilder();
        dropTablesQueryBuiler.append("DROP TABLE IF EXISTS " + TABLE_USERS + ";");
        dropTablesQueryBuiler.append("DROP TABLE IF EXISTS " + TABLE_FAVORITES +";");
        dropTablesQueryBuiler.append("DROP TABLE IF EXISTS " + TABLE_SEARCH_REQUESTS +";");
        db.execSQL(dropTablesQueryBuiler.toString());
        onCreate(db);
    }

    //Working with users

    //add user
    @Override
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_LOGIN, user.getLogin());
        db.insert(TABLE_USERS, null, values);
        db.close();
    }


    @Override
    public User getUser(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID,
                        KEY_USER_LOGIN }, KEY_USER_LOGIN + "=?",
                new String[] { String.valueOf(login) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        User user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
        cursor.close();
        db.close();
        return user;
    }

    //Working with favorites
    @Override
    public void addFavorite(Favorite favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER, favorite.getUser());
        values.put(KEY_SEARCH_REQUEST, favorite.getSearchRequest());
        values.put(KEY_FAVORITE_TITLE, favorite.getTitle());
        values.put(KEY_FAVORITE_WEB_LINK, favorite.getWebLink());
        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    @Override
    public Favorite getFavorite(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID,
                        KEY_USER_LOGIN }, KEY_USER_LOGIN + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        Favorite favorite = new Favorite(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
                cursor.getString(2),cursor.getString(3),cursor.getString(4));
        cursor.close();
        db.close();
        return favorite;
    }

    @Override
    public ArrayList<Favorite> getAllFavorites(int user, String searchRequest) {
        ArrayList<Favorite> favoritesList = new ArrayList<Favorite>();
        String selectQuery=null;
        if(searchRequest==null){
        selectQuery = "SELECT * FROM " + TABLE_FAVORITES+" WHERE "+KEY_USER
                +"= "+user;
        }else{
            selectQuery ="SELECT * FROM " + TABLE_FAVORITES+" WHERE "+KEY_USER
                    +"="+user+" AND "+KEY_SEARCH_REQUEST+"=\'"+searchRequest+"\'";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
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
        db.close();
        return favoritesList;
    }

    @Override
    public int updateFavorite(Favorite favorite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SEARCH_REQUEST, favorite.getSearchRequest());

        db.close();

        return db.update(TABLE_FAVORITES, values, KEY_FAVORITE_WEB_LINK + " = ?",
                new String[] { String.valueOf(favorite.getWebLink()) });

    }

    @Override
    public void deleteFavorite(Favorite favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, KEY_FAVORITE_WEB_LINK + " = ?", new String[] { String.valueOf(favorite.getWebLink())});
        db.close();
    }

    //Working with search requests
    @Override
    public void addSearchRequest(SearchRequest searchRequest) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER, searchRequest.getUser());
        values.put(KEY_SEARCH_REQUEST, searchRequest.getSearchRequest());
        values.put(KEY_SEARCH_REQUEST_SDATETIME, System.currentTimeMillis());
        db.insert(TABLE_SEARCH_REQUESTS, null, values);
        db.close();
    }

    @Override
    public SearchRequest getSearchRequest(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[] { KEY_ID,
                        KEY_USER_LOGIN }, KEY_USER_LOGIN + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        SearchRequest searchRequest = new SearchRequest(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)),
                cursor.getString(2),Long.parseLong(cursor.getString(3)));
        cursor.close();
        db.close();
        return searchRequest;
    }

    @Override
    public ArrayList<SearchRequest> getAllSearchRequests(int user) {
        ArrayList<SearchRequest> searchRequestsList = new ArrayList<SearchRequest>();
        String selectQuery = "SELECT * FROM " + TABLE_SEARCH_REQUESTS+" WHERE "+KEY_USER
                + "=" + user + " ORDER BY " + KEY_SEARCH_REQUEST_SDATETIME + " DESC LIMIT 20";
        Log.d("MOtherFucker",selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                SearchRequest searchRequest = new SearchRequest();
                searchRequest.setId(Integer.parseInt(cursor.getString(0)));
                searchRequest.setUser(Integer.parseInt(cursor.getString(1)));
                searchRequest.setSearchRequest(cursor.getString(2));
                searchRequest.setDate(Long.parseLong(cursor.getString(3)));
                searchRequestsList.add(searchRequest);
                Log.d("MotherFucker",Long.toString(searchRequest.getDateTime()));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return searchRequestsList;
    }

    @Override
    public void deleteSearchRequest(SearchRequest searchRequest) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SEARCH_REQUESTS, KEY_ID + " = ?", new String[] { String.valueOf(searchRequest.getId()) });
        db.close();
    }
}
