package com.milesmagusruber.secretserviceflickrsearch.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.adapters.RequestedPhotosAdapter;
import com.milesmagusruber.secretserviceflickrsearch.db.SSFSDatabase;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.RequestedPhoto;
import com.milesmagusruber.secretserviceflickrsearch.listeners.OnPhotoSelectedListener;

import java.util.ArrayList;

import static com.milesmagusruber.secretserviceflickrsearch.fragments.SettingsFragment.KEY_SEARCH_REQUEST;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestedPhotosFragment extends Fragment {


    //UI elements
    private TextView textViewRequestedPhotosInfotext;
    private RecyclerView rvRequestedPhotos;

    //search eequest
    private String searchRequest;

    //database helping class
    private SSFSDatabase db;

    //list of requested photos
    private ArrayList<RequestedPhoto> requestedPhotos;

    //adapter for Requested Flickr photos
    private RequestedPhotosAdapter adapter;

    //Controlling AsyncTasks in this activity
    private AsyncTask<Void, Void, Integer> asyncTask;

    private OnPhotoSelectedListener listener;

    public RequestedPhotosFragment() {
    }

    public static RequestedPhotosFragment newInstance() {
        return new RequestedPhotosFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        getActivity().setTitle(R.string.title_requested_photos);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requested_photos, container, false);
        rvRequestedPhotos = view.findViewById(R.id.rv_requested_photos);
        textViewRequestedPhotosInfotext = view.findViewById(R.id.requested_photos_infotext);
        searchRequest = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(KEY_SEARCH_REQUEST,"cat");
        showRequestedPhotos();
        return view;
    }

    private void showRequestedPhotos() {
        if ((asyncTask == null) || (asyncTask.getStatus() != AsyncTask.Status.RUNNING)) {
            asyncTask = new AsyncTask<Void, Void, Integer>() {

                @Override
                protected void onPreExecute(){
                    rvRequestedPhotos.setVisibility(View.INVISIBLE);
                    textViewRequestedPhotosInfotext.setVisibility(View.INVISIBLE);
                }

                @Override
                protected Integer doInBackground(Void... data) {
                    //getting all requested photos
                    db = db.getInstance(getActivity());
                    requestedPhotos = new ArrayList<>(db.requestedPhotoDao().getAll());
                    return 0;
                }

                @Override
                protected void onPostExecute(Integer a) {
                    if(requestedPhotos!=null && !requestedPhotos.isEmpty()) {
                        // Create adapter passing in the sample user data
                        adapter = new RequestedPhotosAdapter(requestedPhotos, searchRequest, new RequestedPhotosAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(RequestedPhoto requestedPhoto) {
                                //get to Requested Flick Photo from RequestedPhotosFragment
                                listener.onFlickrPhotoSelected(searchRequest, requestedPhoto.getUrl(), requestedPhoto.getTitle());
                            }

                        });
                        // Attach the adapter to the recyclerview to populate items
                        rvRequestedPhotos.setAdapter(adapter);
                        // Set layout manager to position the items
                        rvRequestedPhotos.setLayoutManager(new LinearLayoutManager(getActivity()));

                        rvRequestedPhotos.setVisibility(View.VISIBLE);

                    }else{
                        textViewRequestedPhotosInfotext.setText(R.string.requested_photos_not_found);
                        textViewRequestedPhotosInfotext.setVisibility(View.VISIBLE);
                    }
                }
            };
            asyncTask.execute();
        }

    }

}
