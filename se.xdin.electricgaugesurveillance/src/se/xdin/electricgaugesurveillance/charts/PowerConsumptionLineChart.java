package se.xdin.electricgaugesurveillance.charts;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

public class PowerConsumptionLineChart {
	GraphicalView chart;
	LiveTimeSeries series;
	
	private void init(int maxPoints) {
		series = new LiveTimeSeries("Test Line");
		series.setMaxPoints(maxPoints);
	}
	
	public View getChartView(Context context, int maxPoints) {
		init(maxPoints);
		int []x = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		int []y = { 10, 20, 50, 10, 25, 45, 15, 30, 40 };
		
		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		
		chart = ChartFactory.getTimeChartView(context, dataset, mRenderer, "Test title");
		return chart;
	}
	
	/**
	 * Called to add more data to chart
	 * @param time
	 * @param value
	 */
	public void addData(Date time, Double value) {
		series.add(time , value);
		chart.repaint();
	}

}
