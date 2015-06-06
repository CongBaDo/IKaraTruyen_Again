package com.ikaratruyen.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ikaratruyen.model.SearchItem;
import com.yamin.reader.R;

public class SearchAdapter extends BaseAdapter{
	
	private static final String TAG = "SearchAdapter";
	boolean canLoadMore=true;
	private Context context;
	int nNewPosition=-1;
	
	private ArrayList<SearchItem> searchItems;
	public SearchAdapter(Context context, ArrayList<SearchItem> searchItems){
		this.context = context;
		this.searchItems = searchItems;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return searchItems.get(position);
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
        	convertView = mInflater.inflate(R.layout.row_search, parent, false);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.tv_title_search);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.txtTitle.setText(searchItems.get(position).title);
        
		return convertView;
	}

	private class ViewHolder {
		TextView txtTitle;
	}
}
