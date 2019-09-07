package com.milesmagusruber.secretserviceflickrsearch.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.DatabaseHelper;
import com.milesmagusruber.secretserviceflickrsearch.db.model.Favorite;
import com.milesmagusruber.secretserviceflickrsearch.fs.FileHelper;

import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_SEARCH_REQUEST;
import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_TITLE;
import static com.milesmagusruber.secretserviceflickrsearch.activities.FlickrSearchActivity.EXTRA_WEBLINK;

public class FlickrViewItemActivity extends AppCompatActivity {

    static final int REQUEST_STORAGE = 62;
    //Current user
    private CurrentUser currentUser;
    //Current Favorite
    private Favorite favorite;

    private WebView webViewFlickrItem; //WebView representation
    private TextView textViewSearchRequestItem; //Search Request
    private Button buttonIsFavorite; //Button that checks if Flickr image is favorite
    private Button buttonIsSaved; // Button that checks if Flickr image saved on device
    private DatabaseHelper db;
    private String searchRequest;
    private String title;
    private String webLink;
    private String fileName;
    private boolean isFavorite = false;
    private boolean isSaved = false;

    //Controlling asyncTasks in this activity
    private AsyncTask<Void, Void, Integer> asyncTask;


    //Permissions
    private String[] permissions;

    //FileHelper
    private FileHelper fileHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_view_item);
        currentUser = CurrentUser.getInstance();

        webViewFlickrItem = (WebView) findViewById(R.id.webview_flickr_item);
        textViewSearchRequestItem = (TextView) findViewById(R.id.search_request_item);
        buttonIsFavorite = (Button) findViewById(R.id.button_is_favorite);
        buttonIsSaved = (Button) findViewById(R.id.button_is_saved);


        //Search Request that was used to find image
        searchRequest = getIntent().getStringExtra(EXTRA_SEARCH_REQUEST);
        webLink = getIntent().getStringExtra(EXTRA_WEBLINK);
        title = getIntent().getStringExtra(EXTRA_TITLE);

        //get name of server file from weblink
        getFileNameFromWebLink();

        //checking permissions
        permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //checkingGalleryPermissions
        if (checkStoragePermissions()) {
            initializeSavingFlickrFiles();
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
        }


        textViewSearchRequestItem.setText(searchRequest);
        //finding image in favorites
        if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            asyncTask = new AsyncTask<Void, Void, Integer>() {
                @Override
                protected void onPreExecute() {
                    buttonIsFavorite.setClickable(false);
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
                    buttonIsFavorite.setClickable(true);
                    if (favorite != null) {
                        buttonIsFavorite.setBackgroundResource(R.drawable.ic_star_favorite);
                        isFavorite = true;
                    } else {
                        buttonIsFavorite.setBackgroundResource(R.drawable.ic_star_not_favorite);
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

        buttonIsFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asyncTask.getStatus() != AsyncTask.Status.RUNNING) {
                    if (!isFavorite) {

                        asyncTask = new AsyncTask<Void, Void, Integer>() {
                            @Override
                            protected void onPreExecute() {
                                buttonIsFavorite.setClickable(false);
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
                                buttonIsFavorite.setClickable(true);
                                buttonIsFavorite.setBackgroundResource(R.drawable.ic_star_favorite);
                                isFavorite = true;
                            }
                        };
                        asyncTask.execute();

                    } else {

                        asyncTask = new AsyncTask<Void, Void, Integer>() {

                            @Override
                            protected void onPreExecute() {
                                buttonIsFavorite.setClickable(false);
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
                                buttonIsFavorite.setClickable(true);
                                buttonIsFavorite.setBackgroundResource(R.drawable.ic_star_not_favorite);
                                isFavorite = false;
                            }
                        };
                        asyncTask.execute();
                    }
                }

            }
        });

    }

    //getting filename for web url
    private void getFileNameFromWebLink() {
        fileName = webLink.substring(webLink.lastIndexOf('/') + 1, webLink.length());
    }

    private boolean checkStoragePermissions() {
        boolean result = true;
        for (String perm : permissions) {
            result = result && (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED);
        }
        return result;
    }

    //If we have permissions for camera and storage camera button will work
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    initializeSavingFlickrFiles();

                } else {
                    Toast.makeText(this, "Having problems with permission requests!!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void activateButtonIsSaved() {
        buttonIsSaved.setVisibility(View.VISIBLE);
        buttonIsSaved.setClickable(true);
        //checking if we've already saved flickr file
        isSaved = fileHelper.isFlickrPhotoSaved(fileName);
        if (isSaved) {
            buttonIsSaved.setBackgroundResource(R.drawable.ic_file_saved);
        }

        //Button that saves Flickr image to the local device
        buttonIsSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSaved) {
                    buttonIsSaved.setBackgroundResource(R.drawable.ic_file_saved);
                    //loading image to file with glide
                    Glide.with(FlickrViewItemActivity.this)
                            .asBitmap()
                            .load(webLink)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                    fileHelper.addFlickrPhoto(fileName, resource);
                                }
                            });
                    isSaved = true;
                } else {
                    buttonIsSaved.setBackgroundResource(R.drawable.ic_file_not_saved);
                    fileHelper.deleteFlickrPhoto(fileName);
                    isSaved = false;
                }
            }
        });
    }

    private void initializeSavingFlickrFiles() {
        currentUser.setFileHelper(this);
        fileHelper = currentUser.getFileHelper();
        activateButtonIsSaved();
    }

}
