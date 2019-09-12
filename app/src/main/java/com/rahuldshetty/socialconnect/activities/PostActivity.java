package com.rahuldshetty.socialconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.admin.v1beta1.Progress;
import com.google.gson.Gson;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.adapters.CommentAdapter;
import com.rahuldshetty.socialconnect.modals.Comment;
import com.rahuldshetty.socialconnect.modals.Post;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    private Post post;
    private Gson gson;
    private ArrayList<Comment> comments;

    EditText commentText;
    TextView title,desc,username,likeCount,commentCount,time;
    ProgressBar progressBar;
    CircleImageView userImage;
    ImageView postImage,likeImage,commentSend;
    RecyclerView recyclerView;
    private CommentAdapter adapter;

    private String myUID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        gson = new Gson();

        post = gson.fromJson(getIntent().getStringExtra("post"),Post.class);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myUID = mAuth.getUid();



        comments = new ArrayList<>();
        adapter = new CommentAdapter(getApplicationContext(),comments);

        commentText = findViewById(R.id.postCommentEditText);
        title = findViewById(R.id.PostTitle);
        desc = findViewById(R.id.PostDesc);
        username = findViewById(R.id.PostuserName);
        likeCount = findViewById(R.id.PostLikeCount);
        commentCount = findViewById(R.id.PostCommentCount);
        userImage = findViewById(R.id.PostuserImage);
        postImage = findViewById(R.id.PostOpenImage);
        likeImage = findViewById(R.id.PostLikeImage);
        commentSend = findViewById(R.id.postCommentSend);
        recyclerView = findViewById(R.id.commentRecycler);
        progressBar = findViewById(R.id.postProgressBar);
        time = findViewById(R.id.PostTime);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(post.getLikeStatus().equals("likes")){
                    //remove item from db
                    db.collection("LIKES")
                            .document("LIKES")
                            .collection(post.getPid())
                            .document(myUID)
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        loadProfile();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Error updating likes.",Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
                else{
                    //add item to db
                    Map<String,String> map = new HashMap<>();
                    map.put("status","likes");
                    db.collection("LIKES")
                            .document("LIKES")
                            .collection(post.getPid())
                            .document(myUID)
                            .set(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        loadProfile();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Error updating likes.",Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }

            }
        });

        commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String msg = commentText.getText().toString();
                final long timestamp = new Date().getTime();
                if(!msg.trim().equals("") ){
                    commentText.setText("");
                    // Check if no view has focus:
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    // get current user details
                    db.collection("USERS")
                    .document(myUID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot doc = task.getResult();
                                        HashMap<String,Object> map = new HashMap<>();
                                        map.put("msg",msg);
                                        map.put("user",doc.getString("name"));
                                        map.put("timestamp",timestamp);
                                        map.put("uid",myUID);
                                        if(doc.contains("image"))
                                            map.put("image",doc.getString("image"));
                                        db.collection("COMMENTS")
                                                .document("COMMENTS")
                                                .collection(post.getPid())
                                                .add(map)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if(task.isSuccessful())
                                                            loadProfile();
                                                        else{
                                                            Toast.makeText(getApplicationContext(),"Error loading.",Toast.LENGTH_SHORT).show();
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                        }

                                                    }
                                                });


                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Error loading.",Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }

    void loadProfile(){
        comments.clear();
        progressBar.setVisibility(View.VISIBLE);
        title.setText(post.getTitle());
        desc.setText(post.getDesc());
        username.setText(post.getUserName());
        time.setText(post.getTimestamp());
        if(post.getUserImage()!=null)
            Glide.with(this).load(post.getUserImage()).placeholder(R.drawable.profile).into(userImage);
        if(post.getImage()!=null)
        {
            Glide.with(this).load(post.getImage()).placeholder(R.drawable.profile).into(postImage);
        }
        else
        {
            postImage.getLayoutParams().height = 0;
        }

        // get no of likes and my like status
        db.collection("LIKES")
                .document("LIKES")
                .collection(post.getPid())
                .whereEqualTo("status","likes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            int count = task.getResult().getDocuments().size();
                            likeCount.setText(count+"");
                            // get my like status
                            db.collection("LIKES")
                                    .document("LIKES")
                                    .collection(post.getPid())
                                    .document(myUID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                final DocumentSnapshot doc = task.getResult();
                                                if(doc.contains("status"))
                                                {
                                                    if(doc.getString("status").equals("likes")) {
                                                        post.setLikeStatus("likes");
                                                        likeImage.setImageResource(R.drawable.heart_filled);
                                                    }
                                                    else {
                                                        post.setLikeStatus("none");
                                                        likeImage.setImageResource(R.drawable.heart);
                                                    }
                                                }
                                                else{
                                                    post.setLikeStatus("none");
                                                    likeImage.setImageResource(R.drawable.heart);
                                                }
                                                //get comments
                                                db.collection("COMMENTS")
                                                        .document("COMMENTS")
                                                        .collection(post.getPid())
                                                        .orderBy("timestamp")
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                                                                    commentCount.setText(docs.size()+"");
                                                                    for(DocumentSnapshot doc:docs){
                                                                        Comment c = new Comment();
                                                                        c.setMsg(doc.getString("msg"));
                                                                        c.setUser(doc.getString("user"));
                                                                        if(doc.contains("image"))
                                                                            c.setImage(doc.getString("image"));
                                                                        else
                                                                            c.setImage(null);
                                                                        c.setTimestamp(doc.getLong("timestamp"));
                                                                        comments.add(c);
                                                                    }
                                                                    if(docs.size()!=0) {
                                                                        adapter.notifyDataSetChanged();
                                                                        recyclerView.smoothScrollToPosition(docs.size() - 1);
                                                                    }

                                                                    progressBar.setVisibility(View.INVISIBLE);

                                                                }
                                                                else{
                                                                    Toast.makeText(getApplicationContext(),"Error loading likes.",Toast.LENGTH_SHORT).show();
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                }

                                                            }
                                                        });


                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(),"Error loading likes.",Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }

                                        }
                                    });
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Error loading likes.",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });



    }

}
