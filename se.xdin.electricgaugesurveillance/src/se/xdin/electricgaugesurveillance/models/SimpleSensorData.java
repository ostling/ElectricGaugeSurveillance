package se.xdin.electricgaugesurveillance.models;

import java.util.Calendar;

public class SimpleSensorData {
	
	int currentPower = 0;
	Calendar currentTime = Calendar.getInstance();
	long numberOfTicks = 0;
	
	public SimpleSensorData(int currentPower, Calendar currentTime, long numberOfTicks) {
		this.currentPower = currentPower;
		this.currentTime = currentTime;
		this.numberOfTicks = numberOfTicks;
	}
	
	public int getCurrentPower() {
		return currentPower;
	}
	
	public void setCurrentPower(int currentPower) {
		this.currentPower = currentPower;
	}
	
	public Calendar getCurrentTime() {
		return currentTime;
	}
	
	public void setCurrentTime(Calendar currentTime) {
		this.currentTime = currentTime;
	}
	
	public long getNumberOfTicks() {
		return numberOfTicks;
	}
	
	public void setNumberOfTicks(long numberOfTicks) {
		this.numberOfTicks = numberOfTicks;
	}
}
