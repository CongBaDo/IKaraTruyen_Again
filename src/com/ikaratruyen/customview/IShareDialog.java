package com.ikaratruyen.customview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.ikaratruyen.R;

public class IShareDialog implements OnClickListener {

	public enum SHARE {EMAIL, FACEBOOK, COPYLINK};
	private static final String TAG = "IShareDialog";
	private Activity context;
	private Dialog dialog;
	private int SCW, SCH;
	private String title, message, subject;

	public interface IShareDialogListener {
		public void dismissListener();
		public void shareCallback(SHARE shareType);
	}
	
	private IShareDialogListener callback;

	public IShareDialog(Activity context, IShareDialogListener callback) {
		this.context = context;
		this.callback = callback;
		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		SCH = metrics.heightPixels;
		SCW = metrics.widthPixels;
	}
	
	public void setContent(String message, String subject){
		this.subject = subject;
		this.message = message;
	}

	public void showRadialDialog() {

		dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View v = mInflater.inflate(R.layout.dialog_share, null, false);
		dialog.setContentView(v);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

		TextView facebook = (TextView) v.findViewById(R.id.tv_facebook);
		TextView email = (TextView) v.findViewById(R.id.tv_email);
		TextView copyLink = (TextView) v.findViewById(R.id.tv_copy_link);
		TextView cancel = (TextView) v.findViewById(R.id.tv_cancel);

		facebook.setOnClickListener(this);
		email.setOnClickListener(this);
		copyLink.setOnClickListener(this);
		cancel.setOnClickListener(this);

		WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

		wmlp.gravity = Gravity.BOTTOM | Gravity.CENTER;
		dialog.show();
		loadAnimation(v);

	}

	@SuppressLint("NewApi")
	private void loadAnimation(View mainV) {
		if (mainV == null) {
			return;
		}
		mainV.setAlpha(1f);
		float h = mainV.getHeight();
		float w = mainV.getWidth();
		float x = mainV.getX();
		float y = mainV.getY();

		mainV.setY(h);

		ViewPropertyAnimator vpa = mainV.animate().x(x).y(y);

		vpa.setDuration(500);
		vpa.setInterpolator(new OvershootInterpolator());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_facebook:
			dialog.dismiss();
			callback.shareCallback(SHARE.FACEBOOK);
			break;

		case R.id.tv_email:
			dialog.dismiss();
			callback.shareCallback(SHARE.EMAIL);
			break;

		case R.id.tv_copy_link:
			Toast.makeText(context, "You have just copy the link ", Toast.LENGTH_SHORT).show();
			callback.shareCallback(SHARE.COPYLINK);
			dialog.dismiss();
			break;

		case R.id.tv_cancel:
			dialog.dismiss();
			break;

		default:
			break;
		}
	}
	
	
}
