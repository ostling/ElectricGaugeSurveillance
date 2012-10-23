package se.xdin.electricgaugesurveillance.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		return null;
	}
	
	private HashMap<String, String> putData(String name, String value) {
		return null;
	}
}
