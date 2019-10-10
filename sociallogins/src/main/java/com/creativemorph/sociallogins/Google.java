package com.creativemorph.sociallogins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

public class Google implements GoogleApiClient.OnConnectionFailedListener {
    private Context mContext;
    private GoogleResponseListener listener;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        generateError("" + connectionResult.getErrorMessage());
    }

    public interface GoogleResponseListener {
        void onGoogleResponseListener(JSONObject response, boolean error);
    }

    public Google(Context mContext) {
        this.mContext = mContext;
        listener = (GoogleResponseListener) mContext;

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(((FragmentActivity) mContext), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void login() {

        if (mGoogleApiClient != null) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            if (mContext instanceof Activity) {
                ((Activity) mContext).startActivityForResult(signInIntent, 101);
            } else {
                generateError(mContext.getString(R.string.context_error));
            }
        } else {
            generateError(mContext.getString(R.string.general_error));
        }
    }

    public void logout() {

        if (mGoogleApiClient != null) {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("message", mContext.getString(R.string.google_logout_success));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    listener.onGoogleResponseListener(jsonObject, false);
                }
            });
        } else {
            generateError(mContext.getString(R.string.general_error));
        }
    }

    public void activityResult(int requestCode, int resultCode, Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("GoogleSignIn", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(mContext, "" + acct.getDisplayName(), Toast.LENGTH_SHORT).show();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", acct.getId());
                jsonObject.put("displayName", acct.getDisplayName());
                jsonObject.put("email", acct.getEmail());
                jsonObject.put("photoUrl", acct.getPhotoUrl());
                jsonObject.put("familyName", acct.getFamilyName());
                jsonObject.put("givenName", acct.getGivenName());
                jsonObject.put("idToken", acct.getIdToken());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            listener.onGoogleResponseListener(jsonObject, false);
        }
    }

    private void generateError(String msg) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listener.onGoogleResponseListener(jsonObject, true);
    }
}