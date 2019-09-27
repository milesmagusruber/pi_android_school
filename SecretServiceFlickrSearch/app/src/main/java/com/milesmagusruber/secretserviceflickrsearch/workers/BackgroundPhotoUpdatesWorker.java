package com.milesmagusruber.secretserviceflickrsearch.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackgroundPhotoUpdatesWorker extends Worker {

    public BackgroundPhotoUpdatesWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork(){
        return Result.success();
    }

}
