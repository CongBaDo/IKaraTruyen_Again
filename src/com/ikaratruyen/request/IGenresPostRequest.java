package com.ikaratruyen.request;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.ikaratruyen.model.GetGenresResponse;
import com.ikaratruyen.utils.Server;

public class IGenresPostRequest extends AsyncTask<Void, Void, GetGenresResponse>{
	
	private static final String TAG = "IGenresPostRequest";

	public interface IGenresPostCallBack {
		public void onResultIGenresPostPost(GetGenresResponse statusObj);
		public void fail();
	}
	
	private IGenresPostCallBack callBack;
    
	public IGenresPostRequest(IGenresPostCallBack callBack) {
        this.callBack = callBack;
    }
	@Override
	protected GetGenresResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		GetGenresResponse response = Server.getGenresResponse(null);
        //Log.e(TAG, "REPONSE "+response.genres.size());
		
        return response;
	}
	
	@Override
    protected void onPostExecute(GetGenresResponse statusObj) {
		if(statusObj == null){
			callBack.fail();
		}else{
			callBack.onResultIGenresPostPost(statusObj);
		}
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
