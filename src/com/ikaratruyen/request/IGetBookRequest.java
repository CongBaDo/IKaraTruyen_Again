package com.ikaratruyen.request;

import android.os.AsyncTask;

import com.ikaratruyen.model.GetBookRequest;
import com.ikaratruyen.model.GetBookResponse;
import com.ikaratruyen.model.GetGenresResponse;
import com.ikaratruyen.utils.Server;

public class IGetBookRequest extends AsyncTask<Void, Void, GetBookResponse>{
	
	private static final String TAG = "IGetBookRequest";
	
	private GetBookRequest reuqest;

	public interface IGetBookPostCallBack {
		public void onResultIGenresPostPost(GetBookResponse statusObj);
		public void fail();
	}
	
	private IGetBookPostCallBack callBack;
    
	public IGetBookRequest(IGetBookPostCallBack callBack, GetBookRequest request) {
        this.callBack = callBack;
        this.reuqest = request;
    }
	@Override
	protected GetBookResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		GetBookResponse response = Server.getBookResponse(reuqest);
        //Log.e(TAG, "REPONSE "+response.genres.size());
		
        return response;
	}
	
	@Override
    protected void onPostExecute(GetBookResponse statusObj) {
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
