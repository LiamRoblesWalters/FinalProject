package com.example.getdetails;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    public EditText userInfo;
    ImageView image;
    EditText email;
    EditText address;
    private Button saveInfo;
    private String[] info;
    private List<User> users;
    private static final String MyPrefs = "myPrefs";
    private static final String Info = "infoKey";
    Boolean textEdited = false;
    private static int Position = 0;


    private SharedPreferences sharedPreferences;


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
        setHasOptionsMenu(true);
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

        sharedPreferences = getActivity().getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        users = RecyclerViewActivity.userViewModel.getAllUsers().getValue();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new myBroadcastReceiver(),
                new IntentFilter("userFilter"));
//

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
        saveInfo.setOnClickListener(this);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.user_menu, menu);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.fragment_image:
                Intent broadcastIntent = new Intent("userFilter");
                broadcastIntent.putExtra("UserName", userInfo.getText().toString());
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(broadcastIntent);
                showCameraDialog();
                break;
//
            case R.id.fragment_save_info:
                userInfo.setFocusable(false);
                email.setFocusable(false);
                address.setFocusable(false);
                saveInfo.setVisibility(View.GONE);

                textEdited = true;

                break;
//
//
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {

            case R.id.edit:
                saveInfo.setVisibility(View.VISIBLE);
                userInfo.setFocusableInTouchMode(true);
                email.setFocusableInTouchMode(true);
                address.setFocusableInTouchMode(true);

                return true;

            case R.id.notes:
                Intent writingIntent = new Intent(getActivity(), ContactNotes.class);
                writingIntent.putExtra("UserName", userInfo.getText().toString().replace("Name:", "").trim());
                startActivity(writingIntent);
                return true;

            case R.id.send_message:
                Intent messageIntent = new Intent(Intent.ACTION_SEND);
                messageIntent.putExtra(Intent.EXTRA_TEXT, "Here's a message");
                messageIntent.setData(Uri.parse("mailto:"));
                String[] address = {email.getText().toString().replace("Email:", "").trim()};
                messageIntent.putExtra(Intent.EXTRA_EMAIL, address);
                messageIntent.setType("text/plain");
                Intent chooser = Intent.createChooser(messageIntent, "Send Email");
                startActivity(chooser);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }



    @Override
    public void onPause(){
        super.onPause();
//
    }

    public void showCameraDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CameraAction();
        dialog.show(getParentFragmentManager(), "NoticeDialogFragment");
    }

}