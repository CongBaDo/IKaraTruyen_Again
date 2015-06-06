package com.ikaratruyen.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ikaratruyen.model.Book;
import com.ikaratruyen.model.Chapter;

public class IKaraDbHelper extends SQLiteOpenHelper {

	private static final String TAG = "IKaraDbHelper";

	private static IKaraDbHelper instance = null;

	private static final String DB_NAME = "IKara.db";
	private static final int DB_VERSION = 1;

	private static final String FAVOR_TABLE 			= "favor_table";
	private static final String TABLE_JUST_READ 		= "just_read_table";
	private static final String TABLE_SAVED_INDEX		= "table_saved_index";
	private static final String TABLE_VIEW_COUNTER		= "table_view_counter";
	private static final String TABLE_RATING			= "table_rating";

	private static final String FAV_ID 						= "fav_id";
	private static final String FAV_ID_TITLE 				= "fav_id_title";
	private static final String FAV_TITLE 					= "fav_title";
	private static final String FAV_DES 					= "fav_des";
	private static final String FAV_AUTHOR 					= "fav_author";
	private static final String FAV_THUMB 					= "fav_thumb";
	private static final String FAV_RATE_COUNT				= "fav_rate_count";
	private static final String FAV_TOTAL_RATE 				= "fav_total_rate";
	private static final String FAV_AVERAGE 				= "fav_average";
	private static final String FAV_SOURCE 					= "fav_source";
	private static final String FAV_STATUS 					= "fav_status";
	private static final String FAV_VIEW_COUNTER 			= "fav_view_count";
	private static final String FAV_LAST_CHAPTER 			= "fav_last_chapter";
	private static final String FAV_SAVE_TIME 				= "fav_save_time";
	private static final String FAV_LAST_VOLUME 			= "fav_last_volume";
	
	private static final String VC_ID						= "vc_id";
	private static final String VC_BOOK_ID					= "vc_bookId";
	
	private static final String RATING_ID					= "rating_id";
	private static final String RATING_BOOK_ID				= "rating_book_id";
	
	private static final String SAVED_INDEX_ID				= "saved_index_id";
	private static final String SAVED_INDEX_BOOK_ID			= "saved_index_bookid";
	private static final String SAVED_INDEX_INFO			= "saved_index_info";
	
	private static final String PREFIX_TABLE				= "table_book_";
	private static final String COL_ID						= "id";
	private static final String COL_CHAP_ID					= "chap_id";
	private static final String COL_CHAP_TITLE				= "chap_title";
	private static final String COL_CHAP_CONTENT			= "chap_content";
	private static final String COL_CHAP_DOWNLOAD			= "chap_downloaded";
	

	public  static IKaraDbHelper getInstance(Context context) {
		if (instance == null) {
			instance = new IKaraDbHelper(context);
		}

		return instance;
	}

	private IKaraDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		Log.v(TAG, "IKaraDbHelper");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "onCreate SQLiteDatabase");
		createFavorTable(db);
		createJustReadTable(db);
		createVC(db);
		createRatingTable(db);
		createSavedIndexTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		Log.e(TAG, "onUpgrade ");
		onCreate(db);
	}
	
	/** hungnm - convert list to array **/
	private String[] convertListToArray(List<String> list) {
		String[] result = new String[list.size()];
		for(int i = 0; i < list.size(); i++) 
			result[i] = list.get(i);
		return result;
	}
	// ======================== MULTI USERS <S> ============================

	private void createFavorTable(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + FAVOR_TABLE + " (" 
					+ FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " 
					+ FAV_ID_TITLE + " TEXT , " 
					+ FAV_TITLE + " TEXT , "
					+ FAV_AUTHOR + " TEXT , " 
					+ FAV_AVERAGE + " REAL , " 
					+ FAV_DES + " TEXT , " 
					+ FAV_LAST_CHAPTER + " REAL , " 
					+ FAV_LAST_VOLUME + " REAL , " 
					+ FAV_SAVE_TIME + " TEXT , " 
					+ FAV_RATE_COUNT + " REAL , " 
					+ FAV_SOURCE + " TEXT , " 
					+ FAV_STATUS + " TEXT , " 
					+ FAV_THUMB + " TEXT , " 
					+ FAV_TOTAL_RATE + " REAL , " 
					+ FAV_VIEW_COUNTER + " REAL)");
