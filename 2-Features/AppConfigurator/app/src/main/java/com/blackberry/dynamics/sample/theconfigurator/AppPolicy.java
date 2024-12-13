/* Copyright (c) 2021 BlackBerry Limited.
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

package com.blackberry.dynamics.sample.theconfigurator;

import java.util.Map;
import java.util.Vector;


public class AppPolicy
{

    //Color values defined in the App Policy.
    public static final int BLACK = 0;
    public static final int BLUE = 1;
    public static final int RED = 2;
    public static final int SILVER = 3;
    public static final int TURQUOISE = 4;
    public static final int YELLOW = 5;

    private Map<String, Object> policy;

    public AppPolicy(Map<String, Object> map)
    {
        policy = map;
    }

    public void setPolicy(Map<String, Object> map)
    {
        policy = map;
    }

    //Returns the version specified that is a hidden variable in the app policy.
    public String getAppPolicyVersion()
    {
        //If the policy has been set (not null), extract the version.
        if (policy != null)
        {
            return (String)policy.get("version");
        }
        else
        {
            return "unknown";
        }
    }

    //Returns true if Enable Sound checkbox is checked in the app policy, false if it is not.
    public boolean enableSound()
    {
        //If the policy has been set (not null), extract the enableSound value.
        if (policy != null)
        {
            if (policy.containsKey("enableSound")) {
                return ((Boolean) policy.get("enableSound")).booleanValue();
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    //Returns true if Auto Play Sound on Startup checkbox is checked in the app policy,
    //false if it is not.
    public boolean enableAutoPlaySound()
    {
        //If the policy has been set (not null), extract the enableAutoPlaySound value.
        if (policy != null)
        {
            if (policy.containsKey("enableAutoPlaySound"))
            {
                return ((Boolean) policy.get("enableAutoPlaySound")).booleanValue();
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    //Returns true if Convertible checkbox is checked in the app policy, false if it is not.
    public boolean isConvertible()
    {
        //If the policy has been set (not null), extract the isConvertible value.
        if (policy != null)
        {
            if (policy.containsKey("isConvertible")) {
                return ((Boolean) policy.get("isConvertible")).booleanValue();
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    //Returns true if Car Name multiselect is checked in the app policy, false if it is not.
    public boolean displayCarName()
    {
        //If the policy has been set (not null), extract the visibleElements
        //value and check if it contains "name".  This indicates "Car Name" was checked.
        if (policy != null)
        {
            Vector displayElements = (Vector)policy.get("visibleElements");

            if (displayElements != null && displayElements.contains("name"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }

    }

    //Returns true if Car Image multiselect is checked in the app policy, false if it is not.
    public boolean displayCarImage()
    {
        //If the policy has been set (not null), extract the visibleElements
        //value and check if it contains "image".  This indicates "Car Image" was checked.
        if (policy != null)
        {
            Vector displayElements = (Vector)policy.get("visibleElements");

            if (displayElements != null && displayElements.contains("image"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    //Returns true if Car Description multiselect is checked in the app policy, false if it is not.
    public boolean displayCarDescription()
    {
        //If the policy has been set (not null), extract the visibleElements
        //value and check if it contains "description".  This indicates "Car Description" was checked.
        if (policy != null)
        {
            Vector displayElements = (Vector)policy.get("visibleElements");

            if (displayElements != null && displayElements.contains("description"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    //Returns the value entered into the Car Name text field in the app policy.
    //If it wasn't set "Not set" is returned.
    public String getCarName()
    {
        //If the policy has been set (not null), extract the carName.
        if (policy != null)
        {
            return (String)policy.get("carName");
        }
        else
        {
            return "Not set";
        }
    }

    //Returns the value entered into the Car Description text field in the app policy.
    //If it wasn't set "Not set" is returned.
    public String getCarDescription()
    {
        //If the policy has been set (not null), extract the carDescription.
        if (policy != null)
        {
            return (String)policy.get("carDescription");
        }
        else
        {
            return "Not set";
        }
    }

    //Returns the numeric value corresponding to the color chosen in the color select field in
    // the app policy.  If it is not set, 0 (black) is returned.  Black is the first choice
    //available in the select box and appears chosen to the user when the policy is first viewed.
    //However, the exteriorColor value is not sent to the application unless the administrator has
    //actually selected something from the select list. So a default of 0 (black) is used if not set.
    public int getCarColor()
    {
        //If the policy has been set (not null), extract the exteriorColor value.
        if (policy != null)
        {
            Integer color = (Integer)policy.get("exteriorColor");

            if (color != null)
            {
                return color.intValue();
            }
            else
            {
                return 0;
            }
        }
        else
        {
            return 0;
        }
    }

}
