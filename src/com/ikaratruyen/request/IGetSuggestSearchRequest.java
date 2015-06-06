package com.ikaratruyen.request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.ikaratruyen.model.GetBookRequest;
import com.ikaratruyen.model.GetBookResponse;
import com.ikaratruyen.model.GetGenresResponse;
import com.ikaratruyen.model.SearchBooksRequest;
import com.ikaratruyen.model.SearchBooksResponse;
import com.ikaratruyen.utils.Server;

public class IGetSuggestSearchRequest extends AsyncTask<Void, Void, JSONArray>{
	
	private static final String TAG = "IGetSuggestSearchRequest";
	
	private String query;

	public interface IGetBookPostCallBack {
		public void onResultSearchPostPost(JSONArray statusObj);
		public void fail();
	}
	
	private IGetBookPostCallBack callBack;
    
	public IGetSuggestSearchRequest(IGetBookPostCallBack callBack, String request) {
        this.callBack = callBack;
        this.query = request;
    }
	@Override
	protected JSONArray doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		JSONArray json = null;
		try {
			json = new JSONArray(Server.suggestsearch(query));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return json;
	}
	
	@Override
    protected void onPostExecute(JSONArray statusObj) {
		if(statusObj == null){
			callBack.fail();
		}else{
			callBack.onResultSearchPostPost(statusObj);
		}
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
