package com.ikaratruyen.model;

import java.io.Serializable;
import java.util.Date;

public class Chapter implements Serializable{
	public String _id;
	public Long number;
	public String title;
	public Date updateTime;
	public String content;
	public Long volume;
	public boolean check;
	public boolean downloaded;
}
