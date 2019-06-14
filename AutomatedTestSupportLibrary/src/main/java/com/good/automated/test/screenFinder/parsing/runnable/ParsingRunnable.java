package com.good.automated.test.screenFinder.parsing.runnable;

import android.util.Log;

import com.good.automated.test.screenFinder.parsing.DumpQueue;
import com.good.automated.test.screenFinder.parsing.Parser;

import java.io.IOException;
import java.io.InputStream;

public class ParsingRunnable implements Runnable {

    private static final String TAG = ParsingRunnable.class.getSimpleName();

    private DumpQueue dumpQueue;
    private InputStream xmlStream;
    private Parser parser;

    public ParsingRunnable(DumpQueue dumpQueue) {
        this.dumpQueue = dumpQueue;
        this.parser = new Parser();
    }

    @Override
    public void run() {

        try {
            xmlStream = dumpQueue.get();
        } catch (InterruptedException e) {
            Log.e(TAG, "Failed to get the next UI XML dump!", e);
            return;
        }
        System.out.println(xmlStream);
        try {
            parser.parse(xmlStream);
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse UI XML dump!", e);
        }

    }
}
