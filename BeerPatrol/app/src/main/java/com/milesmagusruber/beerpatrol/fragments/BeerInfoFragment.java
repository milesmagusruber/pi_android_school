package com.milesmagusruber.beerpatrol.fragments;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.milesmagusruber.beerpatrol.BuildConfig;
import com.milesmagusruber.beerpatrol.R;
import com.milesmagusruber.beerpatrol.db.BeerPatrolDatabase;
import com.milesmagusruber.beerpatrol.db.entities.FavoriteBeer;
import com.milesmagusruber.beerpatrol.network.model.Beer;

/**
 * A simple {@link Fragment} subclass.
 */
public class BeerInfoFragment extends Fragment {

    private final static String EXTRA_BEER_INFO_BEERID = BuildConfig.APPLICATION_ID + ".beerinfo.beerid";
    private final static String EXTRA_BEER_INFO_NAME = BuildConfig.APPLICATION_ID + ".beerinfo.name";
    private final static String EXTRA_BEER_INFO_CATEGORY = BuildConfig.APPLICATION_ID + ".beerinfo.category";
    private final static String EXTRA_BEER_INFO_GLASS = BuildConfig.APPLICATION_ID + ".beerinfo.glass";
    private final static String EXTRA_BEER_INFO_ABV = BuildConfig.APPLICATION_ID + ".beerinfo.abv";
    private final static String EXTRA_BEER_INFO_IBU = BuildConfig.APPLICATION_ID + ".beerinfo.ibu";
    private final static String EXTRA_BEER_INFO_DESCRIPTION = BuildConfig.APPLICATION_ID + ".beerinfo.description";
    private final static String EXTRA_BEER_INFO_ICON = BuildConfig.APPLICATION_ID + ".beerinfo.icon";
    private final static String EXTRA_BEER_INFO_IMAGE = BuildConfig.APPLICATION_ID + ".beerinfo.image";

    //Beer parameters
    private String beerId = null;
    private String name = null;
    private String category = null;
    private String glass = null;
    private String abv = null;
    private String ibu = null;
    private String description = null;
    private String icon = null;
    private String image = null;

    //UI elements
    private ImageView beerInfoImage;
    private MaterialTextView beerInfoCategory;
    private MaterialTextView beerInfoGlass;
    private MaterialTextView beerInfoABV;
    private MaterialTextView beerInfoIBU;
    private MaterialTextView beerInfoDescription;
    private Toolbar toolbar;
    private MenuItem dbMenuItem;

    //Working with database FavoriteBeer
    //Room Database
    private BeerPatrolDatabase db;
    //Controlling database asyncTasks in this activity
    private AsyncTask<Void, Void, Integer> dbAsyncTask;
    //isAddedToDB
    private boolean isFavoriteBeer = false;
    //Current Favorite
    private FavoriteBeer favoriteBeer = null;

    private BeerInfoFragment() {
    }

