package com.example.myo.imagetransfer;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by myo on 2016. 1. 13..
 */
public class SocketClient {
    private String path;
    private Socket socket;

    public SocketClient(String ip,int port){
        try {
            // 1. socket open
            socket = new Socket(ip, port);
        } catch (IOException e) {
            Log.d("SocketClient","Error : IOException");
        }
    }

    public void setPath(String path){
        /**
         * 전송할 이미지 path 설정
         */
        this.path = path;
    }

    public void write(){
        try {
            // 1. file read
            FileInputStream fis = new FileInputStream(path);
            byte[] data = new byte[fis.available()];
            fis.read(data);

            // 2. send data
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(data);

            oos.close();
        } catch (IOException e) {
            Log.d("SocketClient","Error : IOException");
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.d("SocketClient","Error : IOException");
        }
    }
}
