package com.rahuldshetty.socialconnect.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ArrayList<Post> posts;

    private String myUid;

    private View view;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private LinearLayoutManager mgr;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        myUid = auth.getUid();

        posts = new ArrayList<>();

        mgr = new LinearLayoutManager(MainActivity.mainContext);

        progressBar = view.findViewById(R.id.homeProgressBar);
        recyclerView = view.findViewById(R.id.postRecyclerView);
        recyclerView.setLayoutManager(mgr);
        adapter = new PostAdapter(MainActivity.mainContext,posts);
        recyclerView.setAdapter(adapter);

        loadData();

        return view;
    }

    void loadData(){
        progressBar.setVisibility(View.VISIBLE);

        if(myUid==null)
            return;


        // get list of friends
        db.collection("FRIEND")
                .document("FRIEND")
                .collection(myUid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            final ArrayList<String> friendIds = new ArrayList<>();
                            for(DocumentSnapshot doc:task.getResult().getDocuments())
                            {
                                friendIds.add(doc.getId());
                            }
                            friendIds.add(myUid);

                            // create map between uid and user object
                            final Map<String, User> mapUser = new HashMap<>();
                            for(int i =0 ; i<friendIds.size();i++)
                            {
                                final String uid = friendIds.get(i);
                                // collect user details
                                final boolean isLast = (i == friendIds.size() -1) ;
                                db.collection("USERS")
                                        .document(uid)
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
                                                    // add it to list
                                                    mapUser.put(uid,user);

                                                    if(isLast){
                                                        // compute all post

                                                        findAllPost(mapUser,friendIds);
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
                        else{
                            Toast.makeText(MainActivity.mainContext,"Loading failed.",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    void findAllPost(final Map<String,User> mapUser, ArrayList<String> uids){
        for(int i=0;i<uids.size();i++)
        {
            // get all posts of that user
            final String uid = uids.get(i);

            final boolean isLastUser = (i == uids.size() -1);

            db.collection("POSTS")
                    .whereEqualTo("uid",uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful())
                            {
                                User user = mapUser.get(uid);
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
                                    if(isLastPost && isLastUser)
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

}
