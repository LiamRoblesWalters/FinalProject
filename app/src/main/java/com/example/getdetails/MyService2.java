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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService2 extends Service {
    private Intent nIntent;
    private SharedPreferences sharedPreferences;
    private Context context;
    private String MyPrefs = "myPrefs";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String input = intent.getStringExtra("Message");
        String c = sharedPreferences.getString("Class", null);
        //RetrieveUserData();
        if (c.equals(FragmentActivity.class.toString())){
            nIntent = new Intent(context, FragmentActivity.class);
            nIntent.putExtra("imageUrl", "");
            nIntent.putExtra("source", "Recycler");
            nIntent.putExtra("Position", sharedPreferences.getInt("Position", 0));
            Log.d("Frag Activity", "getNotification: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + c);
        }
        else if (c.equals(RecyclerViewActivity.class.toString())){
            nIntent = new Intent(context, RecyclerViewActivity.class);

        }
        else if (c.equals(ContactNotes.class.toString())){
            nIntent = new Intent(context, ContactNotes.class);
        }
        else if (c.equals(MapsActivity.class.toString())){
            nIntent = new Intent(context, MapsActivity.class);
        }
        else {
            Log.d("not a fragment", "getNotification:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + c);
            nIntent = context.getPackageManager().getLaunchIntentForPackage("com.example.getdetails");
        }
//
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = "777";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID, "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel1.setDescription("Don't forget about me!");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentTitle("My Contacts Service")
                .setContentText(input)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        startForeground(1, notification);

        stopSelf();

        return START_STICKY;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        sharedPreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        context = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//
}