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

import java.util.Map;

public class AppPolicy {

    private static final String TAG = AppPolicy.class.getSimpleName();

    //Singleton Code
    private static AppPolicy instance;

    private AppPolicy() {
        super();
    }

    public static AppPolicy sharedInstance() {
        if(AppPolicy.instance == null)
        {
            AppPolicy.instance = new AppPolicy();
        }
        return AppPolicy.instance;
    }



    //Instance Variables & Setters/Getters
    private Map<String, Object> _policy;
    public void setPolicy(Map<String, Object> map)
    {
        this._policy = map;
    }

    public String getMQTTHost() {
        if(this._policy != null && this._policy.containsKey("mqttHost") && this._policy.get("mqttHost") != null) {
            String host = (String)this._policy.get("mqttHost");
            return host;
        }
        else {
            return "";
        }
    }

    public int getMqttPort() {
        if(this._policy != null && this._policy.containsKey("mqttPort") && this._policy.get("mqttPort") != null) {
            int mqttPort = (int)this._policy.get("mqttPort");
            return mqttPort;
        }
        else {
            return 1883;
        }
    }

    public String getMqttTopic() {
        if(this._policy != null && this._policy.containsKey("mqttTopic") && this._policy.get("mqttTopic") != null) {
            String mqttTopic = (String)this._policy.get("mqttTopic");
            return mqttTopic;
        } else {
            return "";
        }
    }

    public int getMqttPublishingInterval() {
        if(this._policy != null && this._policy.containsKey("mqttPublishingInterval") && this._policy.get("mqttPublishingInterval") != null) {
            int mqttPublishingInterval = (int)this._policy.get("mqttPublishingInterval");
            return mqttPublishingInterval;
        }
        else {
            return 30000;
        }
    }

    public float getTemperatureConstant() {
        if(this._policy != null && this._policy.containsKey("temperatureConstant") && this._policy.get("temperatureConstant") != null) {
            Double temperatureConstant = (Double) this._policy.get("temperatureConstant");
            return temperatureConstant.floatValue();
        } else {
            return 0.5f;
        }
    }

    public String getLedColor() {
        if(this._policy != null && this._policy.containsKey("ledColor") && this._policy.get("ledColor") != null) {
            String legColor = (String)this._policy.get("ledColor");
            return legColor;
        }
        else {
            return "#FFFFFF";
        }
    }

    public String getLocalFilePath() {
        if(this._policy != null && this._policy.containsKey("localFilePath") && this._policy.get("localFilePath") != null) {
            String localFilePath = (String)this._policy.get("localFilePath");
            return localFilePath;
        }
        else {
            return "log.txt";
        }
    }

    public int getLocalLoggingInterval() {
        if(this._policy != null && this._policy.containsKey("localLoggingInterval") && this._policy.get("localLoggingInterval") != null) {
            int localLoggingInterval = (int)this._policy.get("localLoggingInterval");
            return localLoggingInterval;
        }
        else {
            return 60000;
        }
    }

    public int getLocalMaxFileSize() {
        if(this._policy != null && this._policy.containsKey("localMaxFileSize") && this._policy.get("localMaxFileSize") != null) {
            int localMaxFileSize = (int)this._policy.get("localMaxFileSize");
            return localMaxFileSize;
        }
        else {
            return 1024;
        }
    }

    public String getRemoteFilePath() {
        if(this._policy != null && this._policy.containsKey("remoteFilePath") && this._policy.get("remoteFilePath") != null) {
            String remoteFilePath = (String)this._policy.get("remoteFilePath");
            return remoteFilePath;
        }
        else {
            return "remoteLog.txt";
        }
    }

    public int getRemoteUploadInterval() {
        if(this._policy != null && this._policy.containsKey("remoteUploadInterval") && this._policy.get("remoteUploadInterval") != null) {
            int remoteUploadInterval = (int)this._policy.get("remoteUploadInterval");
            return remoteUploadInterval;
        }
        else {
            return 600000;
        }
    }

    public String getPolicyVersion() {
        if(this._policy != null && this._policy.containsKey("policyVersion") && this._policy.get("policyVersion") != null) {
            String policyVersion = (String)this._policy.get("policyVersion");
            return policyVersion;
        }
        else {
            return "";
        }
    }

}
