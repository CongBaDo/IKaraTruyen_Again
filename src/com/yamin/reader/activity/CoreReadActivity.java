package com.yamin.reader.activity;

import org.geometerplus.android.fbreader.NavigationPopup;
import org.geometerplus.android.fbreader.PopupPanel;
import org.geometerplus.android.fbreader.SelectionPopup;
import org.geometerplus.android.fbreader.ShowLibraryAction;
import org.geometerplus.android.fbreader.ShowNavigationAction;
import org.geometerplus.android.fbreader.ShowPreferencesAction;
import org.geometerplus.android.fbreader.ShowTOCAction;
import org.geometerplus.android.fbreader.TextSearchPopup;
import org.geometerplus.android.fbreader.api.ApiListener;
import org.geometerplus.android.fbreader.api.ApiServerImplementation;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.book.BookUtil;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.ChangeFontSizeAction;
import org.geometerplus.fbreader.fbreader.ColorProfile;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.SwitchProfileAction;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;
import org.geometerplus.zlibrary.ui.android.application.ZLAndroidApplicationWindow;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.ikaratruyen.IApplication;
import com.ikaratruyen.R;
import com.ikaratruyen.activity.IChapListActivity;
import com.ikaratruyen.model.Chapter;
import com.ikaratruyen.model.GetChapterRequest;
import com.ikaratruyen.model.GetChapterResponse;
import com.ikaratruyen.request.IGetChapterRequest;
import com.ikaratruyen.request.IGetChapterRequest.IChapterPostCallBack;
import com.ikaratruyen.utils.IKaraDbHelper;
import com.ikaratruyen.utils.ISettings;
import com.ikaratruyen.utils.IkaraConstant;
import com.ikaratruyen.utils.KaraUtils;
import com.yamin.reader.utils.ToolUtils;

/**
 * 
 * 
 */
public class CoreReadActivity extends FragmentActivity implements OnSeekBarChangeListener, OnClickListener{
	private static final String TAG = "CoreReadActivity";
	
