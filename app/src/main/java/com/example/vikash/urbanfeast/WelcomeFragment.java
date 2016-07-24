package com.example.vikash.urbanfeast;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Vikash on 23-Jul-16.
 */
public class WelcomeFragment extends Fragment implements View.OnClickListener {
    private  CommunicateBack communicateBack;
    private Button button;
    private TextView textView;
    UserProfile userProfile;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.welcome_fragment, container, false);
        Bundle bundle=getArguments();
        userProfile=(UserProfile)bundle.getSerializable("userProfile");
        serializeProfile(userProfile);
        textView=(TextView)view.findViewById(R.id.welcomeTextView);
        textView.setText(userProfile.getNAME()+"\n"+userProfile.getEMAIL()+"\n"+userProfile.getDOB()+"\n"+userProfile.getLOGINFEATURE()+"\n"+userProfile.getGENDER());
        button=(Button)view.findViewById(R.id.loadHomeButton);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        communicateBack=(CommunicateBack)context;
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if(id==R.id.loadHomeButton){
            communicateBack.goBackAndLoadHomeScreen(userProfile);
        }
    }

    interface CommunicateBack{
        void goBackAndLoadHomeScreen(UserProfile userProfile);
    }
    void serializeProfile(UserProfile userProfile){
        File file=new File(getContext().getFilesDir(),"userProfile");
        try {
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(userProfile);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
