package se.xdin.electricgaugesurveillance;

import se.xdin.electricgaugesurveillance.application.service.SimpleStatisticsService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class SimpleStatisticsActivity extends Activity {
	
	private SimpleStatisticsService service;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			System.out.println("setting service");
			service = ((SimpleStatisticsService.CustomBinder) binder).getService();
			System.out.println("service satt");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_statistics);
		doBindService();
	}
	
	private void doBindService() {
		Intent service = new Intent(SimpleStatisticsActivity.this, SimpleStatisticsService.class);
		service.putExtra("IP_ADDRESS", "10.10.100.36");
		service.putExtra("PORT", 4444);
		startService(service);
		bindService(new Intent(SimpleStatisticsActivity.this, SimpleStatisticsService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	
	public SimpleStatisticsService getService() {
		return service;
	}
	
	@Override
	protected void onDestroy() {
		unbindService(mConnection);
		super.onDestroy();
	}
}
