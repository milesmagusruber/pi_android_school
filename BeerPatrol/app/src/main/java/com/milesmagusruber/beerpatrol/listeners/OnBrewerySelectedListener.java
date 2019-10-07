package com.milesmagusruber.beerpatrol.listeners;

import com.milesmagusruber.beerpatrol.network.model.BreweryLocation;

//this interface is used for choosing brewery
public interface OnBrewerySelectedListener {
    //method that is implemented by MainActivity if we choose brewery in recycler view
    void onBrewerySelected(BreweryLocation selectedBreweryLocation);
}
