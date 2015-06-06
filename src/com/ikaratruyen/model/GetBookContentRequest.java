package com.ikaratruyen.model;

import java.io.Serializable;

public class GetBookContentRequest implements Serializable {
	public String language;
	public String userId;
	public String bookId;
}