package com.example.praactice;

import static com.example.praactice.R.*;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyService extends Service implements RecognitionListener {

    private static final int NOTIFICATION_ID = 1001;
    private static final String CHANNEL_ID = "MyServiceChannel";

    private SpeechRecognizer speechRecognizer;
    public String keyword;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);

        // Start listening for voice commands
        startListening();

        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        keyword = sharedPreferences.getString("keyword", "open").toLowerCase();

        return START_STICKY;
    }

    private void startListening() {
        if (speechRecognizer != null) {
            Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            speechRecognizer.startListening(recognizerIntent);
        }
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            String command = matches.get(0).toLowerCase();
            if (command.contains(keyword)) {
                // Check if MainActivity2 is already running
                createNotification();
            } else if (command.contains("close")) {
                Intent serviceIntent = new Intent(this, MyService.class);
                stopService(serviceIntent);
            }
        }
        // Start listening again
        startListening();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d("SpeechRecognition", "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("SpeechRecognition", "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d("SpeechRecognition", "onRmsChanged: " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d("SpeechRecognition", "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("SpeechRecognition", "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        Log.e("SpeechRecognition", "onError: " + error);
        startListening();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d("SpeechRecognition", "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d("SpeechRecognition", "onEvent");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
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
        new Handler().postDelayed(() -> notificationManager.cancel(NOTIFICATION_ID), 2000);

        startForeground(NOTIFICATION_ID, builder.build());

        // Simulate auto-click
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
