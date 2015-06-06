package com.ikaratruyen.request;

import android.os.AsyncTask;

import com.ikaratruyen.model.Chapter;
import com.ikaratruyen.model.GetBookRequest;
import com.ikaratruyen.model.GetBookResponse;
import com.ikaratruyen.model.GetChapterRequest;
import com.ikaratruyen.model.GetChapterResponse;
import com.ikaratruyen.model.GetGenresResponse;
import com.ikaratruyen.model.NewBooksRequest;
import com.ikaratruyen.model.NewBooksResponse;
import com.ikaratruyen.utils.Server;

public class IGetChapterRequest extends AsyncTask<Void, Void, GetChapterResponse>{
	
	private static final String TAG = "IGetChapterRequest";
	
	private GetChapterRequest reuqest;

	public interface IChapterPostCallBack {
		public void onResultChapterPostPost(GetChapterResponse statusObj);
		public void fail();
	}
	
	private IChapterPostCallBack callBack;
    
	public IGetChapterRequest(IChapterPostCallBack callBack, GetChapterRequest request) {
        this.callBack = callBack;
        this.reuqest = request;
    }
	@Override
	protected GetChapterResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		GetChapterResponse response = Server.getChapterResponse(reuqest);
        //Log.e(TAG, "REPONSE "+response.genres.size());
		
        return response;
	}
	
	@Override
    protected void onPostExecute(GetChapterResponse statusObj) {
		if(statusObj == null){
			callBack.fail();
		}else{
			callBack.onResultChapterPostPost(statusObj);
		}
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
