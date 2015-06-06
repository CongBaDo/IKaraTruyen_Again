package com.ikaratruyen.model;

import java.util.ArrayList;


public class GetHotAppsRequest {
	public String userId;
	public String platform; //ANDROID, IOS, WINDOWSPHONE
	public String language;
	public ArrayList<String> exclusions; //các bundle id bị loại trừ kh�?i kết quả nếu có
	//danh sách này tạo ra khi ngư�?i dùng nhấn No, Thanks 
	//hoặc em kiểm tra ứng dụng đã được cài thì ghi nhớ lại, 
	//để lần sau gửi lên sẽ loại những ứng dụng này ra
}
