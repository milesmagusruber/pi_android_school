package com.milesmagusruber.secretserviceflickrsearch.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.milesmagusruber.secretserviceflickrsearch.activities.MainActivity;

public class AppRunsOnStartup extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intentForActivity = new Intent(context, MainActivity.class);
            intentForActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentForActivity);
        }
    }
}
