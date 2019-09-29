package com.milesmagusruber.secretserviceflickrsearch.workers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;
import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.activities.MainActivity;
import com.milesmagusruber.secretserviceflickrsearch.db.SSFSDatabase;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.RequestedPhoto;
import com.milesmagusruber.secretserviceflickrsearch.network.NetworkHelper;
import com.milesmagusruber.secretserviceflickrsearch.network.model.FlickrResponse;
import com.milesmagusruber.secretserviceflickrsearch.network.model.Photo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class BackgroundPhotoUpdatesWorker extends Worker {

    public static final String WORKER_SEARCH_REQUEST="search_request";

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

    private static final String TAG = "BACKGROUND_UPDATES";


    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();
        networkHelper = NetworkHelper.getInstance(applicationContext);
        String searchRequest = getInputData().getString(WORKER_SEARCH_REQUEST);
        Log.d(TAG,searchRequest);
        try {
            //Downloading Flickr Photos
            final ArrayList<RequestedPhoto> requestedPhotos = new ArrayList<>();

            call = networkHelper.getSearchTextQueryPhotos(searchRequest,1);
            FlickrResponse flickrResponse=call.execute().body();
            List<Photo> photos = null;

            //If Response is not null making a result list of photos
            if (flickrResponse != null) {

                photos = flickrResponse.getPhotos().getPhoto();

            }
            //If photos not null show them
            if (photos != null && !photos.isEmpty()) {
                for(Photo photo: photos){
                    requestedPhotos.add(new RequestedPhoto(photo));
                }
                downloadSuccess=true;
            } else {
                //do another
            }
            Log.d(TAG,"level2");

            //If download wasn't successful return failure
            if(!downloadSuccess){
                showNotification(applicationContext.getString(R.string.requested_photos_notification_title,searchRequest),
                        applicationContext.getString(R.string.requested_photos_notification_body_fail),null);
                return Result.failure();
            }

            //Saving photos to database
            //getting database
            db = db.getInstance(applicationContext);
            //deleting all previous requested photos
            db.requestedPhotoDao().deleteAll();
            //inserting all new requested photos
            db.requestedPhotoDao().insertAll(requestedPhotos);

            Bitmap firstImage=null;
            if(!requestedPhotos.isEmpty()) {
                firstImage = Glide.
                        with(applicationContext).
                        asBitmap().
                        load(requestedPhotos.get(0).getUrl()).into(200, 200).get();
            }

            //Showing Notification about success download
            showNotification(applicationContext.getString(R.string.requested_photos_notification_title,searchRequest),
                    applicationContext.getString(R.string.requested_photos_notification_body_success,requestedPhotos.size()),firstImage);
            return Result.success();
        } catch (Throwable throwable) {
            Log.e(TAG, "Error in background photo updates", throwable);
            showNotification(applicationContext.getString(R.string.requested_photos_notification_title,searchRequest),
                    applicationContext.getString(R.string.requested_photos_notification_body_fail),null);
            return Result.failure();
        }
    }


    //Showing Notifications
    private void showNotification(String title, String body, Bitmap image) {

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        String channelId = "background_photo_updates_task_channel";
        String channelName = "flickr_photo_background_updates";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new
                    NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        //notificationIntent
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.putExtra("menuFragment", "requestedPhotosFragment");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        //notification builder
        NotificationCompat.Builder builder;
        if(image !=null){

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(image);
            bigPictureStyle.bigLargeIcon(null);

            builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setStyle(bigPictureStyle);

        }else {
            builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher);
        }

        manager.notify(1, builder.build());

    }

}
