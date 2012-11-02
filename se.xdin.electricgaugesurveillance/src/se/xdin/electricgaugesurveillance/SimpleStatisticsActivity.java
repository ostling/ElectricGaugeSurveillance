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
	
	public SimpleStatisticsService service;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			System.out.println("conn");
			service = ((SimpleStatisticsService.CustomBinder) binder).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("service started");
		setContentView(R.layout.simple_statistics);
		doBindService();
	}
	
	private void doBindService() {
		startService(new Intent(SimpleStatisticsActivity.this, SimpleStatisticsService.class));
		bindService(new Intent(SimpleStatisticsActivity.this, SimpleStatisticsService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onDestroy() {
		System.out.println("activity destroyed");
		super.onDestroy();
	}
}
