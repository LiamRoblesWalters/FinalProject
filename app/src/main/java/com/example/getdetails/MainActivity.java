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

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static GoogleSignInClient gsiClient;
    private Boolean signedIn = false;
    private int RC_SIGN_IN = 777;
    private static final String MyPrefs = "myPrefs";
    private static final String UserKey = "sharedUsers";
    private String c;
    private List<User> users;
    private Intent nIntent;

    private SharedPreferences sharedPreferences;
    private int Position = 0;

    private AlarmManager alarm;
    private PendingIntent alarmIntent;


    public static GoogleSignInClient getGsiClient() {
        return gsiClient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile().build();
        gsiClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

        sharedPreferences = this.getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        // myObject - instance of MyObject
        editor.putString("Class", getClass().toString());
        editor.apply();

//        String c = sharedPreferences.getString("Class", null);


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

            finish();
        }
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    private void onAppBackgrounded(){
//        Context context = getApplicationContext();
////        Class current_class = context.getApplicationContext().getCurrentActivity();
//        Log.d("Main activity", "onAppBackgrounded: ->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + context);
//        //if () {
//        Toast.makeText(context, "called notification", Toast.LENGTH_LONG).show();
//        getNotification();
//        //}
//    }


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
//    private void getNotification() {
//        String c = sharedPreferences.getString("Class", null);
//        //RetrieveUserData();
//        if (c.equals(FragmentActivity.class.toString())){
//            nIntent = new Intent(this, FragmentActivity.class);
//            nIntent.putExtra("imageUrl", "");
//            nIntent.putExtra("source", "Recycler");
//            nIntent.putExtra("Position", sharedPreferences.getInt("Position", 0));
//            Log.d("Frag Activity", "getNotification: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + c);
//        }
//        else if (c.equals(RecyclerViewActivity.class.toString())){
//            nIntent = new Intent(this, RecyclerViewActivity.class);
//
//        }
//        else {
//            Log.d("not a fragment", "getNotification:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + c);
//            Context myContext = getApplicationContext();
////        Intent nIntent = new Intent(myContext, myContext.getClass());
//            nIntent = getPackageManager().getLaunchIntentForPackage("com.example.getdetails");
//        }
////
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        String CHANNEL_ID = "777";
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel channel1 = new NotificationChannel(
//                    CHANNEL_ID, "Channel 1",
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//
//            channel1.setDescription("Don't forget about me!");
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel1);
//        }
//
//        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID);
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
    public void RetrieveUserData() {
        String serializedObject = sharedPreferences.getString(UserKey, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            users = gson.fromJson(serializedObject, type);
        }

    }
}