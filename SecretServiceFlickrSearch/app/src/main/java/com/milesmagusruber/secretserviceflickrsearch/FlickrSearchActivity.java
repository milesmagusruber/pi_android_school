package com.milesmagusruber.secretserviceflickrsearch;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.concurrent.TimeUnit;

import com.milesmagusruber.secretserviceflickrsearch.model.FlickrResponse;
import com.milesmagusruber.secretserviceflickrsearch.model.Photo;
import com.milesmagusruber.secretserviceflickrsearch.model.Photos;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FlickrSearchActivity extends AppCompatActivity {

    public static final String EXTRA_WEBLINK = BuildConfig.APPLICATION_ID + ".extra.weblink";

    //Declaring UI elements
    private Button buttonSearch;
    private EditText editTextFlickrSearch;
    private ProgressBar downloadProgressBar;
    private TextView textViewFlickrResult;

    //Declaring API Key
    private String flickrApiKey;

    private String BASE_URL = "https://api.flickr.com/services/";
    public static final String TAG = "MainActivity";

    //Retrofit API Consts
    private static String METHOD_NAME = "flickr.photos.search";
    private static String FORMAT = "json";
    private static int NO_JSON_CALL_BACK = 1;
    private static String EXTRAS = "url_s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_search);

        flickrApiKey = getResources().getString(R.string.flickr_api_key); //flickr api key

        //Initialising UI elements
        buttonSearch = (Button) findViewById(R.id.button_search);
        editTextFlickrSearch = (EditText) findViewById(R.id.edittext_flickr_search);
        downloadProgressBar = (ProgressBar) findViewById(R.id.download_progressbar);
        textViewFlickrResult = (TextView) findViewById(R.id.flickr_result);

        //Main function of out app to search photos via Flickr API
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String textSearch = editTextFlickrSearch.getText().toString();
                textViewFlickrResult.setVisibility(TextView.INVISIBLE);

                //At first check network connection
                if (!haveNetworkConnection()) {
                    textViewFlickrResult.setVisibility(TextView.VISIBLE);
                    textViewFlickrResult.setText(getString(R.string.turn_on_internet));
                } else {
                    downloadProgressBar.setVisibility(ProgressBar.VISIBLE); //Making download process visible to user

                    //Creating OkHttpClient
                    OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
                    builder.readTimeout(10, TimeUnit.SECONDS);
                    builder.connectTimeout(5, TimeUnit.SECONDS);

                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
                    builder.addInterceptor(interceptor);
                    OkHttpClient okHttpClient = builder.build();

                    //Using Retrofit
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    final RetrofitAPI client = retrofit.create(RetrofitAPI.class);
                    Call<FlickrResponse> call = client.getSearchQueryPhotos(METHOD_NAME, flickrApiKey, FORMAT, NO_JSON_CALL_BACK, EXTRAS, textSearch);


                    call.enqueue(new Callback<FlickrResponse>() {
                        @Override
                        public void onResponse(Call<FlickrResponse> call, Response<FlickrResponse> response) {
                            FlickrResponse mFlickrResponse = response.body();
                            StringBuilder resultBuilder = new StringBuilder();

                            //If Response is not null making a result String that consists of photo title and urls
                            if (mFlickrResponse != null) {
                                Photos photos = mFlickrResponse.getPhotos();
                                List<Photo> photoList = photos.getPhoto();
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
            startActivity(intent);
        }
    }

    //checking network connection
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
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




