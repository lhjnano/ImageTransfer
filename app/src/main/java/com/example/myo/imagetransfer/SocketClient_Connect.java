package com.example.myo.imagetransfer;

/**
 * Created by myo on 2016. 1. 12..
 */
public class SocketClient_Connect extends Socket_Connect {

    public SocketClient_Connect(Socket_Observer observer) {
        super(observer,false);
    }

    public void responseBroadcast(String ip) {
        new Send(ip, SERVERSIDE_READPORT,"checking...",1).start();
    }

    public void ok(String ip) {
        new Send(ip, SERVERSIDE_READPORT,"ok...",1).start();
    }

}
