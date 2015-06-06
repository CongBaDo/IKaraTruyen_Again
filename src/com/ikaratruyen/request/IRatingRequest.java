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
import com.ikaratruyen.utils.Server;

public class IRatingRequest extends AsyncTask<Void, Void, RateBookResponse>{
	
	private static final String TAG = "IRatingRequest";
	
	private RateBookRequest reuqest;

	public interface RateCallBack {
		public void onRatePost(RateBookResponse statusObj);
		public void fail();
	}
	
	private RateCallBack callBack;
    
	public IRatingRequest(RateCallBack callBack, RateBookRequest request) {
        this.callBack = callBack;
        this.reuqest = request;
    }
	@Override
	protected RateBookResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		RateBookResponse response = Server.rateResponse(reuqest);
        //Log.e(TAG, "REPONSE "+response.genres.size());
		
        return response;
	}
	
	@Override
    protected void onPostExecute(RateBookResponse statusObj) {
		if(statusObj == null){
			callBack.fail();
		}else{
			callBack.onRatePost(statusObj);
		}
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
