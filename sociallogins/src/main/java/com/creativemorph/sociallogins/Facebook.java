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
    private Context mContext;
    private FacebookResponseListener listener;
    private LoginManager loginManager;

    public interface FacebookResponseListener {
        void onFacebookResponseListener(JSONObject response, boolean error);
    }

    public Facebook(Context context) {
        this.mContext = context;
        callbackManager = CallbackManager.Factory.create();
        listener = (FacebookResponseListener) context;
        loginManager = LoginManager.getInstance();
    }

    public void login() {

        if (loginManager != null) {
            //Mark: Set permissions
            loginManager.logInWithReadPermissions((Activity) mContext,
                    Arrays.asList("public_profile", "email"));
            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    getFacebookData(loginResult);
                }

                @Override
                public void onCancel() {
                    generateError(mContext.getString(R.string.facebook_request_cancel));
                }

                @Override
                public void onError(FacebookException error) {
                    generateError(error.getLocalizedMessage());
                }
            });
        } else {
            generateError(mContext.getString(R.string.facebook_login_error));
        }

    }

    public void logout() {
        if (loginManager != null) {
            loginManager.logOut();
        } else {
            generateError(mContext.getString(R.string.facebook_logout_error));
        }
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            generateError(mContext.getString(R.string.general_error));
        }
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

        listener.onFacebookResponseListener(jsonObject, true);
    }
}