	public static final String ACTION_OPEN_BOOK = "android.easyreader.action.VIEW";
	public static final String BOOK_KEY = "esayreader.book";
	public static final String BOOKMARK_KEY = "esayreader.bookmark";
	public static final String BOOK_PATH_KEY = "esayreader.book.path";
	public static final int REQUEST_PREFERENCES = 1;
	public static final int REQUEST_CANCEL_MENU = 2;
	private static final int NIGHT_UPDATEUI = 0;
	private static final int DAY_UPDATEUI = 1;
	public static final int RESULT_DO_NOTHING = RESULT_FIRST_USER;
	public static final int RESULT_REPAINT = RESULT_FIRST_USER + 1;
	private ZLIntegerRangeOption option;
	private ImageView imgChangeState;
	private TextView fontBigButton;
	private TextView fontSmallButton;
	private SeekBar seekPage;
	private LinearLayout topLL;
	private LinearLayout bottomLL;
	private TextView tvChapIndex, tvIndex, tvQuyenIndex;
	private TextView tvChapIndexTop;
	private String chapId;
	private ImageView imgFontText;
	private int currentIndexOfChap = 0;
	private int readerState = IkaraConstant.READER_STATE.NIGHT;
	private boolean isLoadingChapter = false;
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case NIGHT_UPDATEUI:
				Log.v(TAG, "NIGHT");
				topLL.setBackgroundColor(getResources().getColor(R.color.black));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.black));
				break;
			case DAY_UPDATEUI:
				Log.v(TAG, "DAY");
				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_1));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_1));
				break;

			}
			super.handleMessage(msg);
		}
	};

	private static ZLAndroidLibrary getZLibrary() {
		return (ZLAndroidLibrary) ZLAndroidLibrary.Instance();
	}

	private FBReaderApp myFBReaderApp;
	private volatile Book myBook;

	private ZLAndroidWidget myMainView;
	private boolean isBottomAndTopMenuShow = false;
	private String chapTitle;
	private String bookId;
	private String bookTitle;
	private int currentChapIndex = 0;
	private boolean isOpenBook;

	private synchronized void openBook(Intent intent, Runnable action,
			boolean force) {
		
		Log.i(TAG, "openBook");
		if (!force && myBook != null) {
			return;
		}
		if (myBook == null) {
			String path ;//= Environment.getExternalStorageDirectory().getAbsolutePath() +"/nemodotest.fb2";
			
			if(isOpenBook){
				String saveInfo = IKaraDbHelper.getInstance(getApplicationContext()).getSavedInfo(bookId);
//				Log.e(TAG, "isOpenBook "+saveInfo);
				if(!saveInfo.equals("")){
					chapId = saveInfo.split(";")[0];
					currentIndexOfChap = Integer.parseInt(saveInfo.split(";")[1]);
					currentChapIndex = Integer.parseInt(saveInfo.split(";")[2]);
				}
				
				path = KaraUtils.getChapPathFromSdcard(bookId, currentChapIndex+1);
				Log.v(TAG, "oncreate save Info "+chapId+" "+currentChapIndex+" "+currentIndexOfChap);
				this.myBook = myFBReaderApp.Collection.getBookByFile(BookUtil.getBookFileFromSDCard(path));
			}else{
				path = KaraUtils.getChapPathFromSdcard(bookId, currentChapIndex+1);
				Log.i(TAG, "openBook "+path);
				if(path == null){
					loadChapContent(chapId);
				}else{
					this.myBook = myFBReaderApp.Collection.getBookByFile(BookUtil.getBookFileFromSDCard(path));
				}
			}
		}
		
		Log.i(TAG, "openBook "+currentIndexOfChap);
		myFBReaderApp.openBook(myBook, null, action);
	}
	
	public Book createBookForFile(ZLFile file) {
		if (file == null) {
			return null;
		}
		Book book = myFBReaderApp.Collection.getBookByFile(file);
		if (book != null) {
			return book;
		}
		if (file.isArchive()) {
			for (ZLFile child : file.children()) {
				book = myFBReaderApp.Collection.getBookByFile(child);
				if (book != null) {
					return book;
				}
			}
		}
		return null;
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.core_main);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getZLibrary().setActivity(CoreReadActivity.this);
		
		currentChapIndex = getIntent().getExtras().getInt("chap_index");
		chapTitle = getIntent().getExtras().getString("chap_title");
		bookId = getIntent().getExtras().getString("book_id");
		isOpenBook = getIntent().getExtras().getBoolean("open_book");
		bookTitle = getIntent().getExtras().getString("book_title");
		chapId = getIntent().getExtras().getString("chap_id");
		
		Log.v(TAG, "oncreate "+chapTitle +" chapTitle "+chapTitle+" CHAPID "+chapId+" BOOKTITLE "+bookTitle);

		chapTitle = getCurrentChapter(chapId).title;
		Log.v(TAG, "oncreate " +" chapTitle "+chapTitle);
		option = ZLTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
		ZLTextStyleCollection.Instance().getBaseStyle().getFontFamily();
		
		myFBReaderApp = (FBReaderApp) FBReaderApp.Instance();
		if (myFBReaderApp == null) {
			myFBReaderApp = new FBReaderApp(CoreReadActivity.this,
					new BookCollectionShadow());
		}
		getCollection().bindToService(this, null);
		myBook = null;

		final ZLAndroidApplication androidApplication = (ZLAndroidApplication) getApplication();
		if (androidApplication.myMainWindow == null) {
			androidApplication.myMainWindow = new ZLAndroidApplicationWindow(
					myFBReaderApp);
			myFBReaderApp.initWindow();
		}
		
		myFBReaderApp.PageTurningOptions.Animation.setValue(ZLView.Animation.shift);
		if (myFBReaderApp.getPopupById(TextSearchPopup.ID) == null) {
			new TextSearchPopup(myFBReaderApp);
		}
		if (myFBReaderApp.getPopupById(NavigationPopup.ID) == null) {
			new NavigationPopup(myFBReaderApp);
		}
		if (myFBReaderApp.getPopupById(SelectionPopup.ID) == null) {
			new SelectionPopup(myFBReaderApp);
		}

		myFBReaderApp.addAction(ActionCode.SHOW_LIBRARY, new ShowLibraryAction(
				this, myFBReaderApp));
		myFBReaderApp.addAction(ActionCode.SHOW_PREFERENCES,
				new ShowPreferencesAction(this, myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SHOW_TOC, new ShowTOCAction(this,
				myFBReaderApp));

		myFBReaderApp.addAction(ActionCode.SHOW_NAVIGATION,
				new ShowNavigationAction(this, myFBReaderApp));
		initView();
		setListener();
		
		
	}

	/** Init UI View*/
	public void initView() {
		myMainView = (ZLAndroidWidget) findViewById(R.id.main_view);
		imgChangeState = (ImageView)findViewById(R.id.img_change_state_reader);
		imgChangeState.setOnClickListener(this);
		topLL = (LinearLayout) findViewById(R.id.topMenuLL);
		bottomLL = (LinearLayout) findViewById(R.id.bottomMenuLL);
		seekPage = (SeekBar) findViewById(R.id.sk_page);
		seekPage.setOnSeekBarChangeListener(this);
		fontBigButton = (TextView) findViewById(R.id.tv_increase);
		fontSmallButton = (TextView) findViewById(R.id.tv_decrease);
		imgFontText = (ImageView)findViewById(R.id.img_font_text);
		imgFontText.setOnClickListener(this);
		tvIndex = (TextView)findViewById(R.id.tv_index);
		((ImageView) findViewById(R.id.img_back)).setOnClickListener(this);
		((ImageView) findViewById(R.id.img_share)).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title_bar)).setText(bookTitle);
		((ImageView) findViewById(R.id.img_share))
				.setBackgroundResource(R.drawable.view_state_clipboard_button);
		tvChapIndex = (TextView) findViewById(R.id.tv_chapter_index);
		tvChapIndexTop = (TextView) findViewById(R.id.tv_chap_top);
		tvQuyenIndex = (TextView) findViewById(R.id.tv_book_quyen_index);
		tvChapIndexTop.setVisibility(View.VISIBLE);
		tvChapIndexTop.setText((currentChapIndex + 1) + "");
