package com.ikaratruyen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Book implements Serializable{
	public String _id;
	public String title;
	public String shortDescription;
	public String author;
	public ArrayList<Genre> genres;
	public String thumbnailUrl;
	public Long rateCounter;
	public Long totalRate;
	public Double averateRate;
	public String source; 
	public String status;
	public Long viewCounter;
	public ArrayList<Chapter> chapters;
	public Long lastChapter;
	public Date lastUpdateTime;
	public Long lastVolume;
	
	public String savedTime;
}
