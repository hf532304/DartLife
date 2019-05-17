package com.example.dartlife.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dartlife.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Button mRegisterBtn;
    private Button mLoginBtn;
    private FirebaseAuth mAuth;
    private TextInputEditText mInputEmail;
    private TextInputEditText mInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);
        mInputEmail = findViewById(R.id.EmailTextInputEdit);
        mInputPassword = findViewById(R.id.PasswordTextInputEdit);
        mRegisterBtn = findViewById(R.id.RegisterButton);
        mLoginBtn = findViewById(R.id.LoginButton);
        mAuth = FirebaseAuth.getInstance();
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    private void loginUser() {
        String Email = Objects.requireNonNull(mInputEmail.getText()).toString();
        String Password = Objects.requireNonNull(mInputPassword.getText()).toString();
        if(Email.isEmpty()){
            mInputEmail.requestFocus();
            mInputEmail.setError("This field is required");
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            mInputEmail.requestFocus();
            mInputEmail.setError("This email address is invalid");
        }
        else if(Password.isEmpty()){
            mInputPassword.requestFocus();
            mInputPassword.setError("This field is required");
        }
        else if(Password.length() < 6){
            mInputPassword.requestFocus();
            mInputPassword.setError("Password must be at least 6 characters");
        }
        else{
            setProgress(2);
            mAuth.signInWithEmailAndPassword(Email, Password).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getBaseContext(), "LoginFailure: " + task.getException(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    private void registerUser() {
        String Email = Objects.requireNonNull(mInputEmail.getText()).toString();
        String Password = Objects.requireNonNull(mInputPassword.getText()).toString();
        if(Email.isEmpty()){
            mInputEmail.requestFocus();
            mInputEmail.setError("This field is required");
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            mInputEmail.requestFocus();
            mInputEmail.setError("This email address is invalid");
        }
        else if(Password.isEmpty()){
            mInputPassword.requestFocus();
            mInputPassword.setError("This field is required");
        }
        else if(Password.length() < 6){
            mInputPassword.requestFocus();
            mInputPassword.setError("Password must be at least 6 characters");
        }
        else{
            setProgress(2);
            mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getBaseContext(), "Successfully Registered",
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getBaseContext(), "Authentication failed: "+
                                        Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
