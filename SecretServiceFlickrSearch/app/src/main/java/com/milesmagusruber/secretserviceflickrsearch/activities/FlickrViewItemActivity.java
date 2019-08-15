package com.milesmagusruber.secretserviceflickrsearch.activities;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.SearchRequestsAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.Favorite;

import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_SEARCH_REQUEST;
import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_WEBLINK;
import static com.milesmagusruber.secretserviceflickrsearch.activities.LoginActivity.EXTRA_CURRENT_USER;

public class FlickrViewItemActivity extends AppCompatActivity {

    //Current user
    private int currentUser;

    //Current Favorite
    private Favorite favorite;

    private WebView webViewFlickrItem; //WebView representation
    private TextView textViewSearchRequestItem; //Search Request
    private Button buttonLike; //Button
    private DatabaseHelper db;
    private String searchRequest;
    private String webLink;
    private boolean isFavorite=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_view_item);
        currentUser = getIntent().getIntExtra(EXTRA_CURRENT_USER,1);
        webViewFlickrItem = (WebView) findViewById(R.id.webview_flickr_item);
        textViewSearchRequestItem = (TextView) findViewById(R.id.search_request_item);
        buttonLike = (Button) findViewById(R.id.button_like);


        //Search Request that was used to find image
        searchRequest=getIntent().getStringExtra(EXTRA_SEARCH_REQUEST);
        webLink = getIntent().getStringExtra(EXTRA_WEBLINK);
        textViewSearchRequestItem.setText(searchRequest);

        //finding image in favorites
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... data) {
                //Initialize SearchRequests
                db = DatabaseHelper.getInstance(FlickrViewItemActivity.this);
                favorite = db.getFavorite(currentUser,webLink);
                db.close();
                return 0;
            }

            @Override
            protected void onPostExecute(Integer a) {
                if (favorite !=null){
                    buttonLike.setBackgroundResource(R.drawable.ic_star_favorite);
                    isFavorite=true;
                }else{
                    buttonLike.setBackgroundResource(R.drawable.ic_star_not_favorite);
                    isFavorite=false;
                }
            }
        }.execute();


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
                if(!isFavorite){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db = DatabaseHelper.getInstance(FlickrViewItemActivity.this);
                            db.addFavorite(new Favorite(currentUser,searchRequest,"",webLink));
                            db.close();
                        }
                    }).start();
                    buttonLike.setBackgroundResource(R.drawable.ic_star_favorite);
                    isFavorite=true;
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db = DatabaseHelper.getInstance(FlickrViewItemActivity.this);
                            db.deleteFavorite(new Favorite(currentUser,searchRequest,"",webLink));
                            db.close();
                        }
                    }).start();
                    buttonLike.setBackgroundResource(R.drawable.ic_star_not_favorite);
                    isFavorite=false;
                }

            }
        });
    }

}
