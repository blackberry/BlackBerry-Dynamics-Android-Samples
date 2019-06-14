package com.good.automated.test.screenFinder.parsing;

import com.good.automated.test.screenFinder.parsing.runnable.DumpRunnable;

import java.util.concurrent.Executors;

/**
 * Facade for UI dump and parsing result xml.
 */
public class UIParsingFacade {

    private SerialExecutor parsingExecutor;
    private SerialExecutor dumpExecutor;
    private DumpQueue dumpQueue;

    public UIParsingFacade() {
        parsingExecutor = new SerialExecutor(Executors.newSingleThreadExecutor());
        dumpExecutor = new SerialExecutor(Executors.newSingleThreadExecutor());
        dumpQueue = new DumpQueue();
    }

    /**
     * Executes UI dumping and parsing if the result xml.
     */
    public void createDumpAndMap() {
        Runnable dumpRunnable = new DumpRunnable(dumpQueue);
        dumpExecutor.execute(dumpRunnable);

//        We are able to parse just dumped UI xml, but have no need in it right now
//        Runnable parsingRunnable = new ParsingRunnable(dumpQueue);
//        parsingExecutor.execute(parsingRunnable);
    }

}
