package com.ikaratruyen.model;

import java.io.Serializable;
import java.util.ArrayList;

public class NewBooksResponse implements Serializable{
	public ArrayList<Book> books;
	public String cursor;
}