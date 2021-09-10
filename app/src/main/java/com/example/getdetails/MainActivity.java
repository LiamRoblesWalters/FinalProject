package com.example.getdetails;

import static com.example.getdetails.Notification.CHANNEL_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LifecycleObserver {
    private static GoogleSignInClient gsiClient;
    private Boolean signedIn = false;
    private int RC_SIGN_IN = 777;


    public static GoogleSignInClient getGsiClient() {
        return gsiClient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        findViewById(R.id.sign_in_button).setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().build();
        gsiClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

    }

    @Override
    public void onStart(){
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        updateUI(account);
        signedIn = true;
    }


    private void updateUI(GoogleSignInAccount account) {
        if (account!=null){
            //GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(this);
            Intent intent = new Intent(this, RecyclerViewActivity.class);
            intent.putExtra("UserName", account.getDisplayName());
            intent.putExtra("source", "Main");
            startActivity(intent);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded(){
        Context context = getApplicationContext();
        Log.d("Main activity", "onAppBackgrounded: ->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + context);
        //if () {
        getNotification();
        //}
    }


    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = gsiClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            Log.w("tag", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    private void getNotification() {
        Context myContext = getApplicationContext();
        Intent nIntent = getPackageManager().getLaunchIntentForPackage("com.example.getdetails");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "777";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID, "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel1.setDescription("Don't forget about me!");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID);
        notification
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentTitle("Notification")
                .setContentText("Don't forget about me!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(1, notification.build());
    }
}