package com.ikaratruyen.request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.DownloadManager.Request;
import android.os.AsyncTask;
import android.util.Log;

import com.ikaratruyen.IApplication;
import com.ikaratruyen.model.GetBookContentRequest;
import com.ikaratruyen.model.GetBookContentResponse;
import com.ikaratruyen.model.GetBookRequest;
import com.ikaratruyen.model.GetBookResponse;
import com.ikaratruyen.model.GetGenresResponse;
import com.ikaratruyen.model.NewBooksRequest;
import com.ikaratruyen.model.NewBooksResponse;
import com.ikaratruyen.utils.DigitalSignature;
import com.ikaratruyen.utils.IKaraDbHelper;
import com.ikaratruyen.utils.Server;
import com.ikaratruyen.utils.Utils;

public class IGetBooksContentRequest extends AsyncTask<Void, Integer, String>{
	
	private static final String TAG = "IGetBooksContentRequest";
	
	private GetBookContentRequest reuqest;

	public interface IBookContentCallBack {
		public void onResultINewBookPostPost(GetBookContentResponse statusObj);
		public void onProgress(int progress);
		public void fail();
	}
	
	private IBookContentCallBack callBack;
    
	public IGetBooksContentRequest(IBookContentCallBack callBack, GetBookContentRequest request) {
        this.callBack = callBack;
        this.reuqest = request;
    }
	@Override
	protected String doInBackground(Void... params) {
		// TODO Auto-generated method stub
//		GetBookContentResponse response = Server.getBookContent(reuqest);
//        Log.e(TAG, "REPONSE "+Server.GET(reuqest, callBack));
        
		Log.v(TAG, "Server "+1);
        
        HttpPost httppost = new HttpPost(Server.mainServer + Server.GET_BOOK_CONTENT);
		InputStream inputStream = null;
		HttpClient httpclient = new DefaultHttpClient();
//		Log.i(TAG, "responseCode "+mainServer + GET_BOOK_CONTENT);
        try {
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			String parametersInString = Utils.serialize(reuqest);
			nameValuePairs.add(new BasicNameValuePair("parameters", DigitalSignature.encryption(parametersInString)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse httpResponse = httpclient.execute(httppost);
			 inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null){
            	BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	            String line = "";
	            int i = 0;
	            while((line = bufferedReader.readLine()) != null)
	    			try {
	    				i++;
	    				//Log.d(TAG, "index "+i);
	    				publishProgress(i);
	    				IKaraDbHelper.getInstance(IApplication.getInstance().getApplicationContext()).addRowBookTableFollowId(reuqest.bookId, line);
	    			} catch (Exception e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}//line;
	     
	            inputStream.close();
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
		
        return "";
	}
	
	protected void onProgressUpdate(Integer... progress) {
		callBack.onProgress(progress[0]);
    } 
	
	@Override
    protected void onPostExecute(String value) {
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
