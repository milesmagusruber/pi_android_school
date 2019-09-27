package com.milesmagusruber.secretserviceflickrsearch.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.db.SSFSDatabase;
import com.milesmagusruber.secretserviceflickrsearch.db.entities.RequestedPhoto;

import java.util.ArrayList;

public class BackgroundPhotoUpdatesWorker extends Worker {

    private SSFSDatabase db;

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
