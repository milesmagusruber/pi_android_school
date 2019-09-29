package com.milesmagusruber.secretserviceflickrsearch.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.google.android.material.textfield.TextInputEditText;
import com.milesmagusruber.secretserviceflickrsearch.BuildConfig;
import com.milesmagusruber.secretserviceflickrsearch.adapters.PhotosAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.CurrentUser;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.SSFSDatabase;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.SearchRequest;
import com.milesmagusruber.secretserviceflickrsearch.listeners.OnPhotoSelectedListener;
import com.milesmagusruber.secretserviceflickrsearch.network.NetworkHelper;
import com.milesmagusruber.secretserviceflickrsearch.network.model.FlickrResponse;
import com.milesmagusruber.secretserviceflickrsearch.network.model.Photo;

//importing retrofit libraries
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//importing rx libraries
import com.jakewharton.rxbinding3.widget.RxTextView;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;


public class FlickrSearchFragment extends Fragment {

    public static final String EXTRA_WEBLINK = BuildConfig.APPLICATION_ID + ".extra.weblink";
    public static final String EXTRA_TITLE = BuildConfig.APPLICATION_ID + ".extra.title";
    public static final String EXTRA_SEARCH_REQUEST = BuildConfig.APPLICATION_ID + ".extra.search.request";
    public static final String EXTRA_LATITUDE = BuildConfig.APPLICATION_ID + ".extra.latitude";
    public static final String EXTRA_LONGITUDE = BuildConfig.APPLICATION_ID + ".extra.longitude";

    //Current user
    private CurrentUser currentUser;

    //last search request
    private String lastSearchRequest;

    //Declaring UI elements
    private TextInputEditText editTextFlickrSearch;
    private ProgressBar downloadProgressBar;
    private ProgressBar scrollProgressBar;
    private TextView textViewFlickrError;
    private RecyclerView rvFlickrResult;

    //search request
    private String textSearch;

    //for geo coordinates
    private boolean geoResult;
    private double geoResultLatitude;
    private double geoResultLongitude;


    //adapter for Flickr photos
    private PhotosAdapter photosAdapter;
    private int photosPage; //to know a number of pages
    private boolean photosEndReached; // to know if we reach and end of photos
    private boolean isLoading; //to know if we still load our photos
    private ItemTouchHelper itemTouchHelper; //For touch swipes
    private RecyclerView.OnScrollListener onScrollListener; //For scrolls
    private LinearLayoutManager layoutManager;


    public static final String TAG = "FlickrSearchFragment";


    //Working with database;
    private SSFSDatabase db;

    //Working with Network
    private NetworkHelper networkHelper;

    //Controlling AsyncTasks in this activity
    private AsyncTask<Void, Void, Integer> asyncTask;

    //Controlling Retrofit calls in this activity
    private Call<FlickrResponse> call;

    private OnPhotoSelectedListener listener;

    //empty constructor
    public FlickrSearchFragment() {

    }


    public static FlickrSearchFragment newInstance() {
        FlickrSearchFragment fragment = new FlickrSearchFragment();
        return fragment;
    }


    //Use for Geo Search
    public static FlickrSearchFragment newInstance(double latitude, double longitude) {
        FlickrSearchFragment fragment = new FlickrSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble(EXTRA_LATITUDE, latitude);
        bundle.putDouble(EXTRA_LONGITUDE, longitude);
        fragment.setArguments(bundle);
        return fragment;
    }

    //listener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoSelectedListener) {
            listener = (OnPhotoSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement methods of OnPhotoSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().setTitle(R.string.title_flickr_search);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flickr_search, container, false);

        networkHelper = NetworkHelper.getInstance(getActivity());
        currentUser = CurrentUser.getInstance();
        geoResult = false;

        //Initialising UI elements
        editTextFlickrSearch = view.findViewById(R.id.edittext_flickr_search);
        downloadProgressBar = view.findViewById(R.id.download_progressbar);
        rvFlickrResult = view.findViewById(R.id.flickr_result);
        textViewFlickrError = view.findViewById(R.id.flickr_error);
        scrollProgressBar = view.findViewById(R.id.scroll_progressbar);
        isLoading = false;
        photosPage = 1;//number of pages is 1
        photosEndReached = false;

        //Using rxJava to react on text changes in edittext

