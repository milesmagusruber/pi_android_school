package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.broadcast_receivers.PowerReceiver;
import com.milesmagusruber.secretserviceflickrsearch.fragments.TestFragment1;

public class MainActivity extends AppCompatActivity {

    //Navigation Drawer variables
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    /**
     * Whether or not the activity is in two-pane mode,
     * i.e. running on a tablet device.
     */
    private boolean twoPaneMode = false;

    //Power Broadcast Receiver
    private PowerReceiver powerReceiver = new PowerReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting requested orientation for smartphone or wide screen device (such as tablet)
        if(findViewById(R.id.fragment_container_master)!=null){
            twoPaneMode=true;
        }
        if(twoPaneMode){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else{
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        // This will display an Up icon (<-), we will replace it with hamburger later
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        // Tie DrawerLayout events to the ActionBarToggle
        drawer.addDrawerListener(drawerToggle);


        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.main_nav_view);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Register the power receiver using the activity context.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(powerReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        //Unregister the receiver
        this.unregisterReceiver(powerReceiver);
        super.onDestroy();
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Method is used to setup drawer content
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    //Method is used to select drawer item
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_flickr_search_fragment:
                fragmentClass = TestFragment1.class;
                break;
            case R.id.nav_geo_search_fragment:
                fragmentClass = TestFragment1.class;
                break;
            case R.id.nav_last_search_requests_fragment:
                fragmentClass = TestFragment1.class;
                break;
            default:
                fragmentClass = TestFragment1.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(!twoPaneMode) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }else{
            fragmentManager.beginTransaction().replace(R.id.fragment_container_master,fragment).commit();
            try {
                Fragment fragmentSecond = (Fragment) TestFragment1.class.newInstance();
                fragmentManager.beginTransaction().replace(R.id.fragment_container_detail,fragmentSecond).commit();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawer.closeDrawers();

    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open,  R.string.drawer_close);

    }


}
