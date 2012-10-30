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
	
	public static Socket openSocket(final String ipAdress, final int port) {
		Socket s = null;
		System.out.println("previous instances of socket: " + Socket.class);
		try {
			s = new Socket(ipAdress, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("unknown exception");
		}
		
		return s;
	}
	
	public static void closeSocket(Socket s) {
		try {
			if (s != null)
				s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static SimpleSensorData getSimpleSensorData(Socket s) {
	    try {
//            Socket s = new Socket(ipAdress, port);
           
            //outgoing stream redirect to socket
	    	BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            OutputStream out = s.getOutputStream();
           System.out.println("writing");
            out.write(SIMPLE_SENSOR_DATA);
            
            //read line(s)
            System.out.println("wait to ready");
//            int retrys = 0;
//            while (!input.ready()) {
//            	retrys++;
//            	if (retrys > 3)
//            		return null;
            	try{ Thread.sleep(150); } catch(InterruptedException e){}
            	
//            }
            System.out.println("ready to read");
            string = input.readLine();
            System.out.println("reading done: " + string);
            //Close connection
//            s.close();
       
	    } catch (UnknownHostException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	    } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	    } catch (Exception e) {
	    	System.out.println("UNHANDLED EXCEPTION SENSORDATAHELPER");
	    }
	    
		if (string != null)
			if (!string.equals(""))
				return handleReadLine(string);
		return null;
	}
	
	
	/**
	 * sensorData need to be on following format: "yyyyMMdd,hhmmss,power,ticks" where power is a double
	 * containing current power and ticks is a long containing number of ticks
	 * @param sensorData
	 * @return
	 */
	private static SimpleSensorData handleReadLine(String sensorData) {
		String[] pieces = sensorData.split(",");
		try {
			String year = pieces[0], month = pieces[1], day = pieces[2];
			String hour = pieces[3], minute = pieces[4], second = pieces[5];
			double power = Double.parseDouble(pieces[6]);
			long ticks = Integer.parseInt(pieces[7]);
			Calendar dateTime = Calendar.getInstance();
			dateTime.set(Integer.parseInt(year), Integer.parseInt(month)-1,
					Integer.parseInt(day), Integer.parseInt(hour),
					Integer.parseInt(minute), Integer.parseInt(second));
			
			return new SimpleSensorData(dateTime, power, ticks);
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
