package com.creativemorph.sociallogins;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LinkedIn {
    private final static String CANCEL_LINK = "error=user_cancelled_login";

    private Context context;
    private Dialog dialog;

    private String mApiKey = "86nehb46hc21ir";
    private String mSecretKey = "Go47ulm6ifnfxG5D";

    private String JOIN_LINK = "https://www.linkedin.com/start/join?";
    private String FORGET_LINK = "https://www.linkedin.com/checkpoint/rp/request-password-reset";
    private String REDIRECT_URL = "http://test.yooney.s3-website-us-east-1.amazonaws.com/linkedin/";
    private String PROFILE_URL = "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))";
    private String Email_URL = "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))";
    private String AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    private String ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken";

    private String OAUTH_ACCESS_TOKEN_PARAM = "oauth2_access_token";
    private String SECRET_KEY_PARAM = "client_secret";
    private String RESPONSE_TYPE_PARAM = "response_type";
    private String GRANT_TYPE_PARAM = "grant_type";
    private String GRANT_TYPE = "authorization_code";
    private String RESPONSE_TYPE_VALUE = "code";
    private String CLIENT_ID_PARAM = "client_id";
    private String STATE_PARAM = "state";
    private String REDIRECT_URI_PARAM = "redirect_uri";
    private String QUESTION_MARK = "?";
    private String AMPERSAND = "&";
    private String EQUALS = "=";
    private String STATE = "E3ZYKC1T6H2yP4z";
    private String accessToken = "";

    private LinkedInResponseListener listener;
    private JSONObject responseJson;

    public interface LinkedInResponseListener {
        void onLinkedInResponseListener(JSONObject response, boolean error);
    }

    public void init(Context context) {
        this.context = context;
        responseJson = new JSONObject();
        listener = (LinkedInResponseListener) context;
    }

    public void setApiKey(String apiKey) {
        this.mApiKey = apiKey ;
    }

    public void setSecretKey(String secretKey) {
        this.mSecretKey = secretKey ;
    }

    public void login() {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.webview_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(lp);
        }
        final WebView webview = dialog.findViewById(R.id.webview);
        webview.requestFocus(View.FOCUS_DOWN);
        webview.clearCache(true);
        webview.clearHistory();
        clearCookies(context);

        final ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {

                progressBar.setVisibility(View.GONE);
                if (authorizationUrl.startsWith(REDIRECT_URL)) {
                    Uri uri = Uri.parse(authorizationUrl);

                    String stateToken = uri.getQueryParameter(STATE_PARAM);
                    if (stateToken == null || !stateToken.equals(STATE)) {

                        generateError("Authentication Error");

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        return true;
                    }
                    String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
                    if (authorizationToken == null) {

                        if (authorizationUrl.contains(CANCEL_LINK)) {
                            generateError("LinkedIn request cancel");
                        } else {
                            generateError("Authentication Error");
                        }
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        return true;
                    }

                    Log.i("Authorize", "Auth token received: "+authorizationToken);
                    String accessTokenUrl = getAccessTokenUrl(authorizationToken);

                    postRequest(accessTokenUrl);

                } else if (authorizationUrl.startsWith(JOIN_LINK) || authorizationUrl.startsWith(FORGET_LINK)) {

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    webview.loadUrl(authorizationUrl);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        String authUrl = getAuthorizationUrl();

        //Load the authorization URL into the webView
        webview.loadUrl(authUrl);
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                generateError("LinkedIn request cancel");
            }
        });
    }

    private void postRequest(String url) {

        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp", response);
                try {
                    JSONObject jObject = new JSONObject(response);
                    if (jObject.has("access_token")) {
                        accessToken = jObject.getString("access_token");
                        Log.i("Authorize", "Access token received: "+accessToken);
                        getUserData(PROFILE_URL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                generateError(""+error);
            }
        });
        Volley.newRequestQueue(context).add(req);
    }

    private void getUserData(String url) {
        Log.d("resp"," "+url);//object:
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObject = new JSONObject(response);

                    String firstName = jObject.getJSONObject("firstName").getJSONObject("localized").getString("en_US");
                    String lastName = jObject.getJSONObject("lastName").getJSONObject("localized").getString("en_US");
                    String image = jObject.getJSONObject("profilePicture").getJSONObject("displayImage~")
                            .getJSONArray("elements").getJSONObject(0).getJSONArray("identifiers")
                            .getJSONObject(0).getString("identifier");

                    makeResponse("firstName", firstName);
                    makeResponse("lastName", lastName);
                    makeResponse("profileImage", image);
                    Log.i("Authorize", "Object received: "+jObject);
                    getEmail();
                } catch (Exception e) {
                    e.printStackTrace();
                    generateError("Server error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                generateError(""+error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(req);
    }

    private void getEmail() {

        StringRequest req = new StringRequest(Request.Method.GET, Email_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObject = new JSONObject(response);

                    JSONObject elements = jObject.getJSONArray("elements").getJSONObject(0);
                    if (elements.has("handle~")) {
                        String email = elements.getJSONObject("handle~").getString("emailAddress");
                        makeResponse("userEmail", email);
                        listener.onLinkedInResponseListener(responseJson,false);
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    generateError("Server error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                generateError("" + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + accessToken);

                return headers;
            }
        };

        Volley.newRequestQueue(context).add(req);
    }

    private void makeResponse(String key, String value) throws JSONException {
        responseJson.put(key, value);
    }

    private void clearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMgr=CookieSyncManager.createInstance(context);
            cookieSyncMgr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMgr.stopSync();
            cookieSyncMgr.sync();
        }
    }

    private void generateError(String msg) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", "LinkedIn request cancel");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listener.onLinkedInResponseListener(jsonObject,true);
    }

    private String getAccessTokenUrl(String authorizationToken) {
        return (ACCESS_TOKEN_URL
                + QUESTION_MARK
                + GRANT_TYPE_PARAM + EQUALS + GRANT_TYPE
                + AMPERSAND
                + RESPONSE_TYPE_VALUE + EQUALS + authorizationToken
                + AMPERSAND
                + CLIENT_ID_PARAM + EQUALS + mApiKey
                + AMPERSAND
                + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URL
                + AMPERSAND
                + SECRET_KEY_PARAM + EQUALS + mSecretKey);
    }

    private String getAuthorizationUrl() {
        return (AUTHORIZATION_URL
                + QUESTION_MARK + RESPONSE_TYPE_PARAM + EQUALS + RESPONSE_TYPE_VALUE
                + AMPERSAND + CLIENT_ID_PARAM + EQUALS + mApiKey
                + AMPERSAND + STATE_PARAM + EQUALS + STATE
                + AMPERSAND + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URL
                + AMPERSAND + "scope=r_emailaddress,r_basicprofile,w_share,r_liteprofile,rw_company_admin,w_member_social");
    }
}
