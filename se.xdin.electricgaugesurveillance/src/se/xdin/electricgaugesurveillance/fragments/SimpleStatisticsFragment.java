package se.xdin.electricgaugesurveillance.fragments;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import se.xdin.electricgaugesurveillance.R;
import se.xdin.electricgaugesurveillance.SimpleStatisticsGraphActivity;
import se.xdin.electricgaugesurveillance.models.SimpleSensorData;
import se.xdin.electricgaugesurveillance.util.EnergyHelper;
import se.xdin.electricgaugesurveillance.util.SensorDataHelper;

import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.TwoLineListItem;

public class SimpleStatisticsFragment extends ListFragment {
	SharedPreferences sensorSettings = null;
	private Handler handler = new Handler();
	private SimpleAdapter adapter;
	ArrayList<Map<String, String>> list;
	Thread thread;
	private int MAX_RETRYS;
	private String ipAdress;
	private int port;
	
	Socket socket = null;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Fetch preferences
		sensorSettings = getActivity().getSharedPreferences(getString(R.string.SENSOR_PREFS), 0);
		
		// TEMPORARY: Set ip and port
		sensorSettings.edit().putString(getString(R.string.SENSOR_PREFS_IP_ADRESS), "10.10.100.21").commit();
		sensorSettings.edit().putInt(getString(R.string.SENSOR_PREFS_PORT), 4444).commit();
		sensorSettings.edit().putInt(getString(R.string.SENSOR_PREFS_MAX_RETRYS), 3);
		
		ipAdress = sensorSettings.getString(getString(R.string.SENSOR_PREFS_IP_ADRESS), null); // TODO : Handle null
		port = sensorSettings.getInt(getString(R.string.SENSOR_PREFS_PORT), 4444); // TODO : Handle default port
		MAX_RETRYS = sensorSettings.getInt(getString(R.string.SENSOR_PREFS_MAX_RETRYS), 3);
		
		startExec();
	}
	
	private void startExec() {
		new Thread(new Runnable() {
			public void run() {
				if (socket != null) {
					SensorDataHelper.closeSocket(socket);
					socket = null;
				}
				socket = SensorDataHelper.openSocket(ipAdress, port);
			}
		}).start();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, 20000); // Set time out
		
		while (socket == null && Calendar.getInstance().before(cal)) {
			try { Thread.sleep(1000); } catch (Exception e) {}
		}
		
		if (Calendar.getInstance().after(cal)) {
			Toast.makeText(getActivity(), "TIME OUT", Toast.LENGTH_SHORT);
			String[] values = new String[] { "Could not connect, click to retry" };
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, values);
			
			setListAdapter(adapter);
		} else {
			installAdapter();
		}
	}
	
	private void installAdapter() {
		// TODO: Consider using a ArrayAdapter instead of simpleadapter
		new Thread(new Runnable() {
			public void run() {
			// Create list
			ArrayList<Map<String, String>> tempList = getData();
			if (tempList == null) {
				;
			} if (tempList.isEmpty()) {
				;
			} else {
				list = tempList;
			}
			String[] from = { "name", "value" };
			int[] to = {android.R.id.text1, android.R.id.text2 };
			adapter = new SimpleAdapter(getActivity(), list,
				android.R.layout.simple_list_item_2, from, to);
			handler.post(new Runnable() {
				public void run() {
					setListAdapter(adapter);
				};
			});
			}
		}).start();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		TwoLineListItem tlli = (TwoLineListItem) v;
		if (tlli.getText1().getText().toString().equals(getString(R.string.simple_current_power_label))) {
			Intent intent = new Intent(getActivity().getApplicationContext(), SimpleStatisticsGraphActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	public void onPause() {
		SensorDataHelper.closeSocket(socket);
		socket = null;
		super.onPause();
	}
	
	@Override
	public void onResume() {
		if (socket == null)
			startExec();
		super.onResume();
	}
	
	private ArrayList<Map<String, String>> getData() {
		if (socket.isConnected()) {
			SimpleSensorData sensorData = SensorDataHelper.getSimpleSensorData(socket);
			ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
			if (sensorData != null && list != null) {
				list.add(putData(getString(R.string.simple_current_power_label), 
						Double.toString(sensorData.getCurrentPower()) + " kW")); // (label, value)
				list.add(putData(getString(R.string.simple_totalt_energy), 
						Integer.toString((int)EnergyHelper.calculateKWH(sensorData.getNumberOfTicks(),
								sensorSettings.getInt(getString(R.string.SENSOR_PREFS_NUMBER_OF_TICKS),
										Integer.parseInt(getString(R.string.default_number_of_ticks))))) + " kWH"));
				list.add(putData(getString(R.string.simple_last_contact),
						sensorData.getLastContact().getTime().toString())); // TODO: fix with SimpleDateFormat
			}
			return list;
		}
		return null;
	}
	
	
	
	private HashMap<String, String> putData(String name, String value) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("name", name);
		item.put("value", value);
		return item;
	}
	
	
}
