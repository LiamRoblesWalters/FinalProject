package com.example.getdetails;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class GlobalNotification implements LifecycleObserver{
    private Intent nIntent;
    private SharedPreferences sharedPreferences;
    private Context context;

    GlobalNotification(Context context, SharedPreferences sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onAppBackgrounded(){
        Intent serviceIntent = new Intent(context, MyService2.class);
        serviceIntent.putExtra("Message", "See you contacts");
        context.startService(serviceIntent);
        //}
    }

}
