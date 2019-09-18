package com.milesmagusruber.secretserviceflickrsearch.network;

import com.milesmagusruber.secretserviceflickrsearch.network.model.FlickrResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI {

    //Search request where we use text value for search
    @GET("rest")
    Call<FlickrResponse> getSearchTextQueryPhotos(@Query("method") String methodName, @Query("api_key") String API_KEY, @Query("format") String format, @Query("nojsoncallback") int value, @Query("extras") String urlS,@Query("text") String userSearchText,@Query("page") int page);

    //Search request where we use geographical coordinates
    @GET("rest")
    Call<FlickrResponse> getSearchGeoQueryPhotos(@Query("method") String methodName, @Query("api_key") String API_KEY, @Query("format") String format, @Query("nojsoncallback") int value, @Query("extras") String urlS,@Query("lat") double lat, @Query("lon") double lon, @Query("page") int page);

}
