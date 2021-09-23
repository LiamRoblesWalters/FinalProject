package com.example.getdetails;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class ContactNotes extends AppCompatActivity {
    private static final int WRITE_CODE = 99;
    private static final int READ_CODE = 101;
    private static final int SELECT_PHOTO = 27;
    private EditText contactNotes;
    private ImageView photoPic;
    private String userName;
    private static final String MyPrefs = "myPrefs";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_notes);
        sharedPreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();// myObject - instance of MyObject
        editor.putString("Class", getClass().toString());
        editor.apply();

        contactNotes = findViewById(R.id.contact_notes);
        photoPic = findViewById(R.id.photos_pic);
        userName = getIntent().getStringExtra("UserName");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_notes:
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_CODE);
                return true;

            case R.id.read_notes:

                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_CODE);
                return true;

            case R.id.add_image:
                chooseImage();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    public void writeFile(){

        try {
            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path.getAbsolutePath() + "/Documents");
            dir.mkdirs();
            File myUserFile = new File(dir, userName + "myUserNotes.txt");
            if (!myUserFile.exists()){
                myUserFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(myUserFile);
            fos.write(contactNotes.getText().toString().getBytes());
            fos.close();
            contactNotes.setText("");
            Toast.makeText(this, "Did I get here?", Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(this, "I got an error here", Toast.LENGTH_LONG).show();
        }

    }

    public void readFile(){
        String notes = "";
        try {
            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path.getAbsolutePath() + "/Documents");
            dir.mkdirs();
            File myUserFile = new File(dir, userName + "myUserNotes.txt");
            if (!myUserFile.exists()){
                myUserFile.createNewFile();
            }
            FileInputStream fis = new FileInputStream(myUserFile);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader reader = new BufferedReader(new InputStreamReader(dis));
            String line;
            while ((line = reader.readLine()) != null){
                notes = notes + "\n" + line;
            }
            contactNotes.setText(notes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
            if (permission == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                writeFile();
            } else{
                readFile();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                writeFile();
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == READ_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readFile();
            }
            else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void chooseImage(){
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(imageIntent, "Select Photo"), SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == SELECT_PHOTO){
            Toast.makeText(this, "We're in!", Toast.LENGTH_LONG)
                    .show();
            Uri photoUri = data.getData();
            if (photoUri != null){
                photoPic.setImageURI(photoUri);
                photoPic.setVisibility(View.VISIBLE);
            }
        } else{
            Toast.makeText(this, "Something's wrong here", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, FragmentActivity.class);
        startActivity(intent);
        finish();
    }
}