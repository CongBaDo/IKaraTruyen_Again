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
import com.ikaratruyen.utils.Server;

public class IncreaseVCRequest extends AsyncTask<Void, Void, IncreaseViewCounterResponse>{
	
	private static final String TAG = "IncreaseVCRequest";
	
	private IncreaseViewCounterRequest reuqest;

	public interface IVCCallBack {
		public void onVCPost(IncreaseViewCounterResponse statusObj);
		public void fail();
	}
	
	private IVCCallBack callBack;
    
	public IncreaseVCRequest(IVCCallBack callBack, IncreaseViewCounterRequest request) {
        this.callBack = callBack;
        this.reuqest = request;
    }
	@Override
	protected IncreaseViewCounterResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		IncreaseViewCounterResponse response = Server.getViewConterResponse(reuqest);
        //Log.e(TAG, "REPONSE "+response.genres.size());
		
        return response;
	}
	
	@Override
    protected void onPostExecute(IncreaseViewCounterResponse statusObj) {
		if(statusObj == null){
			callBack.fail();
		}else{
			callBack.onVCPost(statusObj);
		}
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
