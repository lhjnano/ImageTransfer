package com.example.myo.imagetransfer;

import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketServer extends AsyncTask<Integer, Integer, Integer> {
    private ServerSocket ss;
    private Socket s;
    private String name;
    private String path;

    public int value =0 ;
    public View view;
    public ProgressBar progress;
    public AlertDialog alertDialog;

    public SocketServer(int port, View view,AlertDialog alertDialog){
        try {
            // 1. ServerSocket open
            ss = new ServerSocket(port);
            Log.d("ss","accept");
        } catch (IOException e) {
            Log.d("SocketServer","Error : IOException");
        }
        this.alertDialog = alertDialog;
        this.view = view;
        progress = (ProgressBar)view.findViewById(R.id.progressBar_loadingbar);

    }

    public void setPath(String name, String path){
        /**
         * 전송할 이미지 path 설정
         */
        this.name = name;
        this.path = path;
    }

    protected void onPreExecute() {
        value = 0;
        progress.setProgress(value);
    }

    protected Integer doInBackground(Integer ... values) {
        while (isCancelled() == false) {
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
                for( int i = 0 ; i < data.length; i++ ) {
                    os.write(data[i]);
                    // 로딩바를 구현하기 위해서 메시지큐에 메시지를 전송
                    value = (int)i*100/data.length;
                    publishProgress(value);
                }

                os.close();
                oos.flush();
                oos.close();
                close();

                alertDialog.dismiss();
            } catch (IOException e) {
                Log.d("SocketServer","Error : IOException");
            }
        }

        return value;
    }

    protected void onProgressUpdate(Integer ... values) {
        progress.setProgress(values[0].intValue());
    }

    protected void onPostExecute(Integer result) {
        progress.setProgress(0);
    }

    protected void onCancelled() {
        progress.setProgress(0);
    }


    public void close(){
        try {
            s.close();
        } catch (IOException e) {
            Log.d("SocketServer","Error : IOException");
        }
    }

}
