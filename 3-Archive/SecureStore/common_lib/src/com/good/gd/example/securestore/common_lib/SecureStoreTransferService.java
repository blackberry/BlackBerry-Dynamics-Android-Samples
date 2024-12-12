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

package com.good.gd.example.securestore.common_lib;

import java.util.HashMap;
import java.util.Map;

/**
 * Service Definition specific to SecureStore App. Used to transfer information between Handheld and
 * Wearable Secure Store application
 */
public class SecureStoreTransferService {

    // version of the service for AppKinetics
    public final static String VERSION = "1.0.0.0";

    // name of Service
    public final static String SERVICENAME = "com.good.gd.example.securestoreservice";

    // name of Good service method for sending number of local files
    public final static String SERVICE_NUM_FILES_METHOD = "numberFiles";

        public final static String NUM_FILES = "num_files";

        private Map<String,Object> map = new HashMap<String,Object>();

        private void add(String key, Object value)
        {
            map.put(key, value);
        }

        public Map<String,Object> toMap()
        {
            return map;
        }

        @SuppressWarnings("unchecked")
        public void setMap(Object aObject){
            map = (Map<String, Object>)aObject;
        }

        public void setNumberFiles(int aNumberFiles){
            add(NUM_FILES, aNumberFiles);
        }

        public int getNumberFiles(){
            return (Integer) map.get(NUM_FILES);
        }

    }

