package com.example.getdetails;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements CameraAction.CameraDialogListener{

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //setContentView(R.layout.activity_user_list);

        userInfo = getActivity().findViewById(R.id.user_info);
        image = getActivity().findViewById(R.id.imageView2);
        email = getActivity().findViewById(R.id.editTextEmail);
        address = getActivity().findViewById(R.id.editTextAddress);
        saveInfo = getActivity().findViewById(R.id.save_info);

        sharedPreferences = getActivity().getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

        RetrieveUserData();

        Intent intent = getActivity().getIntent();

        if (intent != null) {
            String source = intent.getStringExtra("source");
            if (source.equals("Recycler")) {
                Position = intent.getIntExtra("Position", 0);
                String user_details = intent.getStringExtra("UserInfo");
                info = user_details.split("\n");
                userInfo.setText(info[0]);
                if (info[1].replace("Email", "").trim().length() < 3) {
                    if (sharedPreferences.getString(Info, "") == "") {
                        email.setVisibility(View.VISIBLE);
                        address.setVisibility(View.VISIBLE);
                        saveInfo.setVisibility(View.VISIBLE);
                    } else {
                        userInfo.setText(sharedPreferences.getString(Info, ""));
                    }

                } else {
                    userInfo.setText(intent.getStringExtra("UserInfo"));
                }
                //}

                Picasso.with(getActivity()).load(intent.getStringExtra("imageUrl")).resize(500, 500)
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
        dialog.show(getParentFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(getActivity(), "Camera Access Denied", Toast.LENGTH_LONG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            uri = getImageUri(getActivity(), bitmap);
            uriChanged = true;

            Picasso.with(getActivity()).load(uri).resize(500, 500).centerCrop().into(image);
            image.setVisibility(View.VISIBLE);

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


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}