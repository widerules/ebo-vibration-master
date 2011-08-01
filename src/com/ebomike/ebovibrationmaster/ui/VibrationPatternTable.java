package com.ebomike.ebovibrationmaster.ui;

import com.ebomike.ebovibrationmaster.model.VibrationPattern;

import android.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class VibrationPatternTable extends TableLayout {
	
	// The pattern that is being displayed here.
	VibrationPattern vibrationPattern;
	boolean readOnly;
	

	public VibrationPatternTable(Context context) {
		super(context);
	}
	
	public VibrationPatternTable(Context context, AttributeSet set) {
		super(context, set);
		
		readOnly = set.getAttributeBooleanValue("ebo", "readOnly", false);
	}
	
	public void setVibrationPattern(VibrationPattern vibrationPattern) {
		this.vibrationPattern = vibrationPattern;
		
		createViews();
	}
	
	void createViews() {
		long[] pattern = vibrationPattern.getPattern();
		for (int x=0; x<pattern.length; x++) {
			addRow(pattern[x], (x & 1) != 0);
		}

		if (!readOnly) {
			TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, 1.0f);
			
			Button addButton = new Button(getContext());
			addButton.setText("+");
			addView(addButton, layoutParams1);
			
			addButton.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					VibrationPatternTable.this.addRow();
				}
			});
		}
	}
	
	void removeRow(int rowNumber) {
		vibrationPattern.removeElement(rowNumber);
		removeAllViews();
		createViews();
	}

	void addRow() {
		vibrationPattern.addValue(100);
		removeAllViews();
		createViews();
	}
	
	void updateRow(int rowNumber, long value) {
		vibrationPattern.updateValue(rowNumber, value);
	}

	
	public void addRow(long length, boolean on) {
		Context context = getContext();
		
		TableRow.LayoutParams layoutParams1 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, 1.0f);
		TableRow.LayoutParams layoutParams0 = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, 0.0f);
		
		TableRow row = new TableRow(context);
		row.setOrientation(TableRow.HORIZONTAL);
		
		TextView title = new TextView(context);
		title.setText((on) ? com.ebomike.ebovibrationmaster.R.string.on : com.ebomike.ebovibrationmaster.R.string.off);
		row.addView(title, layoutParams0);
		
		SeekBar seekBar = new SeekBar(context);
		seekBar.setMax(2000);
		seekBar.setProgress((int) length);
		seekBar.setIndeterminate(false);

		row.addView(seekBar, layoutParams1);
		
		final EditText lengthText = new EditText(context);
		lengthText.setText(String.format("%.2fs", (float) length / 1000.0f));
		row.addView(lengthText, layoutParams0);
		
		final int rowNumber = getChildCount();
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				float length = (float) progress;
				lengthText.setText(String.format("%.2fs", (float) length / 1000.0f));
				
				if (fromUser) {
					VibrationPatternTable.this.updateRow(rowNumber, (long) progress);
				}
			}
		});
		
		if (!readOnly) {
			Button delButton = new Button(context);
			delButton.setText("-");
			row.addView(delButton, layoutParams0);
			
			delButton.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					VibrationPatternTable.this.removeRow(rowNumber);
				}
			});
		}
		
		addView(row);
	}
}
