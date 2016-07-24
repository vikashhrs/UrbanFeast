package com.example.vikash.urbanfeast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences=getSharedPreferences("logInStatus", Context.MODE_PRIVATE);
        String status=sharedPreferences.getString("flagValue","NULL");
        if(status.matches("CLI") || status.matches("GLI") || status.matches("FLI")){
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }else{
            startActivity(new Intent(getApplicationContext(),SampleLogInActivity.class));
            finish();
        }
    }
}
