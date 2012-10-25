package se.xdin.electricgaugesurveillance.models;

import java.util.Calendar;

public class SimpleSensorData {
	
	double currentPower = 0;
	Calendar contactTime = Calendar.getInstance();
	long numberOfTicks = 0;
	
	public SimpleSensorData(Calendar contactTime, double currentPower, long numberOfTicks) {
		this.currentPower = currentPower;
		this.contactTime = contactTime;
		this.numberOfTicks = numberOfTicks;
	}
	
	public double getCurrentPower() {
		return currentPower;
	}
	
	public void setCurrentPower(double currentPower) {
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
