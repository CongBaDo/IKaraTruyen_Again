package com.ikaratruyen.model;

import java.io.Serializable;


public class SearchBooksRequest implements Serializable{
	public String language;
	public String userId;
	public String keyword;
	public String cursor;
}
