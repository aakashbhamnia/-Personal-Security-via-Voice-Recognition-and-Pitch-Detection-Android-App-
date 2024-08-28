package com.example.praactice;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AudioRecordingService extends Service {

    private AudioRecord audioRecord;
    private short[] audioBuffer;
    private static final int SAMPLE_RATE = 44100; // in Hz
    private static final int BUFFER_SIZE = 2048; // in samples
    private static final double THRESHOLD = 3000; // Adjust this value as needed
    private static  double PITCH_CHANGE_THRESHOLD ;
    private boolean isRecording = false;
    private double previousRMS = 0;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "AudioRecordingChannel";

    private Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRecording();
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        PITCH_CHANGE_THRESHOLD= Integer.parseInt(sharedPreferences.getString("pitch", "4000")); // Default to 3 if not set


        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Check and request audio recording permission if not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Initialize AudioRecord instance
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                Math.max(bufferSize, BUFFER_SIZE));

        audioBuffer = new short[BUFFER_SIZE];
        handler = new Handler();

    }

    @Override
    public void onDestroy() {
        startRecording();
    }

    @SuppressLint("ForegroundServiceType")
    private void startRecording() {

        new Thread(() -> {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
            audioRecord.startRecording();
            isRecording = true;



            while (isRecording) {
                audioRecord.read(audioBuffer, 0, BUFFER_SIZE);
                double rms = calculateRMS(audioBuffer);
                checkPitchChange(rms);
                try {
                    Thread.sleep(1000); // Sleep for 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            audioRecord.stop();
        }).start();
    }



    private double calculateRMS(short[] audioBuffer) {
        double sum = 0;
        for (short sample : audioBuffer) {
            sum += sample * sample;
        }
        double mean = sum / audioBuffer.length;
        return Math.sqrt(mean);
    }

    private void checkPitchChange(double rms) {
        if (hasPitchChanged(rms)) {
            handler.post(() -> createNotification());
        }
        startRecording();
    }
    private boolean hasPitchChanged(double currentRMS) {
        double pitchChange = Math.abs(currentRMS - previousRMS);
        previousRMS = currentRMS; // Update previous RMS value for the next iteration
        return pitchChange > PITCH_CHANGE_THRESHOLD;
    }



    @SuppressLint("MissingPermission")
    private void createNotification() {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ice, null);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap largeIcon = bitmapDrawable.getBitmap();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Cancel the previous notification if it exists
        notificationManager.cancel(NOTIFICATION_ID);

        Intent intent = new Intent(this, MainActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ice)
                .setLargeIcon(largeIcon)
                .setContentTitle("New Message")
                .setContentText("message new")
                .setSubText("new nas")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);  // Auto cancel the notification when clicked

        // Create a notification channel
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "New Channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        notificationManager.createNotificationChannel(channel);

        // Post the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        // Schedule notification cancellation after 2 seconds
        new Handler().postDelayed(() -> notificationManager.cancel(NOTIFICATION_ID), 1000);

        startForeground(NOTIFICATION_ID, builder.build());

        // Simulate auto-click
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
     //   startRecording();
    }

}
