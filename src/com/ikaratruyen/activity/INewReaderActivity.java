package com.ikaratruyen.activity;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ikaratruyen.customview.JustifiedTextView;
import com.ikaratruyen.model.Chapter;
import com.ikaratruyen.model.GetChapterRequest;
import com.ikaratruyen.model.GetChapterResponse;
import com.ikaratruyen.model.IncreaseViewCounterRequest;
import com.ikaratruyen.model.IncreaseViewCounterResponse;
import com.ikaratruyen.request.IGetChapterRequest;
import com.ikaratruyen.request.IGetChapterRequest.IChapterPostCallBack;
import com.ikaratruyen.request.IncreaseVCRequest;
import com.ikaratruyen.request.IncreaseVCRequest.IVCCallBack;
import com.ikaratruyen.utils.IKaraDbHelper;
import com.ikaratruyen.utils.ISettings;
import com.ikaratruyen.utils.IkaraConstant;
import com.ikaratruyen.utils.IkaraPreferences;
import com.ikaratruyen.utils.PageSplitter;
import com.ikaratruyen.utils.PrefConstant;
import com.ikaratruyen.utils.Server;
import com.ikaratruyen.utils.KaraUtils;
import com.ikaratruyen.R;

public class INewReaderActivity extends Activity implements OnClickListener,
		OnSeekBarChangeListener, OnPageChangeListener {

	private static final String TAG = "INewReaderActivity";
	// Splash screen timer
	private LinearLayout headerBar;
	private LinearLayout bottomBar;
	private Animation animShowBottom;
	private PageSplitter pageSplitter;
	private int currentChapIndex = 0;
	private boolean isOpenBook;
	private String chapTitle;
	private String bookTitle;
	private TextView tvChapIndex, tvIndex, tvQuyenIndex;
	private ImageView imgReadState;
	private SeekBar seek;
	private int readerState = IkaraConstant.READER_STATE.NIGHT;
	private int sizeChap = 0;
	private TextView tvIncrease, tvDecrease, tvChapIndexTop;
	private int currentTextSize = 0;
	private String currentContent;
	private ImageView imgIndexTop;
	private String bookId;
	private int viewCountPage = 0;
	private String chapId;
	private int pageIndex = 0;
	private ViewPager pagerReader;
	private boolean isMoved = false;
	MyPagerAdapter adapter;
	private InterstitialAd interstitial;
	private ProgressDialog dialogLoading;
	private String keepText = "";
	private int swipeType = IkaraConstant.SWIPE.NONE;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newreader);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		currentChapIndex = getIntent().getExtras().getInt("chap_index");
		chapTitle = getIntent().getExtras().getString("chap_title");
		bookId = getIntent().getExtras().getString("book_id");
		isOpenBook = getIntent().getExtras().getBoolean("open_book");
		 Log.d(TAG, "onCreate: " + bookId);

		headerBar = (LinearLayout) findViewById(R.id.top_bar_reader);

		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			int actionBarHeight = TypedValue.complexToDimensionPixelSize(
					tv.data, getResources().getDisplayMetrics());
			headerBar.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, actionBarHeight));
		}

		headerBar.setGravity(Gravity.CENTER_VERTICAL);

		bottomBar = (LinearLayout) findViewById(R.id.bottom_view);
		tvChapIndex = (TextView) bottomBar.findViewById(R.id.tv_chapter_index);
		tvChapIndexTop = (TextView) findViewById(R.id.tv_chap_top);
		tvQuyenIndex = (TextView) findViewById(R.id.tv_book_quyen_index);
		tvChapIndexTop.setVisibility(View.VISIBLE);
		tvChapIndexTop.setText((currentChapIndex + 1) + "");
		tvIndex = (TextView) bottomBar.findViewById(R.id.tv_index);
		imgIndexTop = (ImageView) findViewById(R.id.img_share);
		seek = (SeekBar) bottomBar.findViewById(R.id.sk_page);
		tvIncrease = (TextView) bottomBar.findViewById(R.id.tv_increase);
		tvDecrease = (TextView) bottomBar.findViewById(R.id.tv_decrease);
		pagerReader = (ViewPager) findViewById(R.id.pager_reader);
		pagerReader.setOnPageChangeListener(this);
		pagerReader.setOffscreenPageLimit(2);
		
		adapter = new MyPagerAdapter();
		pagerReader.setAdapter(adapter);

		ImageView imgFont = (ImageView) bottomBar
				.findViewById(R.id.img_font_text);
		imgFont.setOnClickListener(this);

		tvChapIndexTop.setOnClickListener(this);
		imgIndexTop.setOnClickListener(this);

		currentTextSize = IkaraPreferences.getIntPref(getApplicationContext(),
				PrefConstant.PREF_TEXT_SIZE,
				(int) getResources().getDimension(R.dimen.text_size));

		if (ISettings.getInstance().getChapListContents().get(currentChapIndex).volume != null) {
			long volume = ISettings.getInstance().getChapListContents()
					.get(currentChapIndex).volume;
			tvQuyenIndex.setVisibility(View.VISIBLE);
			tvQuyenIndex.setText(getResources().getString(R.string.book_value)
					+ " " + volume);
		}

		Log.v(TAG, "current Text Size " + currentTextSize);

		tvDecrease.setOnClickListener(this);
		tvIncrease.setOnClickListener(this);
		seek.setOnSeekBarChangeListener(this);
		tvChapIndex.setText(getResources().getString(R.string.chapter_value)
				+ " " + (currentChapIndex + 1));
		imgReadState = (ImageView) bottomBar
				.findViewById(R.id.img_change_state_reader);
		imgReadState.setOnClickListener(this);
		tvIndex.setOnClickListener(this);
		bookTitle = getIntent().getExtras().getString("book_title");
		chapId = getIntent().getExtras().getString("chap_id");
		Log.i(TAG, "onCreate " + chapId + " " + bookTitle);

		((ImageView) findViewById(R.id.img_back)).setOnClickListener(this);
		((ImageView) findViewById(R.id.img_share)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title_bar)).setText(bookTitle);
		((ImageView) findViewById(R.id.img_share))
				.setBackgroundResource(R.drawable.view_state_clipboard_button);

		headerBar.setVisibility(View.GONE);
		bottomBar.setVisibility(View.GONE);

		interstitial = new InterstitialAd(INewReaderActivity.this);
		interstitial.setAdUnitId("ca-app-pub-8429996645546440/9944646214");
		AdView adView = (AdView) findViewById(R.id.adView);
		adView.setVisibility(View.GONE);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		interstitial.loadAd(adRequest);
		interstitial.setAdListener(new AdListener() {
			public void onAdLoaded() {
				displayInterstitial();
			}
		});
		
		pagerReader.setOnTouchListener(new OnTouchListener() {
			float oldX = 0, newX = 0, sens = 10;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					oldX = event.getX();
					
					break;
		        case MotionEvent.ACTION_UP:
		        	if(!isMoved){
		        		showTopBottomView();
		        	}else{
		        		isMoved = false;
		        	}
		        	
		        	if(newX > oldX && pageIndex == 0){
		        		swipeType = IkaraConstant.SWIPE.LEFT;
		        	}else if(newX < oldX && pageIndex == sizeChap){
		        		swipeType = IkaraConstant.SWIPE.RIGHT;
		        	}else{
		        		swipeType = IkaraConstant.SWIPE.NONE;
		        	}
		        	
		        	if(swipeType == IkaraConstant.SWIPE.LEFT){
		    			Log.v(TAG, "LEFT EFT");
		    			if(currentChapIndex > 0){
		        			 currentChapIndex--;
		        			 loadChapContent(ISettings.getInstance().getChapListContents().get(currentChapIndex)._id);
		        		 }
		    		}else if(swipeType == IkaraConstant.SWIPE.RIGHT){
		    			Log.e(TAG, "RIGHT RUGHT ");
		    			pageIndex = 0;
		    			currentChapIndex++;
		    			if(currentChapIndex < ISettings.getInstance().getChapListContents().size()){
		    				loadChapContent(ISettings.getInstance().getChapListContents().get(currentChapIndex)._id);
		    			}else{
		    				currentChapIndex--;
		    			}
		    		}
		        	
		        	oldX = 0;
		        	newX = 0;
		            break; 
		        case MotionEvent.ACTION_MOVE:
		        	 newX = event.getX();
		        	 if (Math.abs(oldX - newX) > sens) {
		        		 isMoved = true;
		        		 hideToolBar();
		             }
		        	 
		        	break;
				}
				
				return false;
			}
		});
		
		loadChapContent(chapId);
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}

	private Chapter getCurrentChapter(String chapId) {
		for (int i = 0; i < ISettings.getInstance().getChapListContents()
				.size(); i++) {
			if (ISettings.getInstance().getChapListContents().get(i)._id.equals(chapId)) {
				return ISettings.getInstance().getChapListContents().get(i);
			}
		}

		return null;
	}
	
	private void showLoading(){
		dialogLoading = new ProgressDialog(INewReaderActivity.this);
		dialogLoading.setMessage("Loading...");
		dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialogLoading.setCancelable(false);
		dialogLoading.show();
	}
	
	private void hideLoading(){
		if(dialogLoading != null){
			dialogLoading.dismiss();
		}
	}
	
	/**
	 * @param id is chapId*/
	private void loadChapContent(String id) {
		Log.v(TAG, "loadChapContent "+id+" "+currentChapIndex);
		showLoading();

		chapTitle = getCurrentChapter(id).title;
		
		Log.d(TAG, "Chap Title "+chapTitle);
//		if(IKaraDbHelper.getInstance(getApplicationContext()).getChapContent(bookId, 0) != null && IKaraDbHelper.getInstance(getApplicationContext()).getChapContent(bookId, 0).length() > 0){
//			if(isOpenBook){
//				swipeType = IkaraConstant.SWIPE.NONE;
//				isOpenBook = false;
//				processOpenWithIndex();
//			}
//			
//			String content = IKaraDbHelper.getInstance(getApplicationContext()).getChapContent(bookId, currentChapIndex);
//			try {
//				content = Server.decompress(content);
//				JSONObject json = new JSONObject(content);
//				content = json.optString("content");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			processChapContent(content);
//			hideLoading();
//			
//			chapId = id;
//		}else{
//			pageIndex = 0;
//			
//			GetChapterRequest request = new GetChapterRequest();
//			if (isOpenBook) {
//				isOpenBook = false;
//				processOpenWithIndex();
//			} else {
//				chapId = id;
//			}
//			request.chapterId = chapId;
//			request.language = "vi";
//			new IGetChapterRequest(new IChapterPostCallBack() {
//
//				@Override
//				public void onResultChapterPostPost(GetChapterResponse statusObj) {
//					// TODO Auto-generated method stub
//
//					processChapContent(statusObj.chapter.content);
//					
//					processOpenWithIndex();
//					hideLoading();
//				}
//
//				@Override
//				public void fail() {
//					// TODO Auto-generated method stub
//
//				}
//			}, request).execute();
//		}
	}
	
	private void processOpenWithIndex(){
		String indexPageStr = IKaraDbHelper.getInstance(
				getApplicationContext()).getSavedInfo(bookId);
		
		if(indexPageStr.length() == 0){
			pageIndex = 0;
		}else{
			chapId = indexPageStr.split(";")[0];
			if (chapId != null && chapId.length() > 0) {
				pageIndex = Integer.parseInt(indexPageStr.split(";")[1]);
			}
			currentChapIndex = ISettings.getInstance().getChapListContents()
					.indexOf(getCurrentChapter(chapId));
		}
		
		Log.i(TAG, "processIndex "+chapId+" "+currentChapIndex+" "+pageIndex);
	}
	
	private String convert2Html(String originalText){
		String results = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		results = results + "\n";
		results = results + "<body>";
		String[] lines = originalText.split(System.getProperty("line.separator"));
		
		for(int i = 0; i < lines.length; i++){
			results = results + "<p>"+lines[i]+"</p>";
		}
		
		results = results +"</body>";
		
		return results;
	}
	
	private void processChapContent(String content){
		
		currentContent = content;
		
		
		String convertHtml = convert2Html(content);
		Log.i(TAG, "processChapContent "+convertHtml);
		KaraUtils.writeFileOnSDCard(convertHtml, getApplicationContext(), "nemodotest.fb2");
		tvChapIndexTop.setText((currentChapIndex + 1) + "");
		tvIndex.setText(pageIndex + "/" + (sizeChap + 1));
		tvChapIndex.setText(getResources().getString(R.string.chapter_value)
				+ " " + (currentChapIndex + 1));
		resetTextSize(IkaraConstant.UPDATE_READER.NORMAL);
	}

	public void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}

	private void resetTextSize(final int state) {
		int bottomHeight = KaraUtils.dpToPx(INewReaderActivity.this, 50);
		int padding = KaraUtils.dpToPx(INewReaderActivity.this, 12);
//		if(Utils.detectDeviceType(INewReaderActivity.this) == DEVICETYPE.PHONE){
//			bottomHeight = bottomHeight * 3;
//		}else{
//			bottomHeight = bottomHeight * 5;
//		}
		
		pageSplitter = new PageSplitter(ISettings.getInstance().getWidth() - 2*padding,
				ISettings.getInstance().getHeight() - KaraUtils.getStatusBarHeight(getApplicationContext()) - 2*padding , 1, 0);
		TextPaint textPaint = new TextPaint();
		textPaint.setTextSize(currentTextSize);
		pageSplitter.append(currentContent, textPaint);
		sizeChap = pageSplitter.getPages().size();

		seek.setProgress(pageIndex);
		seek.setMax(pageSplitter.getPages().size());
		final String checkText = keepText;
		Log.e(TAG, "resetTextSize " + pageSplitter.getPages().size()+" "+pageIndex+" "+keepText+" "+swipeType+" "+state);
		adapter.setData(pageSplitter);
//		if(!isFirstTime){
			adapter.notifyDataSetChanged();
//			isFirstTime = true;
//		}
		
		new Handler().post(new Runnable() {
            @Override
            public void run() {
            	Log.i(TAG, "pageIndex is "+pageIndex);
            	if(state == IkaraConstant.UPDATE_READER.NORMAL){
            		if(swipeType == IkaraConstant.SWIPE.LEFT){
            			pageIndex = pageSplitter.getPages().size();
            		}else if(swipeType == IkaraConstant.SWIPE.RIGHT){
            			pageIndex = 0;
            		}else{
            			Log.e(TAG, "pageIndex is "+pageIndex);
            		}
            	}else if(state == IkaraConstant.UPDATE_READER.INCREASE || state == IkaraConstant.UPDATE_READER.DECREASE){
            		for(int i = 0; i < pageSplitter.getPages().size(); i++){
            			if(pageSplitter.getPages().get(i).toString().contains(checkText)){
            				pageIndex = i+1;
            				Log.i(TAG, "keepText is "+checkText+" "+pageIndex);
            				break;
            			}
            		}
            	}
            	adapter.notifyDataSetChanged();
            	pagerReader.setCurrentItem(pageIndex);
            	tvDecrease.setEnabled(true);
            	tvIncrease.setEnabled(true);
            }
        });
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.img_back:
//			stopNewService();
			saveIndexPage();
			finish();
			break;

		case R.id.tv_chap_top:
		case R.id.img_share:

			Intent intent = new Intent(getApplicationContext(),
					IChapListActivity.class);
			intent.putExtra("current_index_chap", currentChapIndex);
			intent.putExtra("option_view", IChapListActivity.CHAPTER);
			startActivityForResult(intent, 100);
			break;

		case R.id.img_change_state_reader:
			chageState();
			break;

		case R.id.tv_index:
			break;

		case R.id.tv_increase:
			Log.i(TAG, "currentChapIndex "+currentChapIndex);
//			if(pageIndex > 0){
//				new ProcessTask(1).execute();
//			}
        	tvIncrease.setEnabled(false);
			inCrease();
			break;

		case R.id.tv_decrease:
			Log.i(TAG, "currentChapIndex "+currentChapIndex);
//			if(pageIndex > 0){
//				new ProcessTask(0).execute();
//			}
			tvDecrease.setEnabled(false);
			decrease();
			break;

		case R.id.img_font_text:
			Intent intentA = new Intent(getApplicationContext(),
					IChapListActivity.class);
			intentA.putExtra("current_index_chap", currentChapIndex);
			intentA.putExtra("option_view", IChapListActivity.FONT);
			startActivityForResult(intentA, 100);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// check if the request code is same as what is passed here it is 2
		if (requestCode == 100) {
			Log.v(TAG, "onActivityResult "+requestCode);
			if (resultCode == IChapListActivity.CHAPTER) {
				currentChapIndex = data.getIntExtra("chap_index", 0);
				tvChapIndexTop.setText((currentChapIndex + 1) + "");
				chapTitle = data.getStringExtra("chap_title");
				chapId = data.getStringExtra("chap_id");
				loadChapContent(chapId);
			} else {// FONT
				resetTextSize(IkaraConstant.UPDATE_READER.NORMAL);
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
//		stopNewService();
		saveIndexPage();
	}

	private void saveIndexPage() {
		String savedIndex = chapId + ";" + pageIndex;
		Log.w(TAG, "saveIndexPage " + savedIndex);
		IKaraDbHelper.getInstance(getApplicationContext())
				.addToSavedIndexTable(bookId, savedIndex);
	}

	private void decrease() {
		Log.e(TAG, "decrease");
		currentTextSize = currentTextSize - 10;
		IkaraPreferences.saveIntPref(getApplicationContext(),
				PrefConstant.PREF_TEXT_SIZE, currentTextSize);
		resetTextSize(IkaraConstant.UPDATE_READER.DECREASE);
	}

	private void inCrease() {
		Log.i(TAG, "inCrease");
		currentTextSize = currentTextSize + 10;
		IkaraPreferences.saveIntPref(getApplicationContext(),
				PrefConstant.PREF_TEXT_SIZE, currentTextSize);
		resetTextSize(IkaraConstant.UPDATE_READER.INCREASE);
	}

	private void chageState() {
		//Log.v(TAG, "chageState");
		if(readerState == IkaraConstant.READER_STATE.DAY){
			readerState = IkaraConstant.READER_STATE.NIGHT;
		}else{
			readerState = IkaraConstant.READER_STATE.DAY;
		}
		adapter.updateView(readerState);
	}

	private void showTopBottomView() {

		if (headerBar.isShown()) {
			hideToolBar();
		} else {
			bottomBar.setVisibility(View.VISIBLE);
			bottomBar.setAnimation(animShowBottom);
			headerBar.setVisibility(View.VISIBLE);
		}
	}

	private void hideToolBar() {
		headerBar.setVisibility(View.GONE);
		bottomBar.setVisibility(View.GONE);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		pageIndex = progress;
		tvIndex.setText((progress + 1) + "/" + (sizeChap + 1));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		pagerReader.setCurrentItem(pageIndex);
	}

	private class MyPagerAdapter extends PagerAdapter {

		private PageSplitter iPageSplitter;
		private int state;
		
		public void setData(PageSplitter pageSplitter){
			iPageSplitter = pageSplitter;
		}
		
		public void updateView(int stateRead){
			state = stateRead;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if(iPageSplitter != null){
				return iPageSplitter.getPages().size() + 1;
			}else{
				return 0;
			}
		}
		
		@Override
		public int getItemPosition(Object object) {
		    return POSITION_NONE; 
		} 

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View view =  inflater.inflate(R.layout.page_reader, container, false);
		    JustifiedTextView textView = (JustifiedTextView)view.findViewById(R.id.tv_content);
//		    WebView textView = (WebView)view.findViewById(R.id.tv_content);
//		    LinearLayout llContainer = (LinearLayout)view.findViewById(R.id.ll_container);
//		    TextView textView = (TextView)view.findViewById(R.id.tv_content);
		    TextView tvTitle = (TextView)view.findViewById(R.id.tv_title_chap_page);
		    RelativeLayout containerChild = (RelativeLayout)view.findViewById(R.id.contain_child);
			String getFont = IkaraPreferences.getStringPref(INewReaderActivity.this, PrefConstant.PREF_FONT, "DroidSans.ttf");
			Typeface type = Typeface.createFromAsset(getAssets(),"fonts/"+getFont); 
//			textView.setTypeFace(type);
//			textView.setTypeface(type);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
			tvTitle.setTypeface(type);
			tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
			
			if(position == 0){
				String chapStartText = "<p> <br></br></p>"
						+ "<p> <br></br></p>"
						+ "<p> <br></br></p>"
						+ "<h1><I>"+bookTitle+"</I></h1>" 
						+ "<p>"+getResources().getString(R.string.chapter_value)+" "+(currentChapIndex+1)+"</p>" 
						+ "<p>"+chapTitle+"</p>";
				tvTitle.setVisibility(View.VISIBLE);
//				llContainer.setVisibility(View.GONE);
				tvTitle.setGravity(Gravity.CENTER);
				tvTitle.setText(Html.fromHtml(chapStartText));
			}else{
				tvTitle.setVisibility(View.GONE);
//				llContainer.setVisibility(View.VISIBLE);
				textView.setAlignment(Align.LEFT);
				textView.setText(pageSplitter.getPages().get(position-1).toString());
				
//				String youtContentStr = String.valueOf(Html
//		                .fromHtml("<![CDATA[<body style=\"text-align:justify;color:#222222; \">"
//		                            + pageSplitter.getPages().get(position-1).toString()
//		                            + "</body>]]>")); 
//				textView.loadData(youtContentStr, "text/html;charset=utf-8", null);
		 
			}
			
			if (state == IkaraConstant.READER_STATE.DAY) {
				containerChild.setBackgroundColor(Color.BLACK);
				textView.setTextColor(Color.WHITE);
				tvTitle.setTextColor(Color.WHITE);
			} else {
				containerChild.setBackgroundColor(Color.WHITE);
				tvTitle.setTextColor(Color.BLACK);
				textView.setTextColor(Color.BLACK);
			}
			
			view.setTag(position);
			
			((ViewPager) container).addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((RelativeLayout) object);
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int index) {
		// TODO Auto-generated method stub
		pageIndex = index;
		
		tvIndex.setText((pageIndex+1)+"/"+(sizeChap+1));
		seek.setProgress(index);
		
		if(pageIndex == 0){
			
		}else{
			if(pageSplitter.getPages().get(pageIndex - 1).toString().length() > 10){
				keepText = pageSplitter.getPages().get(pageIndex-1).toString().substring(0, 10);
			}else{
				keepText = pageSplitter.getPages().get(pageIndex-1).toString();
			}
		}
		Log.e(TAG, "onPageSelected "+keepText+" "+index);
		
		viewCountPage++;
		
		if(viewCountPage == sizeChap || viewCountPage == 20){

			if(IKaraDbHelper.getInstance(INewReaderActivity.this).existInVCTable(bookId)){
				return;
			}else{
				IKaraDbHelper.getInstance(INewReaderActivity.this).addToVCTable(bookId);
				IncreaseViewCounterRequest requestVC = new IncreaseViewCounterRequest();
				requestVC.bookId = bookId;
				requestVC.language = "vi";
				
				new IncreaseVCRequest(new IVCCallBack() {
					
					@Override
					public void onVCPost(IncreaseViewCounterResponse statusObj) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void fail() {
						// TODO Auto-generated method stub
						
					}
				}, requestVC).execute();
			}
		}
	}
	
}