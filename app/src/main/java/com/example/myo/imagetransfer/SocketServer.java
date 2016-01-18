package com.example.myo.imagetransfer;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by myo on 2016. 1. 13..
 */
public class SocketServer extends Thread {
    private ServerSocket ss;
    private Socket s;
    private String path;

    public SocketServer(int port){
        try {
            // 1. ServerSocket open
            ss = new ServerSocket(port);
            Log.d("ss","accept");
        } catch (IOException e) {
            Log.d("SocketServer","Error : IOException");
        }
    }
    public boolean accept(){
        return s !=null;
    }
    public void setPath(String path){
        /**
         * 전송할 이미지 path 설정
         */
        this.path = path;
    }

    public void run(){
        try {
            s = ss.accept();
            // 1. file read
            FileInputStream fis = new FileInputStream(path);
            byte[] data = new byte[fis.available()];
            fis.read(data);

            // 2. send data
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeObject(data);

            oos.close();
        } catch (IOException e) {
            Log.d("SocketClient","Error : IOException");
        }
    }


    public void close(){
        try {
            s.close();
        } catch (IOException e) {
            Log.d("SocketServer","Error : IOException");
        }
    }
}
