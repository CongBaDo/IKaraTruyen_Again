package com.ikaratruyen.customview;

import org.json.JSONArray;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.ikaratruyen.adapter.BookAdapter;


public class EndlessListView extends ListView implements OnScrollListener {
	
	private boolean isLoading;
	private BookAdapter adapter;
	private EndlessListener listener;
	String tag= "EndlessListView";
	
	public EndlessListView(Context context) {
		this(context,null);
	}

	public EndlessListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//this.setOnItemClickListener(myListener);
		this.setOnScrollListener(this);
	}
	
	public void setListener(EndlessListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) { //header,footer is also item

		if (getItemCount()==0)
			return;
		
		int l = visibleItemCount + firstVisibleItem;
		if (l >= totalItemCount && !isLoading && canLoadMore()) {
			// It is time to add new data. We call the listener
			Log.d(tag, "add footer "+isLoading+" ");
			
			isLoading = true;
			listener.loadData();
		}
	}
	
	public void resetLoading(){
		isLoading = false;
	}
	
	public int getItemCount(){ // get real item of listview not include header,footer
		if (this.adapter!=null){
			//return this.adapter.getCount(); // = row -> wrong with our context
			return this.adapter.getCount();
		}
		else 
			return 0;
	}
	
	private boolean canLoadMore(){
		if (adapter==null)
			return false;
		return this.adapter.canLoadMore();
	}
	
	public void setAdapter(BookAdapter adapter) {		
		super.setAdapter(adapter);
		this.adapter = adapter;
	}
	
	public void clearAllData(){
//		adapter.clearALlData();
		adapter.notifyDataSetChanged();
	}
	
	public void addMore(JSONArray data){
//		adapter.addMore(data);
	}

	
	public EndlessListener getListener() {
		return listener;
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
	
	public static interface EndlessListener {
		public void loadData() ;
	}
	

}
