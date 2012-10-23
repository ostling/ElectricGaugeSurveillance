package se.xdin.electricgaugesurveillance.models;

import se.xdin.electricgaugesurveillance.DefinedConstants;

public class Sensor {
	int ticksPerKWH = DefinedConstants.defaultNumberOfTicksPerKWH;

	public void setTicksPerKWH(int ticksPerKWH) {
		this.ticksPerKWH = ticksPerKWH;
	}
	
	public int getTicksPerKWH() {
		return ticksPerKWH;
	}
}
