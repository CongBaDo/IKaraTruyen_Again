package com.ikaratruyen.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.ikaratruyen.callback.OnclickMenuListener;
import com.ikaratruyen.model.Genre;
import com.ikaratruyen.model.ItemMenu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ikaratruyen.R;

public class IMenuAdapter extends BaseAdapter implements OnCheckedChangeListener {

	String Tag = "MenuAdapter";
	List<ItemMenu> data;
	private OnclickMenuListener listener;
	
	private static final int HEADER_TYPE_ROW = 0;
	private static final int ACTION_TYPE_ROW = 1;
	private static final int ACTION_TYPE_ROW1 = 2;
	
	
	public static final String MYACCOUNT ="My Account";
	public static final String SUBSCRIBERCODE ="NSTORE Code";
	public static final String SIGNOUT ="Log Out";
	public static final String MYLIBRARY ="My Library";
	public static final String SIGNIN ="Log In";
	public static final String CREATEACCOUNT ="Register";
	public static final String FORGOTPASSWORD ="Reset Password";
	public static final String BUGREPORT ="Feedback";
	public static final String FEATUREREQUEST ="Feature Request";
	public static final String GENERALINQUIRY ="General Inquiry";
	public static String ABOUTNSTORE ="About NSTORE";
	public static final String TERMOFSERVICE ="Terms Of Service";
	public static final String PRIVACY ="Privacy Policy";
	public static final String CHANGESTORE ="Change Store";
	public static final String SHOWUSERGUIDE ="Show User Guide";
	public static final String UPGRAGEAPP ="Update App";
	
	public static final String LIKEFACEBOOK ="Like us on Facebook";
	public static final String FOLLOWTWITTER ="Follow us on Twitter";
	public static final String USECELLULAR ="Use Cellular Network";
	
	public static final String CONTACTUS ="Contact Us";
	public static final String FAQS ="FAQs";
	public static final String HOWTOBUY ="How to Buy";
	
	String infoText="";
	
	public String textCurrentSelected;
	private Context context;
	private ArrayList<Genre> listGenres;
	
