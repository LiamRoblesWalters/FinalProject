package com.example.getdetails;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Another Minute Has Passed!", Toast.LENGTH_LONG)
                .show();
        Log.d("Alarm", "onReceive: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ALARM!");
        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(4000);
//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        if (alarmUri == null) {
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//
//        // setting default ringtone
//        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//
//        // play ringtone
//        new CountDownTimer(5000, 1000) {
//            @Override
//            public void onTick(long l) {
//                ringtone.play();
//            }
//
//            @Override
//            public void onFinish() {
//                ringtone.stop();
//            }
//        }.start();


    }
}
