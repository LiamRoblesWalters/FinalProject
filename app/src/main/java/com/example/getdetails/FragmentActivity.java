package com.example.getdetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
    private static Uri uri = null;
    private boolean uriChanged = false;
    private static final int REQUEST_IMAGE_CAPTURE = 19;
    private static int Position = 0;
    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);


        sharedPreferences = this.getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        users = RecyclerViewActivity.userViewModel.getAllUsers().getValue();



        SharedPreferences.Editor editor = sharedPreferences.edit();// myObject - instance of MyObject
        editor.putString("Class", getClass().toString());
        editor.apply();


        Bundle args = new Bundle();
        if (fragment == null) {
            fragment = new UserFragment();
        }
        args.putString("source", "Recycler");
        args.putInt("Position", sharedPreferences.getInt("Position", 0));
        fragment.setArguments(args);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_view, fragment);
            transaction.commit();
        }

        Position = getIntent().getIntExtra("Position", 2);




    }
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed(){
        Intent backIntent = new Intent(this, RecyclerViewActivity.class);
        startActivity(backIntent);
    }
    @Override
    public void onPause(){
        super.onPause();
        SaveUserData();
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
        Log.d("Am I here", "onActivityResult: >>>>>>>>>>>>>>>>>>");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            uri = getImageUri(this, bitmap);
            uriChanged = true;

            Picasso.with(this).load(uri).resize(500, 500).centerCrop().into(fragment.image);
            fragment.image.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Result", Toast.LENGTH_LONG).show();

//            SaveUserData();

        }else{
            Log.d("Error", "onActivityResult: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + uri);
        }
    }
    public void SaveUserData(){
        User user = users.get(Position);
        if (uriChanged == true) {
            user.imageUri = uri.toString();
//            intent.putExtra("Uri", uri);


            uriChanged = false;

        }
        if (fragment.textEdited == true){
            user.name = fragment.userInfo.getText().toString().replace("Name:", "").trim();
            user.email = fragment.email.getText().toString().replace("Email:", "").trim();
            user.address.street = fragment.address.getText().toString().replace("Street:", "").trim();
            fragment.textEdited = false;
        }
        RecyclerViewActivity.userViewModel.insert(user);

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