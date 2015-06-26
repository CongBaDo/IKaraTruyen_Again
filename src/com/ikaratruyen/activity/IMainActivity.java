package com.ikaratruyen.activity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ikaratruyen.adapter.BookAdapter;
import com.ikaratruyen.adapter.IMenuAdapter;
import com.ikaratruyen.adapter.SearchAdapter;
import com.ikaratruyen.adapter.UngDungHayAdapter;
import com.ikaratruyen.callback.OnclickMenuListener;
import com.ikaratruyen.customview.EndlessListView;
import com.ikaratruyen.customview.EndlessListView.EndlessListener;
import com.ikaratruyen.model.App;
import com.ikaratruyen.model.Book;
import com.ikaratruyen.model.Genre;
import com.ikaratruyen.model.GetGenresResponse;
import com.ikaratruyen.model.GetHotAppsRequest;
import com.ikaratruyen.model.GetHotAppsResponse;
import com.ikaratruyen.model.GetOtherAppsRequest;
import com.ikaratruyen.model.GetOtherAppsResponse;
import com.ikaratruyen.model.NewBooksRequest;
import com.ikaratruyen.model.NewBooksResponse;
import com.ikaratruyen.model.SearchBooksRequest;
import com.ikaratruyen.model.SearchBooksResponse;
import com.ikaratruyen.model.SearchItem;
import com.ikaratruyen.model.TopBooksRequest;
import com.ikaratruyen.model.TopBooksResponse;
import com.ikaratruyen.request.IGenresPostRequest;
import com.ikaratruyen.request.IGenresPostRequest.IGenresPostCallBack;
import com.ikaratruyen.request.IGetHotAppRequest;
import com.ikaratruyen.request.IGetHotAppRequest.IHotAppPostCallBack;
import com.ikaratruyen.request.IGetNewBooksRequest;
import com.ikaratruyen.request.IGetNewBooksRequest.INewBookPostCallBack;
import com.ikaratruyen.request.IGetOtherAppRequest;
import com.ikaratruyen.request.IGetOtherAppRequest.IOtherAppPostCallBack;
import com.ikaratruyen.request.IGetSuggestSearchRequest;
import com.ikaratruyen.request.IGetSuggestSearchRequest.IGetBookPostCallBack;
import com.ikaratruyen.request.ISearchBookRequest;
import com.ikaratruyen.request.ISearchBookRequest.ISerchBookCallBack;
import com.ikaratruyen.request.TopBookPostRequest;
import com.ikaratruyen.request.TopBookPostRequest.TopBookCallBack;
import com.ikaratruyen.utils.IKaraDbHelper;
import com.ikaratruyen.utils.ISettings;
import com.ikaratruyen.utils.IkaraConstant;
import com.ikaratruyen.utils.IkaraPreferences;
import com.ikaratruyen.utils.PrefConstant;
import com.ikaratruyen.utils.KaraUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ikaratruyen.R;

/**
 * This activity is an example of a responsive Android UI.
 * On phones, the SlidingMenu will be enabled only in portrait mode.
 * In landscape mode, it will present itself as a dual pane layout.
 * On tablets, it will will do the same general thing. In portrait
 * mode, it will enable the SlidingMenu, and in landscape mode, it
 * will be a dual pane layout.
 * 
 * @author jeremy
 *
 */
public class IMainActivity extends SlidingFragmentActivity implements OnItemClickListener, OnClickListener, TextWatcher{

	private static final String TAG = "IMainActivity";
	
	private SlidingMenu slidingMenu;
	private IMenuAdapter leftMenuAdapter;
	private ListView leftMenuListView;
	private ArrayList<Genre> listGenres;
	private EndlessListView listView;
	private BookAdapter adapter;
	private String currentCursor = null;
	private ArrayList<Book> bookList;
	private IWebFragment fragment;
	private String savedTitle;
	private String idTitleMenu;
	private EditText searchView;
	private TextView cancel;
	private boolean startSearch = false;
	private String query;
	long time = 0;
	private boolean isFinishSearch = true;
	private InterstitialAd interstitial;
	private String hotAppParams;
	private ProgressDialog dialogLoading;
	private RelativeLayout searchLayout;
	private ArrayList<SearchItem> searchDatas;
	private boolean isSachVuaDoc = false;

