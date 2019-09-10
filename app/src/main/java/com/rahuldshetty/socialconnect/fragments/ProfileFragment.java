package com.rahuldshetty.socialconnect.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.rahuldshetty.socialconnect.MainActivity;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.activities.EditActivity;
import com.rahuldshetty.socialconnect.activities.FriendListActivity;
import com.rahuldshetty.socialconnect.activities.MessageActivity;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private View view;

    private TextView nameView,descView,cityView,friendCountView,postCountView;
    private Button button,button2;
    private CircleImageView profileImageView;
    private ImageView backgroundImageView,friendImageView,postImageView;
    private ProgressBar progressBar;

    private String uid,imageLink,bgImageLink;
    private String myUid,otherUid;
    private String otherName,otherImage;

    private FirebaseFirestore userDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore friendDatabase;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameView = view.findViewById(R.id.profile_name);
        descView = view.findViewById(R.id.profile_desc);
        cityView = view.findViewById(R.id.profile_city);
        button = view.findViewById(R.id.profile_button);
        profileImageView = view.findViewById(R.id.profile_image);
        backgroundImageView = view.findViewById(R.id.profile_background);
        progressBar = view.findViewById(R.id.profileProgressBar);
        button2  = view.findViewById(R.id.profile_button2);
        friendCountView = view.findViewById(R.id.friendViewCount);
        postCountView = view.findViewById(R.id.postViewCount);
        friendImageView = view.findViewById(R.id.friendImageView);
        postImageView = view.findViewById(R.id.postImageView);



        userDatabase = FirebaseFirestore.getInstance();
        firebaseAuth  = FirebaseAuth.getInstance();
        friendDatabase = FirebaseFirestore.getInstance();

        uid = MainActivity.otherUserID;
        myUid = firebaseAuth.getCurrentUser().getUid();
        otherUid =  uid;

        button.setBackgroundColor(getResources().getColor(R.color.defaultProfile));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (button.getText().toString()){

                    case "Edit Account":
                        Intent act = new Intent(MainActivity.mainActivity, EditActivity.class);
                        startActivity(act);
                        break;

                    case "Send Friend Request":
                        // add to database
                        sendFriendRequest();
                        break;

                    case "Accept Friend Request":
                        // accept
                        acceptFriendRequest();
                        break;

                    case "Cancel Friend Request":
                        // remove items from database
                        cancelFriendRequest();
                        break;
                    case "Message":
                        Intent activity = new Intent(MainActivity.mainActivity, MessageActivity.class);
                        activity.putExtra("myUID",myUid);
                        activity.putExtra("otherUID",otherUid);
                        activity.putExtra("otherName",otherName);
                        activity.putExtra("otherImage",otherImage);
                        startActivity(activity);
                        break;
                }

            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelFriendRequest();
                button.setText("Send Friend Request");
                button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                button.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
                button2.setVisibility(View.INVISIBLE);
                button2.setEnabled(false);
            }
        });

        friendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFriends();
            }
        });

        friendCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFriends();
            }
        });

        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: POST COUNT OPENER
            }
        });

        postCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    void loadProfile(){
        progressBar.setVisibility(View.VISIBLE);
        userDatabase.collection("USERS")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String name = "", city = "", desc = "";
                        int friendCount = 0;

                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();

                            if (snapshot.contains("name")) {
                                name = snapshot.getString("name");
                                otherName = name;
                            }
                            if (snapshot.contains("city")) {
                                city = snapshot.getString("city");
                            }
                            if (snapshot.contains("desc")) {
                                desc = snapshot.getString("desc");
                            }

                            nameView.setText(name);
                            cityView.setText(city);
                            descView.setText(desc);

                            //some layout hacks
                            if (desc.equals("")) {
                                descView.setHeight(0);
                            }
                            if (city.equals("")) {
                                cityView.setHeight(0);
                            }
                            if(snapshot.contains("image")){
                                imageLink = snapshot.getString("image");
                                otherImage = imageLink;
                            }
                            if(snapshot.contains("bgimage")){
                                bgImageLink = snapshot.getString("bgimage");
                            }

                            nameView.setText(name);
                            cityView.setText(city);
                            descView.setText(desc);

                            if(imageLink!=null){
                                Glide.with(MainActivity.mainContext).load(imageLink).placeholder(R.drawable.profile).into(profileImageView);
                            }

                            if(bgImageLink!=null){
                                Glide.with(MainActivity.mainContext).load(bgImageLink).placeholder(R.drawable.background_start).into(backgroundImageView);
                            }

                            friendDatabase.collection("FRIEND")
                                    .document("FRIEND")
                                    .collection(uid)
                                    .whereEqualTo("status","friends")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful())
                                            {

                                                int friends_count = task.getResult().getDocuments().size();
                                                friendCountView.setText(friends_count + "");
                                                //TODO: POST COUNT DISPLAYING
                                                if (firebaseAuth.getCurrentUser().getUid().equals(uid)) {
                                                    // If same users.
                                                    button2.setVisibility(View.INVISIBLE);
                                                    button2.setEnabled(false);
                                                    button.setText("Edit Account");
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }

                                                else{
                                                    // they are different users
                                                    handleFriends();
                                                }


                                            }
                                            else{
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(MainActivity.mainContext, "Error loading. Please check your internet and try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });




                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.mainContext, "Error loading. Please check your internet and try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    void sendFriendRequest(){
        progressBar.setVisibility(View.VISIBLE);
        Date date = new Date();
        long time = date.getTime();

        final Map<String,Object> map = new HashMap<>();
        map.put("status","sent");
        map.put("timestamp",time);

        friendDatabase.collection("FRIEND").document("FRIEND").collection(myUid)
                .document(otherUid)
                .set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            map.put("status","recv");
                            friendDatabase.collection("FRIEND").document("FRIEND").collection(otherUid)
                                    .document(myUid)
                                    .set(map,SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                // done with sending request...
                                                button.setText("Cancel Friend Request");
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                            else{
                                                Toast.makeText(MainActivity.mainContext, "Error saving to database. Please check your internet and try again.", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(MainActivity.mainContext, "Error saving to database. Please check your internet and try again.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                });
    }


    void cancelFriendRequest(){
        progressBar.setVisibility(View.VISIBLE);
        friendDatabase.collection("FRIEND").document("FRIEND").collection(myUid)
                .document(otherUid)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            friendDatabase.collection("FRIEND").document("FRIEND").collection(otherUid)
                                    .document(myUid)
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                // cancelled friend req
                                                button.setText("Send Friend Request");
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                            else{
                                                Toast.makeText(MainActivity.mainContext, "Error saving to database. Please check your internet and try again.", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                            loadProfile();
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(MainActivity.mainContext, "Error saving to database. Please check your internet and try again.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });


    }


    void acceptFriendRequest(){
        progressBar.setVisibility(View.VISIBLE);
        Date date = new Date();
        long time = date.getTime();
        Map<String,Object> map = new HashMap<>();
        map.put("status","friends");
        map.put("timestamp",time);
        final Map<String,Object> maps = map;
        friendDatabase.collection("FRIEND")
                .document("FRIEND")
                .collection(myUid)
                .document(otherUid)
                .update(maps)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            friendDatabase.collection("FRIEND")
                                    .document("FRIEND")
                                    .collection(otherUid)
                                    .document(myUid)
                                    .update(maps)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                // both friends
                                                button.setBackgroundColor(getResources().getColor(R.color.defaultProfile));
                                                button.setText("Message");
                                                button.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                                                button2.setText("Unfriend");
                                                button2.setBackgroundColor(getResources().getColor(R.color.noColor));
                                                button2.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                            else{
                                                Toast.makeText(MainActivity.mainContext, "Error saving to database. Please check your internet and try again.", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                            loadProfile();
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(MainActivity.mainContext, "Error saving to database. Please check your internet and try again.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }


    void handleFriends(){

        // cases:
        //  no friends
        // one sent friend req
        // other rec friend req

        myUid = firebaseAuth.getCurrentUser().getUid();
        otherUid = uid;

        friendDatabase.collection("FRIEND").document("FRIEND").collection(myUid)
                .document(otherUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            String status=null;
                            DocumentSnapshot snapshot = task.getResult();

                            if(snapshot.contains("status"))
                                status = snapshot.getString("status");

                            if(status == null){
                                // not friend
                                button.setText("Send Friend Request");
                                button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                button.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
                                button2.setVisibility(View.INVISIBLE);
                                button2.setEnabled(false);

                            }
                            else if(status.equals("recv")){
                                //friend req recv
                                button.setText("Accept Friend Request");
                                button.setBackgroundColor(getResources().getColor(R.color.yesColor));
                                button.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
                                button2.setText("Decline Friend Request");
                                button2.setBackgroundColor(getResources().getColor(R.color.noColor));
                                button2.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
                            }
                            else if(status.equals("sent")){
                                // self sent to other
                                button.setText("Cancel Friend Request");
                                button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                button.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
                                button2.setVisibility(View.INVISIBLE);
                                button2.setEnabled(false);
                            }
                            else if(status.equals("friends")){
                                button.setText("Message");
                                button2.setText("Unfriend");
                                button2.setBackgroundColor(getResources().getColor(R.color.noColor));
                                button2.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark));
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        else{
                            Toast.makeText(MainActivity.mainContext, "Error loading. Please check your internet and try again.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }


    void showFriends(){

        if(myUid.equals(otherUid))
        {
            openFriendsIntent(myUid);
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            friendDatabase.collection("FRIEND")
                    .document("FRIEND")
                    .collection(myUid)
                    .document(otherUid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            progressBar.setVisibility(View.INVISIBLE);
                            if(task.isSuccessful())
                            {
                                DocumentSnapshot docs = task.getResult();
                                if(docs.contains("status") && docs.getString("status").equals("friends"))
                                    openFriendsIntent(otherUid);
                                else{
                                    Toast.makeText(MainActivity.mainContext,"You cannot access other's friend list.",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.mainContext,"Connection Error.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    void openFriendsIntent(String uid){
        Intent act = new Intent(MainActivity.mainActivity, FriendListActivity.class);
        act.putExtra("UID",uid);
        startActivity(act);
    }
}
