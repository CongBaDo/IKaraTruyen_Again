package com.ikaratruyen.utils;

import java.util.ArrayList;

import android.util.Log;

import com.ikaratruyen.IApplication;
import com.ikaratruyen.model.Book;
import com.ikaratruyen.model.Chapter;
import com.ikaratruyen.model.GetChapterRequest;
import com.ikaratruyen.model.GetChapterResponse;
import com.ikaratruyen.request.IGetChapterRequest;
import com.ikaratruyen.request.IGetChapterRequest.IChapterPostCallBack;

public class IDownloader {
	private static final String TAG = "IDownloader";
	
	private IGetChapterRequest requestDownload;
	private String bookID;
	public IDownloader(String bookId){
		this.bookID  = bookId;
	}
	
	private Chapter checkNextChapToDownload(){
		ArrayList<Chapter> allRow = IKaraDbHelper.getInstance(IApplication.getInstance().getApplicationContext()).getAllChapter(bookID);
		//Log.v(TAG, "checkNextChapToDownload "+allRow.size());
		for(int i = 0; i < allRow.size(); i++){
			if(!allRow.get(i).downloaded){
				return allRow.get(i);
			}
		}
		
		return null;
	}
	
	public void stop(){
		if(requestDownload != null){
			requestDownload.onCancelled();
			requestDownload = null;
		}
	}
	
	public void download(){
		Chapter chap =  checkNextChapToDownload();
		if(chap == null){
			//Log.e(TAG, "DOWNLOAD STOP");
			return;
		}
		//Log.i(TAG, "download "+chap._id);
		
		GetChapterRequest request = new GetChapterRequest();
		request.chapterId = chap._id;
		request.language = "vi";
		
		requestDownload = new IGetChapterRequest(new IChapterPostCallBack() {

			@Override
			public void onResultChapterPostPost(GetChapterResponse statusObj) {
				// TODO Auto-generated method stub
//				Log.w(TAG, "onResultChapterPostPost "+statusObj.chapter.content);
				
//				IKaraDbHelper.getInstance(IApplication.getInstance().getApplicationContext()).updateRowBookTableFollowId(bookID, statusObj.chapter);
				download();
			}

			@Override
			public void fail() {
				// TODO Auto-generated method stub

			}
		}, request);
		
		requestDownload.execute();
	}
}
