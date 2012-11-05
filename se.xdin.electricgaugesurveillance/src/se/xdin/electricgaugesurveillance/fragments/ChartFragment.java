package se.xdin.electricgaugesurveillance.fragments;

import java.util.Date;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import se.xdin.electricgaugesurveillance.R;
import se.xdin.electricgaugesurveillance.application.service.SimpleStatisticsService;
import se.xdin.electricgaugesurveillance.models.SimpleSensorData;
import se.xdin.electricgaugesurveillance.SimpleStatisticsGraphActivity;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
	private double mZoomLevel = 10;
	private int mYAxisPadding = 5;
	
	private SharedPreferences sensorSettings = null;
	private String IP_ADDRESS;
	private int PORT;
	private int SOCKET_TIMEOUT;
	private double lastValue = 0;
	private boolean timerIsRunning = false;
	
	private static final int SENSOR_SAMPLE_TIME = 1500;
	private static final String X_LABEL = "Time";
	private static final String Y_LABEL = "Power (kW)";
	private static final String CHART_TITLE = "Power consumption";
	
	private Handler handler = new Handler();
	
	private final CountDownTimer mTimer = new CountDownTimer(15 * 60 * 1000, SENSOR_SAMPLE_TIME) {
		@Override
		public void onTick(final long millisUntilFinished) {
			final SimpleStatisticsService service = getService();
			if (service != null) {
				if (service.isConnected) {
					new Thread(new Runnable() {
						public void run() {
							final double tempValue = getData(service);
							handler.post(new Runnable() {
								public void run() {
									addValue(tempValue);
								};
							});
						}
					}).start();
				}
			}
		}

		@Override
		public void onFinish() {}
	};
	
	private double getData(SimpleStatisticsService service) {
		SimpleSensorData sensorData = service.getSimpleSensorData();
		if (sensorData != null) {
			lastValue = sensorData.getCurrentPower();
		}
		return lastValue;
	}
	
	public SimpleStatisticsService getService() {
		return ((SimpleStatisticsGraphActivity) getActivity()).getService();
	}

	private final ZoomListener mZoomListener = new ZoomListener() {
		public void zoomReset() {
			mZoomLevel = 10;
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
		
		IP_ADDRESS = sensorSettings.getString(getString(R.string.SENSOR_PREFS_IP_ADDRESS), null); // TODO : Handle null
		PORT = sensorSettings.getInt(getString(R.string.SENSOR_PREFS_PORT), 4444); // TODO : Handle default port
		SOCKET_TIMEOUT = sensorSettings.getInt(getString(R.string.SENSOR_PREFS_SOCKET_TIMEOUT), 20000);
		
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
		
		mRenderer.setXTitle(X_LABEL);
		mRenderer.setYTitle(Y_LABEL);
		mRenderer.setChartTitle(CHART_TITLE);
		mRenderer.setLabelsTextSize((float) 10);
	}

	private TimeSeries createSeries() {
		TimeSeries ts = new TimeSeries("Power (kW)");
		return ts;
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		if (Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation) {
			mYAxisPadding = 9;
			mRenderer.setYLabels(15);
		}

		final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.simple_chart_fragment, container, false);
		mChartView = ChartFactory.getTimeChartView(getActivity(), mDataset, mRenderer, "APAN");
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
		
		renderer.setFillBelowLine(true);
		renderer.setFillBelowLineColor(Color.DKGRAY);
		renderer.setColor(Color.GREEN);
		renderer.setLineWidth(2);
		
		mRenderer.addSeriesRenderer(renderer);
		addValue(0.0);
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
	public void onPause() {
		stopTimer();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		startTimer();
		super.onResume();
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
}
