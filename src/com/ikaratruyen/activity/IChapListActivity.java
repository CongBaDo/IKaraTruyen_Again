package com.ikaratruyen.activity;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.ikaratruyen.adapter.ChapAdapter;
import com.ikaratruyen.adapter.FontAdapter;
import com.ikaratruyen.model.Chapter;
import com.ikaratruyen.model.ItemFont;
import com.ikaratruyen.utils.ISettings;
import com.ikaratruyen.utils.IkaraPreferences;
import com.ikaratruyen.utils.PrefConstant;
import com.ikaratruyen.R;

/**
 * This activity is an example of a responsive Android UI. On phones, the
 * SlidingMenu will be enabled only in portrait mode. In landscape mode, it will
 * present itself as a dual pane layout. On tablets, it will will do the same
 * general thing. In portrait mode, it will enable the SlidingMenu, and in
 * landscape mode, it will be a dual pane layout.
 * 
 * @author jeremy
 * 
 */
public class IChapListActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	private static final String TAG = "IChapListActivity";
	public static final int DEVICE_VERSION   = Build.VERSION.SDK_INT;
	public static final int DEVICE_HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;
	
	public static final int CHAPTER = 101;
	public static final int FONT = 100;
	
	private ListView listView;
	private ChapAdapter chapAdapter;
	private FontAdapter fontAdapter;
	private ArrayList<Chapter> chapList;
	private LinearLayout headerBar;
	private EditText edtFill;
	private int typeView;
	private TextView tvTitleBar;
	private ArrayList<ItemFont> fontDatas;
	
	private String[] fontList = new String[]{"Alex Brush", "Arial Narrow", "Arial", "Droid Sans", "Droid Serif", "Liberations Serif", "Open Sans", "Ostrich", "Roboto", "Typograph Times"};

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_chaplist);
		
		headerBar = (LinearLayout)findViewById(R.id.top_bar);
		
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){ 
			int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
			headerBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, actionBarHeight));
		} 
		
		headerBar.setGravity(Gravity.CENTER_VERTICAL);

		Log.i(TAG, "onCreate ");

		listView = (ListView) findViewById(R.id.list_chap);
		tvTitleBar = (TextView)findViewById(R.id.tv_title_bar);
		listView.setOnItemClickListener(this);
		edtFill = (EditText)findViewById(R.id.edt_fill);
		edtFill.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				Log.v(TAG, "AFTER "+s.toString());
				if(s.toString().length() > 0 && s.toString().length() < 6){
					int current = Integer.parseInt(s.toString());
					if(current >= chapList.size()){
						listView.setSelection(chapList.size());
					}else if(current == 0){
						listView.setSelection(current);
					}else{
						listView.setSelection(current - 1);
					}
				}else{
					listView.setSelection(0);
				}
			}
		});
		((ImageView)findViewById(R.id.img_back)).setOnClickListener(this);
		
		int currentIndex = getIntent().getExtras().getInt("current_index_chap");
		typeView = getIntent().getExtras().getInt("option_view");
		
		if(typeView == CHAPTER){
			tvTitleBar.setText(getResources().getString(R.string.chapter_value));
			chapList = ISettings.getInstance().getChapListContents();
			for(int i = 0; i < chapList.size(); i++){
				if(i== currentIndex){
					chapList.get(i).check = true;
				}else{
					chapList.get(i).check = false;
				}
			}
			chapAdapter = new ChapAdapter(getApplicationContext(), chapList);
			listView.setAdapter(chapAdapter);
			listView.setSelection(currentIndex);
		}else{
			edtFill.setVisibility(View.GONE);
			tvTitleBar.setText(getResources().getString(R.string.chon_font));
			 AssetManager assetManager = getApplicationContext().getAssets();
			 
			fontDatas = new ArrayList<ItemFont>();
			 String getFont = IkaraPreferences.getStringPref(IChapListActivity.this, PrefConstant.PREF_FONT, "DroidSans.ttf");
	        try {
				String[] files = assetManager.list("fonts");
				
				for(int i = 0; i < files.length; i++){
					ItemFont item = new ItemFont();
					item.setTitle(fontList[i]);
					item.setRealFontName(files[i]);
					
					if(getFont.equals(files[i])){
						item.setCheck(true);
					}else{
						item.setCheck(false);
					}
					Log.v(TAG, "AFTER "+files[i]);
					
					fontDatas.add(item);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        fontAdapter = new FontAdapter(getApplicationContext(), fontDatas);
	        listView.setAdapter(fontAdapter);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if(typeView == CHAPTER){
			
			intent.putExtra("book_title", chapList.get(position).title);
			intent.putExtra("chap_id", chapList.get(position)._id);
			intent.putExtra("chap_title", chapList.get(position).title);
			intent.putExtra("chap_index", position);
			setResult(CHAPTER, intent);
		}else{
			IkaraPreferences.saveStringPref(getApplicationContext(), PrefConstant.PREF_FONT, fontDatas.get(position).getRealFontName());
			setResult(FONT, intent);
		}
		
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
			
		case R.id.img_share:
			
//			Intent intent=new Intent();  
//            intent.putExtra("MESSAGE",message);  
//            setResult(2,intent);  
            finish();//finishing activity  
			break;
			
		default:
			break;
		}
	}
}
