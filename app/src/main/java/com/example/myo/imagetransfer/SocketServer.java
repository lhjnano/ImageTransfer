package com.example.myo.imagetransfer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

            // 3. send img

            for( int i = 0 ; i < data.length; i++ ) {
                oos.write(data[i]);

                // 로딩바를 구현하기 위해서 메시지큐에 메시지를 전송
                Message progress = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putInt("progress", i/data.length*100 );
                progress.obj = bundle;
                handler.sendMessage(progress);
                oos.flush();
            }
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
