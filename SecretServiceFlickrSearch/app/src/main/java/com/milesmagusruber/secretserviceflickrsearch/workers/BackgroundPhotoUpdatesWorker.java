package com.milesmagusruber.secretserviceflickrsearch.workers;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.SSFSDatabase;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.RequestedPhoto;
import com.milesmagusruber.secretserviceflickrsearch.network.NetworkHelper;
import com.milesmagusruber.secretserviceflickrsearch.network.model.FlickrResponse;
import com.milesmagusruber.secretserviceflickrsearch.network.model.Photo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackgroundPhotoUpdatesWorker extends Worker {

    //Working with db
    private SSFSDatabase db;

    //Working with Flickr via network
    private NetworkHelper networkHelper;
    private Call<FlickrResponse> call;
    private boolean downloadSuccess;

    public BackgroundPhotoUpdatesWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    private static final String TAG = BackgroundPhotoUpdatesWorker.class.getSimpleName();

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();
        String searchRequest = "Cat";
        try {
            //TODO: Downloading Flickr Photos
            call = networkHelper.getSearchTextQueryPhotos(searchRequest,1);
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
                        //do one thing
                    } else {
                        //do another
                    }

                }

                //If we fail then set an error string to textview
                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(Call<FlickrResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: Error");
                    Log.e(TAG, t.toString());
                    downloadSuccess = false;
                }
            });

            //If download wasn't successful return failure
            if(!downloadSuccess) return Result.failure();

            ArrayList<RequestedPhoto> requestedPhotos = new ArrayList<>();

            //Saving photos to database

            //getting database
            db = db.getInstance(applicationContext);
            //deleting all previous requested photos
            db.requestedPhotoDao().deleteAll();
            //inserting all new requested photos
            db.requestedPhotoDao().insertAll(requestedPhotos);

            //Showing Notification about success download
            showNotification("WorkManager", "Test message");
            return Result.success();
        } catch (Throwable throwable) {
            Log.e(TAG, "Error in background photo updates", throwable);
            return Result.failure();
        }
    }


    //Showing Notifications
    private void showNotification(String task, String desc) {

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        String channelId = "background_photo_updates_task_channel";
        String channelName = "flickr_photo_background_updates";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.mipmap.ic_launcher);

        manager.notify(1, builder.build());

    }

}
