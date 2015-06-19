package com.ikaratruyen.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ikaratruyen.adapter.ChapAdapter;
import com.ikaratruyen.customview.IShareDialog;
import com.ikaratruyen.customview.IShareDialog.IShareDialogListener;
import com.ikaratruyen.customview.IShareDialog.SHARE;
import com.ikaratruyen.model.Book;
import com.ikaratruyen.model.Chapter;
import com.ikaratruyen.model.GetBookRequest;
import com.ikaratruyen.model.GetBookResponse;
import com.ikaratruyen.model.RateBookRequest;
import com.ikaratruyen.model.RateBookResponse;
import com.ikaratruyen.request.IGetBookRequest;
import com.ikaratruyen.request.IGetBookRequest.IGetBookPostCallBack;
import com.ikaratruyen.request.IRatingRequest;
import com.ikaratruyen.request.IRatingRequest.RateCallBack;
import com.ikaratruyen.utils.DownloadService;
import com.ikaratruyen.utils.IKaraDbHelper;
import com.ikaratruyen.utils.ISettings;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yamin.reader.activity.CoreReadActivity;
import com.ikaratruyen.IApplication;
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
public class IBookDetailActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	private static final String TAG = "IBookDetailActivity";
	public static final int DEVICE_VERSION   = Build.VERSION.SDK_INT;
	public static final int DEVICE_HONEYCOMB = Build.VERSION_CODES.HONEYCOMB;
	private ProgressBar barDownload;
	private ListView listView;
	private ChapAdapter adapter;
	private ArrayList<Chapter> chapList;
	private LinearLayout headerBar;
	private TextView tvLike;
	private TextView tvRateValue;
	private ImageView imgLike;
	private static final String PERMISSION = "publish_actions";
    private boolean canPresentShareDialog;
    private CallbackManager callbackManager;
	private Book itemBook;
	private RatingBar rateBar;
	private Button butRead;
	private ProgressDialog dialogLoading;
	 private ShareDialog shareDialog;
	 
	 private MyResultReceiver resultReceiver;
	private Intent intent;
	private class MyResultReceiver extends ResultReceiver
		{
			public MyResultReceiver(Handler handler) {
				super(handler);
			}

			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				runOnUiThread(new UpdateUI(resultData.getInt("progress")));
			}
		}
	
	class UpdateUI implements Runnable{
		int progress;
		
		public UpdateUI(int progress) {
			this.progress = progress;
		}
		public void run() {
			float percent = (float)progress*100/chapList.size();
			if(percent == 100){
				stopService(intent);
				butRead.setText(getResources().getString(R.string.title_read));
			}
			
			barDownload.setProgress((int)percent);
		}
	}
	 
	private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
		@Override
		public void onCancel() {
			Log.d("HelloFacebook", "Canceled");
		}

		@Override
		public void onError(FacebookException error) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onSuccess(Sharer.Result result) {
			Log.d("HelloFacebook", "Success!");
			if (result.getPostId() != null) {
			}
		}
	};

	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	private PendingAction pendingAction = PendingAction.NONE;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_book_detail);
		callbackManager = CallbackManager.Factory.create();
		resultReceiver = new MyResultReceiver(null);
		
		itemBook = new Book();
		
		headerBar = (LinearLayout)findViewById(R.id.top_bar);
		tvLike = (TextView)findViewById(R.id.tv_like);
		imgLike = (ImageView)findViewById(R.id.img_like);
		butRead = (Button)findViewById(R.id.but_read);
		butRead.setOnClickListener(this);
		
		tvLike.setOnClickListener(this);
		imgLike.setOnClickListener(this);
		
		barDownload = (ProgressBar)findViewById(R.id.prg_download);
		barDownload.setMax(100);
		
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)){ 
			int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
			headerBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, actionBarHeight));
		} 
		
		headerBar.setGravity(Gravity.CENTER_VERTICAL);
		Log.i(TAG, "onCreate ");
		
		shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);

		listView = (ListView) findViewById(R.id.list_chap);
		listView.setOnItemClickListener(this);
		((ImageView)findViewById(R.id.img_back)).setOnClickListener(this);
		((ImageView)findViewById(R.id.img_share)).setOnClickListener(this);
		
		itemBook.title = getIntent().getExtras().getString("book_title");
		itemBook.author = getIntent().getExtras().getString("book_author");
		itemBook.thumbnailUrl = getIntent().getExtras().getString("book_thumb");
		itemBook.viewCounter = getIntent().getExtras().getLong("book_view");
		itemBook._id = getIntent().getExtras().getString("book_id");
		itemBook.totalRate = getIntent().getExtras().getLong("book_totalrate");
		itemBook.rateCounter = getIntent().getExtras().getLong("book_ratecount");
		itemBook.status = getIntent().getExtras().getString("book_status");

		((TextView) findViewById(R.id.tv_title)).setText(itemBook.title);
		((TextView) findViewById(R.id.tv_author)).setText(itemBook.author);
		((TextView) findViewById(R.id.tv_count_view)).setText(itemBook.viewCounter + "");
		ImageLoader.getInstance().displayImage(itemBook.thumbnailUrl,
				((ImageView) findViewById(R.id.img_thumb_detail)));
		
		((ImageView)findViewById(R.id.img_share)).setBackgroundResource(R.drawable.view_state_share_button);
	
		float rateValue = getIntent().getExtras().getFloat("book_rate");
		
		((LinearLayout)findViewById(R.id.view_rate)).setOnClickListener(this);
		rateBar = (RatingBar)findViewById(R.id.rate_bar);
		setRateValue(rateValue);
		
		if(IKaraDbHelper.getInstance(getApplicationContext()).isFavorite(itemBook._id)){
			imgLike.setBackgroundResource(R.drawable.icon_heart_like);
			tvLike.setText(getResources().getString(R.string.title_dislike));
		}else{
			imgLike.setBackgroundResource(R.drawable.icon_heart);
			tvLike.setText(getResources().getString(R.string.title_like));
		}
		
		getBookValue(true);
		
		Log.i(TAG, "onCreate "+itemBook._id);
		canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	private void showLoading(){
		dialogLoading = new ProgressDialog(IBookDetailActivity.this);
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
	
	private void getBookValue(final boolean begin){
		
		GetBookRequest request = new GetBookRequest();
		request.bookId = itemBook._id;
		request.language = "vi";

		new IGetBookRequest(new IGetBookPostCallBack() {

			@Override
			public void onResultIGenresPostPost(GetBookResponse statusObj) {
				// TODO Auto-generated method stub
				// statusObj.book.
				Log.v(TAG, "ALo Checl rating "+statusObj.book.totalRate+" "+statusObj.book.rateCounter+" "+statusObj.book.averateRate);
				findViewById(R.id.but_read).setEnabled(true);
				
				if(begin){
					itemBook.shortDescription = statusObj.book.shortDescription;
					//barDownload.setVisibility(View.GONE);
					((TextView)findViewById(R.id.tv_read_count)).setText(statusObj.book.chapters.size()+"");
					((TextView) findViewById(R.id.tv_description)).setText(statusObj.book.shortDescription);
					((TextView) findViewById(R.id.tv_description)).setBackgroundColor(Color.WHITE);
					chapList = statusObj.book.chapters;
					
					if(!IKaraDbHelper.getInstance(IBookDetailActivity.this).isTableExists(itemBook)){
						IKaraDbHelper.getInstance(IBookDetailActivity.this).createBookTableFollowId(itemBook._id);
					}
					
					for(int i = 0; i < chapList.size(); i++){
						chapList.get(i).check = false;
						chapList.get(i).index = i;
					}
					
					adapter = new ChapAdapter(getApplicationContext(), chapList);
					listView.setAdapter(adapter);
					
					ISettings.getInstance().setChapListContents(chapList);
					
					ArrayList<Chapter> downloadedRows = IKaraDbHelper.getInstance(IApplication.getInstance().getApplicationContext()).getAllChapter(itemBook._id);
					
					float percent = (float)downloadedRows.size()*100/chapList.size();
					if(percent == 100){
						butRead.setText(getResources().getString(R.string.title_read));
					}
					
					barDownload.setProgress((int)percent);
				}
			}

			@Override
			public void fail() {
				// TODO Auto-generated method stub

			}
		}, request).execute();
	}
	
	private void setRateValue(float rateValue){
		if(Float.isNaN(rateValue)){
			Log.w(TAG, "rateValue "+rateValue);
			((TextView) findViewById(R.id.tv_rate)).setText("0.0");//book_rate
		}else{
			((TextView) findViewById(R.id.tv_rate)).setText(rateValue+"");//book_rate
		}
		rateBar.setRating(rateValue);
	}
	
	private void rateBook(){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(IBookDetailActivity.this);
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_rating, null);
		dialogBuilder.setView(dialogView);
		 
		RatingBar rate = (RatingBar) dialogView.findViewById(R.id.rate_dialog);
		tvRateValue = (TextView)dialogView.findViewById(R.id.tv_rate_value);
		rate.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				// TODO Auto-generated method stub
				tvRateValue.setText((int)rating+"");
			}
		});
		
		dialogBuilder.setPositiveButton("Xong", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				RateBookRequest request = new RateBookRequest();
				request.bookId = itemBook._id;
				request.language = "vi";
				request.rate = Long.parseLong(tvRateValue.getText().toString());
				
				if(!IKaraDbHelper.getInstance(IBookDetailActivity.this).existInRatingTable(itemBook._id) && request.rate > 0){
					IKaraDbHelper.getInstance(IBookDetailActivity.this).addToRatingTable(itemBook._id);
					new IRatingRequest(new RateCallBack() {
						
						@Override
						public void onRatePost(RateBookResponse statusObj) {
							// TODO Auto-generated method stub
							Log.e(TAG, "JSON Rating "+statusObj.book.averateRate+" "+statusObj.book.totalRate+" "+itemBook.rateCounter);
							DecimalFormat df = new DecimalFormat();
							df.setMaximumFractionDigits(1);
							float bookRate = 0.0f;
							if(statusObj.book.rateCounter != null && itemBook.rateCounter != 0){
								bookRate = Float.parseFloat(df.format(itemBook.totalRate/itemBook.rateCounter));
								setRateValue(bookRate);
							}
						}
						
						@Override
						public void fail() {
							// TODO Auto-generated method stub
							
						}
					}, request).execute();
				}else{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_voted), Toast.LENGTH_SHORT).show();
				}
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_vote)+" "+request.rate+" sao", Toast.LENGTH_SHORT).show();
			}
		});
		
		dialogBuilder.setNegativeButton(getResources().getString(R.string.dialog_thoi), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		
		
		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();
	}
	
	private void handlePendingAction() {
        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_PHOTO:
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
		Intent intent = new Intent(getApplicationContext(), CoreReadActivity.class);
		intent.putExtra("book_title", itemBook.title);
		intent.putExtra("chap_id", chapList.get(position)._id);
		intent.putExtra("chap_title", chapList.get(position).title);
		intent.putExtra("book_id", itemBook._id);
		intent.putExtra("chap_index", position);
		startActivity(intent);
		
		IKaraDbHelper.getInstance(getApplicationContext()).addToJustRead(itemBook);
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		
		//Log.v(TAG, "stopNewService");
		Intent intent = new Intent(getApplicationContext(), DownloadService.class);
		stopService(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			
				//Log.v(TAG, "stopNewService");
			Intent serviceInten = new Intent(getApplicationContext(), DownloadService.class);
			stopService(serviceInten);
			break;
			
		case R.id.view_rate:
            // TODO perform your action here
			if(IKaraDbHelper.getInstance(IBookDetailActivity.this).existInRatingTable(itemBook._id)){
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_voted), Toast.LENGTH_SHORT).show();
			}else{
				rateBook();
			}
			break;
			
		case R.id.img_share:
			IShareDialog dialog = new IShareDialog(IBookDetailActivity.this, new IShareDialogListener() {
				
				@SuppressLint("NewApi") @Override
				public void shareCallback(SHARE shareType) {
					// TODO Auto-generated method stub
					if(shareType == SHARE.EMAIL){
						String message =  
								getResources().getString(R.string.share_email_message_mot)+ 
								itemBook.title+ 
								getResources().getString(R.string.share_email_message_hai)+
								itemBook.author+
								getResources().getString(R.string.share_email_message_ba)+
								"\n"+
								"http://www.ikaratruyen.com/book.jsp?bookId="+itemBook._id;
						sendGmail(getResources().getString(R.string.share_email_subject), message);
					}else if(shareType == SHARE.FACEBOOK){
						onClickPostStatusUpdate();
					}else if(shareType == SHARE.COPYLINK){
						ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
						ClipData clip = ClipData.newPlainText("label", "http://www.ikaratruyen.com/book.jsp?bookId="+itemBook._id);
						clipboard.setPrimaryClip(clip);
					}
				}
				
				@Override
				public void dismissListener() {
					// TODO Auto-generated method stub
					
				}
			});
			dialog.showRadialDialog();
			break;
			
		case R.id.but_read:
			
			ArrayList<Chapter> downloadedRows = IKaraDbHelper.getInstance(IApplication.getInstance().getApplicationContext()).getAllChapter(itemBook._id);
			if(downloadedRows.size() == chapList.size()){
				Intent intent = new Intent(getApplicationContext(), CoreReadActivity.class);
				intent.putExtra("book_title", itemBook.title);
				intent.putExtra("chap_id", chapList.get(0)._id);
				intent.putExtra("chap_title", chapList.get(0).title);
				intent.putExtra("book_id", itemBook._id);
				intent.putExtra("chap_index", 0);
				intent.putExtra("open_book", true);
				startActivity(intent);
				IKaraDbHelper.getInstance(getApplicationContext()).addToJustRead(itemBook);
			}else{
				
				CharSequence[] options = new CharSequence[]{ getResources().getString(R.string.title_read_online), "Download" };
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setCancelable(true);
		        builder.setTitle(getResources().getString(R.string.title_dialog_chon));
		        builder.setItems(options, new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int item) {
		                if (item == 0){
		                	Intent intent = new Intent(getApplicationContext(), CoreReadActivity.class);
		    				intent.putExtra("book_title", itemBook.title);
		    				intent.putExtra("chap_id", chapList.get(0)._id);
		    				intent.putExtra("chap_title", chapList.get(0).title);
		    				intent.putExtra("book_id", itemBook._id);
		    				intent.putExtra("chap_index", 0);
		    				intent.putExtra("open_book", false);
		    				startActivity(intent);
		    				IKaraDbHelper.getInstance(getApplicationContext()).addToJustRead(itemBook);
		                }else if (item == 1){
//		                	barDownload.setVisibility(View.VISIBLE);
		                	intent = new Intent(IBookDetailActivity.this, DownloadService.class);
		                	intent.putExtra("book_id", itemBook._id);
		                	intent.putExtra("book_title", itemBook.title);
		            		intent.putExtra("receiver", resultReceiver);
		            		startService(intent);
		                } 
		            }
		        });
		        
		        builder.setNegativeButton(getResources().getString(R.string.dialog_thoi), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				});
		        builder.show();
			}
			
			break;
			
		case R.id.img_like:
		case R.id.tv_like:
			
			if(IKaraDbHelper.getInstance(getApplicationContext()).isFavorite(itemBook._id)){
				Log.i(TAG, "remove favor "+itemBook._id);
				IKaraDbHelper.getInstance(getApplicationContext()).removeFavorite(itemBook._id);
				imgLike.setBackgroundResource(R.drawable.icon_heart);
				tvLike.setText(getResources().getString(R.string.title_like));
			}else{
				
				Log.e(TAG, "add favor "+itemBook._id);
				IKaraDbHelper.getInstance(getApplicationContext()).addToFavorTable(itemBook);
				imgLike.setBackgroundResource(R.drawable.icon_heart_like);
				tvLike.setText(getResources().getString(R.string.title_dislike));
			}

		default:
			break;
		}
	}
	
	private void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }
	
    private void postStatusUpdate() {
        Profile profile = Profile.getCurrentProfile();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(itemBook.title)
                .setContentDescription(itemBook.shortDescription)
                .setContentUrl(Uri.parse("http://www.ikaratruyen.com/"))
                .setImageUrl(Uri.parse(itemBook.thumbnailUrl))
                .build();
        if (canPresentShareDialog) {
            shareDialog.show(linkContent);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("publish_actions");
    }

    private void performPublish(PendingAction action, boolean allowNoToken) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            pendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else {
                // We need to get new permissions, then complete the action when we get called back.
                LoginManager.getInstance().logInWithPublishPermissions(
                        this,
                        Arrays.asList(PERMISSION));
                return;
            }
        }

        if (allowNoToken) {
            pendingAction = action;
            handlePendingAction();
        }
    }
    
    private void sendGmail(String subject, String text) {
		Intent gmailIntent = new Intent();
		gmailIntent.setClassName("com.google.android.gm",
				"com.google.android.gm.ComposeActivityGmail");
		gmailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		gmailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
		gmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
		try {
			startActivity(gmailIntent);
		} catch (ActivityNotFoundException ex) {
			// handle error
		}
	}
    
    
}
