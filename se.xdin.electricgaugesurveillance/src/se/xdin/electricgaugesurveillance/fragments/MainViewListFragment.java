package se.xdin.electricgaugesurveillance.fragments;

import se.xdin.electricgaugesurveillance.SimpleStatisticsActivity;
import se.xdin.electricgaugesurveillance.SimpleStatisticsGraphActivity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainViewListFragment extends ListFragment {
	
	String[] listActivities = new String[] { "Simple statistics", "Last 24-hours", "Last week"
			, "Last month", "Last year", "More history", "Settings"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_expandable_list_item_1, listActivities);
		setListAdapter(adapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		System.out.println("props listview: " + l + ", view: " + v + ", pos: " + position + ", id: " + id);
		TextView textView = (TextView) v;
		if (textView.getText().equals("Simple statistics")) {
			Intent intent = new Intent(getActivity().getApplicationContext(),
					SimpleStatisticsActivity.class);
			startActivity(intent);
		} else if (textView.getText().equals("Last week")) {
			Intent intent = new Intent(getActivity().getApplicationContext(), SimpleStatisticsGraphActivity.class);
			startActivity(intent);
		} else {
			System.out.println("no match: " + textView.getText());
		}
	}
}
