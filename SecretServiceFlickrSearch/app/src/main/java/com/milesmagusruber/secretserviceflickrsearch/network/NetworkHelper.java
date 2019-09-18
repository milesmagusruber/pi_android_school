package com.milesmagusruber.secretserviceflickrsearch.network;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.network.model.FlickrResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetworkHelper {
    //RetrofitAPI contants
    private final String BASE_URL = "https://api.flickr.com/services/";
    private final String METHOD_NAME = "flickr.photos.search";
    private final String FORMAT = "json";
    private final int NO_JSON_CALL_BACK = 1;
    private final String EXTRAS = "url_s";

    //Our Flickr API Key
    private String flickrApiKey;

    //Retrofit API client for server requests
    private RetrofitAPI retrofitAPIclient;


    private static NetworkHelper instance;

    private NetworkHelper(Context context){
        //getting Flickr API key
        flickrApiKey = context.getResources().getString(R.string.flickr_api_key);
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

        retrofitAPIclient = retrofit.create(RetrofitAPI.class);

    }

    public static NetworkHelper getInstance(Context context){
        if (instance == null) {
            instance = new NetworkHelper(context);
        }
        return instance;
    }

    //checking network connection
    public boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    //getting search query response from Flickr
    public Call<FlickrResponse> getSearchTextQueryPhotos(String textSearch, int page){
        return retrofitAPIclient.getSearchTextQueryPhotos(METHOD_NAME, flickrApiKey,
                FORMAT, NO_JSON_CALL_BACK, EXTRAS, textSearch, page);
    }

    public Call<FlickrResponse> getSearchGeoQueryPhotos(double latitude, double longitude, int page){
        return retrofitAPIclient.getSearchGeoQueryPhotos(METHOD_NAME, flickrApiKey,
                FORMAT, NO_JSON_CALL_BACK, EXTRAS, latitude, longitude, page);
    }

}
