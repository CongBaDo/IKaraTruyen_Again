package com.ikaratruyen.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ikaratruyen.model.App;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ikaratruyen.R;

public class UngDungHayAdapter extends BaseAdapter{
	
	private static final String TAG = "UngDungHayAdapter";
	boolean canLoadMore=true;
	private Activity context;
	int nNewPosition=-1;
	
	private ArrayList<App> appItems;
	public UngDungHayAdapter(Activity context, ArrayList<App> appItems){
		this.context = context;
		this.appItems = appItems;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return appItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return appItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
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
        	convertView = mInflater.inflate(R.layout.row_app_hay, parent, false);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.tv_title_app);
            holder.txtContent = (TextView)convertView.findViewById(R.id.tv_content_app);
            holder.txtOpen = (TextView)convertView.findViewById(R.id.tv_open_app_hay);
            holder.imgThumb = (ImageView)convertView.findViewById(R.id.img_thumb_app_hay);
            
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        ImageLoader.getInstance().displayImage(appItems.get(position).thumbnailUrl, holder.imgThumb);
        holder.txtTitle.setText(appItems.get(position).appName);
        holder.txtContent.setText(appItems.get(position).desc);
        
        holder.txtOpen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appItems.get(position).storeId)));
			}
		});
        
		return convertView;
	}

	private class ViewHolder {
		ImageView imgThumb;
		TextView txtTitle;
		TextView txtContent;
		TextView txtOpen;
	}
}
