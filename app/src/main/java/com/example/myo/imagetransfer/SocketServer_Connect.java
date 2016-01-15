package com.example.myo.imagetransfer;

/**
 * Created by myo on 2016. 1. 12..
 */
public class SocketServer_Connect extends Socket_Connect {

    public SocketServer_Connect(Socket_Observer observer) {
        super(observer,true);
    }

    // ip 검색 브로드캐스팅
    public void broadcast() {
        new Send("255.255.255.255", SERVERSIDE_WRITEPORT,"Broadcasting...",3).start();
    }

    // 데이터 전송 요청
    public void request(String ip) {
        new Send(ip, SERVERSIDE_WRITEPORT,"request...",1).start();
    }

}
