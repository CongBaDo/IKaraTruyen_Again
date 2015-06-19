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
	private Book itemBook;
	public IDownloader(Book book){
		itemBook = book;
		this.bookID  = book._id;
	}
	
	interface DownloadCallback{
		public void onFinish();
		public void onProgress(int progress);
		public void onFail();
	}
	
	private DownloadCallback callback;
	
	private ArrayList<Chapter> getRealChapters(){
		
		ArrayList<Chapter> allRow = ISettings.getInstance().getChapListContents();//
		ArrayList<Chapter> downloadedRows = IKaraDbHelper.getInstance(IApplication.getInstance().getApplicationContext()).getAllChapter(bookID);
		
		for(int i = 0; i < allRow.size(); i++){
			for(int j = 0; j < downloadedRows.size(); j++){
				if(allRow.get(i)._id.equals(downloadedRows.get(j)._id)){
					allRow.get(i).downloaded = true;
				}
			}
		}
		
		return allRow;
	}
	
	private Chapter checkNextChapToDownload(){
		
		ArrayList<Chapter> allRow = getRealChapters();
		//Log.e(TAG, "checkNextChapToDownload "+" "+allRow.size());
		
		for(int i = 0; i < allRow.size(); i++){
			if(!allRow.get(i).downloaded){
				return allRow.get(i);
			}
		}
		
		return null;
	}
	
	private boolean existInDownloaded(Chapter chap){
		ArrayList<Chapter> downloadedRows = IKaraDbHelper.getInstance(IApplication.getInstance().getApplicationContext()).getAllChapter(bookID);
		for(int i = 0; i < downloadedRows.size(); i++){
			if(!chap._id.equals(downloadedRows.get(i)._id)){
				return true;
			}
		}
		
		return false;
	}
	
	public void stop(){
		if(requestDownload != null){
			requestDownload.onCancelled();
			requestDownload = null;
		}
	}
	
	public void setCallBack(DownloadCallback callback){
		this.callback = callback;
	}
	
	public void download(){
		Chapter chap =  checkNextChapToDownload();
		if(chap == null){
			Log.e(TAG, "DOWNLOAD STOP");
			this.callback.onFinish();
			return;
		}
		//Log.v(TAG, "download "+chap.title);
		//Log.i(TAG, "download "+chap._id);
		
		GetChapterRequest request = new GetChapterRequest();
		request.chapterId = chap._id;
		request.language = "vi";
		
		final int chapIndex = chap.index;
		final String chapTitle = chap.title;
		
		requestDownload = new IGetChapterRequest(new IChapterPostCallBack() {

			@Override
			public void onResultChapterPostPost(GetChapterResponse statusObj) {
				// TODO Auto-generated method stub
//				Log.w(TAG, "onResultChapterPostPost "+chapIndex+" "+statusObj.chapter._id);
				callback.onProgress(chapIndex);
				
				KaraUtils.saveChapContent2SDCard(itemBook.title, itemBook._id, chapTitle, chapIndex+1, statusObj.chapter.content);
				IKaraDbHelper.getInstance(IApplication.getInstance().getApplicationContext()).addRowBookTable(bookID, statusObj.chapter);
				download();
			}

			@Override
			public void fail() {
				// TODO Auto-generated method stub
				callback.onFail();
			}
		}, request);
		
		requestDownload.execute();
	}
}
