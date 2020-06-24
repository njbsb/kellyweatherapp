package com.fyp.kellyweatherapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.kellyweatherapp.R;
import com.fyp.kellyweatherapp.database.PrefConfig;
import com.fyp.kellyweatherapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password;
    private Button loginButton;
    private TextView textRegister;
    private FirebaseAuth firebaseAuth;
    private User user;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.edittext_email);
        password = findViewById(R.id.edittext_password);
        loginButton = findViewById(R.id.btn_login);
        textRegister = findViewById(R.id.text_register);
        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(this);
        textRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                goLogin();
                break;
            case R.id.text_register:
                goRegister();
                break;
        }
    }

    private void goLogin() {
        String emailData = email.getText().toString();
        String passwordData = password.getText().toString();
        if(emailData.isEmpty() || passwordData.isEmpty()) {
            if(emailData.isEmpty()) {
                email.setError("Please enter email");
                email.requestFocus();
            }
            if(passwordData.isEmpty()) {
                password.setError("Please enter password");
                password.requestFocus();
            }
        }
        else {
            firebaseAuth.signInWithEmailAndPassword(emailData, passwordData).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        user = PrefConfig.loadUser(getApplicationContext());
                        Toast.makeText(LoginActivity.this, "field are empty", Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();

                    }
                    else {
                        Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void goRegister() {
        Intent intentSignup = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intentSignup);
        finish();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        firebaseAuth.addAuthStateListener(authStateListener);
//    }
}