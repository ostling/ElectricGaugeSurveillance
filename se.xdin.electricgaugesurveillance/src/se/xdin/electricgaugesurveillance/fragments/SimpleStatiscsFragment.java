package se.xdin.electricgaugesurveillance.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.xdin.electricgaugesurveillance.R;
import se.xdin.electricgaugesurveillance.models.Sensor;
import se.xdin.electricgaugesurveillance.models.SimpleSensorData;
import se.xdin.electricgaugesurveillance.util.EnergyHelper;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class SimpleStatiscsFragment extends ListFragment {
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}
	
	private ArrayList<Map<String, String>> getData() {
		SimpleSensorData sensorData = null;//getSensorData();
		Sensor sensor = null;//getSensor();
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list.add(putData(getString(R.string.simple_current_power_label), 
				Integer.toString(sensorData.getCurrentPower()))); // (label, value)
		list.add(putData(getString(R.string.simple_totalt_energy), 
				Integer.toString((int)EnergyHelper.calculateKWH(sensorData.getNumberOfTicks(), sensor.getTicksPerKWH()))));
		list.add(putData(getString(R.string.simple_last_contact),
				sensorData.getLastContact().getTime().toString())); // TODO: fix with SimpleDateFormat
		return list;
	}
	
	private HashMap<String, String> putData(String name, String value) {
		return null;
	}
	
	
}
