package se.xdin.electricgaugesurveillance.models;

import se.xdin.electricgaugesurveillance.R;

public class Sensor {
	int ticksPerKWH = (int) R.string.default_number_of_ticks;

	public void setTicksPerKWH(int ticksPerKWH) {
		this.ticksPerKWH = ticksPerKWH;
	}
	
	public int getTicksPerKWH() {
		return ticksPerKWH;
	}
}
