package com.milesmagusruber.beerpatrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.milesmagusruber.beerpatrol.db.entities.FavoriteBeer;
import com.milesmagusruber.beerpatrol.fragments.BeerInfoFragment;
import com.milesmagusruber.beerpatrol.fragments.BeerSearchFragment;
import com.milesmagusruber.beerpatrol.fragments.BreweriesFragment;
import com.milesmagusruber.beerpatrol.fragments.BreweryInfoFragment;
import com.milesmagusruber.beerpatrol.fragments.FavoriteBeersFragment;
import com.milesmagusruber.beerpatrol.fragments.MapLocationFragment;
import com.milesmagusruber.beerpatrol.listeners.OnBeerSelectedListener;
import com.milesmagusruber.beerpatrol.listeners.OnBrewerySelectedListener;
import com.milesmagusruber.beerpatrol.listeners.OnMapWorkSelectedListener;
import com.milesmagusruber.beerpatrol.listeners.OnUseMapInBreweries;
import com.milesmagusruber.beerpatrol.network.model.Beer;
import com.milesmagusruber.beerpatrol.network.model.BreweryLocation;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements OnBeerSelectedListener,
        OnBrewerySelectedListener, OnMapWorkSelectedListener {

    final Fragment beerSearchFragment = BeerSearchFragment.newInstance();
    final Fragment breweriesFragment = BreweriesFragment.newInstance();
    final Fragment favoriteBeersFragment = FavoriteBeersFragment.newInstance();

    private BottomNavigationView bottomNavigation;

    final FragmentManager fragmentManager = getSupportFragmentManager();

    //fragments stack
    LinkedList<Fragment> activeFragments = new LinkedList<>();

    private OnUseMapInBreweries breweriesListener ;

    public void setBreweriesListener(OnUseMapInBreweries listener)
    {
        breweriesListener = listener ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BeerPatrolTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activeFragments.push(beerSearchFragment);
        //bottom navigation view
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        //listener for user's choice
        BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    //choosing beer search fragment
                    case R.id.bottom_nav_beer_search:
                        fragmentManager.beginTransaction().hide(activeFragments.peek()).show(beerSearchFragment).commit();
                        activeFragments.pop();
                        activeFragments.push(beerSearchFragment);
                        return true;

                    //choosing breweries fragment
                    case R.id.bottom_nav_breweries:
                        fragmentManager.beginTransaction().hide(activeFragments.peek()).show(breweriesFragment).commit();
                        activeFragments.pop();
                        activeFragments.push(breweriesFragment);
                        return true;

                    //choosing favorite beers fragment
                    case R.id.bottom_nav_favorite_beers:
                        fragmentManager.beginTransaction().hide(activeFragments.peek()).show(favoriteBeersFragment).commit();
                        activeFragments.pop();
                        activeFragments.push(favoriteBeersFragment);
                        return true;
                }
                return false;
            }
        };

        bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //setting up fragments
        fragmentManager.beginTransaction().add(R.id.fragment_container, favoriteBeersFragment)
                .hide(favoriteBeersFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_container, breweriesFragment)
                .hide(breweriesFragment).commit();
        setBreweriesListener((OnUseMapInBreweries) breweriesFragment);
        fragmentManager.beginTransaction().add(R.id.fragment_container,beerSearchFragment)
                .commit();
    }

    //if beer selected in BeerSearchFragment
    @Override
    public void onBeerSelected(Beer selectedBeer) {
        Fragment fragment = BeerInfoFragment.newInstance(selectedBeer);
        fragmentManager.beginTransaction().add(R.id.fragment_container,fragment).addToBackStack(null)
                .commit();
        fragmentManager.beginTransaction().hide(activeFragments.peek()).show(fragment).commit();
        activeFragments.push(fragment);
        bottomNavigation.setVisibility(View.GONE);
    }

    //if favorite beer selected in FavoriteBeersFragment
    @Override
    public void onBeerSelected(FavoriteBeer selectedBeer) {
        Fragment fragment = BeerInfoFragment.newInstance(selectedBeer);
        fragmentManager.beginTransaction().add(R.id.fragment_container,fragment).addToBackStack(null)
                .commit();
        fragmentManager.beginTransaction().hide(activeFragments.peek()).show(fragment).commit();
        activeFragments.push(fragment);
        bottomNavigation.setVisibility(View.GONE);
    }

    //if brewery selected in BreweriesFragment
    @Override
    public void onBrewerySelected(BreweryLocation selectedBreweryLocation) {
        Fragment fragment = BreweryInfoFragment.newInstance(selectedBreweryLocation);
        fragmentManager.beginTransaction().add(R.id.fragment_container,fragment).addToBackStack(null)
                .commit();
        fragmentManager.beginTransaction().hide(activeFragments.peek()).show(fragment).commit();
        activeFragments.push(fragment);
        bottomNavigation.setVisibility(View.GONE);
    }

    //if user want to select location on Google Map in BreweriesFragment
    @Override
    public void onUseMap() {
        Fragment fragment = MapLocationFragment.newInstance();
        fragmentManager.beginTransaction().add(R.id.fragment_container,fragment).addToBackStack(null)
                .commit();
        fragmentManager.beginTransaction().hide(activeFragments.peek()).show(fragment).commit();
        activeFragments.push(fragment);
        bottomNavigation.setVisibility(View.GONE);
    }

    //return coordinates from MapLocationFragment back to BreweriesFragment
    @Override
    public void onPlaceSelected(double latitude, double longitude) {
        onBackPressed();
        breweriesListener.useMapCoordinates(latitude,longitude);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        activeFragments.pop();
        fragmentManager.beginTransaction().show(activeFragments.peek()).commit();
        if(activeFragments.size()==1) {
            bottomNavigation.setVisibility(View.VISIBLE);
        }
    }




}
