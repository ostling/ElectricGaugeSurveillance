package se.xdin.electricgaugesurveillance.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import se.xdin.electricgaugesurveillance.models.SimpleSensorData;

public class SensorDataHelper {
	
	public final static int SIMPLE_SENSOR_DATA = 10;
	
	public static SimpleSensorData getSimpleSensorData(final String ipAdress, final int port) {
//		 new Thread(new Runnable() {
//	            public void run() {
//	            	Socket client;
//	        		InputStream inputStream;
//	        		OutputStream outputStream;
//	        		
//	        		try {
//						client = new Socket(ipAdress, port);
//						
//						inputStream = client.getInputStream();
//						outputStream = client.getOutputStream();
//						outputStream.write(SIMPLE_SENSOR_DATA); // Request wanted data
//						
//						
//						
//						
//					} catch (UnknownHostException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//	            }
//		 });
		
		// Just use as test:
         return new SimpleSensorData(4, Calendar.getInstance(), 210000);
	}
	

}
