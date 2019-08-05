package com.milesmagusruber.secretserviceflickrsearch;

import com.milesmagusruber.secretserviceflickrsearch.model.FlickrResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI {
    // не используется, встречается два раза
    String BASE_URL = "https://api.flickr.com/services/";
    // параметр methodName можно убрать из заголовка - навание функции предполагает, что он не будет меняться
    // api_key, format, nojsoncallback - то же самое, их можно не передавать в функцию а просто подставлять
    @GET("rest")
    Call<FlickrResponse> getSearchQueryPhotos(@Query("method") String methodName, @Query("api_key") String API_KEY, @Query("format") String format, @Query("nojsoncallback") int value, @Query("extras") String urlS,@Query("text") String userSearchText);
}
