package se.xdin.electricgaugesurveillance;


import se.xdin.electricgaugesurveillance.fragments.ChartFragment;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class SimpleStatisticsGraphActivity extends Activity {
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_statistics_chart);
	}

	@Override
	protected void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (null == savedInstanceState) {
			getFragmentManager().beginTransaction().replace(R.id.chart, new ChartFragment()).commit();
		}
	}

}




