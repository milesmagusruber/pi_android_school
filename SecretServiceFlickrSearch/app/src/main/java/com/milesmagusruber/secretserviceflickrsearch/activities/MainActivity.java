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
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.milesmagusruber.secretserviceflickrsearch.db.SSFSDatabase;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.RequestedPhoto;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.User;
import com.milesmagusruber.secretserviceflickrsearch.fragments.FavoritesFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.FlickrSearchFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.FlickrViewItemFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.GalleryFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.GalleryViewItemFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.GoogleMapsSearchFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.LastSearchRequestsFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.LoginFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.RequestedPhotosFragment;
import com.milesmagusruber.secretserviceflickrsearch.fragments.SettingsFragment;
import com.milesmagusruber.secretserviceflickrsearch.fs.FileHelper;
import com.milesmagusruber.secretserviceflickrsearch.listeners.OnPhotoSelectedListener;
import com.milesmagusruber.secretserviceflickrsearch.workers.BackgroundPhotoUpdatesWorker;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import static com.milesmagusruber.secretserviceflickrsearch.fragments.SettingsFragment.DEFAULT_KEY_INTERVAL;
import static com.milesmagusruber.secretserviceflickrsearch.fragments.SettingsFragment.DEFAULT_KEY_SEARCH_REQUEST;
import static com.milesmagusruber.secretserviceflickrsearch.fragments.SettingsFragment.KEY_ALLOW_UPDATES;
import static com.milesmagusruber.secretserviceflickrsearch.fragments.SettingsFragment.KEY_INTERVAL;
import static com.milesmagusruber.secretserviceflickrsearch.fragments.SettingsFragment.KEY_SEARCH_REQUEST;
import static com.milesmagusruber.secretserviceflickrsearch.fragments.SettingsFragment.KEY_THEME;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener,
        GoogleMapsSearchFragment.MapFragmentListener, OnPhotoSelectedListener, GalleryFragment.OnTakePhotoListener {

    public static final String CURRENT_USER="current_user";

    //Navigation Drawer variables
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private FrameLayout detailContainer;

    //Working with fragments
    private FragmentManager fragmentManager;

    //Working with SharedPreferences
    private SharedPreferences sharedPreferences;

    //Working with db
    private SSFSDatabase db;

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

        //getting shared preferences
        sharedPreferences=getPreferences(MODE_PRIVATE);


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
        nvDrawer.setVisibility(View.INVISIBLE);

        // Register the power receiver using the activity context.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(powerReceiver, filter);


        fragmentManager = getSupportFragmentManager();

        //checking if we're logged up
        String currentLogin=sharedPreferences.getString(CURRENT_USER,"");
        if(!currentLogin.equals("")){
            //getting user from database
            AsyncTask<String,Void,Boolean> asyncTask=new AsyncTask<String, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(String... data) {
                    String login=data[0];
                    db = db.getInstance(MainActivity.this);
                    User user = db.userDao().getUser(login);
                    CurrentUser currentUser = CurrentUser.getInstance();
                    currentUser.setUser(user);
                    return true;
                }
            };
            asyncTask.execute(currentLogin);
            authorize();
        }else{
            Fragment fragment = LoginFragment.newInstance();

            //user Initialization
            if(!twoPaneMode){
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }else{
                fragmentManager.beginTransaction().replace(R.id.fragment_container_master, fragment).commit();
            }
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
                case R.id.nav_requested_photos_fragment:
                    fragment = RequestedPhotosFragment.newInstance();
                    break;
                case R.id.nav_gallery_fragment:
                    fragment = GalleryFragment.newInstance();
                    break;
                case R.id.nav_settings_fragment:
                    fragment = new SettingsFragment();
                    break;
                case R.id.nav_login_fragment:
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fragment = LoginFragment.newInstance();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Insert the fragment by replacing any existing fragment
        if (!twoPaneMode) {
            changeFragment(fragment,true);
        } else {
            changeFragmentTwoPaneModeMaster(fragment,true);
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

    //when we press Enter Button in LoginFragment
    @Override
    public void onLoginButtonEnter(){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_USER, CurrentUser.getInstance().getUser().getLogin());
        editor.commit();

        authorize();
    }

    /*Authorization process
     * setting up Navigation Drawer*/

    public void authorize() {

        //Setting support bar
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

        Fragment fragment=null;

        //checking if we entered into app from notification
        String menuFragment = getIntent().getStringExtra("menuFragment");
        if(menuFragment!=null && menuFragment.equals("requestedPhotosFragment")){
            fragment=RequestedPhotosFragment.newInstance();
        }else{
            fragment=FlickrSearchFragment.newInstance();
        }

        //checking for twoPaneMode
        if (!twoPaneMode) {
            changeFragment(fragment,false);
        } else {
            changeFragmentTwoPaneModeMaster(fragment,false);
        }

    }

    //Getting rid of Navigation Drawer if we go to LoginFragment and clean stack of fragments
    @Override
    public void onLogOut() {
        //removing current user from application
        sharedPreferences.edit().remove(CURRENT_USER).commit();
        SharedPreferences defSharPref= PreferenceManager.getDefaultSharedPreferences(this);
        defSharPref.edit().putBoolean(KEY_ALLOW_UPDATES,false).commit();
        defSharPref.edit().putString(KEY_SEARCH_REQUEST,DEFAULT_KEY_SEARCH_REQUEST).commit();
        defSharPref.edit().putString(KEY_INTERVAL,DEFAULT_KEY_INTERVAL).commit();
        //canceling all workers
        WorkManager workManager = WorkManager.getInstance();
        workManager.cancelAllWork();

        //deleting all requestedPhotos from database
        AsyncTask<Void,Void,Boolean> asyncTask=new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                db = db.getInstance(MainActivity.this);
                db.requestedPhotoDao().deleteAll();
                return true;
            }
        };
        asyncTask.execute();

        //deleting current user from singelone CurrentUser
        CurrentUser.getInstance().setUser(null);

        //removing extra from pending intent
        getIntent().removeExtra("menuFragment");

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }

    //If the place on the map is selected transfer latitude and longitude to FlickrSearchFragment
    @Override
    public void onPlaceSelected(double latitude, double longitude) {
        if (!twoPaneMode) {
            changeFragment(FlickrSearchFragment.newInstance(latitude, longitude),true);
        } else {
            changeFragmentTwoPaneModeMaster(FlickrSearchFragment.newInstance(latitude, longitude),true);
        }
    }

    //If we selected Flickr Photo in FlickrSearchFragment or FavoritesFragment
    @Override
    public void onFlickrPhotoSelected(String searchRequest, String webLink, String title) {
        if (!twoPaneMode) {
            changeFragment(FlickrViewItemFragment.newInstance(searchRequest, webLink, title),true);
        } else {
            changeFragmentTwoPaneModeDetail(FlickrViewItemFragment.newInstance(searchRequest, webLink, title));
        }
    }

    //If we selected photo file in gallery fragment
    @Override
    public void onPhotoFileSelected(String filePath) {
        if (!twoPaneMode) {
            changeFragment(GalleryViewItemFragment.newInstance(filePath),true);
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
    public void changeFragment(Fragment fragment, boolean isAddedToBackStack) {
        if(isAddedToBackStack){
        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                fragment)
                .addToBackStack(null).commit();
        }else{
            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                    fragment).commit();
        }
    }

    //Changing master fragment in wide screen devices such as tablet
    public void changeFragmentTwoPaneModeMaster(Fragment fragment, boolean isAddedToBackStack) {
        if(isAddedToBackStack){
        fragmentManager.beginTransaction().replace(R.id.fragment_container_master,
                fragment)
                .addToBackStack(null).commit();
        }else{
            fragmentManager.beginTransaction().replace(R.id.fragment_container_master,
                    fragment).commit();
        }
        if((fragment instanceof FlickrSearchFragment)||(fragment instanceof FavoritesFragment)||(fragment instanceof GalleryFragment)
        || (fragment instanceof RequestedPhotosFragment)){
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
