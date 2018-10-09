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


package com.example.blackberry.sensordemo.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.blackberry.sensordemo.models.AppPolicy;
import com.example.blackberry.sensordemo.models.DataModel;
import com.example.blackberry.sensordemo.services.DisplayService;
import com.example.blackberry.sensordemo.services.RainbowLEDService;


public class DataModelBroadcastReceiver extends BroadcastReceiver {


    private static final String TAG = DataModelBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if(intent.getAction().equals(DataModel.BROADCAST_ACTION_AIR_TEMPERATURE_UPDATED)) {
            updateDisplay(context);
        } else if(intent.getAction().equals(DataModel.BROADCAST_ACTION_BMX250_PRESSURE_UPDATED)) {
            updateLEDs(context);
        }
    }

    private void updateDisplay(Context context) {
        Log.d(TAG, "updateDisplay");
        Intent intent = new Intent(context, DisplayService.class);
        intent.setAction(DisplayService.ACTION_DISPLAY);

        //calculate temperature
        DataModel dataModel = DataModel.sharedInstance();

        String temperatureString = String.valueOf(DataModel.sharedInstance().getAirTemperature()).substring(0, (String.valueOf(DataModel.sharedInstance().getAirTemperature()).length()-1 >= 3 ? 3 : String.valueOf(DataModel.sharedInstance().getAirTemperature()).length()-1)) + " C";

        intent.putExtra(DisplayService.EXTRA_TEXT, temperatureString);

        context.startService(intent);
    }

    private void updateLEDs(Context context) {
        Log.d(TAG, "updateLEDs");
        Intent intent = new Intent(context, RainbowLEDService.class);
        intent.setAction(RainbowLEDService.ACTION_LEDS);

        DataModel dataModel = DataModel.sharedInstance();

        int leds = (int) (dataModel.getBmx250Pressure() - 970) / 10 + 1;
            leds = (leds > 7 ? 7 : leds);
        leds = (leds < 0 ? 0 : leds);

        intent.putExtra(RainbowLEDService.EXTRA_LED_COUNT, leds);
        intent.putExtra(RainbowLEDService.EXTRA_LED_COLOR, AppPolicy.sharedInstance().getLedColor());

        context.startService(intent);
    }

}
