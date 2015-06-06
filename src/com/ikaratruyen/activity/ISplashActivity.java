package com.ikaratruyen.activity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.facebook.FacebookSdk;
import com.ikaratruyen.utils.IKaraDbHelper;
import com.ikaratruyen.utils.IkaraConstant;
import com.ikaratruyen.utils.IkaraPreferences;
import com.ikaratruyen.utils.PrefConstant;
import com.splunk.mint.Mint;
import com.ikaratruyen.R;

public class ISplashActivity extends SherlockFragmentActivity implements OnClickListener{

	private static final String TAG = "SFSplashActivity";
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 800;
	int openAppCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		FacebookSdk.sdkInitialize(this.getApplicationContext());
		Mint.initAndStartSession(ISplashActivity.this, "6459dcc5");
		IKaraDbHelper.getInstance(ISplashActivity.this);
		openAppCount = IkaraPreferences.getIntPref(getApplicationContext(), PrefConstant.PREF_APP_OPEN, 0);
		
		if(openAppCount < IkaraConstant.COUNT_OPEN_APP){
			openAppCount++;
			IkaraPreferences.saveIntPref(getApplicationContext(), PrefConstant.PREF_APP_OPEN, openAppCount);
		}
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int height = metrics.heightPixels;
		int wwidth = metrics.widthPixels;

		Log.d(TAG, "View: " + height + " " + wwidth);
		// sendImpresionTracking(ReaderTracking.API_BASE_IMPRESSION, refere, trigger)
		createKeyHashForSettingFacebook();
	}

	public void createKeyHashForSettingFacebook() { // should use this if keytool generate wrong key hash
		try {
			PackageInfo info = getPackageManager().getPackageInfo("com.ikaratruyen", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:", "My KeyHash" + " " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
	}

	@Override
	public void onResume() {
		super.onResume();
		
		Thread logoTimer = new Thread() {
			public void run() {
				try {
					sleep(SPLASH_TIME_OUT);
					startActivity(new Intent(ISplashActivity.this,
							IMainActivity.class));
					finish();
				}

				catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

//				finally {
//					finish();
//				}
			}
		};

		logoTimer.start();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
	
	
	
}