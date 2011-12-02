package com.ebomike.ebovibrationmaster;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class EboVibrationContentProvider extends ContentProvider {
	
	private static final String TAG = "EboVibrationContentProvider";

	private DatabaseHelper mOpenHelper;
	
	/*
	 * URI management
	*/
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	private static final int VIBRATIONS = 1;
	private static final int VIBRATION_ID = 2;
	private static final int VIBRATION_DURATIONS = 3;
	private static final int VIBRATION_DURATION_ID = 4;

	
	static {
		sUriMatcher.addURI(EboVibrationDatabase.AUTHORITY, "vibrations", VIBRATIONS);
		sUriMatcher.addURI(EboVibrationDatabase.AUTHORITY, "vibrations/#", VIBRATION_ID);
		sUriMatcher.addURI(EboVibrationDatabase.AUTHORITY, "vibrationdurations", VIBRATION_DURATIONS);
		sUriMatcher.addURI(EboVibrationDatabase.AUTHORITY, "vibrationdurations/#", VIBRATION_DURATION_ID);
	}
	
	static class DatabaseHelper extends SQLiteOpenHelper {
		Context context;
		
		DatabaseHelper(Context context) {
			super(context, EboVibrationDatabase.DB_NAME, null, EboVibrationDatabase.DB_VERSION);
			
			this.context = context;
		}
       
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + EboVibrationDatabase.Vibration.TABLE_NAME + " ("
                   + EboVibrationDatabase.Vibration._ID + " INTEGER PRIMARY KEY,"
                   + EboVibrationDatabase.Vibration.NAME + " TEXT"
                   + ");");

           db.execSQL("CREATE TABLE " + EboVibrationDatabase.VibrationDuration.TABLE_NAME + " ("
                   + EboVibrationDatabase.VibrationDuration._ID + " INTEGER PRIMARY KEY,"
                   + EboVibrationDatabase.VibrationDuration.VIBRATION_ID + " INTEGER,"
                   + EboVibrationDatabase.VibrationDuration.VALUE_INDEX + " INTEGER,"
                   + EboVibrationDatabase.VibrationDuration.LENGTH + " INTEGER"
                   + ");");
           
           createStockPatterns(db);
       }
       
       private void createStockPatterns(SQLiteDatabase db) {
    	   ContentValues values = new ContentValues();
		   ContentValues patternValues = new ContentValues();
    	   
    	   for (EboVibration.StockVibration vibration : EboVibration.STOCK_VIBRATIONS) {
    		   values.put(EboVibrationDatabase.Vibration.NAME, context.getString(vibration.nameStringId));
    		   long patternId = db.insert(EboVibrationDatabase.Vibration.TABLE_NAME, null, values);

    		   int index = 1;
			   patternValues.put(EboVibrationDatabase.VibrationDuration.VIBRATION_ID, patternId);
			   
    		   for (long time : vibration.pattern) {
    			   patternValues.put(EboVibrationDatabase.VibrationDuration.VALUE_INDEX, index++);
    			   patternValues.put(EboVibrationDatabase.VibrationDuration.LENGTH, time);
    			   db.insert(EboVibrationDatabase.VibrationDuration.TABLE_NAME, null, patternValues);
    		   }
    	   }
       }
       
       @Override
       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
           Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
       }
	}
	

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(EboVibrationDatabase.Vibration.TABLE_NAME);
		
		int count = 0;
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String finalWhere;

		switch (sUriMatcher.match(uri)) {
		case VIBRATIONS:
			count = db.delete(EboVibrationDatabase.Vibration.TABLE_NAME, where, whereArgs);
			break;
			
		case VIBRATION_ID:
			finalWhere = EboVibrationDatabase.Vibration._ID + "=" + uri.getPathSegments().get(1);

			if (where != null) {
				finalWhere = finalWhere + " AND " + where;
			}

			count = db.delete(EboVibrationDatabase.Vibration.TABLE_NAME, finalWhere, whereArgs);
			break;
			
		case VIBRATION_DURATIONS:
			count = db.delete(EboVibrationDatabase.VibrationDuration.TABLE_NAME, where, whereArgs);
			break;
			
		case VIBRATION_DURATION_ID:
			finalWhere = EboVibrationDatabase.VibrationDuration._ID + "=" + uri.getPathSegments().get(1);

			if (where != null) {
				finalWhere = finalWhere + " AND " + where;
			}

			count = db.delete(EboVibrationDatabase.VibrationDuration.TABLE_NAME, finalWhere, whereArgs);
			break;
			
				 
			default:
				throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
       mOpenHelper = new DatabaseHelper(getContext());
       return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

	   SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
			case VIBRATIONS:
				qb.setTables(EboVibrationDatabase.Vibration.TABLE_NAME);
//				qb.setProjectionMap();
				break;
				
			case VIBRATION_ID:
				qb.setTables(EboVibrationDatabase.Vibration.TABLE_NAME);
//			   qb.setProjectionMap(sNotesProjectionMap);
				qb.appendWhere(EboVibrationDatabase.Vibration._ID +
						"=" +
						uri.getPathSegments().get(1));
				break;
				
			case VIBRATION_DURATIONS:
				qb.setTables(EboVibrationDatabase.VibrationDuration.TABLE_NAME);
				break;
				
			case VIBRATION_DURATION_ID:
				qb.setTables(EboVibrationDatabase.VibrationDuration.TABLE_NAME);
				qb.appendWhere(EboVibrationDatabase.VibrationDuration._ID +
						"=" +
						uri.getPathSegments().get(1));
				break;
				
			
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		String orderBy;
 
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = EboVibrationDatabase.Vibration.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		Cursor c = qb.query(
			db,            // The database to query
			projection,    // The columns to return from the query
			selection,     // The columns for the where clause
			selectionArgs, // The values for the where clause
			null,          // don't group the rows
			null,          // don't filter by row groups
			orderBy        // The sort order
		);

		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
