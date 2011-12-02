package com.ebomike.ebovibrationmaster;

import android.content.Context;
import android.database.Cursor;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class VibrationRadioGroup extends RadioGroup {

	// Mapping of position within the radio group to the ID of hte vibration it represents.
	// We can't just use the RadioButton's ID for that, IDs can't be negative.
	int[] posToId;
	
	
	public VibrationRadioGroup(Context context) {
		super(context);
		
		createList(context, 0);
	}

	public VibrationRadioGroup(Context context, int currentSelection) {
		super(context);
		
		createList(context, currentSelection);
	}

	
	public VibrationRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		int currentSelection = attrs.getAttributeIntValue("ebo", "selectedId", 0);
		
		createList(context, currentSelection);
	}
	
	private void createList(final Context context, int currentSelection) {
		// Query the database first.
		Cursor cursor = EboVibrationDatabase.getVibrationPatterns(context);
		
		if (cursor == null) {
			// EboVibrationMaster is not installed. Use the stock list then.
			createStockList(context, currentSelection);
		} else {
			int idColumn = cursor.getColumnIndex(EboVibrationDatabase.Vibration._ID);
			int nameColumn = cursor.getColumnIndex(EboVibrationDatabase.Vibration.NAME);
			
			posToId = new int[cursor.getCount()];
			int index = 0;
			
			while (cursor.moveToNext()) {
				RadioButton button = new RadioButton(context);
				button.setText(cursor.getString(nameColumn));
				
				int id = cursor.getInt(idColumn);
				posToId[index] = id;
				button.setId(++index);
				
				if (id == currentSelection) {
					button.setChecked(true);
				}
				
				addView(button);
			}
			
			cursor.close();
		}
	
		setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId != -1) {
					EboVibration.executeVibration(context, posToId[checkedId-1]);
				}
			}
		});
	}
		
	public int getSelectedId() {
		int checkedId = getCheckedRadioButtonId();
		
		if (checkedId == -1) {
			return 0;
		}
		
		return posToId[checkedId-1];
	}
	
	/* Populate the list using the stock vibrations. We do this if EboVibrationMaster is not installed
	 * and there is no content provider available.
	 */
	private void createStockList(Context context, int currentSelection) {
		// Stock patterns have negative IDs.
		int index = 0;
		
		posToId = new int[EboVibration.STOCK_VIBRATIONS.length];
		
		for (EboVibration.StockVibration vibration : EboVibration.STOCK_VIBRATIONS) {
			RadioButton button = new RadioButton(context);
			button.setText(vibration.nameStringId);
			
			// Stock IDs begin at -2
			int id = -2 - index;
			posToId[index] = id;
			button.setId(++index);
			
			if (id == currentSelection) {
				button.setChecked(true);
			}
			

			addView(button);
		}
	}
}
