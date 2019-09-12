package com.rahuldshetty.socialconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rahuldshetty.socialconnect.MainActivity;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.adapters.PostAdapter;
import com.rahuldshetty.socialconnect.modals.Post;
import com.rahuldshetty.socialconnect.modals.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostListActivity extends AppCompatActivity {

    private ArrayList<Post> posts;

    private String myUid;

    private View view;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private LinearLayoutManager mgr;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        recyclerView = findViewById(R.id.postListRecycler);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        myUid = getIntent().getStringExtra("UID");

        posts = new ArrayList<>();

        mgr = new LinearLayoutManager(MainActivity.mainContext);

        progressBar = findViewById(R.id.plProgressBar);
        recyclerView.setLayoutManager(mgr);
        adapter = new PostAdapter(PostListActivity.this,posts);
        recyclerView.setAdapter(adapter);

        loadData();
    }


    void loadData(){
        progressBar.setVisibility(View.VISIBLE);

        db.collection("USERS")
                .document(myUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot doc = task.getResult();
                            User user = new User();
                            user.setName(doc.getString("name"));
                            user.setUid(doc.getString("uid"));
                            if(doc.contains("image"))
                                user.setImage(doc.getString("image"));
                            else
                                user.setImage(null);

                            findAllPost(user);
                        }
                        else{
                            Toast.makeText(MainActivity.mainContext,"Loading failed.",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    void findAllPost(final User user0){
        db.collection("POSTS")
                .whereEqualTo("uid",myUid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            User user = user0;
                            List<DocumentSnapshot> docs = task.getResult().getDocuments();
                            for(int j=0;j<docs.size();j++)
                            {
                                DocumentSnapshot doc = docs.get(j);
                                final Post post = new Post();
                                post.setUserName(user.getName());
                                post.setUserImage(user.getImage());
                                post.setTitle(doc.getString("title"));
                                post.setDesc(doc.getString("desc"));
                                post.setPid(doc.getId());
                                post.setTime(doc.getLong("timestamp"));
                                post.setUid(user.getUid());

                                Date date = new Date(post.getTime());

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ENGLISH);
                                sdf.applyPattern("EEE, d MMM yyyy");
                                String sMyDate = sdf.format(date);
                                post.setTimestamp(sMyDate);

                                if(doc.contains("image"))
                                    post.setImage(doc.getString("image"));
                                else
                                    post.setImage(null);

                                final boolean isLastPost = j == docs.size() -1 ;

                                posts.add(post);
                                if(isLastPost)
                                {
                                    // done loading.
                                    progressBar.setVisibility(View.INVISIBLE);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                        else{
                            Toast.makeText(MainActivity.mainContext,"Loading failed.",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                });
    }

}
