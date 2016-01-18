package com.example.myo.imagetransfer;

import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
         * 이미지를 저장할 path 설정
         */
        this.path = path;
    }

    public void read(){
        try {
            // 1. inputSteam set
            ObjectInputStream oos = new ObjectInputStream(socket.getInputStream());
            byte[] data = (byte[]) oos.readObject();

            // 2. outputString set - File
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(data);
            oos.close();
        }
        catch (IOException e) {
            Log.d("SocketServer","Error : IOException");
        } catch (ClassNotFoundException e) {
            Log.d("SocketServer","Error : ClassNotFoundException");
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
