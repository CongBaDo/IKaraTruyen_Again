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
import com.ikaratruyen.model.SearchBooksRequest;
import com.ikaratruyen.model.SearchBooksResponse;
import com.ikaratruyen.utils.Server;

public class ISearchBookRequest extends AsyncTask<Void, Void, SearchBooksResponse>{
	
	private static final String TAG = "ISearchBookRequest";
	
	private SearchBooksRequest reuqest;

	public interface ISerchBookCallBack {
		public void onSearchPost(SearchBooksResponse statusObj);
		public void fail();
	}
	
	private ISerchBookCallBack callBack;
    
	public ISearchBookRequest(ISerchBookCallBack callBack, SearchBooksRequest request) {
        this.callBack = callBack;
        this.reuqest = request;
    }
	@Override
	protected SearchBooksResponse doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		SearchBooksResponse response = Server.searchBook(reuqest);
        //Log.e(TAG, "REPONSE "+response.genres.size());
		
        return response;
	}
	
	@Override
    protected void onPostExecute(SearchBooksResponse statusObj) {
		if(statusObj == null){
			callBack.fail();
		}else{
			callBack.onSearchPost(statusObj);
		}
	}
	
	public void onCancelled(){
		this.cancel(true);
	}
	
}
