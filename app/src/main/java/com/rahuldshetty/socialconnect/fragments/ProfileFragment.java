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
import com.rahuldshetty.socialconnect.MainActivity;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.activities.EditActivity;

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

    private FirebaseFirestore userDatabase;
    private FirebaseAuth firebaseAuth;

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

        uid = MainActivity.otherUserID;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (button.getText().toString()){

                    case "Edit Account":
                        Intent act = new Intent(MainActivity.mainActivity, EditActivity.class);
                        startActivity(act);
                        break;


                }


            }
        });


        loadProfile();



        return view;
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
                            }
                            if(snapshot.contains("bgimage")){
                                bgImageLink = snapshot.getString("bgimage");
                            }

                            nameView.setText(name);
                            cityView.setText(city);
                            descView.setText(desc);

                            if(imageLink!=null){
                                Glide.with(MainActivity.mainContext).load(imageLink).into(profileImageView);
                            }

                            if(bgImageLink!=null){
                                Glide.with(MainActivity.mainContext).load(bgImageLink).into(backgroundImageView);
                            }


                            //TODO: FRIEND COUNT
                            //TODO: POST DISPLAYING

                            //TODO: BUTTONS
                            if (firebaseAuth.getCurrentUser().getUid().equals(uid)) {
                                // If same users.
                                button2.setVisibility(View.INVISIBLE);
                                button2.setEnabled(false);
                                button.setText("Edit Account");
                            }


                            progressBar.setVisibility(View.INVISIBLE);


                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.mainContext, "Error loading. Please check your internet and try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
