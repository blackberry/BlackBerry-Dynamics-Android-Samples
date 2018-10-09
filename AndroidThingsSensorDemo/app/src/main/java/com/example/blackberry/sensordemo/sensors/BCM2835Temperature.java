/* Copyright (c) 2018 BlackBerry Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.example.blackberry.sensordemo.sensors;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.RandomAccessFile;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import org.jetbrains.annotations.NotNull;


public class BCM2835Temperature implements AutoCloseable {


    private static final String TAG = BCM2835Temperature.class.getSimpleName();

    private static final String CPU_TEMPERATURE_PATH = "/sys/class/thermal/thermal_zone0/temp";
    private static final long UPDATE_CPU_DELAY = 100;

    /**
     * Mininum temperature in Celsius the sensor can measure.
     */
    public static final float MIN_TEMP_C = -40f;
    /**
     * Maximum temperature in Celsius the sensor can measure.
     */
    public static final float MAX_TEMP_C = 85f;

    private Float mCpuTemperature = 0f;
    private Observable<Float> mCpuTemperatureObservable;
    private Handler mHandler;

    public BCM2835Temperature() {
        // create observable for CPI temperature
        mCpuTemperatureObservable = getCpuTemperatureObservable();
        mCpuTemperatureObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        mHandler = new Handler();
        mHandler.post(mTemperatureRunnable);
    }

    private Runnable mTemperatureRunnable = new Runnable() {
        @Override public void run() {
            mCpuTemperatureObservable.subscribe(new Subscriber<Float>() {
                @Override public void onCompleted() {
                    Log.e(BCM2835Temperature.TAG, "Completed.");
                }

                @Override public void onError(Throwable e) {
                    Log.e(BCM2835Temperature.TAG, "Error: " + e.getMessage());
                }

                @Override public void onNext(Float resultCpuTemperature) {
                    Log.i(BCM2835Temperature.TAG, "Temp: " + resultCpuTemperature); // for debugging
                    mCpuTemperature = resultCpuTemperature;
                }
            });

            mHandler.postDelayed(mTemperatureRunnable, UPDATE_CPU_DELAY);
        }
    };

    /**
     * Creates an observable which reads the CPU temperature from the file system.
     *
     * @return the observable
     */
    @NotNull private Observable<Float> getCpuTemperatureObservable() {
        return Observable.create(new Observable.OnSubscribe<Float>() {
            @Override public void call(Subscriber<? super Float> subscriber) {
                RandomAccessFile reader = null;
                try {
                    reader = new RandomAccessFile(CPU_TEMPERATURE_PATH, "r");
                    String rawTemperature = reader.readLine();
                    float cpuTemperature = Float.parseFloat(rawTemperature) / 1000f;
                    subscriber.onNext(cpuTemperature);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    subscriber.onError(ex);
                } finally {
                    if(reader != null) try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

    /**
     * Read the current temperature.
     *
     * @return the current temperature in degrees Celsius
     */
    public float readTemperature() throws IOException {
        //get current Temperature and return as float
        return mCpuTemperature;
    }



    @Override
    public void close() {
        mCpuTemperatureObservable.unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        mCpuTemperatureObservable = null;

    }
}
