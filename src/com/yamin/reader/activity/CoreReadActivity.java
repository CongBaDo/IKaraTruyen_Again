package com.yamin.reader.activity;

import java.io.File;

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
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.ChangeFontSizeAction;
import org.geometerplus.fbreader.fbreader.ColorProfile;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.FBRreshAction;
import org.geometerplus.fbreader.fbreader.SwitchProfileAction;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.options.ZLEnumOption;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;
import org.geometerplus.zlibrary.ui.android.application.ZLAndroidApplicationWindow;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidApplication;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.view.AndroidFontUtil;
import org.geometerplus.zlibrary.ui.android.view.ZLAndroidWidget;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ikaratruyen.R;
import com.ikaratruyen.utils.IkaraConstant;
import com.yamin.reader.utils.ToolUtils;
import com.yamin.reader.view.SwitchButton;

/**
 * 
 * @ClassName: CoreReadActivity
 * @Description: TODO(这里用一句话描述这个类的作用) 基于开源的FBREADERJ1.8.2
 * @author ymcao
 * @date 2013-6-24 下午8:27:04
 * 
 */
public class CoreReadActivity extends Activity {
	private static final String TAG = "CoreReadActivity";
	
	public static final String ACTION_OPEN_BOOK = "android.easyreader.action.VIEW";
	public static final String BOOK_KEY = "esayreader.book";
	public static final String BOOKMARK_KEY = "esayreader.bookmark";
	public static final String BOOK_PATH_KEY = "esayreader.book.path";
	public static final int REQUEST_PREFERENCES = 1;
	public static final int REQUEST_CANCEL_MENU = 2;
	private static final int NIGHT_UPDATEUI = 0;
	private static final int DAY_UPDATEUI = 1;
	private static final int GREEN_UPDATEUI = 2;
	private static final int BROWN_UPDATEUI = 3;
	public static final int RESULT_DO_NOTHING = RESULT_FIRST_USER;
	public static final int RESULT_REPAINT = RESULT_FIRST_USER + 1;
	private static final String PLUGIN_ACTION_PREFIX = "___";
	private ZLIntegerRangeOption option;
	ZLEnumOption<ZLView.Animation>  animoption;
	private boolean isNight = false;
	private ImageView imgChangeState;
	//
	PopupWindow mPopuwindow;
	private TextView fontBigButton;
	private TextView fontSmallButton;
	private ImageView bookMoreButton;
	private ImageView bookHomeButton;
	private RelativeLayout topLL;
	private LinearLayout bottomLL;
	private SeekBar brightness_slider;
	private SwitchButton dayornightSwitch;
	private ScrollView popuMenuLL;
	private LinearLayout navigation_settings;
	private int readerState = IkaraConstant.READER_STATE.NIGHT;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case NIGHT_UPDATEUI:

