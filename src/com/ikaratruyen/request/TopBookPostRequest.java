package com.ikaratruyen.request;

import android.os.AsyncTask;
import android.util.Log;

import com.ikaratruyen.model.TopBooksRequest;
import com.ikaratruyen.model.TopBooksResponse;
import com.ikaratruyen.utils.Server;

public class TopBookPostRequest extends AsyncTask<Void, Void, TopBooksResponse>{
	
	private static final String TAG = "TopBookPostRequest";

	public interface TopBookCallBack {
		public void onResultDashboardPost(TopBooksResponse statusObj);
		public void fail();
	}
	
	private TopBookCallBack callBack;
	private TopBooksRequest data;
    
	public TopBookPostRequest(TopBookCallBack callBack, TopBooksRequest data) {
        this.callBack = callBack;
        this.data = data;
        
        Log.w(TAG, "TopBookPostRequest "+data.cursor);
    }
	@Override
	protected TopBooksResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		TopBooksResponse response = Server.getTopBooksResponse(data);
        //Log.e(TAG, "REPONSE "+response.genres.size());
		
        return response;
	}
	
	@Override
    protected void onPostExecute(TopBooksResponse statusObj) {
		if(statusObj == null){
			callBack.fail();
		}else{
			callBack.onResultDashboardPost(statusObj);
		}
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
