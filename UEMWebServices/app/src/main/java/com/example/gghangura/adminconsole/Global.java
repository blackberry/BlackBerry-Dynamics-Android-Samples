package com.example.gghangura.adminconsole;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import com.good.gd.GDAndroid;

import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by gghangura on 2017-03-22.
 */

//Singlton Class
public class Global {

    //instance of the class
    private static final Global ourInstance = new Global();

    //function to get the instance
    public static Global getInstance() {
        return ourInstance;
    }

    //making the constructor global
    private Global() {

    }

    //GD shares prefernces
    SharedPreferences sp = GDAndroid.getInstance().getGDSharedPreferences("My GD Shared Prefs",
            android.content.Context.MODE_PRIVATE);

    //rest task
    MyDownloadTask restTask;

    //soap task
    SoapTask soapTask;

    //context to show alerts
    Context con;

    //enum to show what user wants to see
    enum groupOrApplication{
        nothing,
        group,
        application
    }

    //enum to specify which rest api to call
    enum restApiIndentifier {
        checkServer,
        getHeader,
        with
    }

    //enum to specify which soap api to call
    enum soapApiIdentifier {
        user,
        wipe,
        applications,
        getHeader
    }

    //check what does user want to see groups or applications
    groupOrApplication gorA;

    //base Rest url
    String restUrl;

    //base soap url
    String soapUrl;

    //soap authentication token
    String soapAuthToken;

    //rest header
    String restHeader;

    //username which the user entered
    String userName;

    //device the user wants to delete
    Element userDevice;

    //user returned by the rest api
    RestUser selectedRestUser;

    //the uid of the device which the user wants to delete
    String userDeviceUid;

    //user returned by the soap api
    Element soapUser;

    //push back two activities
    boolean closeActivity = false;

    ProgressDialog progress;

    //make the rest api call
    public void makeRestCall(String url, ResponseData res, restApiIndentifier restIden) {
        if (restTask != null) {
            restTask.cancel(true);
        }
        Object[] params = {url, res, restHeader, restIden};
        restTask = (MyDownloadTask) new MyDownloadTask().execute(params);
    }

    //show alert
    public void showAlert(final String alert, final String message) {

        Handler mainHandler = new Handler(con.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(con)
                        .setTitle(alert)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .show();
            }
        };
        mainHandler.post(myRunnable);

    }

    //show progress bar
    public void ShowProgress() {
        progress = new ProgressDialog(con);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

    }

    //make soap api call
    public void makeSoapCall(ResponseData res, soapApiIdentifier iden) {
        if (soapTask != null) {
            soapTask.cancel(true);
        }
        Object[] params = new Object[0];
        if (iden == soapApiIdentifier.user) {
            params = new Object[]{soapUrl, res, soapAuthToken, iden};
        } else if (iden == soapApiIdentifier.wipe) {
            params = new Object[]{soapUrl, res, soapAuthToken, iden, userDeviceUid};
        } else if (iden == soapApiIdentifier.applications){
            params = new Object[]{soapUrl, res, soapAuthToken, iden, soapUser.getElementsByTagName("ns2:uid").item(0).getTextContent()};
        } else if (iden == soapApiIdentifier.getHeader){
            params = new Object[]{soapUrl, res, "", iden, userName};
        }

        soapTask = (SoapTask) new SoapTask().execute(params);
    }

    //save data to GD shared preferences
    public void saveToPreferneces(String[] values) {

        sp.edit().putString("url",values[0]).commit();
        sp.edit().putString("provider",values[1]).commit();
        sp.edit().putString("auth",values[2]).commit();
        sp.edit().putString("tenant",values[3]).commit();
        sp.edit().putString("username",values[4]).commit();
        sp.edit().putString("password",values[5]).commit();
    }

    //fetch data from shared preferences
    public String[] getFromPreferneces() {
        String[] values = new String[6];

        values[0] = sp.getString("url","https://attlab-uem.attlab.sw.rim.net:18084");
        values[1] = sp.getString("provider","local");
        values[2] = sp.getString("auth","LOCAL");
        values[3] = sp.getString("tenant","S98376076");
        values[4] = sp.getString("username","admin");
        values[5] = sp.getString("password","password");

        return values;
    }

    //dismiss the progress bar
    public void DismissProgress() {
        progress.dismiss();
    }

}
