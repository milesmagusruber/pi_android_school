package com.milesmagusruber.beerpatrol.network;

import com.milesmagusruber.beerpatrol.network.model.Beer;
import com.milesmagusruber.beerpatrol.network.model.BreweryLocation;
import com.milesmagusruber.beerpatrol.network.model.BreweryDBResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {

    //getting beers
    @GET("search")
    Call<BreweryDBResponse<Beer>> getBeersBySearchQuery(@Query("key") String apiKey,  @Query("type") String type, @Query("q") String query, @Query("p") int page);

    //getting all beers produced by brewery
    @GET("brewery/{breweryId}/beers")
    Call<BreweryDBResponse<Beer>> getBeersByBreweryId(@Path("breweryId") String breweryId, @Query("key") String apiKey);

    //getting breweries by location
    @GET("search/geo/point")
    Call<BreweryDBResponse<BreweryLocation>> getBreweriesByLocation(@Query("key") String apiKey, @Query("lat") double lat, @Query("lng") double lng, @Query("radius") double radius, @Query("unit") String unit, @Query("p") int page);

}
