package com.yammer.responsivesensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import rx.AsyncEmitter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

public class RxSensorManager {

    private static final String LOG_TAG = RxSensorManager.class.getName();

    private final SensorManager sensorManager;

    public RxSensorManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public Observable<SensorEvent> naiveObserveSensorChanged(final Sensor sensor, final int samplingPreiodUs) {
        return Observable.create(new Observable.OnSubscribe<SensorEvent>() {
            @Override
            public void call(final Subscriber<? super SensorEvent> subscriber) {
                final SensorEventListener sensorEventListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        // (3) - checking for subscriptions before emitting values
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(event);
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                        // ignored for this example
                    }
                };

                // (1) - unregistering listener when unsubscribed
                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        try {
                            Log.d(LOG_TAG, "Un-subscribing from SensorManager, calling unregisterListener().");
                            sensorManager.unregisterListener(sensorEventListener, sensor);
                        } catch (Exception ex) {
                            // (3) - checking for subscriptions before emitting values
                            if (!subscriber.isUnsubscribed()) {
                                // (2) - reporting exceptions via onError()
                                subscriber.onError(ex);
                            }
                        }
                    }
                }));

                sensorManager.registerListener(sensorEventListener, sensor, samplingPreiodUs);
            }
        });
    }

    public Observable<SensorEvent> observeSensorChanged(final Sensor sensor, final int samplingPeriodUs, AsyncEmitter.BackpressureMode backpressureMode) {
        return Observable.fromAsync(new Action1<AsyncEmitter<SensorEvent>>() {
            @Override
            public void call(final AsyncEmitter<SensorEvent> sensorEventAsyncEmitter) {
                final SensorEventListener sensorListener = new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent sensorEvent) {
                        sensorEventAsyncEmitter.onNext(sensorEvent);
                    }

                    @Override
                    public void onAccuracyChanged(Sensor originSensor, int i) {
                        // ignored for this example
                    }
                };
                // (1) - unregistering listener when unsubscribed
                sensorEventAsyncEmitter.setCancellation(new AsyncEmitter.Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        Log.d(LOG_TAG, "Un-subscribing from SensorManager, calling unregisterListener().");
                        sensorManager.unregisterListener(sensorListener, sensor);
                    }
                });
                sensorManager.registerListener(sensorListener, sensor, samplingPeriodUs);

            }
            // (4) - specifying the backpressure strategy to use
        }, backpressureMode);
    }

}