        //Our Observable EditText field
        Observable<String> rxEditTextFlickrSearchObservable = RxTextView.textChanges(editTextFlickrSearch)
                .debounce(500, TimeUnit.MILLISECONDS).skip(1)
                .observeOn(AndroidSchedulers.mainThread()).map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(CharSequence charSequence) throws Exception {
                        return charSequence.toString();
                    }
                });

        //Subscribing an Observer that will process input of characters
        rxEditTextFlickrSearchObservable.subscribe(new DisposableObserver<String>() {
            @Override
            public void onNext(String searchText) {
                if (searchText.length() >= 3) {
                    firstTextSearchLoad();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

        //Initialize itemTouchHelper
        itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        removePhoto(position);
                    }
                });

        //Initialize onScrollListener
        onScrollListener = new RecyclerView.OnScrollListener() {
            int visibleItemCount, totalItemCount, pastVisiblesItems;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading) {
                    if (dy > 0) {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            if (!photosEndReached && networkHelper.haveNetworkConnection(getActivity())) {
                                isLoading = true;
                                loadMorePhotos();
                            }
                        }
                    }
                }
            }
        };
        layoutManager = new LinearLayoutManager(getActivity());


        //If we used GeoSearch
        if (getArguments() != null) {
            geoResult = true;
            geoResultLatitude = getArguments().getDouble(EXTRA_LATITUDE, 0);
            geoResultLongitude = getArguments().getDouble(EXTRA_LONGITUDE, 0);
            photosPage = 1;
            photosEndReached = false;

            final String searchRequest = String.format(Locale.getDefault(),
                    getString(R.string.geo_snippet),
                    geoResultLatitude,
                    geoResultLongitude);

            editTextFlickrSearch.setText("");
            Toast.makeText(getActivity(), searchRequest, Toast.LENGTH_LONG).show();
            textViewFlickrError.setVisibility(View.INVISIBLE);
            rvFlickrResult.setVisibility(View.INVISIBLE);
            if (!networkHelper.haveNetworkConnection(getActivity())) {
                textViewFlickrError.setVisibility(TextView.VISIBLE);
                textViewFlickrError.setText(getString(R.string.turn_on_internet));
            } else {
                downloadProgressBar.setVisibility(ProgressBar.VISIBLE); //Making download process visible to user
                //adding Search Request to Database

                addSearchRequestToDB(searchRequest);

                //working with response from Flickr
                call = networkHelper.getSearchGeoQueryPhotos(geoResultLatitude, geoResultLongitude, photosPage);
                initialFlickrSearchCall(searchRequest);
            }
        }

        //getting last search request of the user
        if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            asyncTask = new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... data) {
                    //Initialize SearchRequests
                    db = db.getInstance(getActivity());
                    SearchRequest searchRequest = db.searchRequestDao().getLastForUser(currentUser.getUser().getId());
                    if (searchRequest != null) {
                        lastSearchRequest = searchRequest.getSearchRequest();
                    } else {
                        lastSearchRequest = "";
                    }
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {
                    editTextFlickrSearch.setText(lastSearchRequest);
                }
            };
            asyncTask.execute();
        }

        return view;
    }

    //Use to show list of photos in our view
    private void showPhotos(final List<Photo> photos, final String searchRequest) {


        photosAdapter = new PhotosAdapter(photos, searchRequest, new PhotosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Photo photo) {
                //get to FlickViewItemFragment
                listener.onFlickrPhotoSelected(searchRequest, photo.getPhotoUrl(), photo.getTitle());
            }

        });
        // Attach the adapter to the recyclerview to populate items
        rvFlickrResult.setAdapter(photosAdapter);
        // Set layout manager to position the items
        rvFlickrResult.setLayoutManager(layoutManager);

        // Add the functionality to swipe items in the
        // recycler view to delete that item
        itemTouchHelper.attachToRecyclerView(rvFlickrResult);

        //Add infinity scroll functionality to RecyclerView
        rvFlickrResult.addOnScrollListener(onScrollListener);


        rvFlickrResult.setVisibility(View.VISIBLE);
    }

    //This method is used with swipe to remove photo list item from view
    private void removePhoto(int position) {
        photosAdapter.removePhoto(position);
        photosAdapter.notifyItemRemoved(position);
    }


    //Main function of out app to search photos via Flickr API
    private void firstTextSearchLoad() {
        geoResult = false;
        textSearch = editTextFlickrSearch.getText().toString();
        textViewFlickrError.setVisibility(View.INVISIBLE);
        rvFlickrResult.setVisibility(View.INVISIBLE);
        photosPage = 1;
        photosEndReached = false;
        //At first check network connection
        if (!networkHelper.haveNetworkConnection(getActivity())) {
            textViewFlickrError.setVisibility(TextView.VISIBLE);
            textViewFlickrError.setText(getString(R.string.turn_on_internet));
        } else if (textSearch.equals("")) {
            textViewFlickrError.setVisibility(TextView.VISIBLE);
            textViewFlickrError.setText(getString(R.string.input_search_request));

        } else {

            downloadProgressBar.setVisibility(ProgressBar.VISIBLE); //Making download process visible to user

            //adding Search Request to Database
            addSearchRequestToDB(textSearch);

            //working with response from Flickr
            call = networkHelper.getSearchTextQueryPhotos(textSearch, photosPage);

            initialFlickrSearchCall(textSearch);
        }
    }

    //This method is used to add text or geo search request to database
    private void addSearchRequestToDB(final String searchRequest) {

        if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            asyncTask = new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... voids) {
                    db = db.getInstance(getActivity());
                    db.searchRequestDao().insert(new SearchRequest(currentUser.getUser().getId(), searchRequest));
                    return 0;
                }

            };
            asyncTask.execute();
        }
    }

    //This method is used to process our response from the search request where we loaded first page of photos
    private void initialFlickrSearchCall(final String searchRequest) {
        call.enqueue(new Callback<FlickrResponse>() {
            @Override
            public void onResponse(Call<FlickrResponse> call, Response<FlickrResponse> response) {
                FlickrResponse flickrResponse = response.body();

                List<Photo> photos = null;
                //If Response is not null making a result list of photos
                if (flickrResponse != null) {

                    photos = flickrResponse.getPhotos().getPhoto();

                }
                //If photos not null show them
                if (photos != null && !photos.isEmpty()) {
                    showPhotos(photos, searchRequest);
                    photosPage++;
                } else {
                    textViewFlickrError.setVisibility(View.VISIBLE);
                    textViewFlickrError.setText(R.string.search_request_no_photos);
                }
                //disabling download bar

                downloadProgressBar.setVisibility(View.INVISIBLE);

            }

            //If we fail then set an error string to textview
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<FlickrResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error");
                Log.e(TAG, t.toString());
                downloadProgressBar.setVisibility(ProgressBar.INVISIBLE);
                textViewFlickrError.setVisibility(TextView.VISIBLE);
                textViewFlickrError.setText(getString(R.string.request_error));
            }
        });
    }

    //This method is used when we load more pages of the same photo search request (text or geo)
    private void loadMorePhotos() {


        scrollProgressBar.setVisibility(View.VISIBLE);

        //working with new page response from Flickr
        //Log.d(TAG,"Page number before:"+photosPage);

        if (geoResult != true) {
            call = networkHelper.getSearchTextQueryPhotos(textSearch, photosPage);
        } else {
            call = networkHelper.getSearchGeoQueryPhotos(geoResultLatitude, geoResultLongitude, photosPage);
        }


        call.enqueue(new Callback<FlickrResponse>() {
            @Override
            public void onResponse(Call<FlickrResponse> call, Response<FlickrResponse> response) {
                FlickrResponse flickrResponse = response.body();

                List<Photo> photos = null;
                //If Response is not null making a result list of photos
                if (flickrResponse != null) {

                    photos = flickrResponse.getPhotos().getPhoto();

                }
                //Log.d(TAG,photos.toString());
                //If photos not null show them
                if (photos != null && !photos.isEmpty()) {
                    photosAdapter.addNewPhotos(photos);
                    photosPage++;

                    //Log.d(TAG,"Page number after:"+photosPage);
                } else {
                    photosEndReached = true;
                }
                //disabling download bar
                scrollProgressBar.setVisibility(View.INVISIBLE);
                isLoading = false;

            }

            //If we fail then set an error string to textview
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(Call<FlickrResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error");
                Log.e(TAG, t.toString());
                scrollProgressBar.setVisibility(ProgressBar.INVISIBLE);
                isLoading = false;
            }
        });

    }

}




