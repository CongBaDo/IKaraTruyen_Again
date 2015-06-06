package com.ikaratruyen.model;

import java.io.Serializable;


public class TopBooksRequest implements Serializable{
	public String language;
	public String userId;
	public String genreId;
	public String cursor;
}
