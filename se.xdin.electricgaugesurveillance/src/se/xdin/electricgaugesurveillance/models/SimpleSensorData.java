package se.xdin.electricgaugesurveillance.models;

import java.util.Calendar;

public class SimpleSensorData {
	
	int currentPower = 0;
	Calendar contactTime = Calendar.getInstance();
	long numberOfTicks = 0;
	
	public SimpleSensorData(int currentPower, Calendar contactTime, long numberOfTicks) {
		this.currentPower = currentPower;
		this.contactTime = contactTime;
		this.numberOfTicks = numberOfTicks;
	}
	
	public int getCurrentPower() {
		return currentPower;
	}
	
	public void setCurrentPower(int currentPower) {
		this.currentPower = currentPower;
	}
	
	public Calendar getLastContact() {
		return contactTime;
	}
	
	public void setLastContact(Calendar currentTime) {
		this.contactTime = currentTime;
	}
	
	public long getNumberOfTicks() {
		return numberOfTicks;
	}
	
	public void setNumberOfTicks(long numberOfTicks) {
		this.numberOfTicks = numberOfTicks;
	}
}
