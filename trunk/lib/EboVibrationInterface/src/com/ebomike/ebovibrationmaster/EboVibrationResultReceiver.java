package com.ebomike.ebovibrationmaster;

public interface EboVibrationResultReceiver {
	
	public void onNewVibrationPicked(int id, String name);
	
	public void onVibrationDialogCanceled();
}
