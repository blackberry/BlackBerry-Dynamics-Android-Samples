package com.example.blackberry.sensordemo.networking.socket;

import android.net.Proxy;

import com.good.gd.net.GDSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * Created by davidarnold on 01/12/2017.
 */

public class CustomGDSocket extends GDSocket {

    private String host;
    private int port;
    private boolean isSecure;

    private InetAddress address;
    private InetAddress localAddr;
    private int localPort;

    private Proxy proxy;


    public CustomGDSocket() throws IOException {
        super();
    }

    public CustomGDSocket(boolean isSecure) throws IOException {
        super(isSecure);
        this.isSecure = isSecure;
    }

    public CustomGDSocket(Proxy proxy) throws Exception {
        super();
        this.proxy = proxy;
    }

    public CustomGDSocket(String host, int port) throws Exception {
        super();
        this.host = host;
        this.port = port;
    }

    public CustomGDSocket(String host, int port, InetAddress localAddr, int localPort) throws SocketException, IOException {
        super();
        this.host = host;
        this.port = port;
        this.localAddr = localAddr;
        this.localPort = localPort;

    }

    public CustomGDSocket(String host, int port, boolean isSecure) throws Exception {
        super(isSecure);
        this.host = host;
        this.port = port;
        this.isSecure = isSecure;
    }

    public CustomGDSocket(InetAddress address, int port) throws Exception {
        super();
        this.address = address;
        this.port = port;
    }

    public CustomGDSocket(InetAddress address, int port, InetAddress localAddr, int localPort) throws Exception {
        super();
        this.address = address;
        this.port = port;
        this.localAddr = localAddr;
        this.localPort = localPort;
    }

    @Override
    public void connect(SocketAddress socketAddress) {
        try {
            connect(socketAddress, getSoTimeout());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect(SocketAddress socketAddress, int timeout) throws IOException {
        //super.connect(socketAddress, timeout);

        /*
        socketAddress InetSocketAddress
            holder InetSocketAddressHolder
                port    1883
                addr    Inet4Address
                    holder  InetAddressHolder
                        hostName    iot.eclipse.org
         */

        InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
        String hostName = inetSocketAddress.getHostName();
        int port = inetSocketAddress.getPort();

        connect(hostName, port, timeout);
    }

    @Override
    public void connect(String hostname, int port, int timeout) throws IOException {
        super.connect(hostname,port,timeout);
    }


}
