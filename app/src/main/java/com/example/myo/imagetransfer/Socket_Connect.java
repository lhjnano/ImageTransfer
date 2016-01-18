package com.example.myo.imagetransfer;

import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * Created by myo on 2016. 1. 12..
 */
public class Socket_Connect {
    public final int SERVERSIDE_WRITEPORT = 8000;
    public final int SERVERSIDE_READPORT  = 8200;
    private int receivcePort;
    private Socket_Observer observer;

    private Receive receive = null;

    public Socket_Connect(Socket_Observer observer, boolean isServer) {
        receivcePort = (isServer)? SERVERSIDE_READPORT:SERVERSIDE_WRITEPORT;
        this.observer = observer;
    }
    // 수신 시작
    public void _start(){
        receive = new Receive(receivcePort,observer);
        receive.start();
    }
    // 수신 종료
    public void _stop() {
        receive._stop();
    }

    // 발신부 클래스
    class Send extends Thread {
        private int count       = 0;
        private InetAddress ip  = null;
        private int port        = 0;
        private String message  = null;

        public Send(String ip, int port, String message, int count) {
            // 주소 지정
            try {
                this.ip = InetAddress.getByName(ip);
            } catch (UnknownHostException e) {
                Log.d("SocketServer_Connect", "Error : UnknownHostException");
            }
            this.port = port;
            // 메세지 설정
            this.message = message;
            // 반복 횟수
            this.count = count;
        }

        // 데이터 전송
        public void run(){
            while(count-- > 0) {
                try {
                    DatagramPacket dp = new DatagramPacket(message.getBytes(), message.length(), ip, port);
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);
                    ds.close();
                    Thread.sleep(500);
                } catch (SocketException e) {
                    Log.d("SearchSocket", "Error : SocketException, count : " + count );
                } catch (IOException e) {
                    Log.d("SearchSocket", "Error : IOException");
                } catch (InterruptedException e) {
                    Log.d("SocketServer_Connect", "Error : InterruptedException");
                }
            }
        }
    }
    // 수신부 클래스
    class Receive extends Thread {
        private boolean searchingIp = true;

        private int port                = 0;
        private Socket_Observer observer= null;
        private Object lock             = new Object();

        public Receive(int port, Socket_Observer observer){
            this.port = port;
            this.observer = observer;
        }

        public void run(){
            while(searchingIp){
                try {
                    // 1. 수신
                    byte[] msgBytes = new byte[1000];
                    DatagramPacket dp = new DatagramPacket(msgBytes, msgBytes.length);
                    DatagramSocket ds = new DatagramSocket(port);
                    ds.receive(dp);
                    ds.close();

                    // 2. ip 확인
                    String clientIp = dp.getAddress().getHostAddress();
                    if(clientIp !=null){
                        //3. observing
                        Bundle message = new Bundle();
                        message.putString("ip",clientIp);
                        message.putString("code",dp.getData().toString());
                        observer.update(message);
                    }
                } catch (SocketException e) {
                    Log.d("SearchSocket", "Error : SocketException_Receive");
                } catch (IOException e) {
                    Log.d("SearchSocket", "Error : IOException");
                }
            }
        }

        public void _stop(){
            synchronized (lock){
                searchingIp = false;
            }
        }
    }
}
