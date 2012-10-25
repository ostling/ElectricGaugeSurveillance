package se.xdin.electricgaugesurveillance.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.xdin.electricgaugesurveillance.R;
import se.xdin.electricgaugesurveillance.models.SimpleSensorData;
import se.xdin.electricgaugesurveillance.util.EnergyHelper;
import se.xdin.electricgaugesurveillance.util.SensorDataHelper;

import android.app.ListFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SimpleStatisticsFragment extends ListFragment {
	SharedPreferences sensorSettings = null;
	private Handler handler = new Handler();
	private SimpleAdapter adapter;
	ArrayList<Map<String, String>> list;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Fetch preferences
		sensorSettings = getActivity().getSharedPreferences(getString(R.string.SENSOR_PREFS), 0);
		
		// TEMPORARY: Set ip and port
		sensorSettings.edit().putString(getString(R.string.SENSOR_PREFS_IP_ADRESS), "10.10.100.55").commit();
		sensorSettings.edit().putInt(getString(R.string.SENSOR_PREFS_PORT), 4444).commit();
		
		installAdapter();
	}
	
	private void installAdapter() {
		// TODO: Consider using a ArrayAdapter instead of simpleadapter
				new Thread(new Runnable() {
					public void run() {
					// Create list
					list = getData();
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
		installAdapter();
	}
	
	
	
	private ArrayList<Map<String, String>> getData() {
		String ipAdress = sensorSettings.getString(getString(R.string.SENSOR_PREFS_IP_ADRESS), null); // TODO : Handle null
		int port = sensorSettings.getInt(getString(R.string.SENSOR_PREFS_PORT), 4444); // TODO : Handle default port
		SimpleSensorData sensorData = SensorDataHelper.getSimpleSensorData(ipAdress, port);
		
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (sensorData != null) {
			list.add(putData(getString(R.string.simple_current_power_label), 
					Double.toString(sensorData.getCurrentPower()) + " kW")); // (label, value)
			list.add(putData(getString(R.string.simple_totalt_energy), 
					Integer.toString((int)EnergyHelper.calculateKWH(sensorData.getNumberOfTicks(),
							sensorSettings.getInt(getString(R.string.SENSOR_PREFS_NUMBER_OF_TICKS),
									Integer.parseInt(getString(R.string.default_number_of_ticks))))) + " kWH"));
			list.add(putData(getString(R.string.simple_last_contact),
					sensorData.getLastContact().getTime().toString())); // TODO: fix with SimpleDateFormat
			System.out.println("date set for list: " + sensorData.getLastContact().getTime().toString() + "lsit: " + list);
		}
		return list;
	}
	
	
	
	private HashMap<String, String> putData(String name, String value) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("name", name);
		item.put("value", value);
		return item;
	}
	
	
}
