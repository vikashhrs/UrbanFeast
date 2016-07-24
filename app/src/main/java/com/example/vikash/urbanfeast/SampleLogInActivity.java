package com.example.vikash.urbanfeast;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SampleLogInActivity extends AppCompatActivity implements WelcomeFragment.CommunicateBack,LogInFragment.CommunicateBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_log_in);
        Fragment fragment=new LogInFragment();
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.login_content, fragment, "projectFragment");
        fragmentTransaction.commit();
    }

    @Override
    public void goBackAndLoadHomeScreen(UserProfile userProfile) {
        Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void goBackWithData(UserProfile userProfile) {
        Bundle bundle=new Bundle();
        bundle.putSerializable("userProfile",userProfile);
        Fragment fragment=new WelcomeFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.login_content, fragment, "projectFragment");
        fragmentTransaction.commit();
    }
}
