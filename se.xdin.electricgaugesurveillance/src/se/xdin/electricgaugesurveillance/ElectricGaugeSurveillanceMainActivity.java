package se.xdin.electricgaugesurveillance;

import se.xdin.electricgaugesurveillance.menu.SettingsMenuActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ElectricGaugeSurveillanceMainActivity extends Activity {
	
	private SharedPreferences sensorSettings = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric_gauge_surveillance_main);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.mainmenu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.item1:
    		startActivity(new Intent(getApplicationContext(), SettingsMenuActivity.class));
    	}
    	return true;
    }
}
