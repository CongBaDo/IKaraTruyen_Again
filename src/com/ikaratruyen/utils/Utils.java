package com.ikaratruyen.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikaratruyen.model.Book;
import com.ikaratruyen.utils.IkaraConstant.DEVICETYPE;
import com.ikaratruyen.BuildConfig;
import com.ikaratruyen.R;
//DUNG THU VIEN JACKSON http://wiki.fasterxml.com/JacksonHome


public class Utils {
	private static final Logger log = Logger.getLogger(Utils.class
			.getName());
	static ObjectMapper mapper = new ObjectMapper();
	
	public static int getStatusBarHeight(Context context) {  
	      int result = 0;
	      int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
	      if (resourceId > 0) {
	          result = context.getResources().getDimensionPixelSize(resourceId);
	      }  
	      return result;
	}  
	
	public static void writeFileOnSDCard(String strWrite, Context context,String fileName)
    {
		if (BuildConfig.DEBUG){
            try 
            {
                            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                            File myFile = new File(fullPath + File.separator + "/"+fileName);

                            FileOutputStream fOut = new FileOutputStream(myFile);
                            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                            myOutWriter.append(strWrite);
                            myOutWriter.close();
                            fOut.close();
            }
            catch (Exception e)
            {
                    //do your stuff here
            	e.printStackTrace();
            }
		}
    }
	
//	public static ArrayList<SearchItem> getSearchData(JSONArray arr){
//		ArrayList<SearchItem> datas = new ArrayList<SearchItem>();
//		for(int i = 0; i < arr.length(); i++){
//			SearchItem item = new SearchItem();
//			item.title = arr.optJSONObject(i).names();
//		}
//		return datas;
//	}
	
	public static int detectDeviceType(Context context){
		
		String value = context.getResources().getString(R.string.screen_type);
		int deviceType = -1;
		if (value.equals("phone")){
			deviceType = DEVICETYPE.PHONE;
		}else if (value.equals("7-inch-tablet")){
			deviceType = DEVICETYPE.TABLET7INCH;
		}else{
			if (value.equals("10-inch-tablet")){
				deviceType = DEVICETYPE.TABLET10INCH;
			}
		}
		
		return deviceType;

	}

	public static boolean hasNetworkConnection(Context context) {
		if (context == null)
			return false;
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo[] netInfo = cm.getAllNetworkInfo();
			if (netInfo != null) {
				for (NetworkInfo ni : netInfo) {
					if (ni.getTypeName().equalsIgnoreCase("WIFI"))
						if (ni.isConnected())
							haveConnectedWifi = true;
					if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
						if (ni.isConnected())
							haveConnectedMobile = true;
				}
			}
		}

		return haveConnectedWifi || haveConnectedMobile;
	}

	
	public static String serialize(Object object){
		if (object == null) return null;
		
		StringWriter writer = new StringWriter();
		try {
			mapper.writeValue(writer, object);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return writer.getBuffer().toString();	
	}
	
	public static <T> T deserialize(Class<T> type, String string){
		if (string == null) return null;
		try {
			return (T)mapper.readValue(string,type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.log(Level.WARNING, "BUGS", e);
			e.printStackTrace();
		}
		return null;
	}
	
	public void sortBookFollowTime(ArrayList<Book> books){
		Collections.sort(books, new Comparator<Book>() {

			@Override
			public int compare(Book c1, Book c2) {
				// TODO Auto-generated method stub
				DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					String day1 = c1.savedTime;//c1.getIssue_release_date().split("T")[0];
					String day2 = c2.savedTime;//c2.getIssue_release_date().split("T")[0];

					return f.parse(day2).compareTo(f.parse(day1));
				} catch (ParseException e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
	}
	
	public static int dpToPx(Activity context, int dp) {
	    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	} 
	
}
