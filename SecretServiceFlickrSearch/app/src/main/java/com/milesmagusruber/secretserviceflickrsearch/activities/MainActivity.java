package com.milesmagusruber.secretserviceflickrsearch.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.milesmagusruber.secretserviceflickrsearch.R;
import com.milesmagusruber.secretserviceflickrsearch.broadcast_receivers.PowerReceiver;

public class MainActivity extends AppCompatActivity {

    private PowerReceiver powerReceiver = new PowerReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register the power receiver using the activity context.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(powerReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        //Unregister the receiver
        this.unregisterReceiver(powerReceiver);
        super.onDestroy();
    }


}
