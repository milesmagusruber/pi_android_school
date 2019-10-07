package com.milesmagusruber.beerpatrol.listeners;

import com.milesmagusruber.beerpatrol.db.entities.FavoriteBeer;
import com.milesmagusruber.beerpatrol.network.model.Beer;

//this interface is used for choosing beer
public interface OnBeerSelectedListener {
    //method that is implemented by MainActivity if we choose beer in recycler view
    void onBeerSelected(Beer selectedBeer);

    //method that is implemented by MainActivity if we choose favorite beer in recycler view
    void onBeerSelected(FavoriteBeer selectedBeer);

}
