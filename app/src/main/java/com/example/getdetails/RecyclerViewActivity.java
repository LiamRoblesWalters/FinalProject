package com.example.getdetails;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

    private CardView cardView;
    private Button logOut;
    private List<User> users = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private final OkHttpClient client = new OkHttpClient();
    private String name;
    private static final String MyPrefs = "myPrefs";
    private static final String UserKey = "sharedUsers";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        sharedPreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null){
            name = account.getDisplayName();
        }
        logOut = findViewById(R.id.log_out_button);
        logOut.setOnClickListener(this);

        if (getIntent().getExtras() != null && getIntent().getStringExtra("source").equals("Main")) {
            //name = getIntent().getStringExtra("UserName");
            User newUser = new User(name);
            users.add(newUser);
            try {
                run();

                if (users != null) {

                    for (User user : users) {
                        user.imageUri = String.format("https://robohash.org/%s?set=set5", user.name);

                    }

                } else {
                    Toast.makeText(this, "Something weird happened", Toast.LENGTH_LONG);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG);
            }
        } else {
            RetrieveUserData();
        }


            //
            cardView = findViewById(R.id.cardView);
            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this)); // lets recycler view set up its layout

            recyclerViewAdapter = new RecyclerViewAdapter(this, users); // instantiate recycler view adapter

            recyclerView.setAdapter(recyclerViewAdapter);


        }

        @Override
        public void onPause(){
            super.onPause();

            SaveUserData();
        }


    public void SaveUserData(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonUsers = gson.toJson(users);

        editor.putString(UserKey, jsonUsers);
        editor.apply();

    }

    public void RetrieveUserData() {
        String serializedObject = sharedPreferences.getString(UserKey, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            users = gson.fromJson(serializedObject, type);
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // ...
            case R.id.log_out_button:
                signOut();
                revokeAccess();
                break;
            // ...
        }
    }

    @Override
    public void onBackPressed(){
        Toast.makeText(this, "Please Log Out to Return to Login Page", Toast.LENGTH_LONG)
                .show();
    }
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
        for (int i = 0; i < names.length; i++){
            User user = names[i];
            user.imageUri = String.format("https://robohash.org/%s?set=set5", user.name);
            users.add(user);

        }

    }
}