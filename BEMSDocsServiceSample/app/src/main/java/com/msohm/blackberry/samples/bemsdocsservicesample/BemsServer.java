/* Copyright (c) 2017  BlackBerry Limited.
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
*/

package com.msohm.blackberry.samples.bemsdocsservicesample;

import java.io.Serializable;

/**
 * Created by msohm on 10/6/2016.
 */

//A class to store BEMS server details.

public class BemsServer implements Serializable
{

    private String server;  //The server name and port.
    private int priority;   //The priority.

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


