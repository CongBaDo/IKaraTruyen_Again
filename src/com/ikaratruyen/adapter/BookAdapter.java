package com.ikaratruyen.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ikaratruyen.model.Book;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ikaratruyen.R;

public class BookAdapter extends BaseAdapter{
	
	private static final String TAG = "BookAdapter";
	boolean canLoadMore=true;
	private Context context;
	int nNewPosition=-1;
	
	private ArrayList<Book> books;
	public BookAdapter(Context context, ArrayList<Book> books){
		this.context = context;
		this.books = books;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return books.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return books.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		boolean shouldAnimation = false;
		if (nNewPosition<=position){
			shouldAnimation = true;
			nNewPosition = position;
		}
		
		ViewHolder holder = null;
        
        LayoutInflater mInflater = (LayoutInflater) 
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
        	convertView = mInflater.inflate(R.layout.row_book, parent, false);
            holder = new ViewHolder();
            holder.thumb = (ImageView) convertView.findViewById(R.id.img_book_thumb);
            holder.txtTitle = (TextView) convertView.findViewById(R.id.tv_title);
            holder.txtAuthor = (TextView)convertView.findViewById(R.id.tv_author);
            holder.txtStatus = (TextView) convertView.findViewById(R.id.tv_status);
            holder.txtCountView = (TextView) convertView.findViewById(R.id.tv_count_view);
            holder.containView = (LinearLayout)convertView.findViewById(R.id.contain_data);
            holder.containCount = (LinearLayout)convertView.findViewById(R.id.contain_count);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        ImageLoader.getInstance().displayImage(books.get(position).thumbnailUrl, holder.thumb);
        
        if(books.get(position)._id == null){
        	holder.containView.setVisibility(View.GONE);
        	holder.containCount.setVisibility(View.GONE);
        }else{
        	holder.containCount.setVisibility(View.VISIBLE);
        	holder.containView.setVisibility(View.VISIBLE);
        	holder.txtAuthor.setText(books.get(position).author);
        	holder.txtCountView.setText(books.get(position).viewCounter+"");
        	holder.txtStatus.setText(books.get(position).status);
        	holder.txtTitle.setText(books.get(position).title);
        }
        

		return convertView;
	}

	private class ViewHolder {
		ImageView thumb;
		TextView txtTitle, txtAuthor, txtStatus, txtCountView;
		LinearLayout containView, containCount;
	}
	
	public void addMore(ArrayList<Book> items, int limit) {
		Log.v(TAG, "addMore ");
		if (items == null) {
			return;
		}
		if (items.size() < limit) {
			canLoadMore = false;
		}

		for (int i = 0; i < items.size(); i++) {
			Book bok = items.get(i);
			books.add(bok);
		}
	}

	 
//	public void clearALlData() {
//		// Log.d(TAG, "clear all data");
//		books.clear();
//		canLoadMore = true;
//		nNewPosition = 0;
//	}

	public boolean canLoadMore() {
		Log.d(TAG, "canLoadMore "+canLoadMore);
		return canLoadMore;
	}
}
