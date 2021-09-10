package com.example.getdetails;

import static android.app.PendingIntent.getActivity;
import static android.os.Environment.getExternalStoragePublicDirectory;

import static com.example.getdetails.BuildConfig.APPLICATION_ID;
import static com.example.getdetails.CameraAction.TAG;
import static com.example.getdetails.R.*;
import static com.example.getdetails.R.string.user_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class UserList extends AppCompatActivity implements CameraAction.CameraDialogListener{

    private TextView userInfo;
    private ImageView image;
    private EditText email;
    private EditText address;
    private Button saveInfo;
    private String[] info;
    private List<User> users;
    private static final String MyPrefs = "myPrefs";
    private static final String UserKey = "sharedUsers";
    private static final String Info = "infoKey";
    private static Uri uri = null;
    private Boolean uriChanged = false;
    private static final int REQUEST_IMAGE_CAPTURE = 9;
    private static int Position = 0;

    private SharedPreferences sharedPreferences;

    public ImageView getImage() {
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(layout.activity_user_list);

        userInfo = findViewById(id.user_info);
        image = findViewById(id.imageView2);
        email = findViewById(id.editTextEmail);
        address = findViewById(id.editTextAddress);
        saveInfo = findViewById(id.save_info);

        sharedPreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

        RetrieveUserData();

        Intent intent = getIntent();

        if (intent != null) {
            String source = intent.getStringExtra("source");
            if (source.equals("Recycler")){
                Position = intent.getIntExtra("Position", 0);
                String user_details = intent.getStringExtra("UserInfo");
                info = user_details.split("\n");
                userInfo.setText(info[0]);
                if (info[1].replace("Email", "").trim().length() < 3){
                    if (sharedPreferences.getString(Info, "") == "") {
                        email.setVisibility(View.VISIBLE);
                        address.setVisibility(View.VISIBLE);
                        saveInfo.setVisibility(View.VISIBLE);
                    } else{
                        userInfo.setText(sharedPreferences.getString(Info, ""));
                    }

                } else{
                    userInfo.setText(intent.getStringExtra("UserInfo"));
                }
                //}

                Picasso.with(this).load(intent.getStringExtra("imageUrl")).resize(500, 500)
                        .centerCrop()
                        .into(image);
                image.setVisibility(View.VISIBLE);
            }

        }




        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCameraDialog();
            }
        });

        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String personalInfo = String.format("%s\nEmail: %s\nAddress: %s", userInfo.getText().toString(), email.getText().toString(), address.getText().toString());
                email.setVisibility(View.GONE);
                address.setVisibility(View.GONE);
                saveInfo.setVisibility(View.GONE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Info, personalInfo);
                editor.apply();
                userInfo.setText(sharedPreferences.getString(Info, ""));

            }
        });


    }

    @Override
    public void onPause(){
        super.onPause();
        SaveUserData();
    }

    public void showCameraDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CameraAction();
        dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "File Creation Failed", Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.getdetails", photoFile);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        //}
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

            Picasso.with(this).load(uri).resize(500, 500).centerCrop().into(image);
            image.setVisibility(View.VISIBLE);

            galleryAddPic();
        }
    }
    public void SaveUserData(){
        if (uriChanged == true) {
            users.get(Position).imageUri = "" + uri;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String jsonUsers = gson.toJson(users);

            editor.putString(UserKey, jsonUsers);
            editor.apply();

            uriChanged = false;
        }

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
    public void onBackPressed()
    {
        // code here to show dialog
        Intent backIntent = new Intent(UserList.this, RecyclerViewActivity.class);
        startActivity(backIntent);
    }
    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}