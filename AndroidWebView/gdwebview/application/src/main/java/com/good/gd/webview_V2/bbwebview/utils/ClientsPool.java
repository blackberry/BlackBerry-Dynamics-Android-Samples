/*
 * Copyright (c) 2020 BlackBerry Limited.
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
package com.good.gd.webview_V2.bbwebview.utils;

import android.util.Log;

import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class ClientsPool {

    private final Queue<String> allIds = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, Host> hosts = new ConcurrentHashMap<>();

    public void add(String id){
        boolean offer = allIds.offer(id);

        if(!offer){
            throw new RuntimeException("shared queue is full");
        }
    }

    public Host forHost(String host) {
        Host value = new Host(host);
        Host host1 = hosts.putIfAbsent(host, value);

        return host1 == null?value:host1;
    }

    public void dispose() {
        allIds.clear();
        hosts.clear();
    }

    public class Host {

        public String getName() {
            return name;
        }

        final String name;

        Host(String name) {
            this.name = name;
            tag_ = "ClientsPool(" + name + ")";
        }

        final BlockingQueue<String> availableQueue = new ArrayBlockingQueue<String>(8);
        final Set<String> workingSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
        final String tag_;

        public String take(){
            String result = null;
            try{
                Log.w(tag_, "take IN");
                String connectedId = availableQueue.poll();
                if(connectedId == null){
                    Log.i(tag_, "take 20");
                    String idFromPool = allIds.poll();

                    while (idFromPool == null) {
                        Log.i(tag_, "take 21");
                        try {
                            idFromPool = availableQueue.poll(1, TimeUnit.SECONDS);
                        } catch (Exception e){
                            e.printStackTrace();
                            Log.e(tag_, "take 30",e);
                        }
                    }

                    workingSet.add(result = idFromPool);
                    Log.i(tag_, "take 10 (" + result + ")");
                } else {
                    workingSet.add(result = connectedId);
                    Log.i(tag_, "take 11 (" + result + ")");
                }

            } catch(Exception e){
                e.printStackTrace();
                Log.e(tag_, "take 31 res("+ result +")",e);
            }

            Log.w(tag_, "take OUT res(" + result + ")");
            return result;
        }

        public void release(String id){
            try {
                Log.i(tag_, "returnToPool IN id(" + id + ")");
                boolean removed = workingSet.remove(id);

                if(!removed){
                    Log.i(tag_, "returnToPool 33 already called. id(" + id + ")");
                } else {
                    if(availableQueue.offer(id)){
                        Log.i(tag_, "returnToPool 11 ok id(" + id + ")");
                    } else {
                        Log.i(tag_, "returnToPool 20 poolIsFull id(" + id + ")");
                        allIds.add(id);
                    }
                }

            } catch(Exception e){
                e.printStackTrace();
                Log.e(tag_, "returnToPool 30 id(" + id + ")",e);
            } finally {
                Log.w(tag_, "returnToPool OUT id(" + id + ")");
            }
        }
    }
}