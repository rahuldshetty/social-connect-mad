package com.rahuldshetty.socialconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.adapters.MenuMessageAdapter;
import com.rahuldshetty.socialconnect.modals.UserMessage;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MessageMenuActivity extends AppCompatActivity {

    ArrayList<UserMessage> userMessages;

    private RecyclerView recyclerView;
    private MenuMessageAdapter adapter;
    private ProgressBar progressBar;

    private String myUid;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_menu);

        progressBar = findViewById(R.id.menuMsgProgressBar);
        userMessages = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        myUid = firebaseUser.getUid();

        recyclerView = findViewById(R.id.message_menu_recycler);

        loadData();
    }

    void loadData(){
        progressBar.setVisibility(View.VISIBLE);
        


    }



}
