package com.rahuldshetty.socialconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rahuldshetty.socialconnect.MainActivity;
import com.rahuldshetty.socialconnect.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameFld,emailFld,passFld,cpassFld;
    private TextView signIn;
    private ImageView regBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        nameFld = findViewById(R.id.reg_name);
        emailFld = findViewById(R.id.reg_email);
        passFld = findViewById(R.id.reg_password);
        cpassFld = findViewById(R.id.reg_password2);
        signIn = findViewById(R.id.reg_signin);
        regBtn = findViewById(R.id.reg_imageView);
        progressBar = findViewById(R.id.regProgressBar);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent act = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(act);
                finish();
            }
        });


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                String name = nameFld.getText().toString();
                String email = emailFld.getText().toString();
                String pass = passFld.getText().toString();
                String cpass = cpassFld.getText().toString();

                String msg = validate(name,email,pass,cpass);

                if(msg.equals("")){
                    // password valid

                    //user sign up
                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // sign up completed...
                                // add info to database
                                goToMain();
                            }
                            else
                            {
                                Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        }
                    });
                }
                else{
                    Toast.makeText(RegisterActivity.this,msg,Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    void goToMain(){
        Intent main = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(main);
        finish();

    }

    String validate(String name,String email,String pass,String cpass){
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(cpass) ){
            return "Enter all the fields.";
        }
        else if(!pass.equals(cpass)){
            return "Password Mismatch.";
        }
        else if(!pass.matches(".*[a-z].*"))
            return "Password should contain at least one lower case letter.";
        else if(!pass.matches(".*[A-Z].*"))
            return "Password should contain at least one upper case letter.";
        else if(!pass.matches(".*[0-9].*"))
            return "Password should contain at least one digit.";
        else if(!pass.matches(".*[^a-zA-Z0-9].*"))
            return "Password should contain at least one special symbol.";
        return "";
    }

}