//		TypedValue tv = new TypedValue();
//		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
//			int actionBarHeight = TypedValue.complexToDimensionPixelSize(
//					tv.data, getResources().getDisplayMetrics());
//			topLL.setLayoutParams(new LayoutParams(
//					LayoutParams.MATCH_PARENT, actionBarHeight));
//		}
		
		isBottomAndTopMenuShow = false;
		topLL.setVisibility(View.GONE);
		bottomLL.setVisibility(View.GONE);
		
		if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.NIGHT)) {
			topLL.setBackgroundColor(getResources().getColor(R.color.black));
			bottomLL.setBackgroundColor(getResources().getColor(R.color.black));
		} else {
			if (myFBReaderApp.getColorProfileName() != null
					&& myFBReaderApp.getColorProfileName().equals(
							ColorProfile.SECOND)) {
				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_2));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_2));
			} else {
				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_1));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_1));
			}
		}
	}

	public ZLAndroidWidget getMainView() {
		return myMainView;
	}

	private void setListener() {
		
		fontBigButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (option.getValue() <= 55) {
					myFBReaderApp.runAction(ActionCode.INCREASE_FONT,
							new ChangeFontSizeAction(myFBReaderApp, +2));
				}
			}
		});
		fontSmallButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (option.getValue() >= 30) {
					myFBReaderApp.runAction(ActionCode.DECREASE_FONT,
							new ChangeFontSizeAction(myFBReaderApp, -2));
				}
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.e(TAG, "onStart");
		getCollection().bindToService(this, new Runnable() {
			public void run() {
				new Thread() {
					public void run() {
						openBook(getIntent(), null, false);
						myFBReaderApp.getViewWidget().repaint();
					}
				}.start();

				myFBReaderApp.getViewWidget().repaint();
				gotoPage(currentIndexOfChap);
			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onResume() {
		super.onResume();

		IApplication.getInstance().setCurrentActivity(CoreReadActivity.this);
		PopupPanel.restoreVisibilities(myFBReaderApp);
		ApiServerImplementation.sendEvent(this,
				ApiListener.EVENT_READ_MODE_OPENED);

		getCollection().bindToService(this, new Runnable() {
			public void run() {
				final BookModel model = myFBReaderApp.Model;
				if (model == null || model.Book == null) {
					return;
				}
				onPreferencesUpdate(myFBReaderApp.Collection
						.getBookById(model.Book.getId()));
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		PopupPanel.removeAllWindows(myFBReaderApp, this);
		Log.i("MAIN", "onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		getCollection().unbind();
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		myFBReaderApp.onWindowClosing();
		super.onLowMemory();
	}

	@Override
	public boolean onSearchRequested() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		myFBReaderApp.hideActivePopup();
		final SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
		manager.setOnCancelListener(new SearchManager.OnCancelListener() {
			public void onCancel() {
				if (popup != null) {
					myFBReaderApp.showPopup(popup.getId());
				}
				manager.setOnCancelListener(null);
			}
		});
		startSearch(myFBReaderApp.TextSearchPatternOption.getValue(), true,
				null, false);
		return true;
	}
	
	public void backPress() {
		int y = myFBReaderApp.getTextView().pagePosition().Current;
		int z = myFBReaderApp.getTextView().pagePosition().Total;
		Log.i(TAG, "backPress "+y + "" + "/" + z + ToolUtils.myPercent(y, z));

		Log.i(TAG, "backPress"+myBook.getId());
		myFBReaderApp.Collection.storePosition(myBook.getId(), myFBReaderApp
				.getTextView().getEndCursor());
		finish();
	}

	public void showSelectionPanel() {
		final ZLTextView view = myFBReaderApp.getTextView();
		((SelectionPopup) myFBReaderApp.getPopupById(SelectionPopup.ID)).move(
				view.getSelectionStartY(), view.getSelectionEndY());
		myFBReaderApp.showPopup(SelectionPopup.ID);
	}

	public void hideSelectionPanel() {
		final FBReaderApp.PopupPanel popup = myFBReaderApp.getActivePopup();
		if (popup != null && popup.getId() == SelectionPopup.ID) {
			myFBReaderApp.hideActivePopup();
		}
	}

	private void onPreferencesUpdate(Book book) {
		AndroidFontUtil.clearFontCache();
		myFBReaderApp.onBookUpdated(book);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case REQUEST_CANCEL_MENU:
			myFBReaderApp.runCancelAction(resultCode - 1);
			break;
			
		case 100:
			
			if(resultCode == IChapListActivity.CHAPTER){
				currentChapIndex = data.getIntExtra("chap_index", 0);
				tvChapIndexTop.setText((currentChapIndex + 1) + "");
				chapTitle = data.getStringExtra("chap_title");
				chapId = data.getStringExtra("chap_id");
				loadChapContent(chapId);
			}else{
				
			}
			
			break;
		}
	}

	public void navigate() {
		if (!isBottomAndTopMenuShow) {
			isBottomAndTopMenuShow = true;
			topLL.setVisibility(View.VISIBLE);
			bottomLL.setVisibility(View.VISIBLE);
			topLL.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.layout_enter));
			bottomLL.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.layout_enter));
		} else {
			isBottomAndTopMenuShow = false;
			topLL.setVisibility(View.GONE);
			bottomLL.setVisibility(View.GONE);
			topLL.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.layout_exit));
			bottomLL.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.layout_exit));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//
			saveIndexPage();
			backPress();
			this.onBackPressed();
			return true;
		}
		return (myMainView != null && myMainView.onKeyDown(keyCode, event))
				|| super.onKeyDown(keyCode, event);
	}
	
	
	private PowerManager.WakeLock myWakeLock;
	private boolean myWakeLockToCreate;

	public final void createWakeLock() {
//		Log.i(TAG, "createWakeLock");
		if (myWakeLockToCreate) {
			synchronized (this) {
				if (myWakeLockToCreate) {
					myWakeLockToCreate = false;
					myWakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
							.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
									"FBReader");
					myWakeLock.acquire();
				}
			}
		}
	}

	public void setScreenBrightness(int percent) {
		if (percent < 1) {
			percent = 10;
		} else if (percent > 100) {
			percent = 100;
		}
		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.screenBrightness = percent / 100.0f;
		getWindow().setAttributes(attrs);
	}

	public int getScreenBrightness() {
		final int level = (int) (100 * getWindow().getAttributes().screenBrightness);
		return (level >= 0) ? level : 50;
	}

	private BookCollectionShadow getCollection() {
		return (BookCollectionShadow) myFBReaderApp.Collection;
	}
	
	private void gotoPage(int page) {
		final ZLTextView view = myFBReaderApp.getTextView();
		if (page == 1) {
			view.gotoHome();
		} else {
			view.gotoPage(page);
		}
		myFBReaderApp.getViewWidget().reset();
		myFBReaderApp.getViewWidget().repaint();
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		final int page = progress + 1;
		gotoPage(page);
		tvIndex.setText(myFBReaderApp.getTextView().pagePosition().Current + "/" + (myFBReaderApp.getTextView().pagePosition().Total));
	}
	
	public void stopLoading(){
//		Log.e(TAG, "stopLoading "+currentChapIndex);
		seekPage.setMax(myFBReaderApp.getTextView().pagePosition().Total);
		if (ISettings.getInstance().getChapListContents().get(currentChapIndex).volume != null) {
			long volume = ISettings.getInstance().getChapListContents()
					.get(currentChapIndex).volume;
			tvQuyenIndex.setVisibility(View.VISIBLE);
			tvQuyenIndex.setText(getResources().getString(R.string.book_value)
					+ " " + volume);
		}
		
		Log.v(TAG, "TOATAL "+myFBReaderApp.getTextView().pagePosition().Total+" "+myFBReaderApp.getTextView().pagePosition().Current);
		tvChapIndex.setText(getResources().getString(R.string.chapter_value)
				+ " " + (currentChapIndex + 1));
		
		tvIndex.setText(myFBReaderApp.getTextView().pagePosition().Current + "/" + (myFBReaderApp.getTextView().pagePosition().Total));
		
		gotoPage(currentIndexOfChap);
	}
	
	private String makeProgressText(int page, int pagesNumber) {
		final StringBuilder builder = new StringBuilder();
		builder.append(page);
		builder.append("/");
		builder.append(pagesNumber);
		final TOCTree tocElement = myFBReaderApp.getCurrentTOCElement();
		if (tocElement != null) {
			builder.append("  ");
			builder.append(tocElement.getText());
		}
		return builder.toString();
	}
	
	public void loadNextChap(boolean isNext, boolean isBack){
		
		Log.v(TAG, "loadNextChap "+isNext +" "+isBack+" "+isLoadingChapter);
		
		if(!isBack){
			if(currentChapIndex > 0){
				currentChapIndex--;
				String path = KaraUtils.getChapPathFromSdcard(bookId, currentChapIndex+1);
				
				if(!isLoadingChapter){
					isLoadingChapter = true;
					if(path == null){
						loadChapContent(ISettings.getInstance().getChapListContents().get(currentChapIndex)._id);
					}else{
						this.myBook = myFBReaderApp.Collection.getBookByFile(BookUtil.getBookFileFromSDCard(path));
						isLoadingChapter = false;
						
						myFBReaderApp.openBook(myBook, null, null);
					}
				}
				
			}
		}
		
		if(!isNext){
			currentChapIndex++;
			
			if(currentChapIndex < ISettings.getInstance().getChapListContents().size()){
				String path = KaraUtils.getChapPathFromSdcard(bookId, currentChapIndex+1);
				
				if(!isLoadingChapter){
					isLoadingChapter = true;
					if(path == null){
						loadChapContent(ISettings.getInstance().getChapListContents().get(currentChapIndex)._id);
					}else{
						Log.v(TAG, "loadNextChap path "+path);
						this.myBook = myFBReaderApp.Collection.getBookByFile(BookUtil.getBookFileFromSDCard(path));
						isLoadingChapter = false;
						
						myFBReaderApp.openBook(myBook, null, null);
					}
				}
			}
		}
	}
	
	public void reloadPostition(){
//		Log.e(TAG, "reloadPosition "+myFBReaderApp.getTextView().pagePosition().Current+" ");
//		tvIndex.setText(current + "/" + max);
		
//		final ZLTextView textView = myFBReaderApp.getTextView();
//		final ZLTextView.PagePosition pagePosition = textView.pagePosition();
//
//		if (seekPage.getMax() != pagePosition.Total - 1 || seekPage.getProgress() != pagePosition.Current - 1) {
//			seekPage.setMax(pagePosition.Total - 1);
//			seekPage.setProgress(pagePosition.Current - 1);
//			tvIndex.setText(makeProgressText(pagePosition.Current, pagePosition.Total));
//		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.img_back:
			backPress();
			saveIndexPage();
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
			if(readerState == IkaraConstant.READER_STATE.DAY){
				readerState = IkaraConstant.READER_STATE.NIGHT;
				myFBReaderApp.runAction(
						ActionCode.SWITCH_TO_NIGHT_PROFILE,
						new SwitchProfileAction(myFBReaderApp,
								ColorProfile.NIGHT));
				Message message = new Message();
				message.what = NIGHT_UPDATEUI;
				mHandler.sendMessage(message);
			}else{
				readerState = IkaraConstant.READER_STATE.DAY;
				myFBReaderApp.runAction(
						ActionCode.SWITCH_TO_DAY_PROFILE,
						new SwitchProfileAction(myFBReaderApp,
								ColorProfile.DAY));
				Message message = new Message();
				message.what = DAY_UPDATEUI;
				mHandler.sendMessage(message);
			}
			break;

		case R.id.tv_index:
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
	
	private Chapter getCurrentChapter(String chapId) {
		for (int i = 0; i < ISettings.getInstance().getChapListContents()
				.size(); i++) {
			if (ISettings.getInstance().getChapListContents().get(i)._id.equals(chapId)) {
				return ISettings.getInstance().getChapListContents().get(i);
			}
		}

		return null;
	}
	
	/**
	 * @param id is chapId*/
	private void loadChapContent(String id) {
		Log.v(TAG, "loadChapContent "+id+" "+currentChapIndex);

		chapTitle = getCurrentChapter(id).title;
			
		GetChapterRequest request = new GetChapterRequest();
		if (isOpenBook) {
			isOpenBook = false;
			
		} else {
			chapId = id;
			request.chapterId = chapId;
			request.language = "vi";
			new IGetChapterRequest(new IChapterPostCallBack() {
				
				@Override
				public void onResultChapterPostPost(GetChapterResponse statusObj) {
					// TODO Auto-generated method stub
					Log.v(TAG, "onResuktChapterPost "+chapTitle+" "+bookTitle);
					KaraUtils.saveChapContent2SDCard(bookTitle, bookId, chapTitle, currentChapIndex+1, statusObj.chapter.content);
					String path = KaraUtils.getChapPathFromSdcard(bookId, currentChapIndex+1);
					tvChapIndexTop.setText(""+(currentChapIndex+1));
					
					myBook = myFBReaderApp.Collection.getBookByFile(BookUtil.getBookFileFromSDCard(path));
					myFBReaderApp.openBook(myBook, null, null);
					
					isLoadingChapter = false;
				}
				
				@Override
				public void fail() {
					// TODO Auto-generated method stub
					
				}
			}, request).execute();
		}
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
//		backPress();
		finish();
	}
	
	private void saveIndexPage() {
		String savedIndex = chapId + ";" + myFBReaderApp.getTextView().pagePosition().Current+";"+currentChapIndex;
		Log.w(TAG, "saveIndexPage " + savedIndex);
		IKaraDbHelper.getInstance(getApplicationContext())
				.addToSavedIndexTable(bookId, savedIndex);
	}
}