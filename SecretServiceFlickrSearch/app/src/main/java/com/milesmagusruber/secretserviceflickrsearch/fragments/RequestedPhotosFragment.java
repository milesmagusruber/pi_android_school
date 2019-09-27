package com.milesmagusruber.secretserviceflickrsearch.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.workers.BackgroundPhotoUpdatesWorker;

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
        View view = inflater.inflate(R.layout.fragment_requested_photos, container, false);
        //Testing WorkManager
        WorkManager workManager = WorkManager.getInstance();
        OneTimeWorkRequest workerRequest = new OneTimeWorkRequest.Builder(BackgroundPhotoUpdatesWorker.class).build();
        workManager.enqueue(workerRequest);
        // Inflate the layout for this fragment
        return view;
    }

}
