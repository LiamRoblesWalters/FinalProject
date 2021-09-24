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
//        Class current_class = context.getApplicationContext().getCurrentActivity();
        Log.d("Main activity", "onAppBackgrounded: ->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + context);
        //if () {
        Toast.makeText(context, "called notification", Toast.LENGTH_LONG).show();
        Intent serviceIntent = new Intent(context, MyService2.class);
        serviceIntent.putExtra("Message", "See you contacts");
//        serviceIntent.putExtra("Context", context.toString());
        context.startService(serviceIntent);
        //}
    }
//    public void startService(){
//        Intent serviceIntent = new Intent(context, MyService.class);
//        serviceIntent.putExtra("Message", "See your contacts");
//        startService(serviceIntent);
//
//    }
//
//    public void stopService(){
//        Intent serviceIntent = new Intent(context, MyService.class);
////        serviceIntent.putExtra("Message", "Get Your Contacts");
//        stopService(serviceIntent);
//    }
//
//    private void getNotification() {
//        String c = sharedPreferences.getString("Class", null);
//        //RetrieveUserData();
//        if (c.equals(FragmentActivity.class.toString())){
//            nIntent = new Intent(context, FragmentActivity.class);
//            nIntent.putExtra("imageUrl", "");
//            nIntent.putExtra("source", "Recycler");
//            nIntent.putExtra("Position", sharedPreferences.getInt("Position", 0));
//            Log.d("Frag Activity", "getNotification: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + c);
//        }
//        else if (c.equals(RecyclerViewActivity.class.toString())){
//            nIntent = new Intent(context, RecyclerViewActivity.class);
//
//        }
//        else {
//            Log.d("not a fragment", "getNotification:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + c);
////        Intent nIntent = new Intent(myContext, myContext.getClass());
//            nIntent = context.getPackageManager().getLaunchIntentForPackage("com.example.getdetails");
//        }
////
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        String CHANNEL_ID = "777";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel channel1 = new NotificationChannel(
//                    CHANNEL_ID, "Channel 1",
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//
//            channel1.setDescription("Don't forget about me!");
//
//            NotificationManager manager = context.getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel1);
//        }
//
//        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID);
//        notification
//                .setSmallIcon(R.drawable.ic_android_black_24dp)
//                .setContentTitle("My Contacts Notification")
//                .setContentText("Don't forget about me!")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true)
//                .build();
//
//        notificationManager.notify(1, notification.build());
//    }

//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
}
