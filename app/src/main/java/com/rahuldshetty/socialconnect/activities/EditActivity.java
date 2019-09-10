package com.rahuldshetty.socialconnect.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rahuldshetty.socialconnect.MainActivity;
import com.rahuldshetty.socialconnect.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_BGIMG = 4, RESULT_LOAD_PFIMAGE = 5 ;

    private EditText nameView,descView,cityView;
    private ImageView bgImageView,profileImageView;
    private Button saveBtn;
    private ProgressBar progressBar;

    private String email = "";
    private Bitmap bgImage,profImage;
    private String name="",desc = "",city = "",imageLink="",bgImageLink="";
    private Uri bguri=null,pfuri=null;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        nameView = findViewById(R.id.edit_name);
        descView = findViewById(R.id.edit_desc);
        cityView = findViewById(R.id.edit_city);
        bgImageView = findViewById(R.id.edit_background);
        profileImageView = findViewById(R.id.edit_image);
        saveBtn = findViewById(R.id.edit_save);
        progressBar = findViewById(R.id.editProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();

        bguri = getDrawablUri("background_start");
        pfuri = getDrawablUri("profile");

        bgImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_BGIMG);
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_PFIMAGE);
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final Map<String,Object> map = new HashMap<>();

                name = nameView.getText().toString();
                desc = descView.getText().toString();
                city = cityView.getText().toString();


                map.put("name",name);
                map.put("desc",desc);
                map.put("city",city);
                map.put("email",email);
                map.put("uid",firebaseAuth.getCurrentUser().getUid());

                final StorageReference profileRef = storageReference.child("PROFILES/"+user.getUid()+".jpg");
                final StorageReference bgRef = storageReference.child("BG/"+user.getUid()+".jpg");

                profileRef.putFile(pfuri)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){

                                    profileRef.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String image = uri.toString();
                                                    map.put("image",image);

                                                    bgRef.putFile(bguri)
                                                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                                    if(task.isSuccessful()){

                                                                        bgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                            @Override
                                                                            public void onSuccess(Uri uri2) {
                                                                                String bgimage = uri2.toString();
                                                                                map.put("bgimage",bgimage);

                                                                                firebaseFirestore.collection("USERS")
                                                                                        .document(user.getUid())
                                                                                        .set(map)
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if(task.isSuccessful()){
                                                                                                    // Data Saved successfully.
                                                                                                    Toast.makeText(EditActivity.this,"Changes Saved.",Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                                else{
                                                                                                    Toast.makeText(EditActivity.this,"Unable to save data.",Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                                progressBar.setVisibility(View.INVISIBLE);

                                                                                            }
                                                                                        });


                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(EditActivity.this,"Unable to load images.",Toast.LENGTH_SHORT).show();
                                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                                    }
                                                                        });


                                                                    }
                                                                    else{
                                                                        Toast.makeText(EditActivity.this,"Unable to load images.",Toast.LENGTH_SHORT).show();
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                    }
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(EditActivity.this,"Unable to load images.",Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                                else{
                                    Toast.makeText(EditActivity.this,"Unable to load images.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



            }
        });

        loadProfile(firebaseAuth.getCurrentUser().getUid());

    }

    void loadProfile(String uid){
        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("USERS")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String name = "", city = "", desc = "";
                        int friendCount = 0;

                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();

                            if(snapshot.contains("name")){
                                name = snapshot.getString("name");
                            }
                            if(snapshot.contains("city")){
                                city = snapshot.getString("city");
                            }
                            if(snapshot.contains("desc")){
                                desc = snapshot.getString("desc");
                            }
                            if(snapshot.contains("image")){
                                imageLink = snapshot.getString("image");
                            }
                            if(snapshot.contains("bgimage")){
                                bgImageLink = snapshot.getString("bgimage");
                            }


                            email = snapshot.getString("email");

                            nameView.setText(name);
                            cityView.setText(city);
                            descView.setText(desc);

                            if(imageLink!=null){
                                Glide.with(getApplicationContext()).load(imageLink).into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        profileImageView.setImageDrawable(resource);
                                        Bitmap bm=((BitmapDrawable)profileImageView.getDrawable()).getBitmap();
                                        pfuri = getImageUri(EditActivity.this,bm);
                                    }
                                });
                            }

                            if(bgImageLink!=null){
                                Glide.with(getApplicationContext()).load(bgImageLink).into(new SimpleTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        bgImageView.setImageDrawable(resource);
                                        Bitmap bm=((BitmapDrawable)bgImageView.getDrawable()).getBitmap();
                                        bguri = getImageUri(EditActivity.this,bm);
                                    }
                                });
                            }



                            progressBar.setVisibility(View.INVISIBLE);

                        }
                        else{
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.mainContext,"Error loading. Please check your internet and try again.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {


                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                        switch (reqCode) {

                            case RESULT_LOAD_BGIMG:
                                // User selects bg image
                                bgImageView.setImageBitmap(selectedImage);
                                bgImage = selectedImage;
                                bguri = imageUri;
                                break;


                            case RESULT_LOAD_PFIMAGE:
                                profileImageView.setImageBitmap(selectedImage);
                                profImage = selectedImage;
                                pfuri = imageUri;
                                break;


                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(EditActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }






        }else {
            Toast.makeText(EditActivity.this, "You haven't picked Image",Toast.LENGTH_SHORT).show();
        }
    }

    Uri getDrawablUri(String drawableName){
        Uri uri = Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/drawable/" + drawableName);
        return uri;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 25, bytes);
        String path = MediaStore.Images.Media.insertImage(MainActivity.mainContext.getContentResolver(), inImage, UUID.randomUUID().toString() + ".png", "drawing");
        return Uri.parse(path);
    }


}