	@SuppressLint("NewApi") @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_mainkara);
		fragment = new IWebFragment();
		int adSize = AdSize.BANNER.getHeight();
		int bottomHeight = KaraUtils.dpToPx(this, 48);
		Log.e(TAG, "onCreate "+adSize+" "+bottomHeight);
		
		if(KaraUtils.hasNetworkConnection(getApplicationContext())){
			showLoading();
		}
		
		
		ISettings.getInstance().config(IMainActivity.this);
		
		int openAppCount = IkaraPreferences.getIntPref(getApplicationContext(), PrefConstant.PREF_APP_OPEN, 0);
		if(openAppCount >= IkaraConstant.COUNT_OPEN_APP){
			IkaraPreferences.saveIntPref(getApplicationContext(), PrefConstant.PREF_APP_OPEN, 0);
			
			String exclusionStr = IkaraPreferences.getStringPref(getApplicationContext(), PrefConstant.PREF_EXCLUSION, "");
			String[] values = exclusionStr.split(";");
			GetHotAppsRequest request = new GetHotAppsRequest();
			request.exclusions = null;
			request.language = "vi";
			request.platform = "Android";
			if(values.length > 0){
				ArrayList<String> excluses = new ArrayList<String>();
				for(int i = 0; i < values.length; i++){
					if(values[i].trim().length() > 0){
						Log.w(TAG, "values "+values[i]);
						excluses.add(values[i]);
					}
				}
				request.exclusions = excluses;
			}
			
			new IGetHotAppRequest(new IHotAppPostCallBack() {
					
				@Override
				public void onResultHotAppPostPost(GetHotAppsResponse statusObj) {
					// TODO Auto-generated method stub
					hotAppParams = statusObj.toString();
					Log.v(TAG, "onResultHotAppPostPost ");
					if(statusObj.apps.size() > 0){
						showRateApp(statusObj.apps.get(0));
					}
				}
				
				@Override
				public void fail() {
					// TODO Auto-generated method stub
					
				}
			}, request).execute();
		}

		setBehindContentView(R.layout.view_menu_right_main);
		getSlidingMenu().setSlidingEnabled(true);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// show home as up so we can toggle
		getActionBar().setDisplayHomeAsUpEnabled(false); 
		getActionBar().setHomeButtonEnabled(true); 
		getActionBar().setIcon(R.drawable.icon_menubutton);
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_bar));
		savedTitle = getResources().getString(R.string.title_tien_hiep);
		getActionBar().setTitle(savedTitle);

		// set the Above View Fragment
		if (savedInstanceState != null)

		// customize the SlidingMenu
		slidingMenu = getSlidingMenu();
		getSlidingMenu().setBehindOffsetRes(R.dimen.slidingmenu_offset);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindScrollScale(0.25f);
		getSlidingMenu().setFadeDegree(0.25f);
		
		LayoutInflater inflater = this.getLayoutInflater();
		View header = inflater.inflate(R.layout.list_slideview_item, null); 
		((TextView)header.findViewById(R.id.tv_Title)).setText(getResources().getString(R.string.app_name));
		((ImageView)header.findViewById(R.id.img_Indicator)).setBackgroundResource(R.drawable.icon_book_open);
		
		searchLayout = (RelativeLayout)findViewById(R.id.search_layout);
		searchView = (EditText)findViewById(R.id.search_view);
		searchView.addTextChangedListener(this);
		searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		searchView.setOnEditorActionListener(new TextView.OnEditorActionListener() { 


			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					Log.v(TAG, "Search now "+query);
					searchBaseOnTextInput(query);
		            return true; 
		        }
				return false;
			} 
		}); 
		cancel = (TextView)findViewById(R.id.tv_cancel);
		cancel.setOnClickListener(this);
		
		hideKeyboard();
		
		listView = (EndlessListView)findViewById(R.id.list_book_view);
		listView.setDividerHeight(0);
		listView.setDivider(null);
		listView.setOnItemClickListener(this);

		prepareSampleData();
		
		listView.setListener(new EndlessListener() {
			
			@Override
			public void loadData() {
				// TODO Auto-generated method stub
				Log.i(TAG, "loadData ");
				
				if(getResources().getString(R.string.title_sach_moi).equals(idTitleMenu)){
					NewBooksRequest request = new NewBooksRequest();
					request.cursor = currentCursor;
					request.language = "vi";
					
					new IGetNewBooksRequest(newBookCallBack, request).execute();
				}else if(!idTitleMenu.equals(getResources().getString(R.string.title_ungdunghay))){
					TopBooksRequest request = new TopBooksRequest();
					request.language = "vi";
					request.cursor = currentCursor;
					request.genreId = getResources().getString(R.string.title_tien_hiep);
					new TopBookPostRequest(topBookCallBack, request).execute();
				}
			}
		});
		leftMenuListView = (ListView) getSlidingMenu().getMenu().findViewById(R.id.menuListView);
		leftMenuListView.addHeaderView(header);
        leftMenuAdapter = new IMenuAdapter(getApplicationContext(), new OnclickMenuListener() {
			
			@Override
			public void itemClick(String id, int position) {
				// TODO Auto-generated method stub
				Log.e(TAG, "ID the loai "+id);
				currentCursor = null;
				idTitleMenu = id;
				
				prepareSampleData();
				searchLayout.setVisibility(View.VISIBLE);
				isSachVuaDoc = false;
				
				if(id != null){
					
					getActionBar().setTitle(id);
					if(id.equals(getResources().getString(R.string.title_trogiup))){
						Log.i(TAG, "Help");
						if(!fragment.isAdded()){
							FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); 
							ft.add(android.R.id.content, fragment, "IWebFragment");
							ft.addToBackStack(null);
							ft.commit(); 
						}
					}else if(id.equals(getResources().getString(R.string.title_tu_sach))){
						if(bookList.get(0)._id == null){
							bookList.clear();
						}
						
						bookList = IKaraDbHelper.getInstance(getApplicationContext()).getAllBookInFavor();
						
						Collections.sort(bookList, new Comparator<Book>() {

							@Override
							public int compare(Book c1, Book c2) {
								// TODO Auto-generated method stub
								SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
								try {
									String day1 = c1.savedTime;//c1.getIssue_release_date().split("T")[0];
									String day2 = c2.savedTime;//c2.getIssue_release_date().split("T")[0];

									return dateFormat.parse(day2).compareTo(dateFormat.parse(day1));
								} catch (ParseException e) {
									throw new IllegalArgumentException(e);
								}
							}
						});
						
						processListData();
						adapter.addMore(bookList, 20);
						adapter.notifyDataSetChanged();
					}else if(id.equals(getResources().getString(R.string.title_sach_moi))){
						
						if(!KaraUtils.hasNetworkConnection(getApplicationContext())){
							toggle();
							return;
						}
						NewBooksRequest request = new NewBooksRequest();
						request.cursor = currentCursor;
						request.language = "vi";
						showLoading();
						new IGetNewBooksRequest(newBookCallBack, request).execute();
					} else if(id.equals(getResources().getString(R.string.title_sach_vua_doc))){
						if(bookList.get(0)._id == null){
							bookList.clear();
						}
						isSachVuaDoc = true;
						bookList = IKaraDbHelper.getInstance(getApplicationContext()).getAllBookInJustRead();
						
						Collections.sort(bookList, new Comparator<Book>() {

							@Override
							public int compare(Book c1, Book c2) {
								// TODO Auto-generated method stub
								SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
								try {
									String day1 = c1.savedTime;//c1.getIssue_release_date().split("T")[0];
									String day2 = c2.savedTime;//c2.getIssue_release_date().split("T")[0];

									return dateFormat.parse(day2).compareTo(dateFormat.parse(day1));
								} catch (ParseException e) {
									throw new IllegalArgumentException(e);
								}
							}
						});
						processListData();
						adapter.addMore(bookList, 20);
						adapter.notifyDataSetChanged();
					} else if(id.equals(getResources().getString(R.string.title_thoat))){
						finish();
					} else if(id.equals(getResources().getString(R.string.title_ungdunghay))){
						
						if(!KaraUtils.hasNetworkConnection(getApplicationContext())){
							toggle();
							return;
						}
						
						GetOtherAppsRequest request = new GetOtherAppsRequest();
						request.language = "vi";
						request.platform = "ANDROID";
						showLoading();
						bookList.clear();
						adapter.notifyDataSetChanged();
						new IGetOtherAppRequest(new IOtherAppPostCallBack() {
							
							@Override
							public void onOtherAppPostPost(GetOtherAppsResponse statusObj) {
								// TODO Auto-generated method stub
								Log.e(TAG, "Check Check "+statusObj.apps.size());
								hideLoading();
								searchLayout.setVisibility(View.GONE);
								UngDungHayAdapter adapter = new UngDungHayAdapter(IMainActivity.this, statusObj.apps);
								listView.setAdapter(adapter);
							}
							
							@Override
							public void fail() {
								// TODO Auto-generated method stub
								
							}
						}, request).execute();
					}
					
					else{
						
						if(!KaraUtils.hasNetworkConnection(getApplicationContext())){
							toggle();
							return;
						}
						
						savedTitle = id;
						getSupportFragmentManager().beginTransaction().remove(fragment).commit();
						TopBooksRequest request = new TopBooksRequest();
						request.language = "vi";
						request.cursor = currentCursor;
						request.genreId = id;
						showLoading();
						new TopBookPostRequest(topBookCallBack, request).execute();
					}
				}
				toggle();
			}
		});
        leftMenuListView.setAdapter(leftMenuAdapter);
        
