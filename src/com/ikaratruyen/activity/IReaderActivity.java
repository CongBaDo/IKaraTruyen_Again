//package com.ikaratruyen.activity;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.text.Html;
//import android.text.TextPaint;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.animation.Animation;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.TextView;
//
//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;
//import com.ikaratruyen.R;
//import com.ikaratruyen.curlview.CurlPage;
//import com.ikaratruyen.curlview.CurlView;
//import com.ikaratruyen.curlview.CurlView.CurlTouchListener;
//import com.ikaratruyen.model.Chapter;
//import com.ikaratruyen.model.GetChapterRequest;
//import com.ikaratruyen.model.GetChapterResponse;
//import com.ikaratruyen.model.IncreaseViewCounterRequest;
//import com.ikaratruyen.model.IncreaseViewCounterResponse;
//import com.ikaratruyen.request.IGetChapterRequest;
//import com.ikaratruyen.request.IGetChapterRequest.IChapterPostCallBack;
//import com.ikaratruyen.request.IncreaseVCRequest;
//import com.ikaratruyen.request.IncreaseVCRequest.IVCCallBack;
//import com.ikaratruyen.utils.IKaraDbHelper;
//import com.ikaratruyen.utils.ISettings;
//import com.ikaratruyen.utils.IkaraConstant;
//import com.ikaratruyen.utils.IkaraPreferences;
//import com.ikaratruyen.utils.PageSplitter;
//import com.ikaratruyen.utils.PrefConstant;
//
//public class IReaderActivity extends Activity implements OnClickListener, OnSeekBarChangeListener{
//
//	private static final String TAG = "IReaderActivity";
//	// Splash screen timer
//	private LinearLayout headerBar;
//	private LinearLayout bottomBar;
//	private CurlView mCurlView;
//	private Animation animShowBottom;
//	private int height, width;
//	private PageSplitter pageSplitter;
//	private int currentChapIndex = 0;
//	private boolean isOpenBook;
//	private String chapTitle;
//	private String bookTitle;
//	private TextView tvChapIndex, tvIndex, tvQuyenIndex;
//	private ImageView imgReadState;
//	private SeekBar seek;
//	private int readerState = IkaraConstant.READER_STATE.DAY;
//	private int sizeChap = 0;
//	private TextView tvIncrease, tvDecrease, tvChapIndexTop;
//	private int currentTextSize = 0;
//	private String currentContent;
//	private ImageView imgIndexTop;
//	private String bookId;
//	private int viewCountPage = 0;
//	private String chapId;
//	private int pageIndex = 0;
//	
//	private InterstitialAd interstitial;
//
//	@SuppressLint("NewApi") @Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_reader);
//		DisplayMetrics metrics = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(metrics);
//		height = metrics.heightPixels;
//		width = metrics.widthPixels;
//
//		currentChapIndex = getIntent().getExtras().getInt("chap_index");
//		chapTitle = getIntent().getExtras().getString("chap_title");
//		bookId = getIntent().getExtras().getString("book_id");
//		//Log.d(TAG, "View: " + height + " " + width+" "+chapTitle+" "+currentIndex);
//		
//		headerBar = (LinearLayout)findViewById(R.id.top_bar_reader);
//		
//		TypedValue tv = new TypedValue();
//		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){ 
//			int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
//			headerBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, actionBarHeight));
//		} 
//		
//		headerBar.setGravity(Gravity.CENTER_VERTICAL);
//		
//		bottomBar = (LinearLayout)findViewById(R.id.bottom_view);
//		tvChapIndex = (TextView)bottomBar.findViewById(R.id.tv_chapter_index);
//		tvChapIndexTop = (TextView)findViewById(R.id.tv_chap_top);
//		tvQuyenIndex = (TextView)findViewById(R.id.tv_book_quyen_index);
//		tvChapIndexTop.setVisibility(View.VISIBLE);
//		tvChapIndexTop.setText((currentChapIndex+1)+"");
//		tvIndex = (TextView)bottomBar.findViewById(R.id.tv_index);
//		imgIndexTop = (ImageView)findViewById(R.id.img_share);
//		seek = (SeekBar)bottomBar.findViewById(R.id.sk_page);
//		tvIncrease = (TextView)bottomBar.findViewById(R.id.tv_increase);
//		tvDecrease = (TextView)bottomBar.findViewById(R.id.tv_decrease);
//		
//		ImageView imgFont = (ImageView)bottomBar.findViewById(R.id.img_font_text);
//		imgFont.setOnClickListener(this);
//		
//		tvChapIndexTop.setOnClickListener(this);
//		imgIndexTop.setOnClickListener(this);
//		
//		currentTextSize = IkaraPreferences.getIntPref(getApplicationContext(), PrefConstant.PREF_TEXT_SIZE, (int) getResources().getDimension(R.dimen.text_size));
//		
//		if(ISettings.getInstance().getChapListContents().get(currentChapIndex).volume != null){
//			long volume = ISettings.getInstance().getChapListContents().get(currentChapIndex).volume;
//			tvQuyenIndex.setVisibility(View.VISIBLE);
//			tvQuyenIndex.setText(getResources().getString(R.string.book_value)+" "+volume);
//		}
//		
//		Log.v(TAG, "current Text Size "+currentTextSize+" "+ISettings.getInstance().getChapListContents().get(30).volume);
//		
//		tvDecrease.setOnClickListener(this);
//		tvIncrease.setOnClickListener(this);
//		seek.setOnSeekBarChangeListener(this);
//		tvChapIndex.setText(getResources().getString(R.string.chapter_value)+" "+(currentChapIndex+1));
//		imgReadState = (ImageView)bottomBar.findViewById(R.id.img_change_state_reader);
//		imgReadState.setOnClickListener(this);
//		tvIndex.setOnClickListener(this);
//		int index = 0;
//		if (getLastNonConfigurationInstance() != null) {
//			index = (Integer) getLastNonConfigurationInstance();
//		}
//		
//		mCurlView = (CurlView) findViewById(R.id.curlview);
//		
//		mCurlView.setBackgroundColor(0xFF202830);
//		mCurlView.setTouchListener(new CurlTouchListener() {
//			
//			@Override
//			public void touchDownListener() {
//				// TODO Auto-generated method stub
////				}
//			}
//			
//			@Override
//			public void touchListener() {
//				// TODO Auto-generated method stub
//				Log.i(TAG, "touchListener ");
//				showTopBottomView();
//			}
//			
//			@Override
//			public void getIndex(final int index, final int side) {
//				// TODO Auto-generated method stub
//				Log.i(TAG, "Correct index "+index+" "+sizeChap+" "+side);
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						Log.i(TAG, "index "+index);
//						pageIndex = index;
//						
//						if(side == CurlView.SET_CURL_TO_LEFT && index == (sizeChap + 1)){
//							currentChapIndex++;
//							Log.i(TAG, "Right "+index);
//							chapTitle = ISettings.getInstance().getChapListContents().get(currentChapIndex).title;
//							loadChapContent(ISettings.getInstance().getChapListContents().get(currentChapIndex)._id);
//						}else{
//							tvIndex.setText((pageIndex+1)+"/"+(sizeChap+1));
//							seek.setProgress(index);
//						}
//						
//						viewCountPage++;
//						
//						if(viewCountPage == sizeChap || viewCountPage == 20){
//
//							if(IKaraDbHelper.getInstance(IReaderActivity.this).existInVCTable(bookId)){
//								return;
//							}else{
//								IKaraDbHelper.getInstance(IReaderActivity.this).addToVCTable(bookId);
//								IncreaseViewCounterRequest requestVC = new IncreaseViewCounterRequest();
//								requestVC.bookId = bookId;
//								requestVC.language = "vi";
//								
//								new IncreaseVCRequest(new IVCCallBack() {
//									
//									@Override
//									public void onVCPost(IncreaseViewCounterResponse statusObj) {
//										// TODO Auto-generated method stub
//										
//									}
//									
//									@Override
//									public void fail() {
//										// TODO Auto-generated method stub
//										
//									}
//								}, requestVC).execute();
//							}
//						}
//					}
//				});
//			}
//
//		});
//		
//		bookTitle = getIntent().getExtras().getString("book_title");
//		chapId = getIntent().getExtras().getString("chap_id");
//		Log.i(TAG, "onCreate "+chapId+" "+bookTitle);
//		
//		((ImageView)findViewById(R.id.img_back)).setOnClickListener(this);
//		((ImageView)findViewById(R.id.img_share)).setOnClickListener(this);
//		((TextView)findViewById(R.id.tv_title_bar)).setText(bookTitle);
//		((ImageView)findViewById(R.id.img_share)).setBackgroundResource(R.drawable.view_state_clipboard_button);
//		
//		headerBar.setVisibility(View.GONE);
//		bottomBar.setVisibility(View.GONE);
//		
//		interstitial = new InterstitialAd(IReaderActivity.this);
//		interstitial.setAdUnitId("ca-app-pub-8429996645546440/9944646214");
//		AdView adView = (AdView) findViewById(R.id.adView);
//		AdRequest adRequest = new AdRequest.Builder().build();
//		adView.loadAd(adRequest);
//		interstitial.loadAd(adRequest);
//		interstitial.setAdListener(new AdListener() {
//			public void onAdLoaded() {
//				displayInterstitial();
//			}
//		});
//		loadChapContent(chapId);
//	}
//	
//	private Chapter getCurrentChapter(String chapId){
//		for(int i = 0; i < ISettings.getInstance().getChapListContents().size(); i++){
//			if(ISettings.getInstance().getChapListContents().get(i)._id.equals(chapId)){
//				return ISettings.getInstance().getChapListContents().get(i);
//			}
//		}
//		
//		return null;
//	}
//	
//	private void loadChapContent(String id){
////		chapId = id;
//		pageIndex = 0;
//		isOpenBook = getIntent().getExtras().getBoolean("open_book");
//		GetChapterRequest request = new GetChapterRequest();
//		if(isOpenBook){
//			String indexPageStr = IKaraDbHelper.getInstance(getApplicationContext()).getSavedInfo(bookId);
//			
//			chapId = indexPageStr.split(";")[0];
//			if(chapId != null && chapId.length() > 0){
//				pageIndex = Integer.parseInt(indexPageStr.split(";")[1]);
//				Log.d(TAG, "loadChapContent "+chapId +" "+pageIndex+" "+indexPageStr);
//			}else{
//				chapId = id;
//			}
//			
//			chapTitle = getCurrentChapter(chapId).title;
//			currentChapIndex = ISettings.getInstance().getChapListContents().indexOf(getCurrentChapter(chapId));
//			Log.w(TAG, "loadChapContent "+chapId +" "+pageIndex+" "+currentChapIndex);
//		}else{
//			chapId = id;
//		}
//		request.chapterId = chapId;
//		request.language = "vi";
//		new IGetChapterRequest(new IChapterPostCallBack() {
//			
//			@Override
//			public void onResultChapterPostPost(GetChapterResponse statusObj) {
//				// TODO Auto-generated method stub
//				
//				currentContent = statusObj.chapter.content;
//				Log.w(TAG, "ALO data Chapter "+chapTitle+" "+pageIndex);
//				tvIndex.setText(1+"/"+(sizeChap+1));
//				seek.setProgress(0);
//				resetTextSize();
//				mCurlView.setPageProvider(new PageProvider());
//				mCurlView.setSizeChangedObserver(new SizeChangedObserver());
//				mCurlView.setCurrentIndex(pageIndex);
//				tvChapIndexTop.setText((currentChapIndex+1)+"");
//			}
//			
//			@Override
//			public void fail() {
//				// TODO Auto-generated method stub
//				
//			}
//		}, request).execute();
//	}
//	
//	public void displayInterstitial() {
//		if (interstitial.isLoaded()) {
//			interstitial.show();
//		}
//	}
//	
//	private void resetTextSize(){
//		pageSplitter = new PageSplitter(ISettings.getInstance().getWidth(), ISettings.getInstance().getHeight(), 1, 0);
//		TextPaint textPaint = new TextPaint();
//		textPaint.setTextSize(currentTextSize);
//		pageSplitter.append(currentContent, textPaint);
//		sizeChap = pageSplitter.getPages().size();
//		//tvIndex.setText(mCurlView.getCurrentIndex()+"/"+(sizeChap+1));
//		Log.e(TAG, "SIZE pages "+pageSplitter.getPages().size());
//		
//		mCurlView.setSize(sizeChap + 2);
//		
//		seek.setMax(pageSplitter.getPages().size()+1);
//	}
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		
//		switch (v.getId()) {
//		case R.id.img_back:
//			saveIndexPage();
//			finish();
//			break;
//			
//		case R.id.tv_chap_top:
//		case R.id.img_share:
//			
//			Intent intent = new Intent(getApplicationContext(), IChapListActivity.class);
//			intent.putExtra("current_index_chap", currentChapIndex);
//			intent.putExtra("option_view", IChapListActivity.CHAPTER);
//			startActivityForResult(intent, 100);
//			break;
//			
//		case R.id.img_change_state_reader:
//			chageState();
//			break;
//			
//		case R.id.tv_index:
//			break;
//			
//		case R.id.tv_increase:
//			inCrease();
//			break;
//			
//		case R.id.tv_decrease:
//			decrease();
//			break;
//			
//		case R.id.img_font_text:
//			Intent intentA = new Intent(getApplicationContext(), IChapListActivity.class);
//			intentA.putExtra("current_index_chap", currentChapIndex);
//			intentA.putExtra("option_view", IChapListActivity.FONT);
//			startActivityForResult(intentA, 100);
//			break;
//		}
//	}
//	
//	@Override  
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {  
//		super.onActivityResult(requestCode, resultCode, data);  
//               // check if the request code is same as what is passed  here it is 2  
//        if(requestCode == 100) {  
//        	if(resultCode == IChapListActivity.CHAPTER){
//        		chapTitle = data.getStringExtra("chap_title");
//        		chapId = data.getStringExtra("chap_id");
//        		loadChapContent(chapId);
//        	}else{//FONT
//        		resetTextSize();
//        		mCurlView.setPageProvider(new PageProvider());
//        	}
//        }  
//	}  
//	
//	@Override
//	public void onBackPressed(){
//		super.onBackPressed();
//		saveIndexPage();
//	}
//	
//	private void saveIndexPage(){
//		String savedIndex = chapId+";"+pageIndex;
//		Log.w(TAG, "saveIndexPage "+savedIndex);
//		IKaraDbHelper.getInstance(getApplicationContext()).addToSavedIndexTable(bookId, savedIndex);
//	}
//	
//	private void decrease(){
//		Log.e(TAG, "decrease");
//		currentTextSize = currentTextSize - 10;
//		IkaraPreferences.saveIntPref(getApplicationContext(), PrefConstant.PREF_TEXT_SIZE, currentTextSize);
//		resetTextSize();
//		mCurlView.setPageProvider(new PageProvider());
//	}
//	
//	private void inCrease(){
//		Log.i(TAG, "inCrease");
//		currentTextSize = currentTextSize + 10;
//		IkaraPreferences.saveIntPref(getApplicationContext(), PrefConstant.PREF_TEXT_SIZE, currentTextSize);
//		resetTextSize();
//		mCurlView.setPageProvider(new PageProvider());
//	}
//	
//	private void chageState(){
//		if(readerState == IkaraConstant.READER_STATE.DAY){
//			readerState = IkaraConstant.READER_STATE.NIGHT;
//			findViewById(R.id.contain_reader_main).setBackgroundColor(Color.BLACK);
//		}else{
//			readerState = IkaraConstant.READER_STATE.DAY;
//			findViewById(R.id.contain_reader_main).setBackgroundColor(Color.WHITE);
//		}
//		mCurlView.setPageProvider(new PageProvider());
//	}
//	
//	private class PageProvider implements CurlView.PageProvider {
//
//		@Override
//		public int getPageCount() {
//			return (pageSplitter.getPages().size() + 1);
//		}
//		
//		public Bitmap loadBitmapFromView(View v, int padding) {
//	        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//	        Canvas c = new Canvas(b);
//	        c.drawARGB(255, 99, 99, 99);
//	        v.layout(0, 0, width, height);
//	        v.draw(c);
//	        
//	        Bitmap paddedBitmap = Bitmap.createBitmap(
//	        	      b.getWidth() + padding,
//	        	      b.getHeight() + 2*padding,
//	        	      Bitmap.Config.ARGB_8888);
//
//			Canvas canvas = new Canvas(paddedBitmap);
//			if (readerState == IkaraConstant.READER_STATE.DAY) {
//				canvas.drawARGB(0xFF, 0xFF, 0xFF, 0xFF); // this represents
//															// white color
//			} else {
//				canvas.drawARGB(0xFF, 0x00, 0x00, 0x00); // this represents
//															// black color
//			}
//			canvas.drawBitmap(b, padding / 2, padding / 2, new Paint(
//					Paint.FILTER_BITMAP_FLAG));
//
//			return paddedBitmap;
//	    } 
//
//		@Override
//		public void updatePage(CurlPage page, int width, int height, int index) {
//			Bitmap front = null;
//			TextView tv = new TextView(IReaderActivity.this);
//			
//			if(readerState == IkaraConstant.READER_STATE.DAY){
//				tv.setBackgroundColor(Color.WHITE);
//				tv.setTextColor(Color.BLACK);
//			}else{
//				tv.setBackgroundColor(Color.BLACK);
//				tv.setTextColor(Color.WHITE);
//			}
//			
//			Log.w(TAG, "textSize "+currentTextSize);
//			tv.setTextSize(currentTextSize);
//			
//			if(index == 0){
//				String chapStartText = "<p> <br></br></p>"
//						+ "<p> <br></br></p>"
//						+ "<p> <br></br></p>"
//						+ "<h1><I>"+bookTitle+"</I></h1>" 
//						+ "<p>"+getResources().getString(R.string.chapter_value)+" "+(currentChapIndex+1)+"</p>" 
//						+ "<p>"+chapTitle+"</p>";
//				tv.setGravity(Gravity.CENTER);
//				tv.setText(Html.fromHtml(chapStartText));
//			}else{
//				String chapContent = pageSplitter.getPages().get(index-1).toString();
//				tv.setText(pageSplitter.getPages().get(index-1));
//			}
//			
//			String getFont = IkaraPreferences.getStringPref(IReaderActivity.this, PrefConstant.PREF_FONT, "DroidSans.ttf");
//			
////			Typeface type = Typeface.createFromAsset(getAssets(),"fonts/"+getFont); 
////			tv.setTypeface(type);
//			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize);
//			front = loadBitmapFromView(tv, 100);
//			page.setTexture(front, CurlPage.SIDE_BOTH);
//			page.setColor(Color.argb(127, 255, 255, 255),
//					CurlPage.SIDE_BACK);
//		}
//	}
//	
//	private class SizeChangedObserver implements CurlView.SizeChangedObserver {
//		@Override
//		public void onSizeChanged(int w, int h) {
//			mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
//		}
//	}
//	
//	private void showTopBottomView(){
//		
//		if(headerBar.isShown()){
//			hideToolBar();
//		}else{
//			bottomBar.setVisibility(View.VISIBLE);
//			bottomBar.setAnimation(animShowBottom);
//			headerBar.setVisibility(View.VISIBLE);
//		}
//	}
//	
//	private void hideToolBar(){
//		headerBar.setVisibility(View.GONE);
//		bottomBar.setVisibility(View.GONE);
//	}
//
//	@Override
//	public void onProgressChanged(SeekBar seekBar, int progress,
//			boolean fromUser) {
//		// TODO Auto-generated method stub
//		tvIndex.setText((progress+1)+"/"+(sizeChap+1));
//	}
//
//	@Override
//	public void onStartTrackingTouch(SeekBar seekBar) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onStopTrackingTouch(SeekBar seekBar) {
//		// TODO Auto-generated method stub
//		mCurlView.setCurrentIndex(seekBar.getProgress());
//	}
//	
//	
//	
//}