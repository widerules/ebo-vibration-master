package com.ebomike.ebovibrationmaster;

import com.ebomike.ebovibrationmaster.model.DataConversion;
import com.ebomike.ebovibrationmaster.model.VibrationPattern;
import com.ebomike.ebovibrationmaster.ui.VibrationPatternGraphView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

public class EboVibrationListActivity extends Activity {
	
	private static final String TAG = "EboVibrationListActivity";
	
	private VibrationPatternGraphView graphView;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.vibration_list);
		
		graphView = (VibrationPatternGraphView) findViewById(R.id.graph_view);
		
		((ListView) findViewById(R.id.vibration_list)).setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long id) {
				
				VibrationPattern pattern = DataConversion.getFromDatabase(EboVibrationListActivity.this, id);
				graphView.setVibrationPattern(pattern);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				graphView.setVibrationPattern(null);
				
			}
			
		});

		((ListView) findViewById(R.id.vibration_list)).setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {

				VibrationPattern pattern = DataConversion.getFromDatabase(EboVibrationListActivity.this, id);
				graphView.setVibrationPattern(pattern);
			}
		});
	
	}
	
//	public void addVibration(View view) {
	public void addVibration() {
		Log.v(TAG, "ADD");
		Log.v(TAG, "ADD");
		Log.v(TAG, "ADD");
		Log.v(TAG, "ADD");
		Log.v(TAG, "ADD");
//		Intent intent = new Intent(Intent.ACTION_INSERT, EboVibrationDatabase.CONTENT_URI);
		Intent intent = new Intent(Intent.ACTION_INSERT, Uri.parse(EboVibrationDatabase.CONTENT_TYPE));
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
