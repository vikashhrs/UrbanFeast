package com.example.vikash.urbanfeast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Vikash on 21-Jul-16.
 */
public class TestFragment extends Fragment {
    private UserProfile userProfile;
    Bundle bundle;
    public TestFragment(){

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getContext(), "TestFragment:onCreate", Toast.LENGTH_SHORT).show();
        //userProfile=getUserProfile();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_test,container,false);
        Toast.makeText(getContext(), "TestFragment:onCreateView", Toast.LENGTH_SHORT).show();
        bundle=getArguments();
        userProfile=(UserProfile)bundle.getSerializable("userProfile");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(getContext(), "TestFragment:onViewCreated", Toast.LENGTH_SHORT).show();
        TextView textView=(TextView)view.findViewById(R.id.sampleTestTextView);
        Toast.makeText(getContext(), "Dob="+userProfile.getDOB(), Toast.LENGTH_SHORT).show();
        textView.setText(userProfile.getNAME()+"\n"+userProfile.getEMAIL()+"\n"+userProfile.getDOB()+"\n"+userProfile.getGENDER()+"\n"+userProfile.getLOGINFEATURE());
    }

    public UserProfile getUserProfile() {
        File file=new File(getContext().getFilesDir(),"userProfile.txt");
        UserProfile userProfile=null;
        try {
            ObjectInputStream objectInputStream=new ObjectInputStream(new FileInputStream(file));
            userProfile=(UserProfile)objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return userProfile;
    }
}
