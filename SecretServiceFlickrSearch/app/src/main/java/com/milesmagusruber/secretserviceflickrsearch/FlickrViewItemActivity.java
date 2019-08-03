package com.milesmagusruber.secretserviceflickrsearch;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static com.milesmagusruber.secretserviceflickrsearch.FlickrSearchActivity.EXTRA_WEBLINK;

public class FlickrViewItemActivity extends AppCompatActivity {

    private WebView webViewFlickrItem; //WebView representation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_view_item);
        webViewFlickrItem = (WebView) findViewById(R.id.webview_flickr_item);

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

        //Getting our URL from FlickrSearchActivity
        String webLink = getIntent().getStringExtra(EXTRA_WEBLINK);

        //Loading our image
        webViewFlickrItem.loadUrl(webLink);
    }

}
