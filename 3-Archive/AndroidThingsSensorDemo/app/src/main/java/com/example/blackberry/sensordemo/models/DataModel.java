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


package com.example.blackberry.sensordemo.models;

import android.content.Context;
import android.content.Intent;

import org.jetbrains.annotations.Nullable;

public class DataModel {

    private static final String TAG = DataModel.class.getSimpleName();

    public static final String BROADCAST_ACTION_BMX250_TEMPERATURE_UPDATED = "BROADCAST_ACTION_BMX250_TEMPERATURE_UPDATED";
    public static final String BROADCAST_ACTION_BMX250_PRESSURE_UPDATED = "BROADCAST_ACTION_BMX250_PRESSURE_UPDATED";
    public static final String BROADCAST_ACTION_BCM2385_TEMPERATURE_UPDATED = "BROADCAST_ACTION_BCM2385_TEMPERATURE_UPDATED";
    public static final String BROADCAST_ACTION_AIR_TEMPERATURE_UPDATED = "BROADCAST_ACTION_AIR_TEMPERATURE_UPDATED";

    private Context mContext;

    //Singleton Code
    private static DataModel instance;

    private Context context;

    public void setContext(Context value) {
        context = value;
    }

    private DataModel() {
        super();
    }

    public static DataModel sharedInstance() {
        if(DataModel.instance == null)
        {
           DataModel.instance = new DataModel();
        }
        return DataModel.instance;
    }



    //Instance Variables & Setters/Getters

    private float bmx250Temperature;
    private float bmx250Pressure;
    private float bcm2385Temperature;
    private float airTemperature;

    public float getBmx250Temperature() {
        return bmx250Temperature;
    }

    public float getBmx250Pressure() {
        return bmx250Pressure;
    }

    public float getBcm2385Temperature() {
        return bcm2385Temperature;
    }

    public float getAirTemperature() {
        return airTemperature;
    }

    public void setBmx250Temperature(float value) {
        bmx250Temperature = value;
        broadcast(BROADCAST_ACTION_BMX250_TEMPERATURE_UPDATED);
        calculateAirTemperature();
    }

    public void setBmx250Pressure(float value) {
        bmx250Pressure = value;
        broadcast(BROADCAST_ACTION_BMX250_PRESSURE_UPDATED);
    }

    public void setBcm2385Temperature(float value) {
        bcm2385Temperature = value;
        broadcast(BROADCAST_ACTION_BCM2385_TEMPERATURE_UPDATED);
        calculateAirTemperature();
    }

    public void setAirTemperature(float value) {
        airTemperature = value;
        broadcast(BROADCAST_ACTION_AIR_TEMPERATURE_UPDATED);
    }

    public void calculateAirTemperature() {
        float tempConstant = AppPolicy.sharedInstance().getTemperatureConstant();
        float result = ((getBmx250Temperature() - tempConstant * getBcm2385Temperature()) / (1.0f - tempConstant));
        setAirTemperature(result);
    }

    //Broadcast Intent to Notify of Changes
    private void broadcast(String broadcastAction){
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(broadcastAction);

        context.getApplicationContext().sendBroadcast(broadCastIntent);
    }

}
