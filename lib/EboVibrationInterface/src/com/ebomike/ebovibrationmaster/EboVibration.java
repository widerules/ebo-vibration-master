package com.ebomike.ebovibrationmaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

public class EboVibration {
	
	public static final String ACTION_PICK_VIBRATION = "com.ebomike.ebovibrationmaster.ACTION_PICK_VIBRATION";
	public static int dialogId = 500;
	
	private static final String TAG = "EboVibration";
	private static final int radioGroupId = 10000;
	
	private static final String PREFS_VIBRATION = "com.ebomike.ebovibration.VIBRATION";
	

	static class StockVibration {
		int nameStringId;
		long[] pattern;
		
		StockVibration(int nameStringId, long[] pattern) {
			this.nameStringId = nameStringId;
			this.pattern = pattern;
		}
	}
	
	// Stock patterns
	static final StockVibration[] STOCK_VIBRATIONS = new StockVibration[] {
		new StockVibration(R.string.short_vibration, new long[] { 0L, 250L }),
		new StockVibration(R.string.long_vibration, new long[] { 0L, 500L }),
		new StockVibration(R.string.double_pulse_vibration, new long[] { 0L, 250L, 250L, 250L }),
	};
	
	
	
	public static void pickVibration(final Context context, int currentSelection, final EboVibrationResultReceiver resultReceiver) {
		
		VibrationRadioGroup radioGroup = new VibrationRadioGroup(context, currentSelection);
		radioGroup.setId(radioGroupId);
		
		
		Dialog dialog = new AlertDialog.Builder(context)
		.setPositiveButton(android.R.string.ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (resultReceiver != null) {
					VibrationRadioGroup radioGroup = (VibrationRadioGroup) ((AlertDialog) dialog).findViewById(radioGroupId);
					int id = radioGroup.getSelectedId();
					
					if (id == 0) {
						resultReceiver.onVibrationDialogCanceled();
					} else {
						String name = EboVibration.getVibrationName(context, id);
						resultReceiver.onNewVibrationPicked(id, name);
					}
				}
			}
		})
		.setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.setView(radioGroup)
		.create();
		
		dialog.show();
		
		
		
//		activity.showDialog(dialogId);
/*		try {
			Intent intent = new Intent(ACTION_PICK_VIBRATION);
			activity.startActivityForResult(intent, 0);
		} catch (ActivityNotFoundException e) {
			// EboVibrationMaster is not installed. Fall back to the standard version.
		}*/
	}
	
	public static void executeVibration(Context context, int id) {
		long[] pattern = getPattern(context, id);
		
		for (long v : pattern) {
			Log.v(TAG, "Val: " + v);
		}

		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    	vibrator.vibrate(pattern, -1);
	}
	
	/* Get a vibration pattern from an ID.
	 * 
	 */
	public static long[] getPattern(Context context, int id) {
		Log.v(TAG, "Getting pattern for " + id);
		if (id < -1) {
			// Negative IDs indicate stock vibrations.
			// Perform bounds checking.
			if (id < -STOCK_VIBRATIONS.length - 1) {
				// Out of bounds!
				return STOCK_VIBRATIONS[0].pattern;
			}
			
			return STOCK_VIBRATIONS[-id - 2].pattern;
		}
		
		// This pattern is supposedly the ID of a proper pattern from the database. Let's query it.
		long[] result = EboVibrationDatabase.loadVibrationFromDb(context, id);
		
		return result;		
	}
	
	public static String getVibrationName(Context context, int id) {
		if (id < -1) {
			return context.getString(STOCK_VIBRATIONS[-id - 2].nameStringId);
		}
		
		return EboVibrationDatabase.getVibrationNameFromDb(context, id);
	}
	
	public static Dialog onCreateDialog(Context context, int id) {
		if (id == dialogId) {
			return new AlertDialog.Builder(context)
			.setPositiveButton(android.R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setNegativeButton(android.R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setView(new VibrationRadioGroup(context))
			.create();
		}
		
		return null;		
	}
	
	public static int getDefaultVibration(Context context) {
		int defFromDb = EboVibrationDatabase.getDefaultVibrationFromDb(context);
		
		if (defFromDb != 0) {
			return defFromDb;
		}
		
		// EboVibration not installed, use the stock vibration instead.
		return -2;
	}
	
	public static int loadVibrationSettings(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		return prefs.getInt(PREFS_VIBRATION, getDefaultVibration(context));
	}

	public static void saveVibrationSettings(Context context, int vibrationId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putInt(PREFS_VIBRATION, vibrationId);
		editor.commit();
	}
}
