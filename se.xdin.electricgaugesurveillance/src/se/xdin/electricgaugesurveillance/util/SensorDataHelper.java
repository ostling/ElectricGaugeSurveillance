package se.xdin.electricgaugesurveillance.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;

import se.xdin.electricgaugesurveillance.models.SimpleSensorData;

public class SensorDataHelper {
	
	public final static int SIMPLE_SENSOR_DATA = 10;
	
	static String string = null;
	
	public static Socket openSocket(final String ipAdress, final int port) {
		Socket s = null;
		try {
			s = new Socket(ipAdress, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	public static void closeSocket(Socket s) {
		System.out.println("clost socket method");
		try {
			if (s != null) {
				OutputStream output = s.getOutputStream();
				InputStreamReader input = new InputStreamReader(s.getInputStream());
				try {
		    		if (input != null)
		    			input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
		    		if (output != null)
		    			output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				s.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static SimpleSensorData getSimpleSensorData(Socket s) {
		BufferedReader input = null;
		OutputStream output = null;
		string = null;
	    try {
	    	input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            output = s.getOutputStream();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 15);
//            while (!input.ready() && Calendar.getInstance().before(cal) && string == null) {
//            	try { Thread.sleep(50); } catch (Exception e) {}
//            	System.out.println("Waiting for ack input to be ready");
//            }
//            System.out.println("input: " + input.ready() + ", string: " + string);
//        	String ack = input.readLine();
//        	System.out.println("recived ack: " + ack);
            output.write(SIMPLE_SENSOR_DATA);
            
            while (!input.ready() && Calendar.getInstance().before(cal) && string == null) {
            	try { Thread.sleep(50); } catch (Exception e) {}
            }
            string = input.readLine();
            System.out.println("reading done: " + string);
       
	    } catch (UnknownHostException e) {
	            e.printStackTrace();
	    } catch (IOException e) {
	            e.printStackTrace();
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
			if (year.contains("ack")) { // TODO: fix ack string to be setting or pre-defined
				year = year.substring(3);
			}
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
