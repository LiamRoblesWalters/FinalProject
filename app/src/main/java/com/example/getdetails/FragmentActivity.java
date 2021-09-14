package com.example.getdetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.j2objc.annotations.ObjectiveCName;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.List;

public class FragmentActivity extends AppCompatActivity implements CameraAction.CameraDialogListener{
    private UserFragment fragment;
    private List<User> users;
    private static final String MyPrefs = "myPrefs";
    private static final String UserKey = "sharedUsers";
    private static Uri uri = null;
    private Boolean uriChanged = false;
    private static final int REQUEST_IMAGE_CAPTURE = 9;
    private static int Position = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        sharedPreferences = this.getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

        RetrieveUserData();

        SharedPreferences.Editor editor = sharedPreferences.edit();// myObject - instance of MyObject
        editor.putString("Class", getClass().toString());
        editor.apply();


        Bundle args = new Bundle();
        if (fragment == null) {
            fragment = new UserFragment();
        }
//        args.putString("UserInfo", getIntent().getStringExtra("UserInfo"));
//        args.putString("imageUrl", getIntent().getStringExtra("imageUrl"));
        args.putString("source", "Recycler");
        args.putInt("Position", sharedPreferences.getInt("Position", 0));
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_view, fragment);
        transaction.commit();

        Position = getIntent().getIntExtra("Position", 2);
    }
    @Override
    public void onStart(){
        super.onStart();
//        SharedPreferences.Editor editor = sharedPreferences.edit();// myObject - instance of MyObject
//        editor.putString("Class", getClass().toString());
//        editor.apply();
    }

//    public void showCameraDialog() {
//        // Create an instance of the dialog fragment and show it
//        DialogFragment dialog = new CameraAction();
//        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
//    }

    @Override
    public void onBackPressed(){
        Intent backIntent = new Intent(this, RecyclerViewActivity.class);
        startActivity(backIntent);
    }
    @Override
    public void onPause(){
        super.onPause();
//        SaveUserData();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(this, "Camera Access Denied", Toast.LENGTH_LONG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            uri = getImageUri(this, bitmap);
            uriChanged = true;

            Picasso.with(this).load(uri).resize(500, 500).centerCrop().into(fragment.image);
            fragment.image.setVisibility(View.VISIBLE);

        }
    }
    public void SaveUserData(){
        if (uriChanged == true) {
            RetrieveUserData();
            users.get(Position).imageUri = "" + uri;


            uriChanged = false;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonUsers = gson.toJson(users);

        editor.putString(UserKey, jsonUsers);
        editor.apply();
    }
//
    public void RetrieveUserData() {
        String serializedObject = sharedPreferences.getString(UserKey, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<User>>() {
            }.getType();
            users = gson.fromJson(serializedObject, type);
        }

    }
//
//
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}