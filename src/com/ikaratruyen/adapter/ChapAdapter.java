package com.ikaratruyen.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ikaratruyen.model.Chapter;
import com.yamin.reader.R;

public class ChapAdapter extends BaseAdapter{
	
	private static final String TAG = "ChapAdapter";
	boolean canLoadMore=true;
	private Context context;
	int nNewPosition=-1;
	
	private ArrayList<Chapter> chaps;
	public ChapAdapter(Context context, ArrayList<Chapter> chaps){
		this.context = context;
		this.chaps = chaps;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return chaps.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return chaps.get(position);
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
        	convertView = mInflater.inflate(R.layout.row_chapter, parent, false);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.tv_title_chap);
            holder.imgCheck = (ImageView) convertView.findViewById(R.id.img_check);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        if(chaps.get(position).check){
        	holder.imgCheck.setVisibility(View.VISIBLE);
        }else{
        	holder.imgCheck.setVisibility(View.GONE);
        }
        
        String title = null;
        if(chaps.get(position).volume != null){
        	title = "Q"+chaps.get(position).volume+" - "+"C"+(position+1)+": "+chaps.get(position).title;
        }else{
        	title = "C"+(position+1)+": "+chaps.get(position).title;
        }
        
    	holder.txtTitle.setText(title);

		return convertView;
	}

	private class ViewHolder {
		TextView txtTitle;
		ImageView imgCheck;
	}
}
