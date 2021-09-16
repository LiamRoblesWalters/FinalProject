package com.example.getdetails;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
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
import java.lang.reflect.Type;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements View.OnClickListener{

    private EditText userInfo;
    ImageView image;
    private EditText email;
    private EditText address;
    private Button saveInfo;
    private Button editInfo;
    private String[] info;
    private List<User> users;
    private static final String MyPrefs = "myPrefs";
    private static final String UserKey = "sharedUsers";
    private static final String Info = "infoKey";
    private Boolean textEdited = false;
    private static final int REQUEST_IMAGE_CAPTURE = 9;
    private static int Position = 0;

    private SharedPreferences sharedPreferences;

//    public ImageView getImage() {
//        return image;
//    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        super(R.layout.fragment_user);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        //setContentView(R.layout.activity_user_list);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        userInfo = getActivity().findViewById(R.id.fragment_user_info);
        if (getActivity() == null){
            Log.d("Error", "onCreate: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + getActivity());
        }else{
            Log.d("userInfo", "onCreate: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + userInfo);
            Log.d("Error", "onCreate: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + getActivity());
        }
        image = getActivity().findViewById(R.id.fragment_image);
        email = getActivity().findViewById(R.id.email_fragment);
        address = getActivity().findViewById(R.id.address_fragment);
        saveInfo = getActivity().findViewById(R.id.fragment_save_info);
        editInfo = getActivity().findViewById(R.id.edit_button);

        sharedPreferences = getActivity().getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

        RetrieveUserData();

        if (getArguments() != null) {
            String source = getArguments().getString("source");
            if (source.equals("Recycler")) {
                Position = getArguments().getInt("Position", 0);
                String user_details = sharedPreferences.getString("UserInfo", null);
                info = user_details.split("\n");

                if (info[1].replace("Email", "").trim().length() < 3) {
                    if (sharedPreferences.getString(Info, "") == "") {
                        userInfo.setText("Name: " + users.get(Position).name);
                        email.setText("Email: " + users.get(Position).email);
                        address.setText("Street: " + users.get(Position).address.street);
//                        userInfo.setFocusableInTouchMode(true);
//                        email.setFocusableInTouchMode(true);
//                        address.setFocusableInTouchMode(true);
//                        saveInfo.setVisibility(View.VISIBLE);
                    } else {
                        userInfo.setText("Name: " + users.get(Position).name);
                        email.setText("Email: " + users.get(Position).email);
                        address.setText("Street: " + users.get(Position).address.street);
                    }

                } else {
                    userInfo.setText("Name: " + users.get(Position).name);
                    email.setText("Email: " + users.get(Position).email);
                    address.setText("Street: " + users.get(Position).address.street);
                }
                //}

                Picasso.with(getActivity()).load(users.get(Position).imageUri).resize(500, 500)
                        .centerCrop()
                        .into(image);
                image.setVisibility(View.VISIBLE);
            }

        }


        image.setOnClickListener(this);
//
        editInfo.setOnClickListener(this);

        saveInfo.setOnClickListener(this);

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.fragment_image:
                showCameraDialog();
                break;

            case R.id.fragment_save_info:
                //tring personalInfo = String.format("%s\nEmail: %s\nAddress: %s", userInfo.getText().toString(), email.getText().toString(), address.getText().toString());
                userInfo.setFocusable(false);
                email.setFocusable(false);
                address.setFocusable(false);
                saveInfo.setVisibility(View.GONE);
                editInfo.setVisibility(View.VISIBLE);

                textEdited = true;

                SaveUserData();

//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(Info, personalInfo);
//                editor.apply();
//                userInfo.setText(sharedPreferences.getString(Info, ""));
                break;

            case R.id.edit_button:
                saveInfo.setVisibility(View.VISIBLE);
                editInfo.setVisibility(View.GONE);
                userInfo.setFocusableInTouchMode(true);
                email.setFocusableInTouchMode(true);
                address.setFocusableInTouchMode(true);

                break;


            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    public void onPause(){
        super.onPause();
//        SaveUserData();
    }

    public void showCameraDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CameraAction();
        dialog.show(getParentFragmentManager(), "NoticeDialogFragment");
    }

//    @Override
//    public void onDialogPositiveClick(DialogFragment dialog) {
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
//
//    }
//
//    @Override
//    public void onDialogNegativeClick(DialogFragment dialog) {
//        Toast.makeText(getActivity(), "Camera Access Denied", Toast.LENGTH_LONG);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            uri = getImageUri(getActivity(), bitmap);
//            uriChanged = true;
//
//            Picasso.with(getActivity()).load(uri).resize(500, 500).centerCrop().into(image);
//            image.setVisibility(View.VISIBLE);
//
//        }
//    }
    public void SaveUserData(){
//        RetrieveUserData();
//        if (uriChanged == true) {
//            users.get(Position).imageUri = "" + uri;
//
//            uriChanged = false;
//        }
        if (textEdited == true) {
            users.get(Position).name = userInfo.getText().toString().replace("Name: ", "").trim
                    ();
            users.get(Position).email = email.getText().toString().replace("Email: ", "").trim
                    ();
            users.get(Position).address.street = address.getText().toString().replace("Street: ", "").trim
                    ();

            textEdited = false;

        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonUsers = gson.toJson(users);

        editor.putString(UserKey, jsonUsers);
        editor.commit();
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