	public ArrayList<Genre> getTitles(){
		
		Genre item = new Genre(); 
		
		item.name = context.getResources().getString(R.string.title_tien_hiep);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/tien_hiep.jpg";
		item._id = context.getResources().getString(R.string.title_tien_hiep);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_kiem_hiep);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/kiem_hiep.jpg";
		item._id  = context.getResources().getString(R.string.title_kiem_hiep);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_ngon_tinh);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/ngon_tinh.jpg";
		item._id =  context.getResources().getString(R.string.title_ngon_tinh);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_teen);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/truyen_teen.jpg";
		item._id =  context.getResources().getString(R.string.title_the_teen);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_do_thi);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/do_thi.jpg";
		item._id =  context.getResources().getString(R.string.title_the_do_thi);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_quansu);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/quan_su.jpg";
		item._id =  context.getResources().getString(R.string.title_the_quansu);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_lichsu);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/lich_su.jpg";
		item.name = context.getResources().getString(R.string.title_the_lichsu);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_xuyenkhong);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/xuyen_khong.jpg";
		item._id =  context.getResources().getString(R.string.title_the_xuyenkhong);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_digioi);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/di_gioi.jpg";
		item._id =  context.getResources().getString(R.string.title_the_digioi);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_vongdu);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/vong_du.jpg";
		item._id =  context.getResources().getString(R.string.title_the_vongdu);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_truyenma);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/truyen_ma.jpg";
		item._id =  context.getResources().getString(R.string.title_the_truyenma);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_trinhtham);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/trinh_tham.jpg";
		item._id =  context.getResources().getString(R.string.title_the_trinhtham);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_khoahuyen);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/khoa_huyen.jpg";
		item._id =  context.getResources().getString(R.string.title_the_khoahuyen);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_huyenhuyen);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/huyen_huyen.jpg";
		item._id =  context.getResources().getString(R.string.title_the_huyenhuyen);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_dinang);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/di_nang.jpg";
		item._id = context.getResources().getString(R.string.title_the_dinang);
		listGenres.add(item);
		
		item = new Genre();
		item.name = context.getResources().getString(R.string.title_the_tieuthuyet);
		item._id = context.getResources().getString(R.string.title_the_tieuthuyet);
		item.thumbnailImage = "http://truyen4m.appspot.com/images/genres/tieu_thuyet.jpg";
		listGenres.add(item);
		
		return listGenres;
	}
	
	public void updateTheloai(ArrayList<Genre> genres){
		listGenres.clear();
		listGenres = genres;
		data.clear();
		createMenu();
		notifyDataSetChanged();
	}
	
	private void createMenu(){
		data = new ArrayList<ItemMenu>();
		ItemMenu obj;
		obj = new ItemMenu();
		obj.text = "";
		obj.type = 0;
		obj.draw = null;
		data.add(obj);
		
		obj = new ItemMenu();
		obj.text = context.getResources().getString(R.string.title_sach_vua_doc);
		obj.draw = context.getResources().getDrawable(R.drawable.icon_library);
		obj.id = context.getResources().getString(R.string.title_sach_vua_doc);
		obj.type = 1;
		data.add(obj);
		
		obj = new ItemMenu();
		obj.text = context.getResources().getString(R.string.title_sach_moi);
		obj.draw = context.getResources().getDrawable(R.drawable.icon_library);
		obj.id = context.getResources().getString(R.string.title_sach_moi);
		obj.type = 1;
		data.add(obj);
		
		obj = new ItemMenu();
		obj.text = context.getResources().getString(R.string.title_tu_sach);
		obj.draw = context.getResources().getDrawable(R.drawable.icon_shelf);
		obj.id = context.getResources().getString(R.string.title_tu_sach);
		obj.type = 1;
		data.add(obj);
		
		obj = new ItemMenu();
		obj.text = context.getResources().getString(R.string.title_the_loai);
		obj.type = 0;
		data.add(obj);
		
		for(int i = 0; i < listGenres.size(); i++){
			obj = new ItemMenu();
			obj.text = listGenres.get(i).name;
			obj.thumbUrl = listGenres.get(i).thumbnailImage;
			obj.id = listGenres.get(i)._id;
			obj.marketLink = listGenres.get(i).marketLink;
			obj.type = 1;
			
//			Log.e(Tag, "KINE "+listGenres.get(i).name);
			data.add(obj);
		}
		
		obj = new ItemMenu();
		obj.text = context.getResources().getString(R.string.title_hotro);
		obj.id = context.getResources().getString(R.string.title_hotro);
		obj.type = 0;
		data.add(obj);
		
		
		obj = new ItemMenu();
		obj.text = context.getResources().getString(R.string.title_ungdunghay);
		obj.type = 1;
		obj.id = context.getResources().getString(R.string.title_ungdunghay);
		obj.draw = context.getResources().getDrawable(R.drawable.icon_good_app);
		data.add(obj);
		
		obj = new ItemMenu();
		obj.text = context.getResources().getString(R.string.title_trogiup);
		obj.draw = context.getResources().getDrawable(R.drawable.icon_help);
		obj.type = 1;
		obj.id = context.getResources().getString(R.string.title_trogiup);
		data.add(obj);
		
		obj = new ItemMenu();
		obj.text = context.getResources().getString(R.string.title_thoat);
		obj.draw = context.getResources().getDrawable(R.drawable.icon_thoat);
		obj.id = context.getResources().getString(R.string.title_thoat);
		obj.type = 1;
		data.add(obj);
	}
	
	public IMenuAdapter(Context context, OnclickMenuListener listener){
		super();
		this.context = context;
		listGenres = new ArrayList<Genre>();
		this.listener = listener;
		getTitles();
		createMenu();
	}
	
	@Override
	public int getCount() {
		if (data!=null)
			return data.size();
		
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	public List<ItemMenu> getData() {
		return data;
	}

	public void setData(List<ItemMenu> data) {
		this.data = data;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public int getViewTypeCount() {
		return 3;
	}
	
	@Override
	public int getItemViewType(int position) {
		return data.get(position).type;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		ViewHolder holder;
		ViewHolder2 holder2;
		ViewHolder3 holder3;
		
		int type = getItemViewType(position);
		
		if (convertView==null){
			holder = new ViewHolder();
			holder2 = new ViewHolder2();
			holder3 = new ViewHolder3();
			
			switch (type) {
			case 0:
				convertView = inflater.inflate(R.layout.view_section_header, parent, false);
				holder.holderText = (TextView) convertView.findViewById(R.id.tv_sectionHeader);
				convertView.setTag(R.layout.view_section_header, holder);
				break;
				
			case 1:
				convertView = inflater.inflate(R.layout.list_slideview_item, parent, false);
				holder2.holder2Text = (TextView) convertView.findViewById(R.id.tv_Title);
				holder2.imageView  = (ImageView) convertView.findViewById(R.id.img_Indicator);
				convertView.setTag(R.layout.list_slideview_item, holder2);
				break;
				
			default:
				break;
			}
			
		}else{
			holder = (ViewHolder) convertView.getTag(R.layout.view_section_header);
			holder2 = (ViewHolder2) convertView.getTag(R.layout.list_slideview_item);
		}
		
		switch (type) {
		case 0:
			holder.holderText.setText(data.get(position).text);
			
			break;
			
		case 1:
			holder2.holder2Text.setText(data.get(position).text);
			
			if(data.get(position).draw != null){
				holder2.imageView.setImageDrawable(data.get(position).draw);
			}else{
				
				Log.v(Tag, "image "+data.get(position).thumbUrl+" "+data.get(position).id);
				ImageLoader.getInstance().displayImage(data.get(position).thumbUrl, holder2.imageView);
			}
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					listener.itemClick(data.get(position).id, position);
				}
			});

			break;
			
		default:
			break;
		}
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView holderText;
		ImageView imageView;
	}
	
	static class ViewHolder2{
		TextView holder2Text;
		ImageView imageView;
//		FontAwesomeText iconFont;
	}
	
	static class ViewHolder3{
		TextView holder3Text;
		Switch switchBtn;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		Switch switchBtn = (Switch) buttonView;
		
//		if (switchBtn.getTag() == USECELLULAR){
////			context.getServerAPI().setEnableCellular(isChecked);
//		}
//		
//		if (switchBtn.getTag() == SHOWUSERGUIDE && isChecked){
////			context.getMainActivity().forceShowUserGuide();
//		}
	}
}
