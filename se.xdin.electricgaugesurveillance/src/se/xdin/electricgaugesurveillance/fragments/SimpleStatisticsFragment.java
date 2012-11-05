package se.xdin.electricgaugesurveillance.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.xdin.electricgaugesurveillance.R;
import se.xdin.electricgaugesurveillance.SimpleStatisticsActivity;
import se.xdin.electricgaugesurveillance.SimpleStatisticsGraphActivity;
import se.xdin.electricgaugesurveillance.application.service.SimpleStatisticsService;
import se.xdin.electricgaugesurveillance.models.SimpleSensorData;
import se.xdin.electricgaugesurveillance.util.EnergyHelper;
import android.app.ListFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TwoLineListItem;

public class SimpleStatisticsFragment extends ListFragment {
	SharedPreferences sensorSettings = null;
	private Handler handler = new Handler();
	private SimpleAdapter adapter;
	ArrayList<Map<String, String>> list;
	Thread thread;
	boolean timerIsRunning = false;
	
	private static int MAX_RETRYS;
	private static String IP_ADDRESS;
	private static int PORT;
	private static int SOCKET_TIMEOUT;
	
	private static final int SENSOR_SAMPLE_TIME = 5000;
	
	private final CountDownTimer mTimer = new CountDownTimer(15 * 60 * 1000, SENSOR_SAMPLE_TIME) {
		@Override
		public void onTick(final long millisUntilFinished) {
			final SimpleStatisticsService service = getService();
			if (service != null) {
				if (service.isConnected) {
					new Thread(new Runnable() {
						public void run() {
							String[] from = { "name", "value" };
							System.out.println("requesting data SIMPLE");
							int[] to = {android.R.id.text1, android.R.id.text2 };
							adapter = new SimpleAdapter(getActivity(), getData(service),
								android.R.layout.simple_list_item_2, from, to);
							
							handler.post(new Runnable() {
								public void run() {
									setListAdapter(adapter);
								}
							});
						}
					}).start();
				} else {
					System.out.println("socket not connected, service.isConnected == false");
				}
			} else {
				System.out.println("socket not connected, service == nulll");
			}
		}

		@Override
		public void onFinish() {}
	};
	
	public SimpleStatisticsService getService() {
		return ((SimpleStatisticsActivity) getActivity()).getService();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Fetch preferences
		sensorSettings = getActivity().getSharedPreferences(getString(R.string.SENSOR_PREFS), 0);
		
		// TEMPORARY: Set ip and port
		sensorSettings.edit().putString(getString(R.string.SENSOR_PREFS_IP_ADDRESS), "10.10.100.36").commit();
		sensorSettings.edit().putInt(getString(R.string.SENSOR_PREFS_PORT), 4444).commit();
		sensorSettings.edit().putInt(getString(R.string.SENSOR_PREFS_MAX_RETRYS), 3);
		sensorSettings.edit().putInt(getString(R.string.SENSOR_PREFS_SOCKET_TIMEOUT), 20000);
		
		IP_ADDRESS = sensorSettings.getString(getString(R.string.SENSOR_PREFS_IP_ADDRESS), null); // TODO : Handle null
		PORT = sensorSettings.getInt(getString(R.string.SENSOR_PREFS_PORT), 4444); // TODO : Handle default port
		MAX_RETRYS = sensorSettings.getInt(getString(R.string.SENSOR_PREFS_MAX_RETRYS), 3);
		SOCKET_TIMEOUT = sensorSettings.getInt(getString(R.string.SENSOR_PREFS_SOCKET_TIMEOUT), 20000);
		
		startExec();
	}
	
	private void startExec() {
		String[] values = new String[] { "Waiting" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
		
		startTimer();
	}
	
	private void startTimer() {
		if (!timerIsRunning) {
			mTimer.start();
			timerIsRunning = true;
		}
	}
	
	private void stopTimer() {
		if (timerIsRunning) {
			mTimer.cancel();
			timerIsRunning = false;
		}
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
		stopTimer();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		startTimer();
		super.onResume();
	}
	
	private ArrayList<Map<String, String>> getData(SimpleStatisticsService service) {
		SimpleSensorData sensorData = service.getSimpleSensorData();
		
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
	
	
	
	private HashMap<String, String> putData(String name, String value) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("name", name);
		item.put("value", value);
		return item;
	}
	
	
}
