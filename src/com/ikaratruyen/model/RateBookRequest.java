package com.ikaratruyen.model;

import java.io.Serializable;


public class RateBookRequest implements Serializable{
	public String language;
	public String userId;
	public String bookId;
	public Long rate;
}