//        leftMenuAdapter.updateTheloai(listGenres);
        
        if(KaraUtils.hasNetworkConnection(getApplicationContext())){
        	new IGenresPostRequest(new IGenresPostCallBack() {
        		
        		@Override
        		public void onResultIGenresPostPost(GetGenresResponse statusObj) {
        			// TODO Auto-generated method stub
        			listGenres = statusObj.genres;
        			idTitleMenu = listGenres.get(0)._id; 
        			leftMenuAdapter.updateTheloai(listGenres);
        		}
        		
        		@Override
        		public void fail() {
        			// TODO Auto-generated method stub
        			
        		}
        	}).execute();
        }
        
        
        
        //START LOAD DATA
        TopBooksRequest request = new TopBooksRequest();
        request.language = "vi";
        request.cursor = currentCursor;
        request.genreId = getResources().getString(R.string.title_tien_hiep);
        new TopBookPostRequest(topBookCallBack, request).execute();
        
        interstitial = new InterstitialAd(IMainActivity.this);
		// Insert the Ad Unit ID
		interstitial.setAdUnitId("ca-app-pub-8429996645546440/9944646214");
 
		//Locate the Banner Ad in activity_main.xml
		AdView adView = (AdView) findViewById(R.id.adView);
 
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		interstitial.loadAd(adRequest);
		interstitial.setAdListener(new AdListener() {
			public void onAdLoaded() {
				displayInterstitial();
			}
		});
	}
	
	private void showLoading(){
		dialogLoading = new ProgressDialog(IMainActivity.this);
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
	
	private void processListData(){
		if(bookList.size() < 15){
			for(int i = bookList.size(); i < 15; i++){
				Book bk = new Book();
				bookList.add(bk);
			}
		}
	}
	
	public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
	
	private void hideKeyboard(){
		if(getCurrentFocus() != null){
			InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}
	
	private void prepareSampleData(){
		bookList = new ArrayList<Book>();
		for(int i = 0; i < 15; i++){
			bookList.add(new Book());
		}
		
		adapter = new BookAdapter(getApplicationContext(), bookList);
		listView.setAdapter(adapter);
	}
	
	@SuppressLint("NewApi") @Override
	public void onBackPressed(){
		if(fragment.isVisible()){
			Log.e(TAG, "remove Fragment ");
		}else{
			super.onBackPressed();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onItemClick "+position+" "+isFinishSearch);
		
		if(isFinishSearch){
//			if(listState == IkaraConstant.LIST_STATE.NORMAL){
				DecimalFormat df = new DecimalFormat();
				df.setMaximumFractionDigits(1);
				Log.e(TAG, "onItemClick "+bookList.size());
				float bookRate = Float.parseFloat(df.format((float)bookList.get(position).totalRate/bookList.get(position).rateCounter));
				
				Intent intent = new Intent(getApplicationContext(), IBookDetailActivity.class);
				intent.putExtra("book_thumb", bookList.get(position).thumbnailUrl);
				intent.putExtra("book_title", bookList.get(position).title);
				intent.putExtra("book_author", bookList.get(position).author);
				intent.putExtra("book_id", bookList.get(position)._id);
				intent.putExtra("book_view", bookList.get(position).viewCounter);
				intent.putExtra("book_rate", bookRate);
				intent.putExtra("book_status", bookList.get(position).status);
				intent.putExtra("book_totalrate", bookList.get(position).totalRate);
				intent.putExtra("book_ratecount", bookList.get(position).rateCounter);
				intent.putExtra("sach_vua_doc", isSachVuaDoc);
				startActivity(intent);
//			}
		}else{
			searchBaseOnTextInput(searchDatas.get(position).title);
		}
	}
	
	private void searchBaseOnTextInput(String query){
		SearchBooksRequest request = new SearchBooksRequest();
		request.keyword = query;
		request.language = "vi";
		
		new ISearchBookRequest(new ISerchBookCallBack() {
			
			@Override
			public void onSearchPost(SearchBooksResponse statusObj) {
				// TODO Auto-generated method stub
				Log.v(TAG, "onSearchPost" +statusObj.books.size());
				bookList.clear();
				adapter.addMore(statusObj.books, 20);
				listView.resetLoading();
				currentCursor = statusObj.cursor;
				processListData();
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				hideKeyboard();
				searchDatas.clear();
				isFinishSearch = true;
			}
			
			@Override
			public void fail() {
				// TODO Auto-generated method stub
				
			}
		}, request).execute();
	}
	
	private INewBookPostCallBack newBookCallBack = new INewBookPostCallBack() {
		
		@Override
		public void onResultINewBookPostPost(NewBooksResponse statusObj) {
			// TODO Auto-generated method stub
			if(bookList.get(0)._id == null){
				bookList.clear();
			}
			adapter.addMore(statusObj.books, 20);
			adapter.notifyDataSetChanged();
			
			listView.resetLoading();
			currentCursor = statusObj.cursor;
			hideLoading();
		}
		
		@Override
		public void fail() {
			// TODO Auto-generated method stub
			
		}
	};

	private TopBookCallBack topBookCallBack = new TopBookCallBack() {
		
		@Override
		public void onResultDashboardPost(TopBooksResponse statusObj) {
			// TODO Auto-generated method stub
			
			Log.i(TAG, "Value id "+bookList.get(0)._id+" "+bookList.size());
			if(bookList.get(0)._id == null){
				bookList.clear();
			}
			adapter.addMore(statusObj.books, 20);
			adapter.notifyDataSetChanged();
			
			listView.resetLoading();
			currentCursor = statusObj.cursor;
			
			hideLoading();
		}
		
		@Override
		public void fail() {
			// TODO Auto-generated method stub
		}
	};
	
	private void showRateApp(final App itemApp){
		final Dialog dialog = new Dialog(IMainActivity.this);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_hotapp, null);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView);
		ImageView imgIcon = (ImageView)dialogView.findViewById(R.id.img_thumb_app_hay);
		TextView tvTitle = (TextView)dialogView.findViewById(R.id.tv_title_app);
		TextView tvContent = (TextView)dialogView.findViewById(R.id.tv_content_dialog);
		
		Button butCo = (Button)dialogView.findViewById(R.id.but_co);
		Button butDesau = (Button)dialogView.findViewById(R.id.but_desau);
		Button butKhong = (Button)dialogView.findViewById(R.id.but_khong);
		
		butCo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + itemApp.storeId)));
			}
		});
		butDesau.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		butKhong.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				String exclustion = IkaraPreferences.getStringPref(getApplicationContext(), PrefConstant.PREF_EXCLUSION, "");
				Log.v(TAG, "oldExcuse "+exclustion);
				IkaraPreferences.saveStringPref(getApplicationContext(), PrefConstant.PREF_EXCLUSION, exclustion+";"+itemApp.bundleId);
			}
		});
		
		ImageLoader.getInstance().displayImage(itemApp.thumbnailUrl, imgIcon);
		tvTitle.setText(itemApp.appName);
		tvContent.setText(itemApp.desc);
		
		dialog.show();
	}

	public void onResume(){
		super.onResume();
		isFinishSearch = true;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		
			
		case R.id.tv_cancel:
			isFinishSearch = true;
			if(searchView.getText().toString().trim().length() > 0){
				searchView.setText("");
			}else{
				listView.setAdapter(adapter);
				hideKeyboard();
			}
			break;
			
		default:
			break;
		}
	}
	
	private Runnable runable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onQueryTextChange RUN RUN "+query);
			searchBook();
		}
	};
	
	private void searchBook(){
		new IGetSuggestSearchRequest(new IGetBookPostCallBack() {
			
			@Override
			public void fail() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onResultSearchPostPost(JSONArray statusObj) {
				// TODO Auto-generated method stub
				
				try{
					Log.i(TAG, "respoonse search "+statusObj.length());
					
					searchDatas = new ArrayList<SearchItem>();
					for(int i = 0; i < statusObj.length(); i++){
						SearchItem item = new SearchItem();
						
						try {
							item.title = statusObj.getString(i).trim();
							searchDatas.add(item);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					SearchAdapter adapter = new SearchAdapter(getApplicationContext(), searchDatas);
					listView.setAdapter(adapter);
				}catch(Exception e){
					
				}
			}
		}, query).execute();
	}
	
	protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("Got a new message", "ALO "+msg.arg1);
    		startSearch = true;
        }
    };


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		Log.v(TAG, "beforeTextChanged "+s.toString());
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		isFinishSearch = false;
		
		try{
			if (s.toString().length() > 0) {
				query = s.toString();
				startSearch = false;
				if (System.currentTimeMillis() - time <= 300) {
					startSearch = true;
				}
				time = System.currentTimeMillis();
				Log.i(TAG, "onQueryTextChange " + start);
				
				if (startSearch) {
					handler.removeCallbacks(runable);
				}
				
				handler.postDelayed(runable, 300);
			}
			
		}catch(Exception e){}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		Log.v(TAG, "afterTextChanged "+s.toString());
	}
}
