package com.mobmundo.localmob;

import android.app.Application;
import android.os.Handler;
import android.provider.Settings;

import com.mobmundo.localmob.activity.FirstActivity;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

public class LocalMobApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		// Initialize Parse Connection
		Parse.initialize(this, "HaGxC4bjDGfufZysio4aDG8AeyNfc1hlS9dhgwyu",
				"5aV7PbJAcYvZxXH65tzJ5dVy24YwtwxuK3gnnO9y");
		PushService.setDefaultPushCallback(this, FirstActivity.class);
		final ParseInstallation installation = ParseInstallation
				.getCurrentInstallation();
		//ParseFacebookUtils.initialize("1539529132929916");
		ParseFacebookUtils.initialize(this);
		ParseTwitterUtils.initialize("iRU50Z0Uo4QX6l0dAnqaV3rk2",
				"de9UVEvRrN7niPbzU5UFZvfKrRpr1FwFTdrbISMvf42clDYL8u");
		ParseCrashReporting.enable(this);
		// ParseTwitterUtils.initialize("32457396-Bx08DvyiNZ6vvtKf2jeuVmfuuEdPSVFxgGzcN2UJj","bhIDV5vP2IRVazBz7OWoNod7DMBJHOXPYtkyYevMaPYG6");
		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
		// Optionally enable public read access.
		// defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
		final String androidId = Settings.Secure.getString(
				getApplicationContext().getContentResolver(),
				Settings.Secure.ANDROID_ID);
		// Post the uniqueId delayed
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				installation.put("UniqueId", androidId);
				installation.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						// Saved!
					}
				});
			}
		}, 10000);
	}
}