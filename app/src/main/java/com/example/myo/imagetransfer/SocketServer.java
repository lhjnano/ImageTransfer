package com.example.myo.imagetransfer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketServer extends Thread {
    private ServerSocket ss;
    private Socket s;
    private String name;
    private String path;
    private Handler handler;

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
    public void setPath(String name, String path){
        /**
         * 전송할 이미지 path 설정
         */
        this.name = name;
        this.path = path;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void run(){
        try {
            s = ss.accept();
            // 1. file read
            FileInputStream fis = new FileInputStream(path);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();

            // 2. send name
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            oos.writeUTF(name);
            oos.flush();

            OutputStream os = new BufferedOutputStream(s.getOutputStream());
            // 3. sending
            sendMessage("show",null);
            for( int i = 0 ; i < data.length; i++ ) {
                os.write(data[i]);
                // 로딩바를 구현하기 위해서 메시지큐에 메시지를 전송
                sendMessage("progress", ""+(int)i*100/data.length );
            }
            sendMessage("dismiss", null);
            os.close();
            oos.flush();
            oos.close();
            close();
        } catch (IOException e) {
            Log.d("SocketServer","Error : IOException");
        }
    }

    public void close(){
        try {
            s.close();
        } catch (IOException e) {
            Log.d("SocketServer","Error : IOException");
        }
    }

    public void sendMessage(String key, String value) {
        Message progress = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        progress.obj = bundle;
        handler.sendMessageAtFrontOfQueue(progress);
    }
}
