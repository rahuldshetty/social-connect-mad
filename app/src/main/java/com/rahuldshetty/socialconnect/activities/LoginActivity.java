package com.rahuldshetty.socialconnect.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rahuldshetty.socialconnect.R;

public class LoginActivity extends AppCompatActivity {

    private ImageView loginBtn;
    private EditText emailFld, passFld;
    private TextView forgotPass, signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.login_imageView);
        emailFld = findViewById(R.id.login_email);
        passFld = findViewById(R.id.login_password);
        forgotPass = findViewById(R.id.login_forgot);
        signIn = findViewById(R.id.reg_login);

    }
}
