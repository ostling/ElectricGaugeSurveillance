package se.xdin.electricgaugesurveillance.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import se.xdin.electricgaugesurveillance.models.SimpleSensorData;

public class SensorDataHelper {
	
	public final static int SIMPLE_SENSOR_DATA = 10;
	
	static String string = null;

	
	public static SimpleSensorData getSimpleSensorData(final String ipAdress, final int port) {
//		new Thread(new Runnable() {
//			public void run() {
			    try {
			    	System.out.println("starting socket");
		            Socket s = new Socket(ipAdress, port);
		            System.out.println("socket up");
		           
		            //outgoing stream redirect to socket
		            OutputStream out = s.getOutputStream();
		           
		            out.write(SIMPLE_SENSOR_DATA);
		            
		            //Wait for data
		            try{ Thread.sleep(150); }catch(InterruptedException e){ }
		            
		            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
		            
		            //read line(s)
		            string = input.readLine();
		            System.out.println("read: " + string);
		            //Close connection
		            s.close();
		           
		           
		    } catch (UnknownHostException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		    } catch (IOException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		    }

//		}}).start();
		 if (string != null) {
			 return handleReadLine(string);
		 } else {
			 System.out.println("return null");
			 return null;
		 }
		// Just use as test:
//        return new SimpleSensorData(4, Calendar.getInstance(), 210000);
	}
	
	
	/**
	 * sensorData need to be on following format: "yyyyMMdd,hhmmss,power,ticks" where power is a double
	 * containing current power and ticks is a long containing number of ticks
	 * @param sensorData
	 * @return
	 */
	private static SimpleSensorData handleReadLine(String sensorData) {
		String[] pieces = sensorData.split(",");
		String date = pieces[0];
		String time = pieces[1];
		double power = Double.parseDouble(pieces[2]);
		long ticks = Integer.parseInt(pieces[3]);
		Calendar dateTime = Calendar.getInstance();
		dateTime.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6))-1,
				Integer.parseInt(date.substring(6, 8)), Integer.parseInt(time.substring(0, 2)),
				Integer.parseInt(time.substring(2, 4)), Integer.parseInt(time.substring(4, 6)));
		
		return new SimpleSensorData(dateTime, power, ticks);
	}
}
