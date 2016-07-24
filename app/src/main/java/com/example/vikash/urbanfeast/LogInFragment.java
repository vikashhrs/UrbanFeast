package com.example.vikash.urbanfeast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * Created by Vikash on 21-Jul-16.
 */
public class LogInFragment extends Fragment implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private CommunicateBack communicateBack;
    private LoginButton loginButton;
    private SignInButton signInButton;
    private Button customSignIn;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private static final int RC_SIGN_IN = 0;
    private boolean mSignInClicked;
    private static final String TAG = "LOGINACTIVITY";
    private ConnectionResult mConnectionResult;
    private String gPlusEmail;
    private UserProfile userProfile;
    private int FBORGPLUS=0;
    private CallbackManager mCallbackManager;
    private FacebookCallback<LoginResult> mCallback=new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken=loginResult.getAccessToken();
            final Profile profile=Profile.getCurrentProfile();
            if(profile==null){ // modification: profile!=null
                //textView.setText("Welcome"+profile.getName());
                Toast.makeText(getContext(), "profile==null", Toast.LENGTH_SHORT).show();
            }
            GraphRequest graphRequest=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        Toast.makeText(getActivity().getApplicationContext(), "Email=" + object.getString("email"), Toast.LENGTH_SHORT).show();
                        userProfile=new UserProfile(object.getString("name"),object.getString("birthday"),object.getString("email"),object.getString("gender"),"","FLI");
                        //LoginManager.getInstance().logOut();
                        testMethod();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //testMethod();
                    //Toast.makeText(getActivity().getApplicationContext(), "Email=" + object.getString("email"), Toast.LENGTH_SHORT).show();
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,birthday,picture,email,gender");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
            /*LoginManager.getInstance().logOut();
            SharedPreferences sharedPreferences=getContext().getSharedPreferences("logInStatus", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putString("flagValue", "FLI");
            editor.commit();
            Bundle bundle=new Bundle();
            bundle.putSerializable("userProfile", userProfile);
            serializeProfile(userProfile);
            Intent intent=new Intent(getContext(),HomeActivity.class);
            intent.putExtra("userProfile", bundle);
            startActivity(intent);
            getActivity().finish();*/
        }
        void testMethod(){
            LoginManager.getInstance().logOut();
            SharedPreferences sharedPreferences=getContext().getSharedPreferences("logInStatus", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putString("flagValue", "FLI");
            editor.commit();
            Bundle bundle=new Bundle();
            bundle.putSerializable("userProfile", userProfile);
            communicateBack.goBackWithData(userProfile);
        }
        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };
    public LogInFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        mGoogleApiClient.connect();
        mCallbackManager=CallbackManager.Factory.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_login,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginButton=(LoginButton)view.findViewById(R.id.login_button);
        signInButton=(SignInButton)view.findViewById(R.id.btn_sign_in);
        signInButton.setOnClickListener(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "user_birthday", "user_about_me", "email"));
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mCallback);
    }
    private void resolveSignInError() {
        Toast.makeText(getContext(), "resolveSignInError:called", Toast.LENGTH_SHORT).show();
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
    private void signInWithGplus() {
        Toast.makeText(getContext(), "signInWithGplus:called", Toast.LENGTH_SHORT).show();
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
        Toast.makeText(getContext(), "signOutFromGplus:called", Toast.LENGTH_SHORT).show();
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            revokeGplusAccess();
            //modification
            //mGoogleApiClient.connect();
            //updateUI(false);
        }
    }

    /**
     * Revoking access from google
     * */
    private void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(TAG, "User access revoked!");
                            mGoogleApiClient.connect();
                            //updateUI(false);
                        }

                    });
        }
    }
    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        Toast.makeText(getContext(), "getProfileInformation:called", Toast.LENGTH_SHORT).show();
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                //String personEmail=currentPerson.
                String personName = currentPerson.getDisplayName();
                int personGender=currentPerson.getGender();
                String personDOB=currentPerson.getBirthday();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                userProfile=new UserProfile(personName,personDOB,email,"","","GLI");
                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                //txtName.setText(personName);
                //txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                //personPhotoUrl = personPhotoUrl.substring(0,
                // personPhotoUrl.length() - 2)
                //+ PROFILE_PIC_SIZE;

                // new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(getContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        signOutFromGplus();
        communicateBack.goBackWithData(userProfile);
        // modification:communicateBack.goBackWithData(userProfile);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getContext(), "onActivityResult:called", Toast.LENGTH_SHORT).show();
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getContext(), "onActivityResult:called222", Toast.LENGTH_SHORT).show();

        if (requestCode == RC_SIGN_IN) {
            Toast.makeText(getContext(), "onActivityResult:called:requestCode == RC_SIGN_IN", Toast.LENGTH_SHORT).show();
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(getContext(), "onActivityResult:called:resultCode != Activity.RESULT_OK", Toast.LENGTH_SHORT).show();
                mSignInClicked = false;
            }

            mIntentInProgress = false;
            Toast.makeText(getContext(), "onActivityResult:called: mIntentInProgress = false", Toast.LENGTH_SHORT).show();

            if (!mGoogleApiClient.isConnecting()) {
                Toast.makeText(getContext(), "onActivityResult:called: !mGoogleApiClient.isConnecting()", Toast.LENGTH_SHORT).show();
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getContext(), "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        Toast.makeText(getContext(), "onConnectionSuspended:called:mGoogleApiClient.connect", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.btn_sign_in){
            FBORGPLUS=1;
            Toast.makeText(getContext(), "g+ sign In button clicked", Toast.LENGTH_LONG).show();
            SharedPreferences sharedPreferences=getContext().getSharedPreferences("logInStatus", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putString("flagValue", "GLI");
            editor.commit();
            signInWithGplus();
            /*Bundle bundle=new Bundle();
            bundle.putSerializable("userProfile", userProfile);
            Intent intent=new Intent(getContext(),HomeActivity.class);
            intent.putExtra("userProfile", bundle);
            serializeProfile(userProfile);
            startActivity(intent);
            signOutFromGplus();

            getActivity().finish();*/
        }
    }
    void serializeProfile(UserProfile userProfile){
        File file=new File(getContext().getFilesDir(),"userProfile.txt");
        try {
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(userProfile);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            Toast.makeText(getContext(), "onConnectionFailed:called:!connectionResult.hasResolution", Toast.LENGTH_LONG).show();
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), getActivity(),
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toast.makeText(getContext(), "onAttach:called", Toast.LENGTH_LONG).show();
        communicateBack=(CommunicateBack)context;
    }

    interface CommunicateBack{
        void goBackWithData(UserProfile userProfile);
    }
}
