package com.emre.campusassistant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private static String TAG = StartActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        checkLogin();
        
        Button loginButton = (Button) findViewById(R.id.signInButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                //For faster testing
                //Intent i = new Intent(v.getContext(), MainActivity.class);
                Intent i = new Intent(v.getContext(), LoginActivity.class);
                finish();
                startActivity(i);
            }
        });
    }
    
    private void checkLogin() {
        // TODO: 20.12.2018 Add login check when Database is ready.
    }
}
