package com.milesmagusruber.beerpatrol.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.milesmagusruber.beerpatrol.R;
import com.milesmagusruber.beerpatrol.network.model.Beer;
import com.milesmagusruber.beerpatrol.network.model.BreweryLocation;
import com.milesmagusruber.beerpatrol.network.model.BreweryDBResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetworkHelper {

    //RetrofitAPI constants
    private final String BASE_URL = "https://sandbox-api.brewerydb.com/v2/";

    private final String UNIT_KM = "km";
    private final String UNIT_MI = "mi";

    private final String QUERY_TYPE = "beer";

    //Our BreweryDB API Key
    private String breweryDBApiKey;

    //Retrofit API client for server requests
    private RetrofitAPI retrofitAPIclient;

    private static NetworkHelper instance;

    private NetworkHelper(Context context) {
        //getting BreweryDB API key
        breweryDBApiKey = context.getResources().getString(R.string.brewerydb_api_key);
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

    public static NetworkHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkHelper(context);
        }
        return instance;
    }

    //Checking network connection
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

    //getting all beers filter by name parameter as name=*<nameFilter>*
    public Call<BreweryDBResponse<Beer>> getBeersBySearchQuery(String query, int page) {
        return retrofitAPIclient.getBeersBySearchQuery(breweryDBApiKey, QUERY_TYPE, query, page);
    }

    //getting all beers by id of the brewery
    public Call<BreweryDBResponse<Beer>> getBeersByBreweryId(String breweryId) {

        return retrofitAPIclient.getBeersByBreweryId(breweryId, breweryDBApiKey);
    }

    public Call<BreweryDBResponse<BreweryLocation>> getBreweriesByLocation(double lat, double lng, double radius, int page) {
        return retrofitAPIclient.getBreweriesByLocation(breweryDBApiKey, lat, lng, radius, UNIT_KM, page);
    }


}
