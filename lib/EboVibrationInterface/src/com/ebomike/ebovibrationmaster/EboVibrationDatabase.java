package com.ebomike.ebovibrationmaster;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class EboVibrationDatabase {
	
	private static final String TAG = "EboVibrationDatabase";

	static final String DB_NAME = "vibration.db";
	static final int DB_VERSION = 1;
	

	
	static public class Vibration implements BaseColumns {
		public static final String TABLE_NAME = "vibration";
		
		public static final String NAME = "name";
		public static final String DEFAULT_SORT_ORDER = "name";
	}
	
	static public class VibrationDuration implements BaseColumns {
		public static final String TABLE_NAME = "vibration_duration";
		
		public static final String VIBRATION_ID = "vibration_id";
		public static final String VALUE_INDEX = "value_index";
		public static final String LENGTH = "length";
	}

	public static final String AUTHORITY = "com.ebomike.ebovibrationmaster";

	private static final String SCHEME = "content://";
    private static final String PATH_VIBRATIONS = "/vibrations";
    private static final String PATH_VIBRATION_DURATIONS = "/vibrationdurations";

    public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_VIBRATIONS);
    public static final Uri CONTENT_URI_DURATIONS =  Uri.parse(SCHEME + AUTHORITY + PATH_VIBRATION_DURATIONS);
    
    public static final String CONTENT_TYPE = AUTHORITY + PATH_VIBRATIONS;
    public static final String CONTENT_TYPE_DURATIONS = AUTHORITY + PATH_VIBRATION_DURATIONS;


	private static String[] PROJ_DURATION_ONLY = new String[] { EboVibrationDatabase.VibrationDuration.LENGTH };
	private static String[] PROJ_NAME_ONLY = new String[] { EboVibrationDatabase.Vibration.NAME };
	private static String[] PROJ_ID_ONLY = new String[] { EboVibrationDatabase.Vibration._ID };
	
	public static long[] loadVibrationFromDb(Context context, int id) {
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(EboVibrationDatabase.CONTENT_URI_DURATIONS,
				PROJ_DURATION_ONLY,
				EboVibrationDatabase.VibrationDuration._ID + "=?",
				new String[] { Integer.toString(id) },
				EboVibrationDatabase.VibrationDuration.VALUE_INDEX);
		
		if (cursor == null || !cursor.moveToFirst()) {
			return new long[0];
		}
		
		int elements = cursor.getCount();
		long[] result = new long[elements];
		
		for (int x=0; x<elements; x++) {
			result[x] = cursor.getLong(0);
		}
		
		cursor.close();
		return result;
	}
	
	public static void storeVibrationInDb(Context context, int id, long[] data) {
		// Replace the existing data.
		ContentResolver cr = context.getContentResolver();
		
		cr.delete(EboVibrationDatabase.CONTENT_URI_DURATIONS,
				EboVibrationDatabase.VibrationDuration._ID + "=?",
				new String[] { Integer.toString(id) });
		
		ContentValues values = new ContentValues(3);
		values.put(EboVibrationDatabase.VibrationDuration.VIBRATION_ID, id);
		
		int elements = data.length;
		for (int x=0; x<elements; x++) {
			values.put(EboVibrationDatabase.VibrationDuration.VALUE_INDEX, x);
			values.put(EboVibrationDatabase.VibrationDuration.LENGTH, data[x]);
			cr.insert(EboVibrationDatabase.CONTENT_URI_DURATIONS, values);
		}
	}
	
	public static int createNewVibration(Context context, String vibrationName) {
		ContentResolver cr = context.getContentResolver();
		
		ContentValues values = new ContentValues(1);
		values.put(EboVibrationDatabase.Vibration.NAME, vibrationName);
		Uri result = cr.insert(EboVibrationDatabase.CONTENT_URI, values);
		
		try {
			return Integer.parseInt(result.getLastPathSegment());
		} catch (Exception e) {
			Log.e(TAG, "Error evaluating resulting Uri " + result.toString());
			return 0;
		}
	}
	
	
	/** Return a list of all vibration patterns in the database.
	 * 
	 */
	public static Cursor getVibrationPatterns(Context context) {
		try {
			ContentResolver cr = context.getContentResolver();
			return cr.query(CONTENT_URI, null, null, null, null);
			
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getVibrationNameFromDb(Context context, int id) {
		ContentResolver cr = context.getContentResolver();

		Cursor cursor = cr.query(ContentUris.withAppendedId(EboVibrationDatabase.CONTENT_URI, id),
				PROJ_NAME_ONLY,
				null,
				null,
				null);
	
		String name = "";
		
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				name = cursor.getString(0);
			}

			cursor.close();
		}
		
		return name;
	}
	
	public static int getDefaultVibrationFromDb(Context context) {
		ContentResolver cr = context.getContentResolver();

		Cursor cursor = cr.query(EboVibrationDatabase.CONTENT_URI,
				PROJ_ID_ONLY,
				null,
				null,
				null);

		int result = 0;
		
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				result = cursor.getInt(0);
			}

			cursor.close();
		}
		
		return result;
	}
}
