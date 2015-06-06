package com.ikaratruyen.request;

import android.os.AsyncTask;

import com.ikaratruyen.model.Chapter;
import com.ikaratruyen.model.GetBookRequest;
import com.ikaratruyen.model.GetBookResponse;
import com.ikaratruyen.model.GetChapterRequest;
import com.ikaratruyen.model.GetChapterResponse;
import com.ikaratruyen.model.GetGenresResponse;
import com.ikaratruyen.model.GetOtherAppsRequest;
import com.ikaratruyen.model.GetOtherAppsResponse;
import com.ikaratruyen.model.NewBooksRequest;
import com.ikaratruyen.model.NewBooksResponse;
import com.ikaratruyen.utils.Server;

public class IGetOtherAppRequest extends AsyncTask<Void, Void, GetOtherAppsResponse>{
	
	private static final String TAG = "IGetOtherAppRequest";
	
	private GetOtherAppsRequest reuqest;

	public interface IOtherAppPostCallBack {
		public void onOtherAppPostPost(GetOtherAppsResponse statusObj);
		public void fail();
	}
	
	private IOtherAppPostCallBack callBack;
    
	public IGetOtherAppRequest(IOtherAppPostCallBack callBack, GetOtherAppsRequest request) {
        this.callBack = callBack;
        this.reuqest = request;
    }
	@Override
	protected GetOtherAppsResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		GetOtherAppsResponse response = Server.getOtherAppResponse(reuqest);
        //Log.e(TAG, "REPONSE "+response.genres.size());
		
        return response;
	}
	
	@Override
    protected void onPostExecute(GetOtherAppsResponse statusObj) {
		if(statusObj == null){
			callBack.fail();
		}else{
			callBack.onOtherAppPostPost(statusObj);
		}
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
