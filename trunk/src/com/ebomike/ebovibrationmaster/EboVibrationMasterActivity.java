package com.ebomike.ebovibrationmaster;

import com.ebomike.ebovibrationmaster.model.VibrationPattern;
import com.ebomike.ebovibrationmaster.ui.VibrationPatternTable;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

public class EboVibrationMasterActivity extends Activity {
	
	VibrationPattern vibrationPattern;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // TEST
        vibrationPattern = new VibrationPattern();
        vibrationPattern.addValue(100);
        
        VibrationPatternTable table = (VibrationPatternTable) findViewById(R.id.vibration_table);
        table.setVibrationPattern(vibrationPattern);
    }
    
    public void testVibration(View view) {
    	Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    	
    	vibrator.vibrate(vibrationPattern.getPattern(), -1);
    }
}