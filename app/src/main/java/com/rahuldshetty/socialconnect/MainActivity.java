package com.rahuldshetty.socialconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.etebarian.meowbottomnavigation.MeowBottomNavigationCell;
import com.etebarian.meowbottomnavigation.MeowBottomNavigationKt;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private MeowBottomNavigation.Model addModel,userModel,searchModel,homeModel,notifModel;
    private MeowBottomNavigation navbar;
    private FrameLayout frameLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private AddFragment addFragment;
    private NotificationFragment notificationFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;
        mainContext = this.getApplicationContext();

        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        addFragment = new AddFragment();
        notificationFragment = new NotificationFragment();
        profileFragment = new ProfileFragment();

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
    }



    @Override
    protected void onResume() {
        super.onResume();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frame,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
