package se.xdin.electricgaugesurveillance.menu;

import se.xdin.electricgaugesurveillance.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsMenuActivity extends Activity {
	
	private SharedPreferences sensorSettings = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Fetch preferences
		sensorSettings = getSharedPreferences(getString(R.string.SENSOR_PREFS), 0);
		
		setContentView(R.layout.menu_layout);
		EditText ipText = (EditText) findViewById(R.id.ip_address_edit_text);
		EditText portText = (EditText) findViewById(R.id.port_edit_text);
		
		ipText.setText(sensorSettings.getString(getString(R.string.SENSOR_PREFS_IP_ADDRESS), "10.10.100.0"));
		portText.setText(Integer.toString(sensorSettings.getInt(getString(R.string.SENSOR_PREFS_PORT), 4444)));
	}
	
	public void onClick(View view) {
		EditText ipAddress = (EditText) findViewById(R.id.ip_address_edit_text);
		EditText port = (EditText) findViewById(R.id.port_edit_text);
		
		sensorSettings.edit().putString(getString(R.string.SENSOR_PREFS_IP_ADDRESS), ipAddress.getText().toString()).commit();
		sensorSettings.edit().putInt(getString(R.string.SENSOR_PREFS_PORT), Integer.parseInt(port.getText().toString())).commit();
		
		Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
		finish();
	}

}
