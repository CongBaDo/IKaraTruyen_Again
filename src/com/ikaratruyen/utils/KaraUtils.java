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
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikaratruyen.BuildConfig;
import com.ikaratruyen.IApplication;
import com.ikaratruyen.R;
import com.ikaratruyen.model.Book;
import com.ikaratruyen.utils.IkaraConstant.DEVICETYPE;
//DUNG THU VIEN JACKSON http://wiki.fasterxml.com/JacksonHome


public class KaraUtils {
	
	private static final String TAG = "KaraUtils";
	
	private static final Logger log = Logger.getLogger(KaraUtils.class
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
	
	private static String createLocalPath(String bookId){
		bookId = bookId.replaceAll("/", "");
		String path = Environment.getExternalStorageDirectory() + "/Android/data/" + IApplication.getInstance().packageName + "/" + bookId;
		return path;
	}
	
	/**
	 * @param bookId
	 * @param chapIndex*/
	public static String getChapPathFromSdcard(String bookId, int chapIndex){
		bookId = bookId.replaceAll("/", "");
		String path = Environment.getExternalStorageDirectory() + "/Android/data/" + IApplication.getInstance().packageName + "/" + bookId+"/chap_"+chapIndex+".fb2";
		
		File pathFile = new File(path);
		if(pathFile.exists()){
			return path;
		}else{
			return null;
		}
	}
	
	/**
	 * @param bookName
	 * @param bookId
	 * @param chapTitle
	 * @param chapIndex
	 * @param content*/
	public static void saveChapContent2SDCard(String bookName, String bookId, String chapTitle, int chapIndex, String content){
		String path = createLocalPath(bookId);
		
		try{
			File filePath = new File(path);
			if(!filePath.exists()){
				filePath.mkdirs();
			}
			
			content = convert2Html(chapTitle, bookName, content);
			
			String fileName = "chap_"+chapIndex+".fb2";
			File myFile = new File(path + File.separator + "/"+fileName);
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut, "UTF-8");
			myOutWriter.append(content);
			myOutWriter.close();
			fOut.close();
			
		}catch(Exception e){}
	}
	
	private static String convert2Html(String chapTitle, String bookName, String originalText){
		//Log.d(TAG, "convert2Html "+bookName);
		
		String results = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		results = results + "<body>";
		results = results + "<empty-line/>";
		results = results + "<title><p>"+ bookName +"</p></title>";
		results = results + "<empty-line/>";
		results = results + "<subtitle><p>"+ chapTitle +"</p></subtitle>";
		results = results + "<empty-line/>";
		results = results + "<empty-line/>";
		results = results + "<empty-line/>";
		results = results + "<empty-line/>";
		results = results + "<empty-line/>";
		String[] lines = originalText.split(System.getProperty("line.separator"));
		
		for(int i = 0; i < lines.length; i++){
			results = results + "<p>"+lines[i]+"</p>";
		}
		
		results = results +"</body>";
		
		return results;
	}
	
	public static void writeFileOnSDCard(String strWrite, Context context, String fileName)
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