    public static BeerInfoFragment newInstance(Beer beer) {
        BeerInfoFragment fragment = new BeerInfoFragment();
        Bundle bundle = new Bundle();

        //beer id from brewerydb
        if (beer.id != null) {
            bundle.putString(EXTRA_BEER_INFO_BEERID, beer.id);
        }

        //beer name
        if (beer.name != null) {
            bundle.putString(EXTRA_BEER_INFO_NAME, beer.name);
        }

        //beer category name
        if (beer.style != null) {
            if (beer.style.category != null) {
                bundle.putString(EXTRA_BEER_INFO_CATEGORY, beer.style.category.name);
            }
        }

        //beer used glass
        if (beer.glass != null) {
            bundle.putString(EXTRA_BEER_INFO_GLASS, beer.glass.name);
        }

        //beer abv
        if (beer.abv != null) {
            bundle.putString(EXTRA_BEER_INFO_ABV, beer.abv);
        }

        //beer ibu
        if (beer.ibu != null) {
            bundle.putString(EXTRA_BEER_INFO_IBU, beer.ibu);
        }

        //beer description
        if (beer.description != null) {
            bundle.putString(EXTRA_BEER_INFO_DESCRIPTION, beer.description);
        }

        //beer icon and large image
        if (beer.labels != null) {
            if (beer.labels.icon != null) {
                bundle.putString(EXTRA_BEER_INFO_ICON, beer.labels.icon);
            }
            if (beer.labels.large != null) {
                bundle.putString(EXTRA_BEER_INFO_IMAGE, beer.labels.large);
            }
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    public static BeerInfoFragment newInstance(FavoriteBeer favoriteBeer) {
        BeerInfoFragment fragment = new BeerInfoFragment();
        Bundle bundle = new Bundle();

        //beer id from brewerydb
        bundle.putString(EXTRA_BEER_INFO_BEERID, favoriteBeer.getBeerId());

        //beer name
        bundle.putString(EXTRA_BEER_INFO_NAME, favoriteBeer.getName());

        //beer category name
        bundle.putString(EXTRA_BEER_INFO_CATEGORY, favoriteBeer.getCategory());

        //beer used glass
        bundle.putString(EXTRA_BEER_INFO_GLASS, favoriteBeer.getGlass());

        //beer abv
        bundle.putString(EXTRA_BEER_INFO_ABV, favoriteBeer.getAbv());

        //beer ibu
        bundle.putString(EXTRA_BEER_INFO_IBU, favoriteBeer.getIbu());

        //beer description
        bundle.putString(EXTRA_BEER_INFO_DESCRIPTION, favoriteBeer.getDescription());

        //beer icon and large image
        bundle.putString(EXTRA_BEER_INFO_ICON, favoriteBeer.getIcon());
        bundle.putString(EXTRA_BEER_INFO_IMAGE, favoriteBeer.getImage());

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beer_info, container, false);
        if (getArguments() != null) {
            //getting all beer parameters
            beerId = getArguments().getString(EXTRA_BEER_INFO_BEERID);
            name = getArguments().getString(EXTRA_BEER_INFO_NAME);
            category = getArguments().getString(EXTRA_BEER_INFO_CATEGORY);
            glass = getArguments().getString(EXTRA_BEER_INFO_GLASS);
            abv = getArguments().getString(EXTRA_BEER_INFO_ABV);
            ibu = getArguments().getString(EXTRA_BEER_INFO_IBU);
            description = getArguments().getString(EXTRA_BEER_INFO_DESCRIPTION);
            icon = getArguments().getString(EXTRA_BEER_INFO_ICON);
            image = getArguments().getString(EXTRA_BEER_INFO_IMAGE);
        }

        //setting all UI elements
        beerInfoImage = view.findViewById(R.id.beer_info_image);
        beerInfoCategory = view.findViewById(R.id.beer_info_category);
        beerInfoGlass = view.findViewById(R.id.beer_info_glass);
        beerInfoABV = view.findViewById(R.id.beer_info_abv);
        beerInfoIBU = view.findViewById(R.id.beer_info_ibu);
        beerInfoDescription = view.findViewById(R.id.beer_info_description);
        toolbar = view.findViewById(R.id.toolbar);

        setToolbar();
        setBeerInformation();

        return view;
    }

    //setting all beer information
    private void setBeerInformation() {
        //setting category
        if (category != null) {
            beerInfoCategory.setText(category);
        } else {
            beerInfoCategory.setText(getResources().getString(R.string.beer_info_no_category));
        }

        //setting glass
        if (glass != null) {
            beerInfoGlass.setText(getResources().getString(R.string.beer_info_glass, glass));
        } else {
            beerInfoGlass.setText(getResources().getString(R.string.beer_info_no_glass));
        }

        //setting abv
        if (abv != null) {
            beerInfoABV.setText(getResources().getString(R.string.beer_info_abv, abv));
        } else {
            beerInfoABV.setText(getResources().getString(R.string.beer_info_no_abv));
        }

        //setting ibu
        if (ibu != null) {
            beerInfoIBU.setText(getResources().getString(R.string.beer_info_ibu, ibu));
        } else {
            beerInfoIBU.setText(getResources().getString(R.string.beer_info_no_ibu));
        }

        //setting description
        if (description != null) {
            beerInfoDescription.setText(description);
        } else {
            beerInfoDescription.setText(getResources().getString(R.string.beer_info_no_description));
        }

        //setting image
        if (image != null) {
            Glide.with(getActivity()).load(image).into(beerInfoImage);
        } else {
            Glide.with(getActivity()).load(R.drawable.beer_placeholder).into(beerInfoImage);
        }

    }

    private void setToolbar() {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(name);
            toolbar.setTitleTextColor(getContext().getResources().getColor(R.color.colorElement));

            setHasOptionsMenu(true);
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.favorite_beer_menu_item, menu);
        dbMenuItem = menu.findItem(R.id.action_db);
        //checking if our beer is added to database
        getFavoriteBeer();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_db:
                dbWork();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //working with database
    private void dbWork() {
        if (!isFavoriteBeer) {
            insertFavoriteBeer();
        } else {
            deleteFavoriteBeer();
        }
    }

    //get favorite beer from database
    private void getFavoriteBeer() {

        if ((dbAsyncTask == null) || (dbAsyncTask.getStatus() != AsyncTask.Status.RUNNING)) {

            dbAsyncTask = new AsyncTask<Void, Void, Integer>() {
                @Override
                protected void onPreExecute() {
                    dbMenuItem.setCheckable(false);
                }

                @Override
                protected Integer doInBackground(Void... data) {
                    //Getting Favorite Beer
                    db = db.getInstance(getActivity());
                    favoriteBeer = db.favoriteBeerDao().getByBeerId(beerId);
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {
                    dbMenuItem.setCheckable(true);
                    if (favoriteBeer != null) {
                        dbMenuItem.setIcon(R.drawable.ic_favorite);
                        isFavoriteBeer = true;
                    } else {
                        dbMenuItem.setIcon(R.drawable.ic_favorite_not);
                        isFavoriteBeer = false;
                    }
                }
            };
            dbAsyncTask.execute();
        }
    }

    //insert favorite beer into database
    private void insertFavoriteBeer() {
        if ((dbAsyncTask == null) || (dbAsyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            dbAsyncTask = new AsyncTask<Void, Void, Integer>() {
                @Override
                protected void onPreExecute() {
                    dbMenuItem.setCheckable(false);
                }

                @Override
                protected Integer doInBackground(Void... voids) {
                    db = db.getInstance(getActivity());
                    db.favoriteBeerDao().insert(new FavoriteBeer(beerId, name, category, glass, abv,
                            ibu, description, icon, image));
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {
                    dbMenuItem.setCheckable(true);
                    dbMenuItem.setIcon(R.drawable.ic_favorite);
                    isFavoriteBeer = true;
                }
            };
            dbAsyncTask.execute();
        }
    }

    //delete favorite beer from database
    private void deleteFavoriteBeer() {
        if ((dbAsyncTask == null) || (dbAsyncTask.getStatus() != AsyncTask.Status.RUNNING)) {

            dbAsyncTask = new AsyncTask<Void, Void, Integer>() {

                @Override
                protected void onPreExecute() {
                    dbMenuItem.setCheckable(false);
                }

                @Override
                protected Integer doInBackground(Void... voids) {
                    db = db.getInstance(getActivity());
                    db.favoriteBeerDao().delete(new FavoriteBeer(beerId, name, category, glass, abv,
                            ibu, description, icon, image));
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {
                    dbMenuItem.setCheckable(true);
                    dbMenuItem.setIcon(R.drawable.ic_favorite_not);
                    isFavoriteBeer = false;
                }
            };
            dbAsyncTask.execute();
        }
    }

}
