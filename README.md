# KASocialLogins

[![](https://jitpack.io/v/ProKashif/KASocialLogins.svg)](https://jitpack.io/#ProKashif/KASocialLogins)

This is Social login library in which you can login through Facebook, LinkedIn and Google

## Installation

#### Step 1.  Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```java
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

#### Step 2. Add the dependency

```java
dependencies {
	        implementation 'com.github.ProKashif:KASocialLogins:Tag'
	}
```
where Tag is the version of the lib. You can find the latest version in the badge on top of the page



## Usage for Facebook

To login into some Facebook, you should call KASocialLogin.

```java
import com.creativemorph.sociallogins.Facebook;

facebook = new Facebook(this);
facebook.login();
```
Also you should impliments KASocialLogins listener for callback

```java
implements Facebook.FacebookResponseListener

@Override
    public void onFacebookResponseListener(JSONObject response, boolean error) {
        Log.d("Response", String.valueOf(response));
    }

```
Also you should redirect you onActivityResult() callback to KASocialLogin

```java
@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.activityResult(requestCode, resultCode, data);
    }
```

## Config
For facebook login you will need to add the following to your android manifest

```xml

 <uses-permission android:name="android.permission.INTERNET" />
 
<meta-data
        android:name="com.facebook.sdk.ApplicationId"
        android:value="@string/facebook_app_id" />
```

## Usage for Google

#### Step 1. Register App

Register your app on [Google Developer](https://console.firebase.google.com/) Account

Please view the Goolgle documentation if you face any issue related to dependency

* Add project/build.gradle

```gridle
	
	classpath 'com.google.gms:google-services:4.3.2'

```

* Add the dependencies for the Google Play services to your module (app-level) Gradle file (usually app/build.gradle):


```gridle

	implementation 'com.google.android.gms:play-services-auth:17.0.0'
	
	apply plugin: 'com.google.gms.google-services'
	
```

#### Step 2.  
To login into Google you should call KASocialLogin.

```java
	import com.creativemorph.sociallogins.Google;

        google = new Google(this);
	google.login();
```

```java
	 Google.GoogleResponseListener

 @Override
    public void onGoogleResponseListener(JSONObject response, boolean error) {
        Log.d("Response", String.valueOf(response));
    }

```

Also you should redirect you onActivityResult() callback to KASocialLogin

```java

 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SocialLoginConstant.GOOGLE_REQUEST_CODE) {
            google.activityResult(requestCode, resultCode, data);
        } 
    }

```

## Usage for LinkedIn

#### Step 1. Register App

Register your app on [LinkedIn Developer](https://www.linkedin.com/developers/apps) Account

#### Step 2.  
To login into some network, you should call KASocialLogin.

```java
	import com.creativemorph.sociallogins.LinkedIn;

        linkedIn = new LinkedIn();
        linkedIn.init(this);
	linkedIn.setLinkedInCredentials("apiKey","secretKey","state");
        linkedIn.setRedirect_URL("url");
	linkedIn.login();

```
you can find both keys from developer account ans state from your callbackUrl
Also you should impliments KASocialLogins listener for callback

```java
LinkedIn.LinkedInResponseListener 

@Override
    public void onLinkedInResponseListener(JSONObject response, boolean error) {
        Log.d("Response", String.valueOf(response));
    }

```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT]
