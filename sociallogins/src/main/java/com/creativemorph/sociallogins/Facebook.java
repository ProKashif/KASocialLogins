package com.creativemorph.sociallogins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Facebook {

    private CallbackManager callbackManager;
    private Context context;
    private FacebookResponseListener listener;

    public interface FacebookResponseListener {
        void onFacebookResponseListener(JSONObject response, boolean error);
    }

    public Facebook(Context context) {
        this.context = context;
        callbackManager = CallbackManager.Factory.create();
        listener = (FacebookResponseListener) context;
    }

    public void login() {

        LoginManager loginManager = LoginManager.getInstance();

        loginManager.logOut();

        //Mark: Set permissions
        loginManager.logInWithReadPermissions((Activity) context,
                Arrays.asList("public_profile", "email"));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                getFacebookData(loginResult);
            }

            @Override
            public void onCancel() {
                generateError("Facebook request cancel");
            }

            @Override
            public void onError(FacebookException error) {
                generateError(error.getLocalizedMessage());
            }
        });
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                listener.onFacebookResponseListener(object, false);
            }
        });
        //Here we put the requested fields to be returned from the JSONObject
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, birthday, gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void generateError(String msg) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listener.onFacebookResponseListener(jsonObject, false);
    }
}
