package com.example.vikash.urbanfeast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.Arrays;

/**
 * Created by Vikash on 20-Jul-16.
 */
public class LogInActivity extends Activity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private LoginButton loginButton;
    private SignInButton signInButton;
    private Button customSignIn;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private static final int RC_SIGN_IN = 0;
    private boolean mSignInClicked;
    private static final String TAG = "LOGINACTIVITY";
    private ConnectionResult mConnectionResult;
    private String gPlusEmail;
    private UserProfile userProfile;
    private int FBORGPLUS=0;
    private FacebookCallback<LoginResult> mCallback=new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken=loginResult.getAccessToken();
            final Profile profile=Profile.getCurrentProfile();
            //if(profile!=null){
                //textView.setText("Welcome"+profile.getName());
            //}
            GraphRequest graphRequest=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        Toast.makeText(getApplicationContext(), "Email=" + object.getString("email"), Toast.LENGTH_SHORT).show();
                        userProfile=new UserProfile(profile.getName(),object.getString("birthday"),object.getString("email"),object.getString("gender"),"","FLI");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,birthday,picture,email,gender");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
            LoginManager.getInstance().logOut();
            SharedPreferences sharedPreferences=getSharedPreferences("logInStatus", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putString("flagValue", "FLI");
            editor.commit();
            Bundle bundle=new Bundle();
            bundle.putSerializable("userProfile",userProfile);
            Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
            intent.putExtra("userProfile",bundle);
            startActivity(intent);
            finish();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager=CallbackManager.Factory.create();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        loginButton=(LoginButton)findViewById(R.id.login_button);
        signInButton=(SignInButton)findViewById(R.id.btn_sign_in);
        signInButton.setOnClickListener(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "user_birthday", "user_about_me", "email"));
        loginButton.registerCallback(mCallbackManager, mCallback);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            mCallbackManager.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_SIGN_IN) {
                if (resultCode != RESULT_OK) {
                    mSignInClicked = false;
                }

                mIntentInProgress = false;

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id==R.id.btn_sign_in){
            FBORGPLUS=1;
            SharedPreferences sharedPreferences=getSharedPreferences("logInStatus", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putString("flagValue", "GLI");
            editor.commit();
            signInWithGplus();
            Bundle bundle=new Bundle();
            bundle.putSerializable("userProfile",userProfile);
            Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
            intent.putExtra("userProfile",bundle);
            startActivity(intent);
            signOutFromGplus();
            finish();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
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
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
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
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
