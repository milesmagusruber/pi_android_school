package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.milesmagusruber.secretserviceflickrsearch.BuildConfig;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.broadcast_receivers.PowerReceiver;
import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.fragments.FavoritesFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.FlickrSearchFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.FlickrViewItemFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.GalleryFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.GalleryViewItemFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.GoogleMapsSearchFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.LastSearchRequestsFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.LoginFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.SettingsFragment;
import com.milesmagusruber.secretserviceflickrsearch.fs.FileHelper;
import com.milesmagusruber.secretserviceflickrsearch.listeners.OnPhotoSelectedListener;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import static com.milesmagusruber.secretserviceflickrsearch.fragments.SettingsFragment.KEY_THEME;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener,
        GoogleMapsSearchFragment.MapFragmentListener, OnPhotoSelectedListener, GalleryFragment.OnTakePhotoListener {

    //Navigation Drawer variables
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private FrameLayout detailContainer;

    //Working with fragments
    private FragmentManager fragmentManager;

    public static final int REQUEST_IMAGE_CAPTURE = 30;
    /**
     * Whether or not the activity is in two-pane mode,
     * i.e. running on a tablet device.
     */
    private boolean twoPaneMode = false;

    //use when we need to hide container
    LinearLayout.LayoutParams layoutParamHideContainer=new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            0
    );

    //use when we need to show container
    LinearLayout.LayoutParams layoutParamShowContainer=new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            2
    );;


    private String currentPhotoPath = "";

    //Power Broadcast Receiver
    private PowerReceiver powerReceiver = new PowerReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_SSFS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //setting requested orientation for smartphone or wide screen device (such as tablet)
        if (findViewById(R.id.fragment_container_master) != null) {
            twoPaneMode = true;
            detailContainer = findViewById(R.id.fragment_container_detail);
            detailContainer.setLayoutParams(layoutParamHideContainer);
        }
        if (twoPaneMode) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        // Find our drawer view
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.main_nav_view);


        // Register the power receiver using the activity context.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(powerReceiver, filter);


        fragmentManager = getSupportFragmentManager();
        Fragment fragment = LoginFragment.newInstance();
        nvDrawer.setVisibility(View.INVISIBLE);
        //user Initialization
        if(!twoPaneMode){
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }else{
            fragmentManager.beginTransaction().replace(R.id.fragment_container_master, fragment).commit();
        }
    }

    @Override
    protected void onResume() {
        //setting day or night themes
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(KEY_THEME, false)) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onResume();
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
        try {
            switch (menuItem.getItemId()) {
                case R.id.nav_flickr_search_fragment:
                    fragment = FlickrSearchFragment.newInstance();
                    break;
                case R.id.nav_geo_search_fragment:
                    fragment = GoogleMapsSearchFragment.newInstance();
                    break;
                case R.id.nav_last_search_requests_fragment:
                    fragment = LastSearchRequestsFragment.newInstance();
                    break;
                case R.id.nav_favorites_fragment:
                    fragment = FavoritesFragment.newInstance();
                    break;
                case R.id.nav_gallery_fragment:
                    fragment = GalleryFragment.newInstance();
                    break;
                case R.id.nav_settings_fragment:
                    fragment = new SettingsFragment();
                    break;
                case R.id.nav_login_fragment:
                    fragment = new LoginFragment();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Insert the fragment by replacing any existing fragment
        if (!twoPaneMode) {
            changeFragment(fragment);
        } else {
            changeFragmentTwoPaneModeMaster(fragment);
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Close the navigation drawer
        drawer.closeDrawers();

    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);

    }


    /*if we get result from camera go to uCrop activity
     * if we get result from uCrop activity show image in imageView*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // add your code here
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d("FILETT", Integer.toString(resultCode));
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //If we get photo from camera
            Log.d("FILETT", "Camera returns image");
            Uri uri = Uri.parse(currentPhotoPath);
            openCropActivity(uri, uri);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {

        }

    }

    //this method is used to process photo with UCrop library
    public void openCropActivity(Uri sourceUri, Uri destinationUri) {
        int maxWidth = 1600;
        int maxHeight = 1600;
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(maxWidth, maxHeight)
                .withAspectRatio(5f, 5f)
                .start(this);
    }


    /*authorization process when we press Enter Button in LoginFragment
     * setting up Navigation Drawer*/
    @Override
    public void onLoginButtonEnter() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        drawerToggle = setupDrawerToggle();
        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
        // Tie DrawerLayout events to the ActionBarToggle
        drawer.addDrawerListener(drawerToggle);

        // Setup drawer view

        setupDrawerContent(nvDrawer);
        if (!twoPaneMode) {
            changeFragment(FlickrSearchFragment.newInstance());
        } else {
            changeFragmentTwoPaneModeMaster(FlickrSearchFragment.newInstance());
        }

    }

    //Getting rid of Navigation Drawer if we go to LoginFragment and clean stack of fragments
    @Override
    public void getRidOfNavigationDrawer() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    //If the place on the map is selected transfer latitude and longitude to FlickrSearchFragment
    @Override
    public void onPlaceSelected(double latitude, double longitude) {
        if (!twoPaneMode) {
            changeFragment(FlickrSearchFragment.newInstance(latitude, longitude));
        } else {
            changeFragmentTwoPaneModeMaster(FlickrSearchFragment.newInstance(latitude, longitude));
        }
    }

    //If we selected Flickr Photo in FlickrSearchFragment or FavoritesFragment
    @Override
    public void onFlickrPhotoSelected(String searchRequest, String webLink, String title) {
        if (!twoPaneMode) {
            changeFragment(FlickrViewItemFragment.newInstance(searchRequest, webLink, title));
        } else {
            changeFragmentTwoPaneModeDetail(FlickrViewItemFragment.newInstance(searchRequest, webLink, title));
        }
    }

    //If we selected photo file in gallery fragment
    @Override
    public void onPhotoFileSelected(String filePath) {
        if (!twoPaneMode) {
            changeFragment(GalleryViewItemFragment.newInstance(filePath));
        } else {
            changeFragmentTwoPaneModeDetail(GalleryViewItemFragment.newInstance(filePath));
        }
    }

    //taking photo with camera
    @Override
    public void onTakePhoto() {

        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            FileHelper fileHelper = CurrentUser.getInstance().getFileHelper();
            File file = fileHelper.createUserPhotoFile();

            currentPhotoPath = "file:" + file.getAbsolutePath();
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".fileprovider"), file);
            else
                uri = Uri.fromFile(file);
            Log.d("FILETT", uri.toString());
            pictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (NullPointerException e) {
            Toast.makeText(this, R.string.problem_with_filesystem, Toast.LENGTH_LONG).show();
        }

    }

    //Changing fragments in small screen devices such as smartphones
    public void changeFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                fragment)
                .addToBackStack(null).commit();
    }

    //Changing master fragment in wide screen devices such as tablet
    public void changeFragmentTwoPaneModeMaster(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.fragment_container_master,
                fragment)
                .addToBackStack(null).commit();
        if((fragment instanceof FlickrSearchFragment)||(fragment instanceof FavoritesFragment)||(fragment instanceof GalleryFragment)){
            detailContainer.setLayoutParams(layoutParamShowContainer);
        }else{
            detailContainer.setLayoutParams(layoutParamHideContainer);
        }
    }

    //Changing detail fragment in wide screen devices such as tablet
    public void changeFragmentTwoPaneModeDetail(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.fragment_container_detail,
                fragment).commit();
    }
}
