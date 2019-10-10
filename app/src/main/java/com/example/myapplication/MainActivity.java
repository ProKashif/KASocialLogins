package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.creativemorph.sociallogins.Facebook;
import com.creativemorph.sociallogins.Google;
import com.creativemorph.sociallogins.LinkedIn;
import com.creativemorph.sociallogins.SocialLoginConstant;


import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Facebook.FacebookResponseListener, View.OnClickListener, LinkedIn.LinkedInResponseListener, Google.GoogleResponseListener {

    private Facebook facebook;
    private LinkedIn linkedIn;
    private Google google;
    private Button fbLoginButton, linkedInLoginButton, googleLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        facebook = new Facebook(this);
        google = new Google(this);
        linkedIn = new LinkedIn();
        linkedIn.init(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbLoginButton:
                facebook.login();
                break;
            case R.id.linkedInLoginButton:
                try {
                    linkedIn.login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.googleLoginButton:
                google.login();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SocialLoginConstant.GOOGLE_REQUEST_CODE) {
            google.activityResult(requestCode, resultCode, data);
        } else {
            facebook.activityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onFacebookResponseListener(JSONObject response, boolean error) {
        Log.d("Response", String.valueOf(response));
    }

    @Override
    public void onLinkedInResponseListener(JSONObject response, boolean error) {
        Log.d("Response", String.valueOf(response));
    }

    @Override
    public void onGoogleResponseListener(JSONObject response, boolean error) {
        Log.d("Response", String.valueOf(response));
    }

    private void initView() {
        fbLoginButton = findViewById(R.id.fbLoginButton);
        linkedInLoginButton = findViewById(R.id.linkedInLoginButton);
        googleLoginButton = findViewById(R.id.googleLoginButton);
        fbLoginButton.setOnClickListener(this);
        linkedInLoginButton.setOnClickListener(this);
        googleLoginButton.setOnClickListener(this);
    }
}
