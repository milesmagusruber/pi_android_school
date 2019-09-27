package com.milesmagusruber.secretserviceflickrsearch.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.milesmagusruber.secretserviceflickrsearch.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestedPhotosFragment extends Fragment {


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requested_photos, container, false);
    }

}
