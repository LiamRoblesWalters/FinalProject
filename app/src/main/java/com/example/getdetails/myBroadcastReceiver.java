package com.example.getdetails;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class myBroadcastReceiver extends BroadcastReceiver {
    @Override

    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra("UserName") != null) {
            Toast.makeText(context, intent.getStringExtra("UserName"), Toast.LENGTH_LONG)
                    .show();
        } else {
            boolean AirplanModeOn = intent.getBooleanExtra("state", false);
            if (AirplanModeOn) {
                Toast.makeText(context, "DEVICE IN AIRPLANE MODE", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(context, "Airplane Mode Now Off", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
