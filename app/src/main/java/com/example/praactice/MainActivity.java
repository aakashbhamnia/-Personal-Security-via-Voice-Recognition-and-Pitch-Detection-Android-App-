package com.example.praactice;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.praactice.MyService;

public class MainActivity extends AppCompatActivity {

    private TextView switchStateTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       updatePreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void updatePreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String exampleText = sharedPreferences.getString("example_text", "Default Text");

        boolean switchState = sharedPreferences.getBoolean("example_switch", false);
    /*    if(switchState){
            Intent serviceINtent2=new Intent(this, AudioRecordingService.class);
            startForegroundService(serviceINtent2);
            foregroundServiceRunning2();
        }
        else{
              Intent serviceINtent=new Intent(this, MyService.class);
            startForegroundService(serviceINtent);
            foregroundServiceRunning();

        }*/
        Intent serviceINtent=new Intent(this, MyService.class);
        startForegroundService(serviceINtent);
        foregroundServiceRunning();
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo service:activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(MyService.class.getName().equals((service.service.getClassName()))){
                return true;
            }
        }
        return false;
    }


   /* public boolean foregroundServiceRunning2(){
        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo service:activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(AudioRecordingService.class.getName().equals((service.service.getClassName()))){
                return true;
            }
        }
        return false;
    }

    // Method to start recording activity
    public void start(){
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }*/
}


//}if(b==true){
//        Intent serviceINtent=new Intent(this, MyService.class);
//        startForegroundService(serviceINtent);
//        foregroundServiceRunning();
//
//        }
//        else{
//        Intent serviceINtent2=new Intent(this, AudioRecordingService.class);
//        startForegroundService(serviceINtent2);
//        foregroundServiceRunning2();
//        }
//
//
//
//
//
//        }
//public boolean foregroundServiceRunning(){
//        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//
//        for(ActivityManager.RunningServiceInfo service:activityManager.getRunningServices(Integer.MAX_VALUE)){
//        if(MyService.class.getName().equals((service.service.getClassName()))){
//        return true;
//        }
//        }
//        return false;
//        }
//
//public boolean foregroundServiceRunning2(){
//        ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//
//        for(ActivityManager.RunningServiceInfo service:activityManager.getRunningServices(Integer.MAX_VALUE)){
//        if(AudioRecordingService.class.getName().equals((service.service.getClassName()))){
//        return true;
//        }
//        }
//        return false;
//        }