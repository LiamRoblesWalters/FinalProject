package com.example.getdetails;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RecyclerViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int FRAGMENT_REQUEST = 7;
    private CardView cardView;
    private List<User> users;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private final OkHttpClient client = new OkHttpClient();
    private static final String MyPrefs = "myPrefs";
    private SharedPreferences sharedPreferences;
    public static UserViewModel userViewModel;
    private static boolean firstLogin = false;
    private AlarmManager alarm;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);


        sharedPreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        users = new ArrayList<>();

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                //update RecyclerView
                recyclerViewAdapter.setUsers(users);
            }
        });

        if (getIntent().getExtras() != null && getIntent().getStringExtra("source").equals("Main")) {

            if (sharedPreferences.getString("FirstLogin",  null)==null) {

                User newUser = new User(account.getDisplayName());
                newUser.imageUri = String.format("https://robohash.org/%s?set=set5", newUser.name);
                newUser.id = -77;

                users.add(newUser);
                userViewModel.insert(newUser);
                try {
                    run();


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();// myObject - instance of MyObject
                editor.putString("FirstLogin", "first_login");
                editor.apply();
            }
        }


        SharedPreferences.Editor editor = sharedPreferences.edit();// myObject - instance of MyObject
        editor.putString("Class", getClass().toString());
        editor.apply();
//
        cardView = findViewById(R.id.cardView);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // lets recycler view set up its layout
        RecyclerViewAdapter.ClickListener listener = new RecyclerViewAdapter.ClickListener() {
            @Override
            public void onItemClicked(RecyclerViewAdapter.ViewHolder viewHolder) {
                Intent args = new Intent(getApplicationContext(), FragmentActivity.class);
//
                args.putExtra("imageUrl", viewHolder.imageUrl);
                args.putExtra("source", "Recycler");
                args.putExtra("Position", viewHolder.Position);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("Position", viewHolder.Position);
                editor.putString("UserInfo", viewHolder.info);
                editor.apply();

                startActivityForResult(args, FRAGMENT_REQUEST);

                finish();
            }
        };



        recyclerViewAdapter = new RecyclerViewAdapter(this, users, listener); // instantiate recycler view adapter

        recyclerView.setAdapter(recyclerViewAdapter);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new GlobalNotification(this, sharedPreferences));

        alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent newIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, newIntent, 0);
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 10, 60*1000, alarmIntent);


    }



    @Override
    public void onStart() {
        super.onStart();

        IntentFilter airplaneFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        registerReceiver(new myBroadcastReceiver(), airplaneFilter);
    }




    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.recyclerview_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            // ...
            case R.id.log_out:
                signOut();
                revokeAccess();
                return true;
            // ...
            case R.id.show_map:
                Intent mapIntent = new Intent(this, MapsActivity.class);
                startActivity(mapIntent);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    //    @Override
//    public void onBackPressed(){
//        Toast.makeText(this, "Please Log Out to Return to Login Page", Toast.LENGTH_LONG)
//                .show();
//    }
    private void signOut() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        MainActivity.getGsiClient().signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //...
                    }
                });
    }

    private void revokeAccess() {
        MainActivity.getGsiClient().revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public void run() throws Exception {
        Request request = new Request.Builder()
                .url("http://jsonplaceholder.typicode.com/users")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    Gson gson = new Gson();
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    User[] names = gson.fromJson(response.body().string(), User[].class);
                    //System.out.println(response.body().string());
                    runOnUiThread((new Runnable() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void run() {
                            getUsers(names);
                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                    }));

                }
            }
        });
    }

    private void getUsers(User[] names) {
        for (int i = 0; i < names.length; i++) {
            User user = names[i];
            user.imageUri = String.format("https://robohash.org/%s?set=set5", user.name);
            users.add(user);
            userViewModel.insert(user);

        }

    }

    @Override
    public void onClick(View view) {

    }
}