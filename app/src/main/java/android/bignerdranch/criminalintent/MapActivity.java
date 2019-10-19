package android.bignerdranch.criminalintent;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

public class MapActivity extends FragmentActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mLocation;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLocation = mSensorManager.getDefaultSensor(Sensor.TYPE_)
    }

}
