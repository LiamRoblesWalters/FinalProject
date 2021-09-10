package com.example.getdetails;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Notification extends Application {

    public static final String CHANNEL_ID = "notification_channel";

    @Override
    public void onCreate(){
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {

    }
}
