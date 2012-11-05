package se.xdin.electricgaugesurveillance;

import se.xdin.electricgaugesurveillance.application.service.SimpleStatisticsService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SimpleStatisticsActivity extends Activity {
	
	private SimpleStatisticsService service;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((SimpleStatisticsService.CustomBinder) binder).getService();
			Log.d("service", "Service connected in SimpleStatisticsActivity");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!isServiceRunning()) {
			startService();
		}
		doBindService();
		setContentView(R.layout.simple_statistics);
	}
	
	private void startService() {
		Intent service = new Intent(SimpleStatisticsActivity.this, SimpleStatisticsService.class);
		service.putExtra("IP_ADDRESS", "10.10.100.36");
		service.putExtra("PORT", 4444);
		startService(service);
		Log.d("service", "Simple statistics service started");
	}
	
	private void doBindService() {
		bindService(new Intent(SimpleStatisticsActivity.this, SimpleStatisticsService.class), mConnection, Context.BIND_AUTO_CREATE);
		Log.d("service", "Service bound in SimpleStatisticsActivity");
	}
	
	public SimpleStatisticsService getService() {
		return service;
	}
	
	@Override
	protected void onDestroy() {
		unbindService(mConnection);
		Log.d("service", "Service unbound in SimpleStatisticsActivity");
		super.onDestroy();
	}
	
	private boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("se.xdin.electricgaugesurveillance.application.service.SimpleStatisticsService".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
