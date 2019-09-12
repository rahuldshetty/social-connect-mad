package com.rahuldshetty.socialconnect.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rahuldshetty.socialconnect.MainActivity;
import com.rahuldshetty.socialconnect.R;
import com.rahuldshetty.socialconnect.activities.EditActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFragment extends Fragment {


    public static final int LOAD_IMAGE = 4;

    private View view;
    private EditText title,desc;
    private ImageView imageView;
    private FloatingActionButton fab;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore db;


    private Uri imageData=null;

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_add, container, false);

        title = view.findViewById(R.id.addTitle);
        desc = view.findViewById(R.id.addDesc);
        imageView = view.findViewById(R.id.addImageView);
        fab = view.findViewById(R.id.addFAB);
        progressBar = view.findViewById(R.id.addProgressBar);

        firebaseStorage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String titleMsg = title.getText().toString();
                String descMsg = desc.getText().toString();

                if(TextUtils.isEmpty(titleMsg)||TextUtils.isEmpty(descMsg))
                {
                    Toast.makeText(MainActivity.mainContext, "Enter title and description.",Toast.LENGTH_SHORT).show();
                    return;
                }

                long timestamp = new Date().getTime();

                progressBar.setVisibility(View.VISIBLE);

                final HashMap<String,Object> map = new HashMap<>();
                map.put("title",titleMsg);
                map.put("desc",descMsg);
                map.put("uid",firebaseAuth.getUid());
                map.put("timestamp",timestamp);

                // add post to database
                db.collection("POSTS")
                        .add(map)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful())
                                {
                                    imageView.setImageResource(R.drawable.addimage);
                                    title.setText("");
                                    desc.setText("");
                                    //update post id
                                    DocumentReference doc = task.getResult();
                                    final String postid = doc.getId();
                                    final Map<String,Object> local = map;
                                    local.put("postid",postid);
                                    db.collection("POSTS")
                                            .document(postid)
                                            .set(local, SetOptions.merge())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        if(imageData!=null)
                                                        {
                                                            final StorageReference postRef = firebaseStorage.getReference().child("POSTS/"+postid+".jpg");
                                                            postRef.putFile(imageData)
                                                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                            if(task.isSuccessful())
                                                                            {
                                                                                postRef.getDownloadUrl()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Uri> task) {
                                                                                                    if(task.isSuccessful())
                                                                                                    {
                                                                                                        String image = task.getResult().toString();
                                                                                                        Map<String,Object> localmap = local;
                                                                                                        localmap.put("image",image);

                                                                                                        db.collection("POSTS")
                                                                                                                .document(postid)
                                                                                                                .set(localmap,SetOptions.merge())
                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        if(task.isSuccessful())
                                                                                                                        {
                                                                                                                            //TODO: OPEN POST
                                                                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                                                                            Toast.makeText(MainActivity.mainContext, "Upload Successful",Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                        else{
                                                                                                                            progressBar.setVisibility(View.INVISIBLE);
                                                                                                                            Toast.makeText(MainActivity.mainContext, "Error updating image",Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    }
                                                                                                                });
                                                                                                    }
                                                                                                    else{
                                                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                                                        Toast.makeText(MainActivity.mainContext, "Error updating image",Toast.LENGTH_SHORT).show();
                                                                                                    }

                                                                                            }
                                                                                        });
                                                                            }
                                                                            else{
                                                                                progressBar.setVisibility(View.INVISIBLE);
                                                                                Toast.makeText(MainActivity.mainContext, "Error updating image",Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        }
                                                                    });
                                                        }
                                                        else{
                                                            //TODO: OPEN POST
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(MainActivity.mainContext, "Upload Successful",Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                    else{
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(MainActivity.mainContext, "Error updating...",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                                else{
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MainActivity.mainContext, "Error updating...",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, LOAD_IMAGE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = MainActivity.mainActivity.getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                switch (requestCode) {

                    case LOAD_IMAGE:
                        // User selects bg image
                        imageView.setImageBitmap(selectedImage);
                        imageData = imageUri;
                        break;
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.mainContext, "Something went wrong", Toast.LENGTH_SHORT).show();
            }






        }else {
            Toast.makeText(MainActivity.mainContext, "You haven't picked Image",Toast.LENGTH_SHORT).show();
        }
    }
}
