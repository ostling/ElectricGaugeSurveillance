package se.xdin.electricgaugesurveillance.application.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import se.xdin.electricgaugesurveillance.models.SimpleSensorData;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class SimpleStatisticsService extends Service {
	
	private Socket socket;
	private final IBinder binder = new CustomBinder();
	
	/* Settings : TODO : Fix to fetch from shared preferences */
	public final static int SIMPLE_SENSOR_DATA = 10;
	public final static int SENSOR_SAMPLE_TIME = 50;
	public final static int SENSOR_TIME_OUT = 15000;
	public final static String ACK_STRING = "ack";
	public boolean isConnected = false;
	
	@Override
	public void onCreate() {
		System.out.println("created service");
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Uri data = intent.getData();
		final String ipAdress = intent.getStringExtra("IP_ADDRESS");
		final int port = intent.getIntExtra("PORT", 4444);
		
		System.out.println("starting socket: " + ipAdress + " port: " + port);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				socket = null;
				try {
					socket = new Socket(ipAdress, port);
					System.out.println("socket open");
					isConnected = true;
//					s.setKeepAlive(true);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		return Service.START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public class CustomBinder extends Binder {
		public SimpleStatisticsService getService() {
			return SimpleStatisticsService.this;
		}
	}
	
	public SimpleSensorData getSimpleSensorData() {
		BufferedReader input = null;
		OutputStream output = null;
		String string = null;
	    try {
	    	System.out.println("get simple sensor data");
	    	input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = socket.getOutputStream();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MILLISECOND, SENSOR_TIME_OUT);
            
            output.write(SIMPLE_SENSOR_DATA);
            System.out.println("sent request");
            
            if (!socket.isClosed())
            	string = input.readLine();
            System.out.println("passed read");
       
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
	private SimpleSensorData handleReadLine(String sensorData) {
		String[] pieces = sensorData.split(",");
		try {
			String year = pieces[0], month = pieces[1], day = pieces[2];
			String hour = pieces[3], minute = pieces[4], second = pieces[5];
			if (year.contains(ACK_STRING)) { // TODO: fix ack string to be setting or pre-defined
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
