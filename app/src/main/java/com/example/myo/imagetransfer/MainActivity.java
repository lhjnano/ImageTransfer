package com.example.myo.imagetransfer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    // socket port
    private  static final int port = 5555;

    // tranfer identification
    private static final int TRANSFER_CODE_NFC         = 0x0000;
    private static final int TRANSFER_CODE_BLUETOOTH   = 0x0001;
    private static final int TRANSFER_CODE_WIFI        = 0x0002;

    // 데이터 교환 연결자 관찰자
    private Socket_Observer serverObserver = new Socket_Observer();
    private Socket_Observer clientObserver = new Socket_Observer();
    // NFC          --- 추후 구현
    // Bluetooth    --- 추후 구현

    // 데이터 교환 연결자
    private SocketServer_Connect socketServer_connect = new SocketServer_Connect(serverObserver);
    private SocketClient_Connect socketClient_connect = new SocketClient_Connect(clientObserver);

    // 이미지 파일 관리자
    private FilePathManager filePathManager = new FilePathManager();

    // 전송할 데이터셋
    private String[] dataSet = new String[2];       // filename, transferCode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1.  이미지 파일 불러오기
        // 2.  리스트어뎁터 생성
        ImageListAdapter imageListAdapter_main  = new ImageListAdapter();

        // 3. 메인 레이아웃 연결
        ListView listView_main = (ListView) findViewById(R.id.listview_main);

        // 4. listview와 adapter 연결
        listView_main.setAdapter(imageListAdapter_main);

        // 5. listview에 어뎁터를 통하여 item 추가
        filePathManager.searchFiles();

        ArrayList<String> filenames = filePathManager.ketSet();
        for(String filename : filenames) {
            imageListAdapter_main.add(filename);
        }

        listView_main.setOnItemClickListener(imageItemClickListener_main);

        // 클라이언트측 소켓 개방 (브로드캐스팅 캐치)
        refresh_listeningCenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        // 액티비티가 정상적으로 종료되었을 경우
        {
            if(requestCode==1)
            // InformationInput에서 호출한 경우에만 처리합니다.
            {
                Intent intent = new Intent();
                //intent.putExtra()
            }
        }

    }

    @Override
    public void finalize(){
        socketClient_connect._stop();
    }

    private void refresh_listeningCenter(){
        /**
         * 모든 전송작업이 끝나거나 처음 시작할때 연결 설정을 위한 클라이언트 서버 리스닝 시작
         */
        ListeningCenter listeningCenter = new ListeningCenter(clientObserver);
    }


    private void broadcast_Socket(){
        /**
         * 소켓 설정 브로드케스팅
         */
        Log.d("MainActivity", "broadcast_Socket");

        socketServer_connect.broadcast();
        // 수신 시작
        socketServer_connect._start();
        for(int count = 5; count > 0 ; count++) {   //receiving Messages
            try{
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.d("broadcast_socket()","Error : InterruptedException");
            }
        }
        socketServer_connect._stop();

        // 브로드캐스팅 응답이 왔다면
        if( serverObserver.hasMessage() ) {
            ArrayList<Bundle> messages = serverObserver.getMessages();
            ArrayList<String> clientIpList = new ArrayList<String>();

            // ip 캐치
            for(Bundle bundle : messages){
                if(bundle.getString("code").matches("check..."))
                    clientIpList.add(bundle.getString("ip"));
            }

            // ip를 선택하여 connect 설정 - 선택 후 ipItemClickListener_main() 수행 (데이터 전송 요청)
            this.createIpDialog(clientIpList).show();
        }

        // timeout
        else {
            // 추후 구현
        }
    }
    // ------------ ip 선택 다이얼로그  ------------
    private ArrayAdapter<String> arrayAdapters = null;
    private AlertDialog createIpDialog(ArrayList<String> clientIpList) {

        // 1. 어뎁터 설정 (기본 텍스트)
        arrayAdapters = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item_custom);

        // 2. 아이템 추가
        for(String clientIp : clientIpList)
            arrayAdapters.add(clientIp);

        // 3. 다이얼로그 생성 및 레이아웃 설정
        AlertDialog.Builder builder;
        AlertDialog alertDialog;

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("타겟 설정");
        builder.setAdapter(arrayAdapters, ipItemClickListener_main);
        alertDialog = builder.create();
        return alertDialog;
    }

    // ------------  IP 선택 리스너 (데이터 전송 요청)  ------------
    private DialogInterface.OnClickListener ipItemClickListener_main = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            String clientIp = arrayAdapters.getItem(i);

            // 로딩바 --- 추후 구현

            // 1. 데이터 전송 요청
            socketServer_connect.request(clientIp);

            // 2. 승인 확인
            // 수신 시작
            socketServer_connect._start();
            for(int count = 5; count > 0 ; count++) {   //receiving Messages
                try{
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.d("broadcast_socket()","Error : InterruptedException");
                }
            }
            socketServer_connect._stop();

            // 승인 응답이 왔다면
            if( serverObserver.hasMessage() ) {
                ArrayList<Bundle> messages = serverObserver.getMessages();

                // ip 캐치
                for(Bundle bundle : messages) {
                    if(bundle.getString("code").matches("ok...") && bundle.getString("ip").matches(clientIp)) {
                        // 클라이언트 연결
                        SocketClient sc = new SocketClient(clientIp, port);
                        // 파일 설정
                        //sc.setPath(filePathManager.get(""));
                    }
                }
            }
        }
    };

    // ------------ 이미지 리스트뷰 선택 리스너  ------------
    private AdapterView.OnItemClickListener imageItemClickListener_main = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l_position) {
            /**
             * main 이미지 리스트뷰에서 각 아이템을 선택하였을경우 전송방법 다이얼 로그를 실행
             */
            // 선택된 이미지

            // 다이얼로그 생성
            this.createDialog().show();

        }
        private AlertDialog createDialog() {

            // 1. 어뎁터 생성
            TrasferListAdapter trasferListAdapter = new TrasferListAdapter();

            // 2. item list 불러오기
            String[] names = getResources().getStringArray(R.array.transfer_imgNames_string_array);
            TypedArray imgs = getResources().obtainTypedArray(R.array.transfer_imgs_string_array);

            // 3. item 추가
            for(int i = 0 ; i < names.length; i++) {
                trasferListAdapter.add(names[i],imgs.getDrawable(i));
            }

            // 4. 다이얼로그 생성 및 레이아웃 설정
            AlertDialog.Builder builder;
            AlertDialog alertDialog;

            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("전송 방법");
            builder.setAdapter(trasferListAdapter, transferItemClickListener_main);
            alertDialog = builder.create();
            return alertDialog;
        }
    };

    // ------------  전송 방법 선택 다이얼로그 선택 리스너  ------------
    private DialogInterface.OnClickListener transferItemClickListener_main = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            /**
             * 전송방법에 따라 타켓 설정
             */
            switch (i){
                case TRANSFER_CODE_NFC:
                    break;
                case TRANSFER_CODE_BLUETOOTH:
                    break;
                case TRANSFER_CODE_WIFI:
                    broadcast_Socket(); // 서버측 브로드캐스팅 시작
                    break;

            }
        }
    };

    // ------------ listening Center ------------
    public class ListeningCenter extends Thread {
        Socket_Observer observer;
        public ListeningCenter(Socket_Observer observer){
            this.observer = observer;
        }
        public void run(){
            socketClient_connect._start();
            while(!observer.hasMessage()){
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.d("Listening_center","Error : InterruptedException");
                }
            }
            socketClient_connect._stop();

            Bundle message = observer.getMessage();
            if(message.getString("code").matches("broadcast...")){
                // 브로드캐스팅 수신 확인
                socketClient_connect.responseBroadcast(message.getString("ip"));
                refresh_listeningCenter();
            }
            if(message.getString("code").matches("request...")){
                // 데이터 전송 요청 승인
                createAcceptDialog(message.getString("ip")).show();
                // ip를 가지고 서버측과 데이터 교환 -- 추후 구현
            }

        }
    }
    // ------------  데이터 전송 요청 승인 다이얼로그  ------------
    private String ip;
    private AlertDialog createAcceptDialog(String ip) {
        this.ip = ip;
        // 다이얼로그 생성
        AlertDialog.Builder builder;
        AlertDialog alertDialog;

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("데이터 전송 요청");
        TextView textView = new TextView(this);
        textView.setText(ip + " 로부터 전송요청이 왔습니다.\n 승인하시겠습니까?");
        builder.setView(textView);
        builder.setPositiveButton("승인", acceptClickListener);
        builder.setNegativeButton("거절", null);
        alertDialog = builder.create();
        return alertDialog;
    }

    private DialogInterface.OnClickListener acceptClickListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            /**
             * 데이터 요청 승인 메세지 전송
             */
            socketClient_connect.ok(ip);
            SocketServer ss = new SocketServer(port);
        }
    };




}
