package com.ikaratruyen.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ikaratruyen.model.Chapter;
import com.ikaratruyen.utils.IkaraConstant.DEVICETYPE;
import com.ikaratruyen.R;

public class ISettings {
	
	public static boolean isGuest = true;
	public static int deviceType;
	public static boolean CHECK_UPDATE = true;
	
	private static ISettings sSetting;
	private int height, width;
	private ArrayList<Chapter> chapListContents;
	
	public ArrayList<Chapter> getChapListContents() {
		return chapListContents;
	}

	public void setChapListContents(ArrayList<Chapter> chapListContents) {
		this.chapListContents = chapListContents;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public static ISettings getInstance(){
		if(sSetting == null){
			sSetting = new ISettings();
		}
		return sSetting;
	}
	
	public void config(Activity activity){
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		height = metrics.heightPixels;
		width = metrics.widthPixels;
		
		Log.e("ISetting ", "W - H" +height+" "+width);
	}
	
	public static void detectDeviceType(Context context){
		
		String value = context.getResources().getString(R.string.screen_type);
		
		if (value.equals("phone")){
			deviceType = DEVICETYPE.PHONE;
		}else if (value.equals("7-inch-tablet")){
			deviceType = DEVICETYPE.TABLET7INCH;
		}else{
			if (value.equals("10-inch-tablet")){
				deviceType = DEVICETYPE.TABLET10INCH;
			}
		}
	}
}
