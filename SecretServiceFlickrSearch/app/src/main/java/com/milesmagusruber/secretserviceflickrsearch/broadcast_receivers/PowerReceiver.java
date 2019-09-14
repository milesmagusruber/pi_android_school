package com.milesmagusruber.secretserviceflickrsearch.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import com.milesmagusruber.secretserviceflickrsearch.R;

public class PowerReceiver extends BroadcastReceiver {

    //current battery level
    private int batteryLevel=0;

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction != null && intentAction.equals(Intent.ACTION_BATTERY_CHANGED)) {
            //getting battery level
            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int level = -1;
            if (rawlevel >= 0 && scale > 0) {
                level = (rawlevel * 100) / scale;
            }
            //show toast message only if we have new integer battery level
            if(level!=batteryLevel) {
                //display toast message
                String toastMessage = context.getString(R.string.battery_level, level);
                Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
                //assign new level value
                batteryLevel=level;
            }
        }
    }
}
