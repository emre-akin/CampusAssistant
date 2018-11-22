package com.emre.campusassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private static String TAG = LoginActivity.class.getName();
    private EditText emailView;
    private EditText passwordView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: 4");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();
        Log.d(TAG, "onCreate: 5");

        emailView = findViewById(R.id.emailText);
        passwordView = findViewById(R.id.passwordText);
        Button loginButton = findViewById(R.id.loginButton);
        Log.d(TAG, "onCreate: 6");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                attemptLogin();
            }
        });
    }

    private void attemptLogin(){
        Log.d(TAG, "attemptLogin: 1");
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        Boolean cancel = false;
        View focusView = null;
        Log.d(TAG, "attemptLogin: 2");
        if(TextUtils.isEmpty(password)){
            Log.d(TAG, "attemptLogin: 3");
            focusView=passwordView;
            cancel=true;
        }
        if(!isPasswordValid(password)){
            Log.d(TAG, "attemptLogin: 4");
            focusView=passwordView;
            cancel=true;
        }
        if(TextUtils.isEmpty(email)){
            Log.d(TAG, "attemptLogin: 5");
            focusView=emailView;
            cancel=true;
        }
        if (!isEmailValid(email)){
            Log.d(TAG, "attemptLogin: 6");
            focusView=emailView;
            cancel=true;
        }
        if(cancel){
            Log.d(TAG, "attemptLogin: Canceled");
            focusView.requestFocus();
        }else{
            Log.d(TAG, "attemptLogin: "+email+password);
            //Temporary Login
            if(email.equals("a@a.com") && password.equals("123")){
                Log.d(TAG, "attemptLogin: 7");
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        }
    }
    private boolean isPasswordValid(String password){
        return password.length() > 2;
    }
    private boolean isEmailValid(String email){
        return (email.contains("@") && email.contains("."));
    }
}
