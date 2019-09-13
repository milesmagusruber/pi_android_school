package com.milesmagusruber.powerreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CustomReceiver extends BroadcastReceiver {

    private static final String ACTION_CUSTOM_BROADCAST =
            BuildConfig.APPLICATION_ID + ".ACTION_CUSTOM_BROADCAST";
    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction != null) {
            String toastMessage = context.getString(R.string.broadcast_unknown);
            switch (intentAction){
                case Intent.ACTION_POWER_CONNECTED:
                    toastMessage = context.getString(R.string.broadcast_power_connected);
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    toastMessage = context.getString(R.string.broadcast_power_disconnected);
                    break;
                case Intent.ACTION_HEADSET_PLUG:
                    if(intent.getIntExtra("state",0)==1){
                        toastMessage=context.getString(R.string.broadcast_headset_plugged);
                    }else{
                        toastMessage=context.getString(R.string.broadcast_headset_unplugged);
                    }
                    break;
                case ACTION_CUSTOM_BROADCAST:
                    int number=intent.getIntExtra("extra",0);
                    number*=number;
                    toastMessage = context.getString(R.string.broadcast_custom,number);
                    break;
            }
            //Display the toast.
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
