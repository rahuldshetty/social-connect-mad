package com.rahuldshetty.socialconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.adapters.FriendGridAdapter;
import com.rahuldshetty.socialconnect.modals.User;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    GridView gridView;

    private FirebaseFirestore db;
    private FriendGridAdapter adapter;
    private ProgressBar progressBar;

    private ArrayList<User> users;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        gridView = findViewById(R.id.friend_grid);
        progressBar = findViewById(R.id.friendGridProgressBar);

        users = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        uid = getIntent().getExtras().getString("UID");


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems();
    }

    void loadItems(){
        // get friends
        users.clear();
        progressBar.setVisibility(View.VISIBLE);
        db.collection("FRIEND")
                .document("FRIEND")
                .collection(uid)
                .whereEqualTo("status","friends")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    ArrayList<String> friends = new ArrayList<>();
                    QuerySnapshot snapshots = task.getResult();
                    List<DocumentSnapshot> docs = snapshots.getDocuments();

                    for(DocumentSnapshot doc:docs){
                        friends.add(doc.getId());
                    }
                    // find details of friends
                    for(int i=0;i<friends.size();i++)
                    {
                        storeFriendData(friends.get(i),i == friends.size()-1 );
                    }

                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(FriendListActivity.this,"Connection Error.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void storeFriendData(String uid, final boolean islast){
        db.collection("USERS")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            User user = snapshot.toObject(User.class);
                            users.add(user);

                            if(islast){
                                progressBar.setVisibility(View.INVISIBLE);
                                // show it to user
                                adapter = new FriendGridAdapter(FriendListActivity.this,users);
                                gridView.setAdapter(adapter);
                            }

                        }
                        else{
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(FriendListActivity.this,"Connection Error.",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


}
