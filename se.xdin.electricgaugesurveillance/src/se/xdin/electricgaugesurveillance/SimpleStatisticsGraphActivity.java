package se.xdin.electricgaugesurveillance;


import se.xdin.electricgaugesurveillance.application.service.SimpleStatisticsService;
import se.xdin.electricgaugesurveillance.fragments.ChartFragment;
import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SimpleStatisticsGraphActivity extends Activity {
	
	private SimpleStatisticsService service;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((SimpleStatisticsService.CustomBinder) binder).getService();
			Log.d("service", "Service connected in SimpleStatisticsGraphActivity");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			service = null;
		}
	};
	
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		doBindService();
		setContentView(R.layout.simple_statistics_chart);
	}
	
	private void doBindService() {
		Intent service = new Intent(SimpleStatisticsGraphActivity.this, SimpleStatisticsService.class);
		bindService(service, mConnection, Context.BIND_AUTO_CREATE);
		Log.d("service", "Service bound in SimpleStatisticsGraphActivity");
	}
	
	@Override
	public void onDestroy() {
		unbindService(mConnection);
		Log.d("service", "Service unbound in SimpleStatisticsGraphActivity");
		super.onDestroy();
	}


	@Override
	protected void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (null == savedInstanceState) {
			getFragmentManager().beginTransaction().replace(R.id.chart, new ChartFragment()).commit();
		}
	}
	
	public SimpleStatisticsService getService() {
		return service;
	}

}




