package com.rahuldshetty.socialconnect.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rahuldshetty.socialconnect.MainActivity;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.adapters.NotificationAdapter;
import com.rahuldshetty.socialconnect.modals.NotificationModal;
import com.rahuldshetty.socialconnect.modals.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private View view;
    private ListView listView;

    private NotificationAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseUser user;

    private ProgressBar progressBar;

    private ArrayList<NotificationModal> users;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_notification, container, false);

        listView = view.findViewById(R.id.notification_list_view);
        progressBar = view.findViewById(R.id.notifProgressBar);

        users = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    void loadData(){
        progressBar.setVisibility(View.VISIBLE);
        // get all req details
        db.collection("FRIEND")
                .document("FRIEND")
                .collection(user.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            QuerySnapshot snapshots = task.getResult();
                            List<DocumentSnapshot> docs = snapshots.getDocuments();

                            users.clear();

                            List<DocumentSnapshot> temp = new ArrayList<>();

                            for(int i=0;i<docs.size();i++)
                            {
                                if(!docs.get(i).getString("status").equals("friends"))
                                    temp.add(docs.get(i));
                            }

                            if(temp.size()==0)
                                progressBar.setVisibility(View.INVISIBLE);

                            for(int i=0;i<temp.size();i++)
                            {
                                storeData2Users(temp.get(i),i==temp.size()-1);
                            }

                        }
                        else{
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.mainContext,"Loading Error.",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    void storeData2Users(DocumentSnapshot snapshot, final boolean isLast){
        final String otheruid = snapshot.getId();
        String statusReal = snapshot.getString("status");

        if(statusReal.equals("friends"))
            return;

        final long timestamp = snapshot.getLong("timestamp");

        final String status = (statusReal.equals("sent") ?  "You sent a request." : "You got a request." );

        db.collection("USERS")
                .document(otheruid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            DocumentSnapshot doc = task.getResult();

                            final NotificationModal modal = new NotificationModal();
                            modal.setName(doc.getString("name"));
                            modal.setImage(doc.getString("image"));
                            modal.setStatus(status);
                            modal.setUid(otheruid);
                            modal.setTimestamp(timestamp);
                            users.add(modal);

                            if(isLast)
                            {
                                adapter = new NotificationAdapter(MainActivity.mainContext,users);
                                listView.setAdapter(adapter);
                                progressBar.setVisibility(View.INVISIBLE);
                            }


                        }
                        else{
                            Toast.makeText(MainActivity.mainContext,"Loading Error.",Toast.LENGTH_SHORT).show();
                        }
                        if(isLast)
                            progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

}
