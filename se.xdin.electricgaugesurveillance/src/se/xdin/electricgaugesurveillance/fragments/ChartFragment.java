package se.xdin.electricgaugesurveillance.fragments;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
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
	
	private Socket socket;


	private final CountDownTimer mTimer = new CountDownTimer(15 * 60 * 1000, 1000) {
		@Override
		public void onTick(final long millisUntilFinished) {
			new GetSensorData().execute("");
		}

		@Override
		public void onFinish() {}
	};
	

	private final ZoomListener mZoomListener = new ZoomListener() {
		public void zoomReset() {
			mZoomLevel = 0.1;
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
	
	private void openConnection() {
		new Thread(new Runnable() {
			public void run() {
				if (socket != null) {
					SensorDataHelper.closeSocket(socket);
					socket = null;
				}
				socket = SensorDataHelper.openSocket(ipAdress, port);
			}
		}).start();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MILLISECOND, 20000); // Set time out
		
		while (socket == null || Calendar.getInstance().after(cal)) {
			try { Thread.sleep(1000); } catch (Exception e) {}
		}
		if (Calendar.getInstance().after(cal)) {
			System.out.println("time out");
			Toast.makeText(getActivity(), "TIME OUT", Toast.LENGTH_SHORT);
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		System.out.println("craete");
		super.onCreate(savedInstanceState);
		
		// Fetch preferences
		sensorSettings = getActivity().getSharedPreferences(getString(R.string.SENSOR_PREFS), 0);
		
		ipAdress = sensorSettings.getString(getString(R.string.SENSOR_PREFS_IP_ADRESS), null); // TODO : Handle null
		port = sensorSettings.getInt(getString(R.string.SENSOR_PREFS_PORT), 4444); // TODO : Handle default port
		
		openConnection();
		
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
		TimeSeries ts = new TimeSeries("Power (kW)");
		return ts;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		System.out.println("view");
		if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
			mYAxisPadding = 9;
			mRenderer.setYLabels(15);
		}

		final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.simple_chart_fragment, container, false);
		mChartView = ChartFactory.getTimeChartView(getActivity(), mDataset, mRenderer, "Power consumption");
		mChartView.addZoomListener(mZoomListener, true, false);
		view.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return view;
	}


	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		System.out.println("act");
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
		renderer.setPointStyle(PointStyle.CIRCLE);
		
		mRenderer.addSeriesRenderer(renderer);
		addValue(0.0);
		mTimer.start();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (null != mTimer) {
			mTimer.cancel();
		}
		SensorDataHelper.closeSocket(socket);
	}

	private void addValue(double value) {
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
	
	private class GetSensorData extends AsyncTask<String, Void, Double> {

		@Override
		protected Double doInBackground(String... params) {
			System.out.println("Fetching data, ip: " + ipAdress + ", port: " + port);
			if (!socket.isConnected())
				openConnection();
			if (socket.isConnected()) {
				SimpleSensorData sensorData = SensorDataHelper.getSimpleSensorData(socket);
				
				if (sensorData != null) {
					System.out.println("Data fetched from sensor " + sensorData.getCurrentPower());
					return sensorData.getCurrentPower();
				}
			}
			return 0.0;
		}
		
		@Override
		protected void onPostExecute(Double result) {
			addValue(result);
		}

	}
}
