package com.example.notificationsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button mNotifyButton;
    Button mUpdateButton;
    Button mCancelButton;
    private NotificationManager mNotifyManager;
    private static final int NOTIFICATION_ID=0;
    private static final String NOTIF_URL="www.google.com";
    private static final String CHANNEL_ID="ch1";
    private static final String ACTION_UPDATE_NOTIFICATION="com.example.notificationsapp.ACTION_UPDATE_NOTIFICATION";
    private static final String ACTION_CANCEL_NOTIFICATION="com.example.notificationsapp.ACTION_CANCEL_NOTIFICATION";
    private NotificationReceiver mReceiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotifyButton=findViewById(R.id.notify);
        mUpdateButton=findViewById(R.id.update);
        mCancelButton=findViewById(R.id.cancel);
        mNotifyManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION);
        intentFilter.addAction(ACTION_CANCEL_NOTIFICATION);
        registerReceiver(mReceiver, intentFilter);

        mNotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNotification();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotification();
            }
        });
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = CHANNEL_ID;
            String description = "Description .. ";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification() {
        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,NOTIFICATION_ID,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent learnMoreIntent=new Intent(Intent.ACTION_VIEW, Uri.parse(NOTIF_URL));
        PendingIntent morePendingIntent =  PendingIntent.getActivity(this,NOTIFICATION_ID,learnMoreIntent,PendingIntent.FLAG_ONE_SHOT);

        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this,NOTIFICATION_ID,updateIntent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifyBuilder=new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your notification Title")
                .setContentText("Notification Content .....")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(R.drawable.ic_stat_name,"Learn more", morePendingIntent)
                .addAction(R.drawable.ic_stat_name,"Update", updatePendingIntent)
                .setContentIntent(notificationPendingIntent);

        Notification myNotification=notifyBuilder.build();
        mNotifyManager.notify(NOTIFICATION_ID, myNotification);
    }

    private void updateNotification(){
        Bitmap image= BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_background);
        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,NOTIFICATION_ID,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder=new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your notification Title")
                .setContentText("Notification Content .....")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(notificationPendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .setBigContentTitle("Notification Updated!"));
        Notification myNotification=notifyBuilder.build();
        mNotifyManager.notify(NOTIFICATION_ID, myNotification);
    }

    private void cancelNotification(){
        mNotifyManager.cancel(NOTIFICATION_ID);
    }

    private class NotificationReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case ACTION_CANCEL_NOTIFICATION:
                    cancelNotification();
                    break;
                case ACTION_UPDATE_NOTIFICATION:
                    updateNotification();
                    break;
            }
        }
    }
}
