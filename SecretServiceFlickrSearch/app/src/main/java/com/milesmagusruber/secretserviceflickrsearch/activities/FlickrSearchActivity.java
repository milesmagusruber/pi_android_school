package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import com.google.android.material.button.MaterialButton;
import com.milesmagusruber.secretserviceflickrsearch.BuildConfig;
import com.milesmagusruber.secretserviceflickrsearch.adapters.PhotosAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.SearchRequest;
import com.milesmagusruber.secretserviceflickrsearch.network.NetworkHelper;
import com.milesmagusruber.secretserviceflickrsearch.network.model.FlickrResponse;
import com.milesmagusruber.secretserviceflickrsearch.network.model.Photo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FlickrSearchActivity extends AppCompatActivity {

    public static final String EXTRA_WEBLINK = BuildConfig.APPLICATION_ID + ".extra.weblink";
    public static final String EXTRA_TITLE = BuildConfig.APPLICATION_ID + ".extra.title";
    public static final String EXTRA_SEARCH_REQUEST = BuildConfig.APPLICATION_ID + ".extra.search.request";
    public static final String EXTRA_LATITUDE = BuildConfig.APPLICATION_ID + ".extra.latitude";
    public static final String EXTRA_LONGITUDE = BuildConfig.APPLICATION_ID + ".extra.longitude";


    public static final int GEO_SEARCH_REQUEST = 1;

    //Current user
    private CurrentUser currentUser;

    //last search request
    private String lastSearchRequest;

    //Declaring UI elements
    private MaterialButton buttonTextSearch;
    private MaterialButton buttonGeoSearch;
    private EditText editTextFlickrSearch;
    private ProgressBar downloadProgressBar;
    private ProgressBar scrollProgressBar;
    private TextView textViewFlickrError;
    private RecyclerView rvFlickrResult;
    private String textSearch;

    //for geo coordinates
    private boolean geoResult;
    private double geoResultLatitude;
    private double geoResultLongitude;


    //adapter for Flickr photos
    private PhotosAdapter photosAdapter;
    private int photosPage; //to know a number of pages
    private boolean photosEndReached; // to know if we reach and end of photos
    private boolean isLoading; //to know if we still load our photos
    private ItemTouchHelper itemTouchHelper; //For touch swipes
    private RecyclerView.OnScrollListener onScrollListener; //For scrolls
    private LinearLayoutManager layoutManager;


    public static final String TAG = "MainActivity";


    //Working with database;
    private DatabaseHelper db;

    //Working with Network
    private NetworkHelper networkHelper;

    //Controlling AsyncTasks in this activity
    private AsyncTask<Void, Void, Integer> asyncTask;

    //Controlling Retrofit calls in this activity
    private Call<FlickrResponse> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_search);

        networkHelper = NetworkHelper.getInstance(this);
        currentUser = CurrentUser.getInstance();
        geoResult=false;

        //Initialising UI elements
        buttonTextSearch = findViewById(R.id.button_text_search);
        buttonGeoSearch = findViewById(R.id.button_geo_search);
        editTextFlickrSearch = (EditText) findViewById(R.id.edittext_flickr_search);
        downloadProgressBar = (ProgressBar) findViewById(R.id.download_progressbar);
        rvFlickrResult = (RecyclerView) findViewById(R.id.flickr_result);
        textViewFlickrError = (TextView) findViewById(R.id.flickr_error);
        scrollProgressBar = (ProgressBar) findViewById(R.id.scroll_progressbar);
        isLoading = false;
        photosPage = 1;//number of pages is 1
        photosEndReached = false;



        //Initialize itemTouchHelper
        itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        removePhoto(position);
                    }
                });

        //Initialize onScrollListener
        onScrollListener = new RecyclerView.OnScrollListener() {
            int visibleItemCount, totalItemCount, pastVisiblesItems;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading) {
                    if (dy > 0) {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if (!photosEndReached && networkHelper.haveNetworkConnection(FlickrSearchActivity.this)) {
                                isLoading = true;
                                loadMorePhotos();
                            }
                        }
                    }
                }
            }
        };
        layoutManager = new LinearLayoutManager(this);
        //getting last search request of the user
        if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            asyncTask = new AsyncTask<Void, Void, Integer>() {
                @Override
                protected void onPreExecute() {
                    buttonTextSearch.setClickable(false);
                }

                @Override
                protected Integer doInBackground(Void... data) {
                    //Initialize SearchRequests
                    db = DatabaseHelper.getInstance(FlickrSearchActivity.this);
                    lastSearchRequest = db.getLastTextSearchRequest(currentUser.getUser().getId()).getSearchRequest();
                    db.close();
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {
                    editTextFlickrSearch.setText(lastSearchRequest);
                    buttonTextSearch.setClickable(true);
                }
            };
            asyncTask.execute();
        }

        //Main function of out app to search photos via Flickr API

        buttonGeoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(FlickrSearchActivity.this, GoogleMapsSearchActivity.class), GEO_SEARCH_REQUEST);
            }
        });

        buttonTextSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoResult=false;
                textSearch = editTextFlickrSearch.getText().toString();
                textViewFlickrError.setVisibility(View.INVISIBLE);
                rvFlickrResult.setVisibility(View.INVISIBLE);
                photosPage = 1;
                photosEndReached = false;
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
                    addSearchRequestToDB(textSearch);

                    //working with response from Flickr
                    call = networkHelper.getSearchTextQueryPhotos(textSearch, photosPage);

                    initialFlickrSearchCall(textSearch);
                }
            }
        });
    }

    //Creating main menu of our application
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Transitions between app activities via main menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent=null;
        switch(id){
            case R.id.activity_last_search_requests :
                //going to Last Search Requests
                intent = new Intent(this, LastSearchRequestsActivity.class);
                startActivity(intent);
                return true;
            case R.id.activity_favorites:
                //going to Favorites
                intent = new Intent(this, FavoritesActivity.class);
                startActivity(intent);
                return true;
            case R.id.activity_gallery:
                //going to gallery
                intent = new Intent(this, GalleryActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Using this method to get geo coords from GoogleMapsSearchActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode,resultCode,intent);
        if (requestCode == GEO_SEARCH_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (intent != null) {
                    geoResult=true;
                    geoResultLatitude=intent.getDoubleExtra(EXTRA_LATITUDE,0);
                    geoResultLongitude = intent.getDoubleExtra(EXTRA_LONGITUDE,0);
                    photosPage=1;
                    photosEndReached = false;

                    final String searchRequest=String.format(Locale.getDefault(),
                            getString(R.string.geo_snippet),
                            geoResultLatitude,
                            geoResultLongitude);

                    editTextFlickrSearch.setText("");
                    Toast.makeText(FlickrSearchActivity.this,searchRequest,Toast.LENGTH_LONG).show();
                    textViewFlickrError.setVisibility(View.INVISIBLE);
                    rvFlickrResult.setVisibility(View.INVISIBLE);
                    if (!networkHelper.haveNetworkConnection(FlickrSearchActivity.this)) {
                        textViewFlickrError.setVisibility(TextView.VISIBLE);
                        textViewFlickrError.setText(getString(R.string.turn_on_internet));
                    }else{
                        downloadProgressBar.setVisibility(ProgressBar.VISIBLE); //Making download process visible to user
                        //adding Search Request to Database
                        addSearchRequestToDB(searchRequest);

                        //working with response from Flickr
                        call = networkHelper.getSearchGeoQueryPhotos(geoResultLatitude,geoResultLongitude,photosPage);
                        initialFlickrSearchCall(searchRequest);
                    }
                }
            }
        }
    }

    //Use to show list of photos in our view
    private void showPhotos(final List<Photo> photos, final String searchRequest) {


        photosAdapter = new PhotosAdapter(photos, searchRequest, new PhotosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Photo photo) {
                //get to FlickViewItemActivity
                Intent intent = new Intent(FlickrSearchActivity.this, FlickrViewItemActivity.class);
                intent.putExtra(EXTRA_WEBLINK, photo.getPhotoUrl());
                intent.putExtra(EXTRA_TITLE, photo.getTitle());
                intent.putExtra(EXTRA_SEARCH_REQUEST, searchRequest);
                startActivity(intent);
            }

        });
        // Attach the adapter to the recyclerview to populate items
        rvFlickrResult.setAdapter(photosAdapter);
        // Set layout manager to position the items
        rvFlickrResult.setLayoutManager(layoutManager);

        // Add the functionality to swipe items in the
        // recycler view to delete that item
        itemTouchHelper.attachToRecyclerView(rvFlickrResult);

        //Add infinity scroll functionality to RecyclerView
        rvFlickrResult.addOnScrollListener(onScrollListener);


        rvFlickrResult.setVisibility(View.VISIBLE);
    }

    //This method is used with swipe to remove photo list item from view
    private void removePhoto(int position) {

        photosAdapter.removePhoto(position);
        photosAdapter.notifyItemRemoved(position);
    }

    //This method is used to add text or geo search request to database
    private void addSearchRequestToDB(final String searchRequest){
        if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            asyncTask = new AsyncTask<Void, Void, Integer>() {
                @Override
                protected void onPreExecute() {
                    buttonTextSearch.setClickable(false);
                    buttonGeoSearch.setClickable(false);
                }

                @Override
                protected Integer doInBackground(Void... voids) {
                    db = DatabaseHelper.getInstance(FlickrSearchActivity.this);
                    db.addSearchRequest(new SearchRequest(currentUser.getUser().getId(), searchRequest));
                    db.close();
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {
                    buttonTextSearch.setClickable(true);
                    buttonGeoSearch.setClickable(true);
                }
            };
            asyncTask.execute();
        }
    }

    //This method is used to process our response from the search request where we loaded first page of photos
    private void initialFlickrSearchCall(final String searchRequest){
        call.enqueue(new Callback<FlickrResponse>() {
            @Override
            public void onResponse(Call<FlickrResponse> call, Response<FlickrResponse> response) {
                FlickrResponse flickrResponse = response.body();

                List<Photo> photos = null;
                //If Response is not null making a result list of photos
                if (flickrResponse != null) {

                    photos = flickrResponse.getPhotos().getPhoto();

                }
                //If photos not null show them
                if (photos != null && !photos.isEmpty()) {
                    showPhotos(photos,searchRequest);
                    photosPage++;
                } else {
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

    //This method is used when we load more pages of the same photo search request (text or geo)
    private void loadMorePhotos() {


        scrollProgressBar.setVisibility(View.VISIBLE);

        //working with new page response from Flickr
        //Log.d(TAG,"Page number before:"+photosPage);

        if(geoResult!=true) {
            call = networkHelper.getSearchTextQueryPhotos(textSearch, photosPage);
        }else{
            call = networkHelper.getSearchGeoQueryPhotos(geoResultLatitude,geoResultLongitude, photosPage);
        }


        call.enqueue(new Callback<FlickrResponse>() {
            @Override
            public void onResponse(Call<FlickrResponse> call, Response<FlickrResponse> response) {
                FlickrResponse flickrResponse = response.body();

                List<Photo> photos = null;
                //If Response is not null making a result list of photos
                if (flickrResponse != null) {

                    photos = flickrResponse.getPhotos().getPhoto();

                }
                //Log.d(TAG,photos.toString());
                //If photos not null show them
                if (photos != null && !photos.isEmpty()) {
                    photosAdapter.addNewPhotos(photos);
                    photosPage++;

                    //Log.d(TAG,"Page number after:"+photosPage);
                } else {
                    photosEndReached = true;
                }
                //disabling download bar
                scrollProgressBar.setVisibility(View.INVISIBLE);
                isLoading = false;

            }

            //If we fail then set an error string to textview
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<FlickrResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error");
                Log.e(TAG, t.toString());
                scrollProgressBar.setVisibility(ProgressBar.INVISIBLE);
                isLoading = false;
            }
        });

    }

}




