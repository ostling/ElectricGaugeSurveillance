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
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SimpleStatisticsFragment extends ListFragment {
	SharedPreferences sensorSettings = null;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Fetch preferences
		sensorSettings = getActivity().getSharedPreferences(getString(R.string.SENSOR_PREFS), 0);
		
		// Create list
		ArrayList<Map<String, String>> list = getData();
		String[] from = { "name", "value" };
		int[] to = {android.R.id.text1, android.R.id.text2 };
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), list,
				android.R.layout.simple_list_item_2, from, to);
		setListAdapter(adapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}
	
	private ArrayList<Map<String, String>> getData() {
		String ipAdress = sensorSettings.getString(getString(R.string.SENSOR_PREFS_IP_ADRESS), null); // TODO : Handle null
		int port = sensorSettings.getInt(getString(R.string.SENSOR_PREFS_PORT), 4444); // TODO : Handle default port
		SimpleSensorData sensorData = SensorDataHelper.getSimpleSensorData(ipAdress, port);
		
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list.add(putData(getString(R.string.simple_current_power_label), 
				Integer.toString(sensorData.getCurrentPower()) + " kW")); // (label, value)
		list.add(putData(getString(R.string.simple_totalt_energy), 
				Integer.toString((int)EnergyHelper.calculateKWH(sensorData.getNumberOfTicks(),
						sensorSettings.getInt(getString(R.string.SENSOR_PREFS_NUMBER_OF_TICKS),
								Integer.parseInt(getString(R.string.default_number_of_ticks))))) + " kWH"));
		list.add(putData(getString(R.string.simple_last_contact),
				sensorData.getLastContact().getTime().toString())); // TODO: fix with SimpleDateFormat
		return list;
	}
	
	
	
	private HashMap<String, String> putData(String name, String value) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("name", name);
		item.put("value", value);
		return item;
	}
	
	
}
