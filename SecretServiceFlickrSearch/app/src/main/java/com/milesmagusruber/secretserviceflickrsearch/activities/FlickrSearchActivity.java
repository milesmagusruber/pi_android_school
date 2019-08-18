package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import com.milesmagusruber.secretserviceflickrsearch.BuildConfig;
import com.milesmagusruber.secretserviceflickrsearch.adapters.PhotosAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.SearchRequest;
import com.milesmagusruber.secretserviceflickrsearch.network.NetworkHelper;
import com.milesmagusruber.secretserviceflickrsearch.model.FlickrResponse;
import com.milesmagusruber.secretserviceflickrsearch.model.Photo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FlickrSearchActivity extends AppCompatActivity {

    public static final String EXTRA_WEBLINK = BuildConfig.APPLICATION_ID + ".extra.weblink";
    public static final String EXTRA_SEARCH_REQUEST = BuildConfig.APPLICATION_ID + ".extra.search.request";


    //Current user
    private CurrentUser currentUser;

    //last search request
    private String lastSearchRequest;

    //Declaring UI elements
    private Button buttonSearch;
    private Button buttonFavorites;
    private Button buttonLastSearchRequests;
    private EditText editTextFlickrSearch;
    private ProgressBar downloadProgressBar;
    private TextView textViewFlickrError;
    private RecyclerView rvFlickrResult;
    private String textSearch;

    //adapter for Flickr photos
    private PhotosAdapter photosAdapter;


    public static final String TAG = "MainActivity";


    //Working with database;
    private DatabaseHelper db;

    //Working with Network
    private NetworkHelper networkHelper;

    //Controlling AsyncTasks in this activity
    private AsyncTask<Void, Void, Integer> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_search);

        networkHelper = NetworkHelper.getInstance();
        currentUser = CurrentUser.getInstance();


        //Initialising UI elements
        buttonSearch = (Button) findViewById(R.id.button_search);
        buttonFavorites = (Button) findViewById(R.id.button_favorites);
        buttonLastSearchRequests = (Button) findViewById(R.id.button_last_search_requests);
        editTextFlickrSearch = (EditText) findViewById(R.id.edittext_flickr_search);
        downloadProgressBar = (ProgressBar) findViewById(R.id.download_progressbar);
        rvFlickrResult = (RecyclerView) findViewById(R.id.flickr_result);
        textViewFlickrError = (TextView) findViewById(R.id.flickr_error);
        //getting last search request of the user
        if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            asyncTask = new AsyncTask<Void, Void, Integer>() {
                @Override
                protected void onPreExecute() {
                    buttonSearch.setClickable(false);
                }

                @Override
                protected Integer doInBackground(Void... data) {
                    //Initialize SearchRequests
                    db = DatabaseHelper.getInstance(FlickrSearchActivity.this);
                    lastSearchRequest = db.getLastSearchRequest(currentUser.getUser().getId()).getSearchRequest();
                    db.close();
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {
                    editTextFlickrSearch.setText(lastSearchRequest);
                    buttonSearch.setClickable(true);
                }
            };
            asyncTask.execute();
        }

        //Main function of out app to search photos via Flickr API

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSearch = editTextFlickrSearch.getText().toString();
                textViewFlickrError.setVisibility(View.INVISIBLE);
                rvFlickrResult.setVisibility(View.INVISIBLE);

                //At first check network connection
                if (!networkHelper.haveNetworkConnection(FlickrSearchActivity.this)) {
                    textViewFlickrError.setVisibility(TextView.VISIBLE);
                    textViewFlickrError.setText(getString(R.string.turn_on_internet));
                } else if (textSearch.equals("")) {
                    textViewFlickrError.setVisibility(TextView.VISIBLE);
                    textViewFlickrError.setText(getString(R.string.input_search_request));

                } else {
                    downloadProgressBar.setVisibility(ProgressBar.VISIBLE); //Making download process visible to user

                    //adding Search Request to Database
                    if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
                        asyncTask = new AsyncTask<Void, Void, Integer>() {
                            @Override
                            protected void onPreExecute() {
                                buttonSearch.setClickable(false);
                            }

                            @Override
                            protected Integer doInBackground(Void... voids) {
                                db = DatabaseHelper.getInstance(FlickrSearchActivity.this);
                                db.addSearchRequest(new SearchRequest(currentUser.getUser().getId(), textSearch));
                                db.close();
                                return 0;
                            }

                            @Override
                            protected void onPostExecute(Integer a) {
                                buttonSearch.setClickable(true);
                            }
                        };
                        asyncTask.execute();
                    }

                    //working with response from Flickr
                    Call<FlickrResponse> call = networkHelper.getSearchQueryPhotos(FlickrSearchActivity.this, textSearch);

                    call.enqueue(new Callback<FlickrResponse>() {
                        @Override
                        public void onResponse(Call<FlickrResponse> call, Response<FlickrResponse> response) {
                            FlickrResponse flickrResponse = response.body();
                            StringBuilder resultBuilder = new StringBuilder();

                            List<Photo> photos = null;
                            //If Response is not null making a result list of photos
                            if (flickrResponse != null) {

                                photos = flickrResponse.getPhotos().getPhoto();

                            }
                            //If photos not null show them
                            if(photos!=null){
                             showPhotos(photos);
                            }else{
                                textViewFlickrError.setVisibility(View.VISIBLE);
                                textViewFlickrError.setText(R.string.search_request_no_photos);
                            }
                            //disabling download bar

                            downloadProgressBar.setVisibility(View.INVISIBLE);

                        }

                        //If we fail then set an error string to textview
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onFailure(Call<FlickrResponse> call, Throwable t) {
                            Log.e(TAG, "onFailure: Error");
                            Log.e(TAG, t.toString());
                            downloadProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            textViewFlickrError.setVisibility(TextView.VISIBLE);
                            textViewFlickrError.setText(getString(R.string.request_error));
                        }
                    });
                }
            }
        });

        //Going to last search requests
        buttonLastSearchRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FlickrSearchActivity.this, LastSearchRequestsActivity.class);
                startActivity(intent);
            }
        });

        //Going to favorites
        buttonFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FlickrSearchActivity.this, FavoritesActivity.class);
                startActivity(intent);
            }
        });
    }


    private void showPhotos(List<Photo> photos){


        photosAdapter = new PhotosAdapter(photos, textSearch, new PhotosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Photo photo) {
                //get to Favorite Flick Photo from FavoritesActivity
                Intent intent = new Intent(FlickrSearchActivity.this, FlickrViewItemActivity.class);
                intent.putExtra(EXTRA_WEBLINK, photo.getPhotoUrl());
                intent.putExtra(EXTRA_SEARCH_REQUEST, textSearch);
                startActivity(intent);
            }

        });
        // Attach the adapter to the recyclerview to populate items
        rvFlickrResult.setAdapter(photosAdapter);
        // Set layout manager to position the items
        rvFlickrResult.setLayoutManager(new LinearLayoutManager(FlickrSearchActivity.this));

        rvFlickrResult.setVisibility(View.VISIBLE);
    }


}