				topLL.setBackgroundColor(getResources().getColor(R.color.black));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.black));
				if (mPopuwindow != null && mPopuwindow.isShowing()) {
				}
				break;
			case DAY_UPDATEUI:
				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_1));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_1));
				break;

			case BROWN_UPDATEUI:
				myFBReaderApp.runAction(ActionCode.SWITCH_TO_BG3,
						new SwitchProfileAction(myFBReaderApp,
								ColorProfile.THIRD));
				myFBReaderApp.runAction(ActionCode.JUST_REFRESH,
						new FBRreshAction(myFBReaderApp, 0));
				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_3));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_3));
				break;
			case GREEN_UPDATEUI:
				myFBReaderApp.runAction(ActionCode.SWITCH_TO_BG2,
						new SwitchProfileAction(myFBReaderApp,
								ColorProfile.SECOND));
				myFBReaderApp.runAction(ActionCode.JUST_REFRESH,
						new FBRreshAction(myFBReaderApp, 0));

				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_2));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_2));
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

	private RelativeLayout myRootView;
	private ZLAndroidWidget myMainView;
	private boolean isBottomAndTopMenuShow = false;

	private synchronized void openBook(Intent intent, Runnable action,
			boolean force) {
		
		Log.i(TAG, "openBook");
		if (!force && myBook != null) {
			return;
		}
//		myBook = SerializerUtil
//				.deserializeBook(intent.getStringExtra(BOOK_PATH_KEY));
		if (myBook == null) {
//			final Uri data = intent.getData();
//			if (data != null) {
//				ZipFile file = new ZipFile(file)
//				this.myBook = createBookForFile(ZLFile.createFileByPath(data
//						.getPath()));
				String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/nemodotest.fb2";
				File check = new File(path);
				Log.e(TAG, "ALO "+check.exists()+" "+path);
				
				this.myBook = myFBReaderApp.Collection.getBookByFile(BookUtil.getBookFileFromSDCard(path));
//			}?
		}
		myFBReaderApp.openBook(myBook, null, action);
	}
	
	private void loadBookFromSDCard(String path){
		myFBReaderApp.Collection.getBookByFile(BookUtil.getBookFileFromSDCard(path));
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

		Log.e(TAG, "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.core_main);
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		getZLibrary().setActivity(CoreReadActivity.this);
		//
		option = ZLTextStyleCollection.Instance().getBaseStyle().FontSizeOption;
		//
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

	/*
	 * Init UI View
	 */
	public void initView() {
		myRootView = (RelativeLayout) findViewById(R.id.root_view);
		myMainView = (ZLAndroidWidget) findViewById(R.id.main_view);
		imgChangeState = (ImageView)findViewById(R.id.img_change_state_reader);
		
		topLL = (RelativeLayout) findViewById(R.id.topMenuLL);
		bottomLL = (LinearLayout) findViewById(R.id.bottomMenuLL);
//		bookMoreButton = (ImageView) findViewById(R.id.bookMoreButton);
		fontBigButton = (TextView) findViewById(R.id.tv_increase);
		fontSmallButton = (TextView) findViewById(R.id.tv_decrease);
//		bookHomeButton = (ImageView) findViewById(R.id.bookHomeButton);
		//
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

			} else if (myFBReaderApp.getColorProfileName() != null
					&& myFBReaderApp.getColorProfileName().equals(
							ColorProfile.THIRD)) {
				topLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_3));
				bottomLL.setBackgroundColor(getResources().getColor(
						R.color.main_bg_3));
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
//		bookMoreButton.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				showPopupWindow(bookMoreButton);
//				Log.i("MAIN", "onClick()");
//			}
//		});
		imgChangeState.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(readerState == IkaraConstant.READER_STATE.DAY){
					readerState = IkaraConstant.READER_STATE.NIGHT;
					myFBReaderApp.runAction(
							ActionCode.SWITCH_TO_NIGHT_PROFILE,
							new SwitchProfileAction(myFBReaderApp,
									ColorProfile.NIGHT));
					Toast.makeText(CoreReadActivity.this, "夜间模式开启",
							Toast.LENGTH_SHORT).show();
					Message message = new Message();
					message.what = NIGHT_UPDATEUI;
					mHandler.sendMessage(message);
					isNight = true;
				}else{
					readerState = IkaraConstant.READER_STATE.DAY;
					myFBReaderApp.runAction(
							ActionCode.SWITCH_TO_DAY_PROFILE,
							new SwitchProfileAction(myFBReaderApp,
									ColorProfile.DAY));
					Toast.makeText(CoreReadActivity.this, "白天模式开启",
							Toast.LENGTH_SHORT).show();
					Message message = new Message();
					message.what = DAY_UPDATEUI;
					mHandler.sendMessage(message);
					isNight = false;
				}
			}
		});
		
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
//		bookHomeButton.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				backPress();
//			}
//		});
	}

	@Override
	protected void onNewIntent(final Intent intent) {
		Log.i("TAG", "onNewIntent()");
		final String action = intent.getAction();
		final Uri data = intent.getData();

		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0) {
			super.onNewIntent(intent);
		} else if (Intent.ACTION_VIEW.equals(action) && data != null
				&& "fbreader-action".equals(data.getScheme())) {
			myFBReaderApp.runAction(data.getEncodedSchemeSpecificPart(),
					data.getFragment());
		} else if (ACTION_OPEN_BOOK.equals(action)) {

			getCollection().bindToService(this, new Runnable() {
				public void run() {
					Log.i("TAG", "openBook()");
					//openBook(intent, null, true);
				}
			});
		} else {
			super.onNewIntent(intent);
		}
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
			}
		});

		((PopupPanel) myFBReaderApp.getPopupById(TextSearchPopup.ID))
				.setPanelInfo(CoreReadActivity.this, myRootView);
		((PopupPanel) myFBReaderApp.getPopupById(NavigationPopup.ID))
				.setPanelInfo(CoreReadActivity.this, myRootView);
		((PopupPanel) myFBReaderApp.getPopupById(SelectionPopup.ID))
				.setPanelInfo(CoreReadActivity.this, myRootView);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onResume() {
		super.onResume();

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
//		myFBReaderApp.stopTimer();
		if (mPopuwindow != null && mPopuwindow.isShowing()) {
			mPopuwindow.dismiss();
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
//		ApiServerImplementation.sendEvent(this,
//				ApiListener.EVENT_READ_MODE_CLOSED);
		PopupPanel.removeAllWindows(myFBReaderApp, this);
		Log.i("MAIN", "onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		getCollection().unbind();
		if (mPopuwindow != null && mPopuwindow.isShowing()) {
			mPopuwindow.dismiss();
		}
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
		Log.i("MAIN", y + "" + "/" + z + ToolUtils.myPercent(y, z));

		Log.i("MAIN", "" + myFBReaderApp.getTextView().getEndCursor() +" "+myBook.getId());
		myFBReaderApp.Collection.storePosition(myBook.getId(), myFBReaderApp
				.getTextView().getEndCursor());
		startActivity(new Intent(CoreReadActivity.this, MainActivity.class));
		CoreReadActivity.this.overridePendingTransition(R.anim.activity_enter,
				R.anim.activity_exit);
		CoreReadActivity.this.finish();
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
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			//
//			backPress();
//			this.onBackPressed();
//			return true;
//		}
//		return (myMainView != null && myMainView.onKeyDown(keyCode, event))
//				|| super.onKeyDown(keyCode, event);
//	}

	private PowerManager.WakeLock myWakeLock;
	private boolean myWakeLockToCreate;

	public final void createWakeLock() {
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

	/*
	 * @弹出POPU MENU
	 */
	public void showPopupWindow(View v) {
		ScrollView layout = (ScrollView) LayoutInflater.from(
				CoreReadActivity.this).inflate(R.layout.book_settings, null);
		brightness_slider = (SeekBar) layout.findViewById(R.id.brightness_slider);
		dayornightSwitch = (SwitchButton) layout.findViewById(R.id.main_myslipswitch);
		popuMenuLL = (ScrollView) layout.findViewById(R.id.popuMenuBg);

		if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.NIGHT)) {

		} else if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.SECOND)) {
		} else if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.THIRD)) {
		} else {
		}
		navigation_settings = (LinearLayout) layout
				.findViewById(R.id.navigation_settings);
		mPopuwindow = new PopupWindow(layout,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		ColorDrawable cd = new ColorDrawable(-0000);
		mPopuwindow.setBackgroundDrawable(cd);
		mPopuwindow.setBackgroundDrawable(cd);

		mPopuwindow.setOutsideTouchable(true);
		mPopuwindow.setFocusable(true);
		mPopuwindow.showAsDropDown(v);
		setPopuListener();
	}

	private void setPopuListener() {
		// TODO Auto-generated method stub

		brightness_slider
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					/**
					 * 拖动条停止拖动的时候调用
					 */
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {

					}
					/**
					 * 拖动条开始拖动的时候调用
					 */
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}
					/**
					 * 拖动条进度改变的时候调用
					 */
					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						setScreenBrightness(progress);
					}
				});
		dayornightSwitch
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							myFBReaderApp.runAction(
									ActionCode.SWITCH_TO_NIGHT_PROFILE,
									new SwitchProfileAction(myFBReaderApp,
											ColorProfile.NIGHT));
							Toast.makeText(CoreReadActivity.this, "夜间模式开启",
									Toast.LENGTH_SHORT).show();
							Message message = new Message();
							message.what = NIGHT_UPDATEUI;
							mHandler.sendMessage(message);
							isNight = true;
						} else {
							myFBReaderApp.runAction(
									ActionCode.SWITCH_TO_DAY_PROFILE,
									new SwitchProfileAction(myFBReaderApp,
											ColorProfile.DAY));
							Toast.makeText(CoreReadActivity.this, "白天模式开启",
									Toast.LENGTH_SHORT).show();
							Message message = new Message();
							message.what = DAY_UPDATEUI;
							mHandler.sendMessage(message);
							isNight = false;
						}
					}
				});
		navigation_settings.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPopuwindow != null && mPopuwindow.isShowing()) {
					mPopuwindow.dismiss();
				}
				((NavigationPopup) myFBReaderApp
						.getPopupById(NavigationPopup.ID)).runNavigation();
			}
		});
		if (myFBReaderApp.getColorProfileName() != null
				&& myFBReaderApp.getColorProfileName().equals(
						ColorProfile.NIGHT)) {
			isNight = true;
		} else {
			isNight = false;
		}

	}
}