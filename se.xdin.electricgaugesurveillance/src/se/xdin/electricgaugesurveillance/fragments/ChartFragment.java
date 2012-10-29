package se.xdin.electricgaugesurveillance.fragments;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import se.xdin.electricgaugesurveillance.R;
import se.xdin.electricgaugesurveillance.models.SimpleSensorData;
import se.xdin.electricgaugesurveillance.util.SensorDataHelper;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ChartFragment extends Fragment implements OnClickListener {

	private static Random RAND = new Random();
	private static final String TIME = "H:mm:ss";

	private static final int TEN_SEC = 10000;
	private static final int TWO_SEC = 2000;
	private static final float RATIO = 0.618033988749895f;

	private View mViewZoomIn;
	private View mViewZoomOut;
	private View mViewZoomReset;
	private GraphicalView mChartView;
	private XYSeriesRenderer renderer;
	private XYMultipleSeriesRenderer mRenderer;
	private XYMultipleSeriesDataset mDataset;
	private TimeSeries series;
	private double mYAxisMin = Double.MAX_VALUE;
	private double mYAxisMax = Double.MIN_VALUE;
	private double mZoomLevel = 0.1;
	private int mYAxisPadding = 5;
	
	private SharedPreferences sensorSettings = null;
	private String ipAdress;
	private int port;


	private final CountDownTimer mTimer = new CountDownTimer(15 * 60 * 1000, 5000) {
		@Override
		public void onTick(final long millisUntilFinished) {
			new Thread(new Runnable() {
				
				public void run() {
					addValue();
				}
			}).start();
		}

		@Override
		public void onFinish() {}
	};

	private final ZoomListener mZoomListener = new ZoomListener() {
		public void zoomReset() {
			mZoomLevel = 1;
			scrollGraph(new Date().getTime());
		}

		public void zoomApplied(final ZoomEvent event) {
			if (event.isZoomIn()) {
				mZoomLevel /= 2;
			}
			else {
				mZoomLevel *= 2;
			}
			scrollGraph(new Date().getTime());
		}
	};

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Fetch preferences
		sensorSettings = getActivity().getSharedPreferences(getString(R.string.SENSOR_PREFS), 0);
		
		ipAdress = sensorSettings.getString(getString(R.string.SENSOR_PREFS_IP_ADRESS), null); // TODO : Handle null
		port = sensorSettings.getInt(getString(R.string.SENSOR_PREFS_PORT), 4444); // TODO : Handle default port
		
		System.out.println("ip: " + ipAdress + ", port: " + port);
		
		mDataset = new XYMultipleSeriesDataset();
		mRenderer = new XYMultipleSeriesRenderer();

		mRenderer.setLabelsColor(Color.LTGRAY);
		mRenderer.setAxesColor(Color.LTGRAY);
		mRenderer.setGridColor(Color.rgb(136, 136, 136));
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setApplyBackgroundColor(true);

		mRenderer.setLegendTextSize(20);
		mRenderer.setLabelsTextSize(20);
		mRenderer.setPointSize(8);
		mRenderer.setMargins(new int[] { 60, 60, 60, 60 });

		mRenderer.setFitLegend(true);
		mRenderer.setShowGrid(true);
		mRenderer.setZoomEnabled(true);
		mRenderer.setExternalZoomEnabled(true);
		mRenderer.setAntialiasing(true);
		mRenderer.setInScroll(true);

	}

	private TimeSeries createSeries() {
		TimeSeries ts = new TimeSeries("Random");
		return ts;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
			mYAxisPadding = 9;
			mRenderer.setYLabels(15);
		}

		final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.simple_chart_fragment, container, false);
		mChartView = ChartFactory.getTimeChartView(getActivity(), mDataset, mRenderer, TIME);
		mChartView.addZoomListener(mZoomListener, true, false);
		view.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return view;
	}


	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mViewZoomIn = getActivity().findViewById(R.id.zoom_in);
		mViewZoomOut = getActivity().findViewById(R.id.zoom_out);
		mViewZoomReset = getActivity().findViewById(R.id.zoom_reset);
		mViewZoomIn.setOnClickListener(this);
		mViewZoomOut.setOnClickListener(this);
		mViewZoomReset.setOnClickListener(this);
		
		series = createSeries();
		mDataset.addSeries(series);
		
		renderer = new XYSeriesRenderer();
		renderer.setColor(Color.RED);
		mRenderer.addSeriesRenderer(renderer);

		mTimer.start();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (null != mTimer) {
			mTimer.cancel();
		}
	}


	private double randomValue() {
		final int value = Math.abs(RAND.nextInt(32));
		final double percent = (value * 100) / 31.0;
		return ((int) (percent * 10)) / 10.0;
	}

	private void addValue() {
		SimpleSensorData sensorData = SensorDataHelper.getSimpleSensorData(ipAdress, port);
		final double value = sensorData.getCurrentPower();
		if (mYAxisMin > value) mYAxisMin = value;
		if (mYAxisMax < value) mYAxisMax = value;

		final Date now = new Date();
		final long time = now.getTime();

		series.add(now, value);

		scrollGraph(time);
		mChartView.repaint();
	}

	private void scrollGraph(final long time) {
		final double[] limits = new double[] { time - TEN_SEC * mZoomLevel, time + TWO_SEC * mZoomLevel, mYAxisMin - mYAxisPadding,
				mYAxisMax + mYAxisPadding };
		mRenderer.setRange(limits);
	}

	public void onClick(final View v) {
		System.out.println("ON CLICK");
		switch (v.getId()) {
		case R.id.zoom_in:
			mChartView.zoomIn();
			break;

		case R.id.zoom_out:
			mChartView.zoomOut();
			break;

		case R.id.zoom_reset:
			mChartView.zoomReset();
			break;

		default:
			break;
		}

	}
}
