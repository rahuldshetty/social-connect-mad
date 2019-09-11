package com.rahuldshetty.socialconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.adapters.MessageAdapter;
import com.rahuldshetty.socialconnect.modals.Message;
import com.rahuldshetty.socialconnect.utils.OnVerticalScrollListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView sendBtn;
    private EditText editText;
    private ProgressBar progressBar;
    private TextView nameView;
    private CircleImageView circleImageView;

    private DocumentSnapshot lastQuery = null,newQuery = null;
    private MessageAdapter adapter;

    private String myUID,otherUID,otherName,otherImage,myName;
    private ArrayList<Message> messageArrayList;

    private static int SIZE = 10;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageArrayList = new ArrayList<>();


        otherUID = getIntent().getStringExtra("otherUID");
        otherName = getIntent().getStringExtra("otherName");
        otherImage = getIntent().getStringExtra("otherImage");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        myUID = firebaseUser.getUid();

        recyclerView = findViewById(R.id.message_recycler);
        sendBtn = findViewById(R.id.message_send);
        editText = findViewById(R.id.message_edit);
        progressBar = findViewById(R.id.messageProgressBar);
        nameView = findViewById(R.id.message_name);
        circleImageView = findViewById(R.id.message_image_view);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String msg = editText.getText().toString();
                if(!TextUtils.isEmpty(msg)){

                    // Check if no view has focus:
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    final long timestamp = new Date().getTime();
                    final Map<String,Object> map = new HashMap<>();

                    map.put("msg",msg);
                    map.put("timestamp",timestamp);
                    map.put("status","sent");

                    db.collection("MESSAGE")
                            .document(myUID)
                            .collection(otherUID)
                            .add(map)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful())
                                    {
                                        Map<String,Object> map = new HashMap<>();
                                        map.put("msg",msg);
                                        map.put("timestamp",timestamp);
                                        map.put("status","recv");
                                        editText.setText("");

                                        db.collection("MESSAGE")
                                                .document(otherUID)
                                                .collection(myUID)
                                                .add(map)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if(task.isSuccessful())
                                                        {

                                                            if(newQuery==null)
                                                                db.collection("MESSAGE")
                                                                        .document(myUID)
                                                                        .collection(otherUID)
                                                                        .orderBy("timestamp", Query.Direction.ASCENDING)
                                                                        .limit(1)
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if(task.isSuccessful())
                                                                                {
                                                                                    newQuery = task.getResult().getDocuments().get(0);

                                                                                    // TODO: ADD ALL USER


                                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                                }
                                                                                else{
                                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                                }
                                                                            }
                                                                        });
                                                            else
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                        }
                                                        else{
                                                            Toast.makeText(MessageActivity.this,"Error in Database.",Toast.LENGTH_SHORT).show();
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }
                                                    }
                                                });
                                    }
                                    else{
                                        Toast.makeText(MessageActivity.this,"Error sending.",Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }

                                }
                            });

                }
            }
        });


        recyclerView.setOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledToTop() {
                super.onScrolledToTop();
                // do something
                progressBar.setVisibility(View.VISIBLE);
                if(lastQuery!=null) {
                    db.collection("MESSAGE")
                            .document(myUID)
                            .collection(otherUID)
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .startAfter(lastQuery)
                            .limit(SIZE)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                                        int start = messageArrayList.size();
                                        for (int i = 0; i < docs.size() - 1; i++) {
                                            messageArrayList.add(docs.get(i).toObject(Message.class));
                                            lastQuery = docs.get(i);
                                        }
                                        adapter.notifyItemRangeInserted(start, messageArrayList.size());
                                        progressBar.setVisibility(View.INVISIBLE);
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(MessageActivity.this, "Error Loading.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                else   progressBar.setVisibility(View.INVISIBLE);
            }
        });



        loadProfile();

    }

    void getNewData(QuerySnapshot snapshots){
        System.out.println(newQuery);
        if(newQuery==null)
            db.collection("MESSAGE")
                    .document(myUID)
                    .collection(otherUID)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                newQuery = task.getResult().getDocuments().get(0);
                                db.collection("MESSAGE")
                                        .document(myUID)
                                        .collection(otherUID)
                                        .orderBy("timestamp", Query.Direction.ASCENDING)
                                        .startAt(newQuery)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                                        messageArrayList.add(0, doc.toObject(Message.class));
                                                        newQuery = doc;
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                    recyclerView.scrollToPosition(0);
                                                } else {

                                                }
                                            }
                                        });
                            }
                        }
                    });
        else{
            db.collection("MESSAGE")
                    .document(myUID)
                    .collection(otherUID)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .startAfter(newQuery)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                    messageArrayList.add(0, doc.toObject(Message.class));
                                    newQuery = doc;
                                }
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(0);
                            } else {

                            }
                        }
                    });

        }
    }

    void loadProfile(){
        progressBar.setVisibility(View.VISIBLE);
        loadProfileMeta();
        getData();
    }

    void loadProfileMeta(){
        nameView.setText(otherName);
        Glide.with(MessageActivity.this).load(otherImage).placeholder(R.drawable.profile).into(circleImageView);
    }

    void getData(){

        db.collection("MESSAGE")
                .document(myUID)
                .collection(otherUID)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(SIZE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            List<DocumentSnapshot> docs = task.getResult().getDocuments();

                            for(DocumentSnapshot doc:docs){
                                if(newQuery==null)newQuery = doc;
                                messageArrayList.add( doc.toObject(Message.class) );
                                lastQuery = doc;
                            }

                            db.collection("USERS")
                                    .document(myUID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                myName = task.getResult().getString("name");
                                                adapter = new MessageAdapter(MessageActivity.this,messageArrayList,myName,otherName);
                                                LinearLayoutManager manager = new LinearLayoutManager(MessageActivity.this);
                                                manager.setReverseLayout(true);
                                                recyclerView.setLayoutManager(manager);
                                                recyclerView.setAdapter(adapter);
                                                recyclerView.scrollToPosition(0);
                                                progressBar.setVisibility(View.INVISIBLE);

                                                db.collection("MESSAGE")
                                                        .document(myUID)
                                                        .collection(otherUID)
                                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                                                    getNewData(queryDocumentSnapshots);
                                                                }
                                                            }
                                                        });

                                            }
                                            else{
                                                Toast.makeText(MessageActivity.this,"Error loading.",Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(MessageActivity.this,"Error loading.",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

}
