package com.rahuldshetty.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;


import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rahuldshetty.socialconnect.activities.LoginActivity;
import com.rahuldshetty.socialconnect.fragments.AddFragment;
import com.rahuldshetty.socialconnect.fragments.HomeFragment;
import com.rahuldshetty.socialconnect.fragments.NotificationFragment;
import com.rahuldshetty.socialconnect.fragments.ProfileFragment;
import com.rahuldshetty.socialconnect.fragments.SearchFragment;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    public static Activity mainActivity;
    public static Context mainContext;
    public static String otherUserID="";
    private static FragmentTransaction sft;

    private FragmentTransaction ft;
    private MeowBottomNavigation.Model addModel,userModel,searchModel,homeModel,notifModel;
    private MeowBottomNavigation navbar;
    private FrameLayout frameLayout;
    private Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private AddFragment addFragment;
    private NotificationFragment notificationFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sft = ft;

        mainActivity = this;
        mainContext = this.getApplicationContext();

        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        addFragment = new AddFragment();
        notificationFragment = new NotificationFragment();
        profileFragment = new ProfileFragment();
        toolbar = findViewById(R.id.main_toolbar);

        frameLayout = findViewById(R.id.main_frame);
        navbar = findViewById(R.id.main_nav_bar);

        addNavItems();

        navbar.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model p1) {

                int i = p1.getId();
                switch (i){
                    case 1:
                        loadFragment(homeFragment);
                        break;
                    case 2:
                        loadFragment(searchFragment);
                        break;
                    case 3:
                        loadFragment(addFragment);
                        break;
                    case 4:
                        loadFragment(notificationFragment);
                        break;
                    case 5:
                        otherUserID  = firebaseAuth.getCurrentUser().getUid();
                        loadFragment(profileFragment);
                        break;
                }
                return Unit.INSTANCE;
            }
        });

        navbar.show(1,true);
        loadFragment(homeFragment);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbarmenu,menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if(currentUser==null){
            // User is not logged in
            Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }
    }

    void addNavItems(){
        homeModel = new MeowBottomNavigation.Model(1,R.drawable.posts);
        searchModel = new MeowBottomNavigation.Model(2,R.drawable.search);
        addModel = new MeowBottomNavigation.Model(3,R.drawable.add);
        notifModel = new MeowBottomNavigation.Model(4,R.drawable.notification);
        userModel = new MeowBottomNavigation.Model(5,R.drawable.person);


        navbar.add(homeModel);
        navbar.add(searchModel);
        navbar.add(addModel);
        navbar.add(notifModel);
        navbar.add(userModel);
    }

    void loadFragment(Fragment fragment){
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){

            case R.id.toolbar_logout:
                firebaseAuth.signOut();
                Intent act = new Intent(MainActivity.this,MainActivity.class);
                startActivity(act);
                finish();
                break;

            case R.id.toolbar_message:
                break;

            case R.id.toolbar_settings:
                break;



            default: return false;
        }
        return true;
    }


    public static void loadUserFragment(){
        if(otherUserID!=null){
            // load fragment from other ui
            sft = ((FragmentActivity)mainActivity).getSupportFragmentManager().beginTransaction();
            ProfileFragment newProfile = new ProfileFragment();
            sft.replace(R.id.main_frame,newProfile);
            sft.addToBackStack(null);
            sft.commit();
        }
    }

}
