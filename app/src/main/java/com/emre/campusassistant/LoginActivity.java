package com.emre.campusassistant;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {

    private static String TAG = LoginActivity.class.getName();
    private EditText usernameView;
    private EditText passwordView;
    public int studentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: 4");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        studentID=0;

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();
        Log.d(TAG, "onCreate: 5");

        usernameView = findViewById(R.id.usernameText);
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
        String username = usernameView.getText().toString();
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
        if(TextUtils.isEmpty(username)){
            Log.d(TAG, "attemptLogin: 5");
            focusView=usernameView;
            cancel=true;
        }
        if(cancel){
            Log.d(TAG, "attemptLogin: Canceled");
            focusView.requestFocus();
        }else{
            Log.d(TAG, "attemptLogin: "+username+password);
            sendPost();
            SystemClock.sleep(1000);
            if(studentID != 0){
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("username",username);
                finish();
                startActivity(i);
            }
        }
    }
    private boolean isPasswordValid(String password){
        return password.length() > 2;
    }

    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://emreiot.baykalsarioglu.com/login.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("studentUsername", usernameView.getText().toString());
                    jsonParam.put("studentPassword", passwordView.getText().toString());

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();
                    String response;
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    try (Scanner scanner = new Scanner(in)) {
                        String responseBody = scanner.useDelimiter("\\A").next();
                        System.out.println(responseBody);
                        response=responseBody;
                    }

                    JSONObject obj = new JSONObject(response);
                    String test = obj.getString("response");
                    if(test.equals("ok")){
                        String resp = obj.getString("id");
                        studentID = Integer.parseInt(resp);
                        System.out.println(resp);
                    }else {
                        System.out.println("Failed");
                    }


                    Log.i(TAG, "STATUS: "+ conn.getResponseCode());
                    Log.i(TAG,"MSG: " + conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
