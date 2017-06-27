package com.msohm.blackberry.samples.bemsdirectorylookupsample;

import java.io.Serializable;

/**
 * Created by msohm on 10/6/2016.
 */

public class BemsServer implements Serializable
{

    private String server;
    private int priority;

    public BemsServer(String server, int priority)
    {
        this.server = server;
        this.priority = priority;
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public String toString()
    {
        return server + " Priority: " + priority;
    }
}


