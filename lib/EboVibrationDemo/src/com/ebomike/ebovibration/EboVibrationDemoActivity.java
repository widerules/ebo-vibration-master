package com.ebomike.ebovibration;

import com.ebomike.ebovibrationmaster.EboVibration;
import com.ebomike.ebovibrationmaster.EboVibrationResultReceiver;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EboVibrationDemoActivity extends Activity implements EboVibrationResultReceiver {

	int currentVibration;
	
	TextView vibrationNameView;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Load the selected vibration from the persistent settings (or use a default value).
        currentVibration = EboVibration.loadVibrationSettings(this); 
        
        // Show the name of the currently selected vibration in the UI
        vibrationNameView = (TextView) findViewById(R.id.vibration_name);
        vibrationNameView.setText(EboVibration.getVibrationName(this, currentVibration));
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	// Update the persistent settings with the currently selected vibration
    	EboVibration.saveVibrationSettings(this, currentVibration);
    }
    
    public void pickVibration(View v) {
    	// Trigger a dialog to allow the user to pick a vibration pattern.
    	// We're passing ourselves as the EboVibrationResultReceiver, so the dialog
    	// will either call onNewVibrationPicked() or onVibrationDialogCanceled().
    	EboVibration.pickVibration(this, currentVibration, this);
    }
    
    public void triggerVibration(View v) {
    	// Execute the currently selected vibration.
    	EboVibration.executeVibration(this, currentVibration);
    }

	@Override
	public void onNewVibrationPicked(int id, String name) {
		// This is a callback from the vibration picker dialog.
		// The user has picked a new vibration through the dialog. Remember this
		// selection, and update the UI to reflect the name of the selection.
		vibrationNameView.setText(name);
		currentVibration = id;
	}

	@Override
	public void onVibrationDialogCanceled() {
		// This is a callback from the vibration picker dialog.
		// The user has dismissed the dialog. Nothing for us to do, the user
		// chose not to pick a vibration after all.
	}
}