package com.milesmagusruber.secretserviceflickrsearch.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.Favorite;
import com.milesmagusruber.secretserviceflickrsearch.db.model.SearchRequest;

import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_SEARCH_REQUEST;
import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_WEBLINK;

public class FlickrViewItemActivity extends AppCompatActivity {

    private WebView webViewFlickrItem; //WebView representation
    private TextView textViewSearchRequestItem; //Search Request
    private Button buttonLike; //Button
    private DatabaseHelper db;
    private String searchRequest;
    private String webLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_view_item);
        webViewFlickrItem = (WebView) findViewById(R.id.webview_flickr_item);
        textViewSearchRequestItem = (TextView) findViewById(R.id.search_request_item);
        buttonLike = (Button) findViewById(R.id.button_like);

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

        //Search Request that was used to find image
        searchRequest=getIntent().getStringExtra(EXTRA_SEARCH_REQUEST);
        webLink = getIntent().getStringExtra(EXTRA_WEBLINK);
        textViewSearchRequestItem.setText(searchRequest);

        //Loading our image
        webViewFlickrItem.loadUrl(webLink);
        //Going to favorites
        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = new DatabaseHelper(FlickrViewItemActivity.this);
                db.addFavorite(new Favorite(1,searchRequest,"",webLink));
                db.close();
            }
        });
    }

}
