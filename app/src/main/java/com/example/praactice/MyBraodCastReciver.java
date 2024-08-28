package com.example.praactice;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBraodCastReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceINtent=new Intent(context,MyService.class);
            context.startForegroundService(serviceINtent);
        }


    }
}
