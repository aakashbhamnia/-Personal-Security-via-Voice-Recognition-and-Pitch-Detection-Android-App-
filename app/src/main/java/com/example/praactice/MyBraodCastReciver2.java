package com.example.praactice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class MyBraodCastReciver2 extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Device has booted up, start your service here
            Intent serviceIntent = new Intent(context, AudioRecordingService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
        // Add more conditions if you want to start the service for other events
    }
}
