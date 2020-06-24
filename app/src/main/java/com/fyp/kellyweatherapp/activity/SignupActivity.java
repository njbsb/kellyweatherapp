package com.fyp.kellyweatherapp.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.kellyweatherapp.R;
import com.fyp.kellyweatherapp.database.PrefConfig;
import com.fyp.kellyweatherapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText name, email, pass, confirmpass;
    private Button signupButton;
    private TextView loginText;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = findViewById(R.id.et_name);
        email = findViewById(R.id.et_email);
        pass = findViewById(R.id.et_pass);
        confirmpass = findViewById(R.id.et_confirmpass);
        signupButton = findViewById(R.id.btn_signup);
        loginText = findViewById(R.id.text_login);
        firebaseAuth = FirebaseAuth.getInstance();
        signupButton.setOnClickListener(this);
        loginText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                goSignUp();
                break;
            case R.id.text_login:
                goLoginPage();
                break;
        }
    }

    private void goLoginPage() {
        Intent intentLogin = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intentLogin);
        finish();
    }

    private void goSignUp() {
        String namedata = name.getText().toString();
        final String emaildata = email.getText().toString();
        String passworddata = pass.getText().toString();
        String confirmpassdata = confirmpass.getText().toString();

        if(namedata.isEmpty() || emaildata.isEmpty() || passworddata.isEmpty() || confirmpassdata.isEmpty()) {
            Toast.makeText(SignupActivity.this, "fields are empty", Toast.LENGTH_SHORT).show();
        }
        else if(!passworddata.equals(confirmpassdata)) {
            confirmpass.setError("Non matching password");
            confirmpass.requestFocus();
        }
        else {
            firebaseAuth.createUserWithEmailAndPassword(emaildata, passworddata).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    createDatabase(namedata, emaildata, firebaseAuth.getUid());
                    Intent main = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                }
                else {
                    Toast.makeText(SignupActivity.this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createDatabase(String name, String email, String uid) {
        // create database on firebase server
        try {
            user = User.getInstance();
            user.setName(name);
            user.setEmail(email);
            user.setUserID(uid);
            PrefConfig.saveUser(getApplicationContext(), user);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}