package se.xdin.electricgaugesurveillance.fragments;

import se.xdin.electricgaugesurveillance.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SimpleStatisticsGraphInformationFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.simple_statistics_chart_information_view,
				container, false);
		return view;
	}
}
