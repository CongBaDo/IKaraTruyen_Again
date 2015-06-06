package com.ikaratruyen.request;

import android.os.AsyncTask;

import com.ikaratruyen.model.GetBookRequest;
import com.ikaratruyen.model.GetBookResponse;
import com.ikaratruyen.model.GetGenresResponse;
import com.ikaratruyen.model.NewBooksRequest;
import com.ikaratruyen.model.NewBooksResponse;
import com.ikaratruyen.utils.Server;

public class IGetNewBooksRequest extends AsyncTask<Void, Void, NewBooksResponse>{
	
	private static final String TAG = "IGetNewBooksRequest";
	
	private NewBooksRequest reuqest;

	public interface INewBookPostCallBack {
		public void onResultINewBookPostPost(NewBooksResponse statusObj);
		public void fail();
	}
	
	private INewBookPostCallBack callBack;
    
	public IGetNewBooksRequest(INewBookPostCallBack callBack, NewBooksRequest request) {
        this.callBack = callBack;
        this.reuqest = request;
    }
	@Override
	protected NewBooksResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		NewBooksResponse response = Server.getNewBooksResponse(reuqest);
        //Log.e(TAG, "REPONSE "+response.genres.size());
		
        return response;
	}
	
	@Override
    protected void onPostExecute(NewBooksResponse statusObj) {
		if(statusObj == null){
			callBack.fail();
		}else{
			callBack.onResultINewBookPostPost(statusObj);
		}
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
