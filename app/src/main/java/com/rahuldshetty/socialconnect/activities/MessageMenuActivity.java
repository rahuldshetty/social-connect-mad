package com.rahuldshetty.socialconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.adapters.MenuMessageAdapter;
import com.rahuldshetty.socialconnect.modals.User;
import com.rahuldshetty.socialconnect.modals.UserMessage;
import com.rahuldshetty.socialconnect.utils.TimePretty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MessageMenuActivity extends AppCompatActivity {

    ArrayList<UserMessage> userMessages;

    private RecyclerView recyclerView;
    private MenuMessageAdapter adapter;
    private ProgressBar progressBar;

    private String myUid;
    private  ArrayList<String> list ;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private FirebaseFunctions firebaseFunctions;


    private boolean isResume = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_menu);

        progressBar = findViewById(R.id.menuMsgProgressBar);
        list = new ArrayList<String>();
        userMessages = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        firebaseFunctions = FirebaseFunctions.getInstance();

        myUid = firebaseUser.getUid();

        recyclerView = findViewById(R.id.message_menu_recycler);
        LinearLayoutManager mgr = new LinearLayoutManager(MessageMenuActivity.this);
        recyclerView.setLayoutManager(mgr);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mgr.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new MenuMessageAdapter(MessageMenuActivity.this,userMessages);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    void loadData(){
        progressBar.setVisibility(View.VISIBLE);
        list.clear();
        userMessages.clear();
        adapter.notifyDataSetChanged();
        Map<String, Object> data = new HashMap<>();
        data.put("foo", myUid);
        getCollections().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful())
                {
                    String data = ( task.getResult() );
                    try {
                        JSONArray jsonArray = new JSONArray(data);
                        for (int i=0; i<jsonArray.length(); i++) {
                            list.add( jsonArray.getString(i) );
                        }

                        // Have all users
                        // get top msg and user details.
                        for(int i=0;i<list.size();i++)
                        {
                            getDetails(list.get(i),i == list.size()-1 );
                        }

                    }
                    catch (Exception e){}
                }
                else{
                    Toast.makeText(getApplicationContext(),"Failed to load:" + task.getException(),Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });
    }



    void getDetails(final String uid, final boolean isLast){
        db.collection("USERS")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();
                            final String name = doc.getString("name");
                            final String image = doc.contains("image")? doc.getString("image") : null;
                            // get content from message

                            db.collection("MESSAGE")
                                    .document(myUid)
                                    .collection(uid)
                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                    .limit(1)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                                final String desc = doc.getString("msg");
                                                final long time = doc.getLong("timestamp");
                                                final String timestamp = TimePretty.getTimeAgo(time);

                                                UserMessage msg = new UserMessage();
                                                msg.setUid(uid);
                                                msg.setDesc(desc);
                                                msg.setImage(image);
                                                msg.setName(name);
                                                msg.setTime(time);
                                                msg.setTimestamp(timestamp);

                                                userMessages.add(msg);
                                                adapter.notifyDataSetChanged();
                                                if(isLast)
                                                {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }

                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(),"Failed to load:" + task.getException(),Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Failed to load:" + task.getException(),Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private Task<String> getCollections() {
        // Create the arguments to the callable function.

        return firebaseFunctions
                .getHttpsCallable("getCollections")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        Gson g = new Gson();
                        Map<String,Object> map = (HashMap<String,Object>) task.getResult().getData();
                        return g.toJson(map.get("users"));
                    }
                });
    }



}
