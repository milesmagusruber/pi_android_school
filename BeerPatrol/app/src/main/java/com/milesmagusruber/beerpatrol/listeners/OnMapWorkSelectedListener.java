package com.milesmagusruber.beerpatrol.listeners;

//this interface is used to work with map
public interface OnMapWorkSelectedListener {

    //return latitude and longitude to BreweriesFragment
    void onPlaceSelected(double latitude, double longitude);

    //go from BreweriesFragment to MapLocationFragment
    void onUseMap();
}
