package com.ikaratruyen.request;

import android.os.AsyncTask;

import com.ikaratruyen.model.GetHotAppsRequest;
import com.ikaratruyen.model.GetHotAppsResponse;
import com.ikaratruyen.utils.Server;

public class IGetHotAppRequest extends AsyncTask<Void, Void, GetHotAppsResponse>{
	
	private static final String TAG = "IGetHotAppRequest";
	
	private GetHotAppsRequest reuqest;

	public interface IHotAppPostCallBack {
		public void onResultHotAppPostPost(GetHotAppsResponse statusObj);
		public void fail();
	}
	
	private IHotAppPostCallBack callBack;
    
	public IGetHotAppRequest(IHotAppPostCallBack iHotAppPostCallBack,
			GetHotAppsRequest request) {
		// TODO Auto-generated constructor stub
		this.callBack = iHotAppPostCallBack;
		this.reuqest = request;
	}
	@Override
	protected GetHotAppsResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		GetHotAppsResponse response = Server.getHotAppResponse(reuqest);
        //Log.e(TAG, "REPONSE "+response.genres.size());
		
        return response;
	}
	
	@Override
    protected void onPostExecute(GetHotAppsResponse statusObj) {
		if(statusObj == null){
			callBack.fail();
		}else{
			callBack.onResultHotAppPostPost(statusObj);
		}
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
