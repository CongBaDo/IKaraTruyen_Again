package com.ikaratruyen.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.ikaratruyen.R;

public class IWebFragment extends Fragment{

	private static final String TAG = "IWebFragment";

	public IWebFragment() { }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_web, null);
		Log.i(TAG, "onCreateView");
		
		WebView web = (WebView)view.findViewById(R.id.webview);
		web.loadUrl("http://www.ikaratruyen.com/help.jsp");
		return view;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}