//			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void createVC(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_VIEW_COUNTER + " (" 
					+ VC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " 
					+ VC_BOOK_ID + " TEXT )");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void addRowBookTableFollowId(Book book, Chapter item){
		
		String bookId = getBookId(book._id);
		
		ContentValues values = new ContentValues();
		values.put(COL_CHAP_CONTENT, "");
		values.put(COL_CHAP_ID, item._id);
		values.put(COL_CHAP_TITLE, item.title);
		values.put(COL_CHAP_DOWNLOAD, "0");
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.insert((PREFIX_TABLE+bookId), null, values);
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void addRowBookTableFollowId(String bookId, String chapContent){
		
		bookId = getBookId(bookId);
		
		ContentValues values = new ContentValues();
		values.put(COL_CHAP_CONTENT, chapContent);
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.insert((PREFIX_TABLE+bookId), null, values);
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public int getBookSize(String bookId) {
		bookId = getBookId(bookId);
		
		Log.e(TAG, "getBookSize "+PREFIX_TABLE+bookId);
		
			try {
				SQLiteDatabase db = this.getWritableDatabase();
				Cursor cursor = db.query((PREFIX_TABLE+bookId), null, null, null, null, null, COL_ID);
				int size = cursor.getCount();
				
				
				cursor.close();
				db.close();
				
				return size;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return 0;
	}
	
	public ArrayList<Chapter> getAllChapter(String bookId) {
		bookId = getBookId(bookId);
		
		Log.e(TAG, "getAllChapter "+PREFIX_TABLE+bookId);
		
		ArrayList<Chapter> items = new ArrayList<Chapter>();
			try {
				SQLiteDatabase db = this.getWritableDatabase();
				Cursor cursor = db.query((PREFIX_TABLE+bookId), null, null, null, null, null, COL_ID);
				if (cursor != null && cursor.moveToFirst()) {
					do {
						Chapter item = new Chapter();
						item._id = cursor.getString(cursor.getColumnIndex(COL_CHAP_ID));
						item.content = cursor.getString(cursor.getColumnIndex(COL_CHAP_CONTENT));
						if(cursor.getString(cursor.getColumnIndex(COL_CHAP_DOWNLOAD)).equals("1")){
							item.downloaded = true;
						}
						item.title = cursor.getString(cursor.getColumnIndex(COL_CHAP_TITLE));
						
						items.add(item);
					} while (cursor.moveToNext());
				}

				cursor.close();
				db.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		return items;
	}

	
//	public void updateRowBookTableFollowId(String bookId, Chapter item){
//		bookId = getBookId(bookId);
//		ContentValues values = new ContentValues();
//		values.put(COL_CHAP_CONTENT, item.content);
//		values.put(COL_CHAP_DOWNLOAD, "1");
//		try {
//			SQLiteDatabase db = this.getWritableDatabase();
//			db.update((PREFIX_TABLE+bookId), values, COL_CHAP_ID + "=?", new String[] { item._id });
//			db.close();
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//	}

	public String getChapContent(String bookId, int index){
		bookId = getBookId(bookId);
		
		String value = "";
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery("select * from " + (PREFIX_TABLE + bookId) , new String[] {});
			
			if (cursor != null) {
				cursor.moveToPosition(index);
				value = cursor.getString(cursor.getColumnIndex(COL_CHAP_CONTENT));
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return value;
	}
	
	public Chapter getChapterInfo(String bookId, String chapId) {
		
		bookId = getBookId(bookId);
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery("select * from " + (PREFIX_TABLE + bookId) + " where " + COL_CHAP_ID + "= ? ", new String[] { chapId});
			// result = cursor != null && cursor.getCount() > 0;
			if (cursor != null && cursor.moveToFirst()) {
				Chapter item = new Chapter();
				item._id = cursor.getString(cursor.getColumnIndex(COL_CHAP_ID));
				item.title = cursor.getString(cursor.getColumnIndex(COL_CHAP_TITLE));
				item.content =   cursor.getString(cursor.getColumnIndex(COL_CHAP_CONTENT));
				if(cursor.getString(cursor.getColumnIndex(COL_CHAP_DOWNLOAD)).equals("1")){
					item.downloaded = true;
				}
				return item;
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return null;
	}
	
	private String getBookId(String bookId){
		String value = bookId;
		
		value = value.replaceAll("/", "");
		value = value.replaceAll("-", "");
		
		if(value.length() <= 10){
			return value;
		}else{
			value = value.substring(0, 10);
		}
		return value;
	}

	public void createBookTableFollowId(String bookId) {
		Log.i(TAG, "createBookTableFollowId "+bookId);
		bookId = getBookId(bookId);
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + (PREFIX_TABLE+bookId) + " (" 
					+ COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " 
					+ COL_CHAP_CONTENT + " TEXT )"
//					+ COL_CHAP_ID + " TEXT , "
//					+ COL_CHAP_TITLE + " TEXT , "
//					+ COL_CHAP_DOWNLOAD + " TEXT )"
					);
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void createRatingTable(SQLiteDatabase db) {
		try {
			Log.v(TAG, "createRatingTable");
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RATING + " (" 
					+ RATING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " 
					+ RATING_BOOK_ID + " TEXT )");
//			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void createSavedIndexTable(SQLiteDatabase db) {
		try {
			Log.v(TAG, "createRatingTable");
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SAVED_INDEX + " (" 
					+ SAVED_INDEX_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " 
					+ SAVED_INDEX_BOOK_ID + " TEXT ,"
					+ SAVED_INDEX_INFO + " TEXT )");
//			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public ArrayList<String> getFavorIds() {

		ArrayList<String> list = new ArrayList<String>();
		try {

			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.query(FAVOR_TABLE, null, null, null, null, null, FAV_ID);
			if (cursor != null && cursor.moveToFirst()) {
				do {
					// Map<String, String> item = new HashMap<String, String>();
					list.add(cursor.getString(cursor.getColumnIndex(FAV_ID)));
				} while (cursor.moveToNext());

				cursor.close();
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}

	public String getIDTitle(String id) {
		String value = "";
		try {
			SQLiteDatabase db = this.getReadableDatabase();

			Cursor cursor = db.query(FAVOR_TABLE, new String[] { FAV_ID, FAV_ID_TITLE, FAV_DES, FAV_THUMB }, FAV_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
			if (cursor != null)
				cursor.moveToFirst();

			value = cursor.getString(3);

			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return value;
	}
	
	public String getSavedInfo(String bookId) {
		String value = "";
		try {
			SQLiteDatabase db = this.getReadableDatabase();

			Cursor cursor = db.query(TABLE_SAVED_INDEX, new String[] { SAVED_INDEX_ID, SAVED_INDEX_BOOK_ID, SAVED_INDEX_INFO }, SAVED_INDEX_BOOK_ID + "=?", new String[] { String.valueOf(bookId) }, null, null, null, null);
			if (cursor != null)
				cursor.moveToFirst();

			value = cursor.getString(2);

			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return value;
	}

	public int removeFromFavor(String id) {
		int result = -1;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			result = db.delete(FAVOR_TABLE, "id = ?", new String[] { id });
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	public void addToFavorTable(Book item) {

		ContentValues values = new ContentValues();
		values.put(FAV_ID_TITLE, item._id);
		values.put(FAV_AUTHOR, item.author);
		values.put(FAV_AVERAGE, item.averateRate);
		values.put(FAV_DES, item.shortDescription);
		values.put(FAV_LAST_CHAPTER, item.lastChapter);
		values.put(FAV_LAST_VOLUME, item.lastVolume);
		values.put(FAV_RATE_COUNT, item.rateCounter);
		values.put(FAV_SOURCE, item.source);
		values.put(FAV_STATUS, item.status);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		values.put(FAV_SAVE_TIME, dateFormat.format(date));
		values.put(FAV_THUMB, item.thumbnailUrl);
		values.put(FAV_TITLE, item.title);
		values.put(FAV_TOTAL_RATE, item.totalRate);
		values.put(FAV_VIEW_COUNTER, item.viewCounter);
		
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.insert(FAVOR_TABLE, null, values);
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public int removeFavorite(String idTitle) {
		int result = -1;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			result = db.delete(FAVOR_TABLE, FAV_ID_TITLE + "=? ", new String[] { idTitle});
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	public void clearFavorTable() {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(FAVOR_TABLE, null, null);
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public boolean isFavorite(String idTitle) {
		boolean result = false;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery("select * from " + FAVOR_TABLE + " where " + FAV_ID_TITLE + "= ? ", new String[] { idTitle});
			result = cursor != null && cursor.getCount() > 0;
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<Book> getAllBookInFavor() {
		ArrayList<Book> items = new ArrayList<Book>();
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			String query = "SELECT * FROM " + FAVOR_TABLE;
			
			Cursor cursor = db.rawQuery(query, new String[] {});
			if (cursor != null && cursor.moveToFirst()) {
				do {
					Book item = new Book();
					item._id = cursor.getString(cursor.getColumnIndex(FAV_ID_TITLE));
					item.author = cursor.getString(cursor.getColumnIndex(FAV_AUTHOR));
					item.averateRate = cursor.getDouble(cursor.getColumnIndex(FAV_AVERAGE));
					item.lastChapter = cursor.getLong(cursor.getColumnIndex(FAV_LAST_CHAPTER));
					item.lastVolume = cursor.getLong(cursor.getColumnIndex(FAV_LAST_VOLUME));
					item.rateCounter = cursor.getLong(cursor.getColumnIndex(FAV_RATE_COUNT));
					item.shortDescription = cursor.getString(cursor.getColumnIndex(FAV_DES));
					item.source = cursor.getString(cursor.getColumnIndex(FAV_SOURCE));
					item.status = cursor.getString(cursor.getColumnIndex(FAV_STATUS));
					item.thumbnailUrl = cursor.getString(cursor.getColumnIndex(FAV_THUMB));
					item.savedTime = cursor.getString(cursor.getColumnIndex(FAV_SAVE_TIME));
					item.title = cursor.getString(cursor.getColumnIndex(FAV_TITLE));
					item.totalRate = cursor.getLong(cursor.getColumnIndex(FAV_TOTAL_RATE));
					item.viewCounter = cursor.getLong(cursor.getColumnIndex(FAV_VIEW_COUNTER));
					items.add(item);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		Log.i(TAG, "What ALO " + items.size());
		return items;
	}

	private void createJustReadTable(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_JUST_READ + " (" 
					+ FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " 
					+ FAV_ID_TITLE + " TEXT , " 
					+ FAV_TITLE + " TEXT , "
					+ FAV_AUTHOR + " TEXT , " 
					+ FAV_AVERAGE + " REAL , " 
					+ FAV_DES + " TEXT , " 
					+ FAV_LAST_CHAPTER + " REAL , " 
					+ FAV_LAST_VOLUME + " REAL , " 
					+ FAV_SAVE_TIME + " TEXT , " 
					+ FAV_RATE_COUNT + " REAL , " 
					+ FAV_SOURCE + " TEXT , " 
					+ FAV_STATUS + " TEXT , " 
					+ FAV_THUMB + " TEXT , " 
					+ FAV_TOTAL_RATE + " REAL , " 
					+ FAV_VIEW_COUNTER + " REAL)");
//			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public boolean existInJustReadTable(String idTitle) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("select 1 from " + TABLE_JUST_READ + " where " + FAV_ID_TITLE + "= ? ", new String[] { idTitle});
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	} 
	
	public boolean existInRatingTable(String bookId) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("select 1 from " + TABLE_RATING + " where " + RATING_BOOK_ID + "= ? ", new String[] { bookId});
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	} 
	
	public boolean existInSavedTable(String bookId) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("select 1 from " + TABLE_SAVED_INDEX + " where " + SAVED_INDEX_BOOK_ID + "= ? ", new String[] { bookId});
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	} 
	
	public boolean existInVCTable(String bookId) {
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("select 1 from " + TABLE_VIEW_COUNTER + " where " + VC_BOOK_ID + "= ? ", new String[] { bookId});
		boolean exists = (cursor.getCount() > 0);
		cursor.close();
		return exists;
	} 
	
	public void addToRatingTable(String bookId) {

		ContentValues values = new ContentValues();
		values.put(RATING_BOOK_ID, bookId);
		
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.insert(TABLE_RATING, null, values);
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void addToSavedIndexTable(String bookId, String info) {

		ContentValues values = new ContentValues();
		values.put(SAVED_INDEX_BOOK_ID, bookId);
		values.put(SAVED_INDEX_INFO, info);
		
		if(existInSavedTable(bookId)){
			try {
				SQLiteDatabase db = this.getWritableDatabase();
				db.update(TABLE_SAVED_INDEX, values, SAVED_INDEX_BOOK_ID +" = '"+bookId+"'", null);//"_id=" + alarm.getId()
				db.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}else{
			
			try {
				SQLiteDatabase db = this.getWritableDatabase();
				db.insert(TABLE_SAVED_INDEX, null, values);
				db.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public void addToVCTable(String bookId) {

		ContentValues values = new ContentValues();
		values.put(VC_BOOK_ID, bookId);
		
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.insert(TABLE_VIEW_COUNTER, null, values);
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void addToJustRead(Book item) {
		
		if(existInJustReadTable(item._id)){
			return;
		}

		ContentValues values = new ContentValues();
		values.put(FAV_ID_TITLE, item._id);
		values.put(FAV_AUTHOR, item.author);
		values.put(FAV_AVERAGE, item.averateRate);
		values.put(FAV_DES, item.shortDescription);
		values.put(FAV_LAST_CHAPTER, item.lastChapter);
		values.put(FAV_LAST_VOLUME, item.lastVolume);
		values.put(FAV_RATE_COUNT, item.rateCounter);
		values.put(FAV_SOURCE, item.source);
		values.put(FAV_STATUS, item.status);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		values.put(FAV_SAVE_TIME, dateFormat.format(date));
		values.put(FAV_THUMB, item.thumbnailUrl);
		values.put(FAV_TITLE, item.title);
		values.put(FAV_TOTAL_RATE, item.totalRate);
		values.put(FAV_VIEW_COUNTER, item.viewCounter);
		
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.insert(TABLE_JUST_READ, null, values);
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public int removeJustRead(Book item) {
		int result = -1;
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			result = db.delete(TABLE_JUST_READ, FAV_ID_TITLE + "=? ", new String[] { item._id});
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	public void clearJustReadTable() {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_JUST_READ, null, null);
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public ArrayList<Book> getAllBookInJustRead() {
		ArrayList<Book> items = new ArrayList<Book>();
		try {
			SQLiteDatabase db = this.getWritableDatabase();

			String query = "SELECT * FROM " + TABLE_JUST_READ;
			
			Cursor cursor = db.rawQuery(query, new String[] {});
			if (cursor != null && cursor.moveToFirst()) {
				do {
					Book item = new Book();
					item._id = cursor.getString(cursor.getColumnIndex(FAV_ID_TITLE));
					item.author = cursor.getString(cursor.getColumnIndex(FAV_AUTHOR));
					item.averateRate = cursor.getDouble(cursor.getColumnIndex(FAV_AVERAGE));
					item.lastChapter = cursor.getLong(cursor.getColumnIndex(FAV_LAST_CHAPTER));
					item.lastVolume = cursor.getLong(cursor.getColumnIndex(FAV_LAST_VOLUME));
					item.rateCounter = cursor.getLong(cursor.getColumnIndex(FAV_RATE_COUNT));
					item.shortDescription = cursor.getString(cursor.getColumnIndex(FAV_DES));
					item.source = cursor.getString(cursor.getColumnIndex(FAV_SOURCE));
					item.status = cursor.getString(cursor.getColumnIndex(FAV_STATUS));
					item.thumbnailUrl = cursor.getString(cursor.getColumnIndex(FAV_THUMB));
					item.savedTime = cursor.getString(cursor.getColumnIndex(FAV_SAVE_TIME));
					item.title = cursor.getString(cursor.getColumnIndex(FAV_TITLE));
					item.totalRate = cursor.getLong(cursor.getColumnIndex(FAV_TOTAL_RATE));
					item.viewCounter = cursor.getLong(cursor.getColumnIndex(FAV_VIEW_COUNTER));
					items.add(item);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		Log.i(TAG, "What ALO " + items.size());
		return items;
	}
	
	public boolean isTableExists(Book book){
		String bookId = getBookId(book._id);
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", (PREFIX_TABLE+bookId)});
			if (!cursor.moveToFirst()){
				return false;
			}
			int count = cursor.getInt(0);
			cursor.close();
			db.close();
			return count > 0;
		}catch(Exception e){
		
		}
		
		return false;
	}
}