package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import com.milesmagusruber.secretserviceflickrsearch.BuildConfig;
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
    private TextView textViewFlickrResult;
    private String textSearch;


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
        textViewFlickrResult = (TextView) findViewById(R.id.flickr_result);

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
                textViewFlickrResult.setVisibility(TextView.INVISIBLE);

                //At first check network connection
                if (!networkHelper.haveNetworkConnection(FlickrSearchActivity.this)) {
                    textViewFlickrResult.setVisibility(TextView.VISIBLE);
                    textViewFlickrResult.setText(getString(R.string.turn_on_internet));
                } else if (textSearch.equals("")) {
                    textViewFlickrResult.setVisibility(TextView.VISIBLE);
                    textViewFlickrResult.setText(getString(R.string.input_search_request));

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

                            //If Response is not null making a result String that consists of photo title and urls
                            if (flickrResponse != null) {

                                List<Photo> photoList = flickrResponse.getPhotos().getPhoto();

                                for (Photo photo : photoList) {
                                    resultBuilder.append(photo.getTitle() + " : " + photo.getPhotoUrl() + "<br><br>");
                                }
                            }
                            //linkify out result string
                            makeLinksFromText(resultBuilder.toString());
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
                            textViewFlickrResult.setVisibility(TextView.VISIBLE);
                            textViewFlickrResult.setText(getString(R.string.request_error));
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


    //LinkSpan to bind our links
    private class LinkSpan extends URLSpan {
        private LinkSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View view) {
            String url = getURL();
            Intent intent = new Intent(FlickrSearchActivity.this, FlickrViewItemActivity.class);
            intent.putExtra(EXTRA_WEBLINK, url);
            intent.putExtra(EXTRA_SEARCH_REQUEST, textSearch);
            startActivity(intent);
        }
    }

    //Making our urls from Flickr result string clickable
    private void makeLinksFromText(String resultString) {

        //Linkify the TextView
        Spannable spannable = new SpannableString(Html.fromHtml(resultString));
        Linkify.addLinks(spannable, Linkify.WEB_URLS);

        //Replace each URLSpan by a LinkSpan
        URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (URLSpan urlSpan : spans) {
            LinkSpan linkSpan = new LinkSpan(urlSpan.getURL());
            int spanStart = spannable.getSpanStart(urlSpan);
            int spanEnd = spannable.getSpanEnd(urlSpan);
            spannable.setSpan(linkSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.removeSpan(urlSpan);
        }

        // Make sure the TextView supports clicking on Links
        textViewFlickrResult.setMovementMethod(LinkMovementMethod.getInstance());
        textViewFlickrResult.setText(spannable, TextView.BufferType.SPANNABLE);
        textViewFlickrResult.setVisibility(View.VISIBLE);
    }

}




