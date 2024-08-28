




package com.example.praactice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.media.MediaScannerConnection;
import androidx.preference.PreferenceManager;

import com.example.praactice.util.Appdata;

public class MainActivity2 extends AppCompatActivity implements SurfaceHolder.Callback {

    private MediaRecorder m;
    private SurfaceView s;
    private SurfaceHolder sh;
    private boolean isRecording = false;
    private Handler h = new Handler();
    private int d;
    public File o;
    private static final int code = 101;
    private static final String[] REQUIRED_PERMISSIONS = {android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private int count;
    private String n_;
    private String t_;
    private String tt_;

    private EmailUtil emailUtil;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initialize preferences
        android.content.SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        count = Integer.parseInt(sharedPreferences.getString("count", "3"));
        d = Integer.parseInt(sharedPreferences.getString("duration", "3")) * 1000 + 1000;
        t_ = sharedPreferences.getString("subject", "subject");
        n_ = sharedPreferences.getString("name", "main");
        tt_ = sharedPreferences.getString("text", "subject");

        Appdata.Sender_Email = sharedPreferences.getString("Sender_email", "xyz@gmail.com");
        Appdata.Sender_Email_Password = sharedPreferences.getString("Sender_email_password", "password");
        Appdata.Reciver_Email_Address = sharedPreferences.getString("Reciver_Email_Address", "xyz@gmail.com");

        s = findViewById(R.id.surfaceView);
        sh = s.getHolder();
        sh.addCallback(this);

        emailUtil = new EmailUtil(this);

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, code);
        }
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void startRecording() {
        if (allPermissionsGranted()) {
            m = new MediaRecorder();
            m.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            m.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            m.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            o = new File(outputDir, "video_" + System.currentTimeMillis() + ".mp4");
            m.setOutputFile(o.getAbsolutePath());

            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            m.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
            m.setVideoEncodingBitRate(profile.videoBitRate);
            m.setVideoFrameRate(profile.videoFrameRate);
            m.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            m.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            if (sh.getSurface() == null || !sh.getSurface().isValid()) {
                Toast.makeText(this, "Surface is not valid", Toast.LENGTH_SHORT).show();
                return;
            }

            m.setPreviewDisplay(sh.getSurface());

            try {
                m.prepare();
                m.start();
                isRecording = true;
                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();

                h.postDelayed(this::stopRecording, d);
            } catch (IOException e) {
                Log.e("Recording", "Failed to start recording", e);
                Toast.makeText(this, "Failed to start recording: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, code);
        }
    }

    private void stopRecording() {
        if (isRecording) {
            try {
                m.stop();
            } catch (RuntimeException stopException) {
                Log.e("Recording", "Failed to stop recording properly", stopException);
            }
            m.release();
            m = null;
            isRecording = false;
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();

            MediaScannerConnection.scanFile(this,
                    new String[]{o.getAbsolutePath()},
                    null,
                    (path, uri) -> {
                        Log.i("TAG", "Scanned " + path + ":");
                        Log.i("TAG", "-> uri=" + uri);
                        emailUtil.sendEmailWithVideo(n_, t_, tt_, o);  // Automatically send the video via email
                    });

            if (count > 0) {
                count--;
                startRecording();
            }
            if (count == 0) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private class EmailUtil {
        private Context context;

        public EmailUtil(Context context) {
            this.context = context;
        }

        public void sendEmailWithVideo(String n_, String t_, String tt_, File outputFile) {
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", Appdata.Gmail_Host);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Appdata.Sender_Email, Appdata.Sender_Email_Password);
                }
            });

            MimeMessage message = new MimeMessage(session);
            try {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(Appdata.Reciver_Email_Address));
                message.setSubject(t_);

                // Attach the video file directly
                MimeBodyPart filePart = new MimeBodyPart();
                FileDataSource source = new FileDataSource(outputFile);
                filePart.setDataHandler(new DataHandler(source));
                filePart.setFileName(outputFile.getName());

                // Add text
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText("From: " + n_ + "\n" + "Text: " + tt_);

                // Create Multipart email
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(textPart);
                multipart.addBodyPart(filePart);

                message.setContent(multipart);

                new Thread(() -> {
                    try {
                        Transport.send(message);
                    } catch (MessagingException e) {
                        Log.e("Email", "Failed to send email", e);
                    }
                }).start();

            } catch (MessagingException e) {
                Log.e("Email", "Failed to create email message", e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        startRecording();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes if needed
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // Handle surface destruction if needed
    }
}
