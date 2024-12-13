package com.example.blackberry.sensordemo.networking.socket;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

/**
 * Created by davidarnold on 01/12/2017.
 */

public class CustomSocketFactory extends SocketFactory {

    private static int DEFAULT_TIMEOUT = 30000;

    @Override
    public Socket createSocket()  throws IOException {
        return new CustomGDSocket();
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        try {
            CustomGDSocket socket = new CustomGDSocket(host, port);
            socket.connect(host, port, DEFAULT_TIMEOUT);
            return socket;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddr, int localPort) throws IOException, UnknownHostException {
        CustomGDSocket socket = new CustomGDSocket(host, port, localAddr, localPort);
        socket.connect(host, port, DEFAULT_TIMEOUT);
        return socket;
    }

    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        try {
            CustomGDSocket socket = new CustomGDSocket(address, port);
            socket.connect(address.getHostName(), port, DEFAULT_TIMEOUT);
            return socket;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException {
        try {
            CustomGDSocket socket = new CustomGDSocket(address, port, localAddr, localPort);
            socket.connect(address.getHostName(), port, DEFAULT_TIMEOUT);
            return socket;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
