/* Copyright (c) 2023 BlackBerry Ltd.
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

package com.good.gd.example.securesql;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.widget.Toast;

import com.good.gd.example.utils.DbContract;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.os.Looper.getMainLooper;

/**
 * Class CancellationSignalUtil is created to demonstrate functionality of CancellationSignal class
 * and ability to cancel query in progress.  
 * To see how CancellationSignal class works press “Add 500 contacts”, then after contacts will be added,
 * press “Heavy query” - and you will have ability to cancel this query.
 * This functionality is present only on devices with API 16 and above.
 */

public class CancellationSignalUtil {

    private static CancellationSignalUtil _instance = null;

    private boolean inProgress = false;
    private Context context;
    private AlertDialog alertDialog;
    private QueryResult result;
    private String message;
    private Object signal; // android.os.CancellationSignal, to support API less than 16
    private ExecutorService executor;


    private CancellationSignalUtil() {
    }

    private ExecutorService getExecutor() {
        if(executor == null) {
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    public static CancellationSignalUtil getInstance() {
        if (_instance == null) {
            _instance = new CancellationSignalUtil();
        }
        return _instance;
    }

    public void update(Context context) {
        this.context = context;
        if (inProgress == true) {
            showAlertDialog();
        }
    }

    public void start() {
        if (inProgress == false) {
            final HeavyQuery hq = new HeavyQuery();
            hq.query();
        } else {
            Toast.makeText(context, "Busy Processing Previous Query. Try after some time", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAlertDialog() {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setMessage(message).setTitle("Heavy query");
            if (result != null) {
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        alertDialog = null;
                        inProgress = false;
                        result = null;
                    }
                });
            } else {
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((android.os.CancellationSignal) signal).cancel();

                    }
                });
            }

            alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }

    private class QueryResult {
        public Integer count;
        public boolean isCanceled;

        public QueryResult(Integer count, boolean isCanceled) {
            this.count = count;
            this.isCanceled = isCanceled;
        }
    }

    private class HeavyQuery {
        private long startTime;
        Handler uiHandler = new Handler(getMainLooper());

        void query() {
            onPreQuery();

            Runnable task = new Runnable() {
                QueryResult result;

                @Override
                public void run() {
                    result = doInBackground();
                    uiHandler.post(() -> onPostQuery(result));
                }
            };
            getExecutor().submit(task);
        }

        private void onPreQuery() {
            startTime = System.currentTimeMillis();

            signal = new CancellationSignal();

            message = "Executing...";
            inProgress = true;
            showAlertDialog();
        }

        private QueryResult doInBackground() {
            inProgress = true;
            Cursor cursor = null;
            if (context != null) {
                final ContentResolver contentResolver = context.getContentResolver();
                try {
                    Uri uri = DbContract.CONTENT_CONTACTS_HEAVY_QUERY_URI;
                    cursor = contentResolver.query(uri, null, null, null, null, ((android.os.CancellationSignal) signal));

                } catch (RuntimeException ex) {
                    return new QueryResult(0, true);
                }
            }
            return cursor != null ? new QueryResult(cursor.getCount(), false) : new QueryResult(0, false);
        }

        protected void onPostQuery(QueryResult queryResult) {
            final double time = (System.currentTimeMillis() - startTime) / 1000d;

            result = queryResult;
            message = "Count: " + queryResult.count + "; Time: " + time + " s";

            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }

            if (queryResult.isCanceled) {
                Toast.makeText(context, "Query canceled.", Toast.LENGTH_LONG).show();
                inProgress = false;
                result = null;
            } else {
                showAlertDialog();
            }
        }
    }
}