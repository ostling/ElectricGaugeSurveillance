package se.xdin.electricgaugesurveillance;

import se.xdin.electricgaugesurveillance.charts.PowerConsumptionLineChart;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class SimpleStatisticsGraphActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_statistics_chart);
		
		PowerConsumptionLineChart powerView = new PowerConsumptionLineChart();
		View chartView = powerView.getChartView(this, 200); // TODO: Fetch maxpoints from settings
		chartView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1f));
		LinearLayout layout = (LinearLayout) findViewById(R.id.powerChartLayout);
		
		layout.addView(chartView,0);
	}

}
