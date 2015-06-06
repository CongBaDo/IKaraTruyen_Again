package com.ikaratruyen.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Config;
import android.util.Log;

import com.ikaratruyen.IApplication;
import com.ikaratruyen.model.GetBookContentRequest;
import com.ikaratruyen.model.GetBookContentResponse;
import com.ikaratruyen.model.GetBookRequest;
import com.ikaratruyen.model.GetBookResponse;
import com.ikaratruyen.model.GetChapterRequest;
import com.ikaratruyen.model.GetChapterResponse;
import com.ikaratruyen.model.GetGenresRequest;
import com.ikaratruyen.model.GetGenresResponse;
import com.ikaratruyen.model.GetHotAppsRequest;
import com.ikaratruyen.model.GetHotAppsResponse;
import com.ikaratruyen.model.GetOtherAppsRequest;
import com.ikaratruyen.model.GetOtherAppsResponse;
import com.ikaratruyen.model.IncreaseViewCounterRequest;
import com.ikaratruyen.model.IncreaseViewCounterResponse;
import com.ikaratruyen.model.NewBooksRequest;
import com.ikaratruyen.model.NewBooksResponse;
import com.ikaratruyen.model.RateBookRequest;
import com.ikaratruyen.model.RateBookResponse;
import com.ikaratruyen.model.SearchBooksRequest;
import com.ikaratruyen.model.SearchBooksResponse;
import com.ikaratruyen.model.StoreGcmIdRequest;
import com.ikaratruyen.model.StoreGcmIdResponse;
import com.ikaratruyen.model.TopBooksRequest;
import com.ikaratruyen.model.TopBooksResponse;
import com.ikaratruyen.request.IGetBooksContentRequest.IBookContentCallBack;

public class Server {
	private static final String TAG = "Server";
	
	public final static String mainServer = "http://www.ikaratruyen.com";
	public final static String INCREASE_VIEW_COUNTER_URL = "/test.IncreaseViewCounter";
	public final static String GETGENRES = "/test.GetGenres";
	public final static String GETTOPBOOK = "/test.TopBooks";
	public final static String GETBOOK = "/test.GetBook";
	public final static String GETNEWBOOKS = "/test.NewBooks";
	public final static String GETCHAPTER = "/test.GetChapter";
	public final static String GETHOTAPP	= "/test.GetHotApps";
	public final static String SUGGESTION_SEARCH = "/test.SuggestionSearch";
	public final static String GET_OTHER_APP	= "/test.GetOtherApps";
	public final static String VIEW_COUNTER = "/test.IncreaseViewCounter";
	public final static String RATING = "/test.RateBook";
	public final static String STOREGCM	= "/test.StoreGcmId";
	public final static String SEARCH_BOOK = "/test.SearchBooks";
	public final static String GET_BOOK_CONTENT = "/test.GetBookContent";
//	public static String GETCHAPTER = mainServer + GETCHAPTER;
	
//	public static GetBookContentResponse getBookContent(GetBookContentRequest recording) {
//		//		 TODO Auto-generated method stub
//		HttpClient httpclient = new DefaultHttpClient();
//		HttpPost httppost = null;
//		httppost = new HttpPost(mainServer + GET_BOOK_CONTENT);
//		InputStream inputStream = null;
//		Log.i(TAG, "responseCode "+mainServer + GET_BOOK_CONTENT);
//		try {
//			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//			String parametersInString = Utils.serialize(recording);
//			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
//			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
////			Log.i(GETGENRES, "content "+mainServer + GETGENRES);
//			HttpResponse httpResponse = httpclient.execute(httppost);
////			String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//			 inputStream = httpResponse.getEntity().getContent();
////			content = decompress(content);
//			int responseCode = httpResponse.getStatusLine().getStatusCode();
////			Log.v(TAG, "content "+content+" "+responseCode);
//			return Utils.deserialize(GetBookContentResponse.class, content);
//			
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		
//		return null;
//	}
	
	public static String GET(GetBookContentRequest recording, IBookContentCallBack callback){
		
        String result = "";
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + GET_BOOK_CONTENT);
		InputStream inputStream = null;
		HttpClient httpclient = new DefaultHttpClient();
		Log.i(TAG, "responseCode "+mainServer + GET_BOOK_CONTENT);
        try {
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(recording);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse httpResponse = httpclient.execute(httppost);
			 inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream, recording, callback);
            else
                result = "Did not work!";
 
            Log.v(TAG, "result "+result);
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
	
	// convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream, GetBookContentRequest recording, IBookContentCallBack callback) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        int i = 0;
        while((line = bufferedReader.readLine()) != null)
			try {
				//result += line;
				i++;
				Log.d(TAG, "index "+i);
				callback.onProgress(i);
				IKaraDbHelper.getInstance(IApplication.getInstance().getApplicationContext()).addRowBookTableFollowId(recording.bookId, line);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//line;
 
        inputStream.close();
        return result;
 
    }
 
	
	
	public static SearchBooksResponse searchBook(SearchBooksRequest recording) {
		//		 TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + SEARCH_BOOK);
		
//		Log.i(GETGENRES, "responseCode "+recording.);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(recording);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			Log.i(GETGENRES, "content "+mainServer + GETGENRES);
			HttpResponse httpResponse = httpclient.execute(httppost);
			String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			Log.v(TAG, "content "+content+" "+responseCode);
			return Utils.deserialize(SearchBooksResponse.class, content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static StoreGcmIdResponse storeGCMI(StoreGcmIdRequest recording) {
		//		 TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + STOREGCM);
		
//		Log.i(GETGENRES, "responseCode "+recording.);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(recording);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			Log.i(GETGENRES, "content "+mainServer + GETGENRES);
			HttpResponse httpResponse = httpclient.execute(httppost);
			String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			Log.v(TAG, "content "+content+" "+responseCode);
			return Utils.deserialize(StoreGcmIdResponse.class, content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static RateBookResponse rateResponse(RateBookRequest recording) {
		//		 TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + RATING);
		
//		Log.i(GETGENRES, "responseCode "+recording.);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(recording);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			Log.i(GETGENRES, "content "+mainServer + GETGENRES);
			HttpResponse httpResponse = httpclient.execute(httppost);
			String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			Log.v(TAG, "content "+content+" "+responseCode);
			return Utils.deserialize(RateBookResponse.class, content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static IncreaseViewCounterResponse getViewConterResponse(IncreaseViewCounterRequest recording) {
		//		 TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + VIEW_COUNTER);
		
//		Log.i(GETGENRES, "responseCode "+recording.);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(recording);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			Log.i(GETGENRES, "content "+mainServer + GETGENRES);
			HttpResponse httpResponse = httpclient.execute(httppost);
			String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			Log.v(TAG, "content "+content+" "+responseCode);
			return Utils.deserialize(IncreaseViewCounterResponse.class, content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static GetOtherAppsResponse getOtherAppResponse(GetOtherAppsRequest recording) {
		//		 TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + GET_OTHER_APP);
		
//		Log.i(GETGENRES, "responseCode "+recording.);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(recording);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			Log.i(GETGENRES, "content "+mainServer + GETGENRES);
			HttpResponse httpResponse = httpclient.execute(httppost);
			   String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();
//			Log.i(GETGENRES, "responseCode "+responseCode);
			Log.v(TAG, "content "+responseCode+" "+content);
			return Utils.deserialize(GetOtherAppsResponse.class, content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static GetGenresResponse getGenresResponse(GetGenresRequest recording) {
		//		 TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + GETGENRES);
		
//		Log.i(GETGENRES, "responseCode "+recording.);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			GetGenresRequest rateRecordingRequest = new GetGenresRequest();
			rateRecordingRequest.language = "vi";
			rateRecordingRequest.platform = "Android";
			String parametersInString = Utils.serialize(rateRecordingRequest);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
//			Log.i(GETGENRES, "content "+mainServer + GETGENRES);
			HttpResponse httpResponse = httpclient.execute(httppost);
			   String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();
//			Log.i(GETGENRES, "responseCode "+responseCode);
//			Log.v(GETGENRES, "content "+content);
			return Utils.deserialize(GetGenresResponse.class, content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static TopBooksResponse getTopBooksResponse(TopBooksRequest recording) {
		//		 TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + GETTOPBOOK);
		
		//Log.i(GETGENRES, "content "+mainServer + GETTOPBOOK);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(recording);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			//Log.i(GETGENRES, "content "+mainServer + GETTOPBOOK);
			HttpResponse httpResponse = httpclient.execute(httppost);
			String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			//Log.i(GETGENRES, "responseCode "+responseCode);
			///Log.v(GETGENRES, "content "+content);
			
			writeFileOnSDCard(content, IApplication.getInstance().getApplicationContext(), "CongBa.txt");
			return Utils.deserialize(TopBooksResponse.class, content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static GetBookResponse getBookResponse(GetBookRequest recording) {
		//		 TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + GETBOOK);
		
		//Log.i(GETGENRES, "content "+mainServer + GETBOOK);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(recording);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse httpResponse = httpclient.execute(httppost);
			String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			//Log.i(GETGENRES, "responseCode "+responseCode +" "+content);
//			Log.v(GETGENRES, "content "+content);
			
			writeFileOnSDCard(content, IApplication.getInstance().getApplicationContext(), "GetBookResponse.txt");
			return Utils.deserialize(GetBookResponse.class, content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static NewBooksResponse getNewBooksResponse(NewBooksRequest recording) {
		//		 TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + GETNEWBOOKS);
		
		Log.i(TAG, "url "+mainServer + GETNEWBOOKS);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(recording);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse httpResponse = httpclient.execute(httppost);
			String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			//Log.i(GETGENRES, "responseCode "+responseCode +" "+content);
			Log.v(TAG, "content "+responseCode+" - "+content);
			
			writeFileOnSDCard(content, IApplication.getInstance().getApplicationContext(), "NewBooksResponse.txt");
			return Utils.deserialize(NewBooksResponse.class, content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static void writeFileOnSDCard(String strWrite, Context context,
			String fileName) {
		try {
			String fullPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			File myFile = new File(fullPath + File.separator + "/" + fileName);

			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(strWrite);
			myOutWriter.close();
			fOut.close();
		} catch (Exception e) {
			// do your stuff here
			e.printStackTrace();
		}
	}
	
	public static GetChapterResponse getChapterResponse(GetChapterRequest recording) {
		//		 TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + GETCHAPTER);
		
		Log.i(TAG, "url "+mainServer + GETCHAPTER+" "+recording.chapterId);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(recording);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse httpResponse = httpclient.execute(httppost);
			String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			//Log.i(GETGENRES, "responseCode "+responseCode +" "+content);
//			Log.v(TAG, "content "+responseCode+" - "+content);
			
			writeFileOnSDCard(content, IApplication.getInstance().getApplicationContext(), "GetChapterResponse.txt");
			return Utils.deserialize(GetChapterResponse.class, content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static GetHotAppsResponse getHotAppResponse(GetHotAppsRequest recording) {
		//		 TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = null;
		httppost = new HttpPost(mainServer + GETHOTAPP);
		
//		Log.i(GETGENRES, "responseCode "+recording.);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(recording);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse httpResponse = httpclient.execute(httppost);
			String content = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
			
			int responseCode = httpResponse.getStatusLine().getStatusCode();
			//Log.i(GETGENRES, "responseCode "+responseCode +" "+content);
			Log.v(TAG, "content "+responseCode+" - "+content);
			
			writeFileOnSDCard(content, IApplication.getInstance().getApplicationContext(), "GetHotAppsResponse.txt");
			return Utils.deserialize(GetHotAppsResponse.class, content);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static String suggestsearch(String query) {
    	//Log.i(TAG, "trackImpression "+trigger);
        try {
			
            String server = mainServer+SUGGESTION_SEARCH +  
            		"?query=" + URLEncoder.encode(query, "utf-8") + 
            		"&size=" + 10;
            
            DefaultHttpClient httpclient = new DefaultHttpClient();
            BasicHttpContext mHttpContext = new BasicHttpContext();
			
			HttpPost httppost = new HttpPost(server);
			HttpResponse response = httpclient.execute(httppost, mHttpContext);
			int responseCode = response.getStatusLine().getStatusCode();
			//Log.e(TAG, "responde CODE " + responseCode+" "+server);
            
			if (responseCode == HttpStatus.SC_OK) {
				Log.e(TAG, "200 "+server);
				HttpEntity httpEntity = response.getEntity();
				String respondString  = new String(EntityUtils.toString(httpEntity));
				Log.v(TAG, "jSOn " +respondString.toString());
				
				return respondString;
			}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return null;
    }
	
	public static String compress(String str) throws Exception {
        if (str == null || str.length() == 0) {
            return str;
        }
        System.out.println("String length : " + str.length());
        ByteArrayOutputStream obj=new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        String outStr = new String(Base64.encode(obj.toByteArray(), Base64.DEFAULT));
        System.out.println("Output String length : " + outStr.length());
        return outStr;
       
     }

      public static String decompress(String str) throws Exception {
        if (str == null || str.length() == 0) {
            return str;
        }
        System.out.println("Input String length : " + str.length());
        
        byte[] bytes = Base64.decode(str.getBytes("UTF-8"), Base64.DEFAULT);
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        String outStr = "";
        String line;
        while ((line=bf.readLine())!=null) {
          outStr += line;
        }
        System.out.println("Output String lenght : " + outStr.length());
        return outStr;
     }
}
