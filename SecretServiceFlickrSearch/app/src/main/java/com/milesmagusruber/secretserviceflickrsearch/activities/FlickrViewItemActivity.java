package com.milesmagusruber.secretserviceflickrsearch.activities;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.Favorite;

import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_SEARCH_REQUEST;
import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_TITLE;
import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_WEBLINK;

public class FlickrViewItemActivity extends AppCompatActivity {

    //Current user
    private CurrentUser currentUser;
    //Current Favorite
    private Favorite favorite;

    private WebView webViewFlickrItem; //WebView representation
    private TextView textViewSearchRequestItem; //Search Request
    private Button buttonLike; //Button
    private DatabaseHelper db;
    private String searchRequest;
    private String title;
    private String webLink;
    private boolean isFavorite = false;

    //Controlling asyncTasks in this activity
    private AsyncTask<Void, Void, Integer> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_view_item);
        currentUser = CurrentUser.getInstance();
        webViewFlickrItem = (WebView) findViewById(R.id.webview_flickr_item);
        textViewSearchRequestItem = (TextView) findViewById(R.id.search_request_item);
        buttonLike = (Button) findViewById(R.id.button_like);


        //Search Request that was used to find image
        searchRequest = getIntent().getStringExtra(EXTRA_SEARCH_REQUEST);
        webLink = getIntent().getStringExtra(EXTRA_WEBLINK);
        title = getIntent().getStringExtra(EXTRA_TITLE);
        textViewSearchRequestItem.setText(searchRequest);

        //finding image in favorites
        if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            asyncTask = new AsyncTask<Void, Void, Integer>() {
                @Override
                protected void onPreExecute() {
                    buttonLike.setClickable(false);
                }

                @Override
                protected Integer doInBackground(Void... data) {
                    //Initialize SearchRequests
                    db = DatabaseHelper.getInstance(FlickrViewItemActivity.this);
                    favorite = db.getFavorite(currentUser.getUser().getId(), webLink);
                    db.close();
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {
                    buttonLike.setClickable(true);
                    if (favorite != null) {
                        buttonLike.setBackgroundResource(R.drawable.ic_star_favorite);
                        isFavorite = true;
                    } else {
                        buttonLike.setBackgroundResource(R.drawable.ic_star_not_favorite);
                        isFavorite = false;
                    }
                }
            };
            asyncTask.execute();
        }


        // Make sure we handle clicked links ourselves
        webViewFlickrItem.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // we handle the url ourselves if it a network url (http / https)
                return !URLUtil.isNetworkUrl(url);
            }
        });

        //Enabling JS and Zoom control
        webViewFlickrItem.getSettings().setJavaScriptEnabled(true);
        webViewFlickrItem.getSettings().setSupportZoom(true);
        webViewFlickrItem.getSettings().setBuiltInZoomControls(true);

        //Loading our image
        webViewFlickrItem.loadUrl(webLink);

        //Adding to favorites or deleting from favorites

        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asyncTask.getStatus() != AsyncTask.Status.RUNNING) {
                    if (!isFavorite) {

                        asyncTask = new AsyncTask<Void, Void, Integer>() {
                            @Override
                            protected void onPreExecute() {
                                buttonLike.setClickable(false);
                            }

                            @Override
                            protected Integer doInBackground(Void... voids) {
                                db = DatabaseHelper.getInstance(FlickrViewItemActivity.this);
                                db.addFavorite(new Favorite(currentUser.getUser().getId(), searchRequest, title, webLink));
                                db.close();
                                return 0;
                            }

                            @Override
                            protected void onPostExecute(Integer a) {
                                buttonLike.setClickable(true);
                                buttonLike.setBackgroundResource(R.drawable.ic_star_favorite);
                                isFavorite = true;
                            }
                        };
                        asyncTask.execute();

                    } else {

                        asyncTask = new AsyncTask<Void, Void, Integer>() {

                            @Override
                            protected void onPreExecute() {
                                buttonLike.setClickable(false);
                            }

                            @Override
                            protected Integer doInBackground(Void... voids) {
                                db = DatabaseHelper.getInstance(FlickrViewItemActivity.this);
                                db.deleteFavorite(new Favorite(currentUser.getUser().getId(), searchRequest, title, webLink));
                                db.close();
                                return 0;
                            }

                            @Override
                            protected void onPostExecute(Integer a) {
                                buttonLike.setClickable(true);
                                buttonLike.setBackgroundResource(R.drawable.ic_star_not_favorite);
                                isFavorite = false;
                            }
                        };
                        asyncTask.execute();
                    }
                }

            }
        });
    }

}
