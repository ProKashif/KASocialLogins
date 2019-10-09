# KASocialLogins

This is Social login library in which you can login through Facebook And LinkedIn

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

To login into some network, you should call KASocialLogin.

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


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT]
