package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.creativemorph.sociallogins.Facebook;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Facebook.FacebookResponseListener, View.OnClickListener {

    private Facebook facebook;
    private Button fbLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        facebook = new Facebook(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbLoginButton:
                facebook.login();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.activityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFacebookResponseListener(JSONObject response, boolean error) {
        Log.d("Response", String.valueOf(response));
    }

    private void initView() {
        fbLoginButton = findViewById(R.id.fbLoginButton);
        fbLoginButton.setOnClickListener(this);
    }
}
