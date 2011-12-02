package com.ebomike.ebovibrationmaster.model;

import android.util.Log;

public class VibrationPattern {
	
	private static final String TAG = "VibrationPattern";
	

	// The actual pattern, as it is passed into Android's vibration manager
	private long[] pattern = new long[] { 100 };
	
	// User-defined name for this pattern
	public String name;
	
	
	
	
	/** Returns the vibration pattern as it is used in Android's vibration API.
	 *  
	 * @return The vibration pattern that can be forwarded straight to the Android API. 
	 */
	public long[] getPattern() {
		return pattern;
	}

	public void setPattern(long[] pattern) {
		this.pattern = pattern;
	}
	
	public void addValue(long value) {
		long[] newPattern = new long[pattern.length + 1];
		System.arraycopy(pattern,  0, newPattern, 0, pattern.length);
		newPattern[pattern.length] = value;
		
		pattern = newPattern;
	}
	
	public void updateValue(int index, long value) {
		pattern[index] = value;
	}
	
	public void removeElement(int index) {
		int oldLen = pattern.length;
		
		long[] newPattern = new long[oldLen - 1];
		System.arraycopy(pattern, 0, newPattern, 0, index);
		System.arraycopy(pattern, index + 1, newPattern, index, oldLen - 1 - index);
		
		pattern = newPattern;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
