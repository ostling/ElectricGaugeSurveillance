package se.xdin.electricgaugesurveillance;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ElectricGaugeSurveillanceMainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric_gauge_surveillance_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_electric_gauge_surveillance_main, menu);
        return true;
    }
}
