package com.yammer.responsivesensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;

import rx.AsyncEmitter;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    private Spinner bpModes;

    private SensorManager sensorManager;
    private RxSensorManager rxSensorManager;
    private Sensor accelerometer;
    private Subscription rxSensorSubscription;
    private Subscription rxSensorSubscriptionAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bpModes = (Spinner) findViewById(R.id.bpModes);
        bpModes.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, AsyncEmitter.BackpressureMode.values()));

        setupSensorListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: Unregister existing listeners/subscriptions
    }

    public void registerSensor(View view) {
        Log.d(LOG_TAG, "registerSensor()");
        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterSensor(View view) {
        Log.d(LOG_TAG, "unregisterSensor()");
        sensorManager.unregisterListener(sensorListener, accelerometer);
    }

    public void registerSensorRx(View view) {
        Log.d(LOG_TAG, "registerSensorRx()");
        rxSensorSubscription = rxSensorManager.naiveObserveSensorChanged(accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
                .observeOn(Schedulers.computation())
                .subscribe(sensorChangedOnNext, sensorChangedOnError);
    }

    public void unregisterSensorRx(View view) {
        Log.d(LOG_TAG, "unregisterSensorRx()");
        rxSensorSubscription.unsubscribe();
    }

    public void registerSensorRxAsync(View view) {
        Log.d(LOG_TAG, "registerSensorRxAsync()");
        rxSensorSubscriptionAsync = rxSensorManager.observeSensorChanged(accelerometer, SensorManager.SENSOR_DELAY_FASTEST, (AsyncEmitter.BackpressureMode)bpModes.getSelectedItem())
                .observeOn(Schedulers.computation())
                .subscribe(sensorChangedOnNext, sensorChangedOnError);
    }

    public void unregisterSensorRxAsync(View view) {
        Log.d(LOG_TAG, "unregisterSensorRxAsync()");
        rxSensorSubscriptionAsync.unsubscribe();
    }

    private void setupSensorListener() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rxSensorManager = new RxSensorManager(sensorManager);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            try {
                // simulate a semi-expensive operation
                Thread.sleep(10);
            } catch (InterruptedException ignore) {}
            Log.d(LOG_TAG, "onSensorChanged() sensorEvent.timestamp=" + sensorEvent.timestamp + ", sensorEvent.values=" + Arrays.toString(sensorEvent.values));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            // ignored for this example
        }
    };

    private final Action1<SensorEvent> sensorChangedOnNext = new Action1<SensorEvent>() {
        @Override
        public void call(SensorEvent sensorEvent) {
            try {
                // simulate a semi-expensive operation
                Thread.sleep(10);
            } catch (InterruptedException ignore) {}
            Log.d(LOG_TAG, "sensorChangedOnNext - sensorEvent.timestamp=" + sensorEvent.timestamp + ", sensorEvent.values=" + Arrays.toString(sensorEvent.values));
        }
    };

    private final Action1<Throwable> sensorChangedOnError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.e(LOG_TAG, "sensorChangedOnError", throwable);
        }
    };

}
