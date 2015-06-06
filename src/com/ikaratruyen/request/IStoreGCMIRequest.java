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
import com.ikaratruyen.model.IncreaseViewCounterRequest;
import com.ikaratruyen.model.IncreaseViewCounterResponse;
import com.ikaratruyen.model.NewBooksRequest;
import com.ikaratruyen.model.NewBooksResponse;
import com.ikaratruyen.model.RateBookRequest;
import com.ikaratruyen.model.RateBookResponse;
import com.ikaratruyen.model.StoreGcmIdRequest;
import com.ikaratruyen.model.StoreGcmIdResponse;
import com.ikaratruyen.utils.Server;

public class IStoreGCMIRequest extends AsyncTask<Void, Void, StoreGcmIdResponse>{
	
	private static final String TAG = "IStoreGCMIRequest";
	
	private StoreGcmIdRequest reuqest;

	public interface StoreCallBack {
		public void onStorePost(StoreGcmIdResponse statusObj);
		public void fail();
	}
	
	private StoreCallBack callBack;
    
	public IStoreGCMIRequest(StoreCallBack callBack, StoreGcmIdRequest request) {
        this.callBack = callBack;
        this.reuqest = request;
    }
	@Override
	protected StoreGcmIdResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		StoreGcmIdResponse response = Server.storeGCMI(reuqest);
        //Log.e(TAG, "REPONSE "+response.genres.size());
		
        return response;
	}
	
	@Override
    protected void onPostExecute(StoreGcmIdResponse statusObj) {
		if(statusObj == null){
			callBack.fail();
		}else{
			callBack.onStorePost(statusObj);
		}
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
