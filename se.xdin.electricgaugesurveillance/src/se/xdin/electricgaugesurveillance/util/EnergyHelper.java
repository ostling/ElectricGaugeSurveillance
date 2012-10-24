package se.xdin.electricgaugesurveillance.util;

public class EnergyHelper {
	
	public static long calculateKWH(long numberOfTicks, int ticksPerKWH) {
		return numberOfTicks / ticksPerKWH;
	}
}
