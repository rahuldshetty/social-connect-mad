package com.rahuldshetty.socialconnect.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.rahuldshetty.socialconnect.MainActivity;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.adapters.SearchAdapter;
import com.rahuldshetty.socialconnect.modals.User;
import com.rahuldshetty.socialconnect.utils.OnVerticalScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private View view;

    private SearchAdapter adapter;
    private ArrayList<User> userList;
    private  List<DocumentSnapshot> docs;

    private final static int TOTAL_PULL = 5;
    private String queryText="";
    private DocumentSnapshot lastVisible;

    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseStorage storage;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_search, container, false);

        userList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.search_recycler_view);
        searchView = view.findViewById(R.id.search_view);
        progressBar = view.findViewById(R.id.searchProgressBar);

        db =  FirebaseFirestore.getInstance().collection("USERS").getFirestore();
        storage = FirebaseStorage.getInstance();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                progressBar.setVisibility(View.VISIBLE);
                queryText = query.toLowerCase();
                Query q =  db.collection("USERS");
                q.orderBy("name")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if(task.isSuccessful() && task.getResult().size()>0){

                                    QuerySnapshot snapshot = task.getResult();
                                    docs = snapshot.getDocuments();

                                    userList.clear();
                                    for(DocumentSnapshot doc:docs){
                                        User temp = doc.toObject(User.class);
                                        if(temp.getName().toLowerCase().contains(queryText) || temp.getEmail().toLowerCase().contains(queryText) || temp.getCity().toLowerCase().equals(queryText)) {
                                            userList.add(temp);
                                            lastVisible = doc;
                                        }
                                            if(userList.size()==TOTAL_PULL){
                                            break;
                                        }

                                    }

                                    adapter = new SearchAdapter(userList,MainActivity.mainContext);
                                    adapter.setHasStableIds(true);

                                    LinearLayoutManager mgr = new LinearLayoutManager(getContext());
                                    recyclerView.setLayoutManager(mgr);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(adapter);


                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                                else{
                                    // some error occured
                                    Toast.makeText(getContext(),"Error while searching.",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }

                            }
                        });


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        recyclerView.setOnScrollListener(new OnVerticalScrollListener() {
            @Override
            public void onScrolledToBottom() {
                super.onScrolledToBottom();
                // load more items here
                progressBar.setVisibility(View.VISIBLE);
                db.collection("USERS")
                        .orderBy("name")
                        .startAfter(lastVisible)
                        .limit(TOTAL_PULL)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    QuerySnapshot snapshot = task.getResult();
                                    List<DocumentSnapshot> docs = snapshot.getDocuments();

                                    for(DocumentSnapshot doc:docs){
                                        User temp = doc.toObject(User.class);
                                        if(temp.getName().toLowerCase().contains(queryText) || temp.getEmail().toLowerCase().contains(queryText) || temp.getCity().toLowerCase().equals(queryText)) {
                                            lastVisible = doc;
                                            userList.add(temp);
                                        }
                                        if(userList.size()==userList.size() + TOTAL_PULL)
                                        {
                                            break;
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                                else{
                                    Toast.makeText(getContext(),"Error while searching.",Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }

                            }
                        });
            }
        });


        return view;
    }

}
