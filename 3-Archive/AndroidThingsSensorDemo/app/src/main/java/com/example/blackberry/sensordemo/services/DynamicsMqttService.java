package com.example.blackberry.sensordemo.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import android.util.Log;


import com.example.blackberry.sensordemo.networking.socket.CustomSocketFactory;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jetbrains.annotations.Nullable;


/**
 * Created by davidarnold on 01/12/2017.
 */

public class DynamicsMqttService extends Service {

    private static final String TAG = DynamicsMqttService.class.getSimpleName();

    public static final String ACTION_CONNECT = "ACTION_CONNECT";
    public static final String ACTION_MESSAGE = "ACTION_MESSAGE";

    public static final String BROADCAST_ACTION_READY = "BROADCAST_ACTION_READY";

    public static final String EXTRA_HOST = "EXTRA_HOST";
    public static final String EXTRA_PORT = "EXTRA_PORT";

    public static final String EXTRA_VALUE = "VALUE";
    public static final String EXTRA_TOPIC = "EXTRA_TOPIC";

    private MqttAndroidClient mqttAndroidClient;

    private final String clientId = "DynamicsMqttService" + System.currentTimeMillis();

    @Override
    public void onCreate() {
        super.onCreate();

        //promote the service to foreground in the hope this will run

        String CHANNEL_ID = "dynamicsMQTT";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "DynamicsMQTTService NotificationChannel",
                NotificationManager.IMPORTANCE_DEFAULT);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Dynamics MQTT Service")
                .setContentText("This service provides MQTT Publishing Services for the Application")
                .setSmallIcon(com.good.gd.R.drawable.bbd_logo)
                .build();


        startForeground(1, notification);

        Log.d(TAG, "onCreate");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        if(intent.getAction() == ACTION_CONNECT) {
            connect(intent.getStringExtra(EXTRA_HOST), intent.getIntExtra(EXTRA_PORT,1883));
        }
        else if(intent.getAction() == ACTION_MESSAGE) {
            publishMessage(intent.getStringExtra(EXTRA_VALUE), intent.getStringExtra(EXTRA_TOPIC));
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }


    private void sendBroadcastActionReady()
    {
        try
        {
            Intent broadCastIntent = new Intent();
            broadCastIntent.setAction(BROADCAST_ACTION_READY);

            sendBroadcast(broadCastIntent);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void connect(String host, int port) {
        Log.d(TAG, "connect");

        final String serverUri = "tcp://" + host + ":" + String.valueOf(port);

        if(mqttAndroidClient == null) {

            Context context = this.getApplication().getApplicationContext();

            mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);

            mqttAndroidClient.setCallback(new MqttCallbackExtended() {

                @Override
                public void connectComplete(boolean reconnect, String serverURI) {

                    if (reconnect) {
                        Log.d(TAG,"Reconnected to : " + serverURI);

                    } else {
                        Log.d(TAG,"Connected to: " + serverURI);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    Log.d(TAG,"The Connection was lost.");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d(TAG,"Incoming message: " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    Log.d(TAG, "Delivery Complete");
                }
            });
        }

        /*
        if(mqttAndroidClient.isConnected() && mqttAndroidClient.getServerURI() != serverUri ) {
            try {
                mqttAndroidClient.disconnect();  //this isn't quite correct but will do for now
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        */

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setConnectionTimeout(30);
        mqttConnectOptions.setMaxInflight(10);
        mqttConnectOptions.setKeepAliveInterval(60);
        mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);


        mqttConnectOptions.setSocketFactory(new CustomSocketFactory());


        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "connection successful");

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    sendBroadcastActionReady();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG,"Failed to connect to: " + serverUri);

                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    sendBroadcastActionReady();
                }
            });


        } catch (MqttException ex){
            ex.printStackTrace();
        }


    }


    public void publishMessage(String message, String topic){

        try {

            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());

            if(mqttAndroidClient.isConnected()) {
                mqttAndroidClient.publish(topic, mqttMessage);
                Log.d(TAG,"Message Published");
            }

            if(!mqttAndroidClient.isConnected()){
                mqttAndroidClient.publish(topic, mqttMessage);
                Log.d(TAG,mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
            }

        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
