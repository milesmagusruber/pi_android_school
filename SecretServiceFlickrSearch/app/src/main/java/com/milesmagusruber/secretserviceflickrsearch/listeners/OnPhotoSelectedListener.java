package com.milesmagusruber.secretserviceflickrsearch.listeners;

public interface OnPhotoSelectedListener {
    void onFlickrPhotoSelected(String searchRequest, String webLink, String title);
    void onPhotoFileSelected(String filePath);
}
