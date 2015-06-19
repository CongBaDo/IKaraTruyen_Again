package com.ikaratruyen.utils;


import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.ikaratruyen.model.Book;
import com.ikaratruyen.model.GetBookContentRequest;
import com.ikaratruyen.utils.IDownloader.DownloadCallback;

public class DownloadService extends Service {
	
	private static final String TAG = "DownloadService";
	private String bookId;
	IDownloader downloader;
	ResultReceiver resultReceiver;
	
	public DownloadService() {
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		resultReceiver = intent.getParcelableExtra("receiver");
		Log.e(TAG, "onStartCommand");
		bookId = intent.getExtras().getString("book_id");
		String bookTitle = intent.getExtras().getString("book_title");
		GetBookContentRequest request = new GetBookContentRequest();
		request.bookId = bookId;
		request.language = "vi";
		Log.i(TAG, "Click Download ");
		
		Book itembook = new Book();
		itembook._id = bookId;
		itembook.title = bookTitle;
		
		downloader = new IDownloader(itembook);
		downloader.setCallBack(new DownloadCallback() {
			
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				downloader.stop();
				onDestroy();
			}
			
			@Override
			public void onFail() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProgress(int progress) {
				// TODO Auto-generated method stub
				Log.e(TAG, "onProgress "+progress);
				Bundle bundle = new Bundle();
				bundle.putInt("progress", progress);
				resultReceiver.send(100, bundle);
			}
		});
		downloader.download();
		
//		new IGetBooksContentRequest(new IBookContentCallBack() {
//			
//			@Override
//			public void onResultINewBookPostPost(GetBookContentResponse statusObj) {
//				// TODO Auto-generated method stub
//				Log.i(TAG, "BOOK CONTENT "+statusObj.book.chapters.get(0).content);
//			}
//			
//			@Override
//			public void fail() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onProgress(int progress) {
//				Bundle bundle = new Bundle();
//				bundle.putInt("progress", progress);
//				resultReceiver.send(100, bundle);
//				// TODO Auto-generated method stub
//				//Log.i(TAG, "BOOK progress "+progress);
//			}
//		}, request).execute();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
    public void onCreate() {
        //Log.e(TAG, "onCreate ");
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
    	
    }
 
    @Override
    public void onDestroy() {
    	Log.e(TAG, "onDestroy");
//    	downloader.stop();
    }
}

