package com.example.myo.imagetransfer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class MainActivity extends Activity {

    // socket ports
    private static final int SERVERSIDE_WPORT          = 8000;

    // tranfer identification
    private static final int TRANSFER_CODE_NFC         = 0x0000;
    private static final int TRANSFER_CODE_BLUETOOTH  = 0x0001;
    private static final int TRANSFER_CODE_WIFI        = 0x0002;

    // ProgressDialog
    private static final int PROGRESS_DIALOG           = 0x0011;

    // 이미지 파일 관리자
    private FilePathManager filePathManager = new FilePathManager();
    // 이미지 어뎁터
    private  ImageListAdapter imageListAdapter_main;

    // 이미지 전송 로딩다이얼로그 관련
    private ProgressHandler progressHandler = new ProgressHandler();;
    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 메인 액티비티
         * 전송 가능한 이미지 목록을 보여준다.
         */

        // 1.  리스트어뎁터 생성
        imageListAdapter_main  = new ImageListAdapter();

        // 2. 메인 레이아웃 연결
        ListView listView_main = (ListView) findViewById(R.id.listview_main);

        // 3. listview와 adapter 연결
        listView_main.setAdapter(imageListAdapter_main);

        // 4. listview에 어뎁터를 통하여 item 추가
        filePathManager = new FilePathManager();
        HashMap<String, String[]> fileMap = filePathManager.getFileMap();
        for(String uuid : fileMap.keySet()){
            imageListAdapter_main.add(uuid, fileMap.get(uuid));
        }
        imageListAdapter_main.add("123", new String[]{"noimage", "path"});

        // 5. 리스너 등록
        listView_main.setOnItemClickListener(imageItemClickListener_main); // 1) 이미지 리스트뷰 선택 리스너

        progressDialog = onCreateDialogToID(PROGRESS_DIALOG);
    }


    // ------------ 1) 이미지 리스트뷰 선택 리스너  ------------
    String fileUuid;
    private AdapterView.OnItemClickListener imageItemClickListener_main = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l_position) {
            /**
             * main 이미지 리스트뷰에서 각 아이템을 선택하였을경우 전송방법 다이얼 로그를 실행
             */
            // 선택된 이미지
            final TextView textView = (TextView)view.findViewById(R.id.text_imagelist_custom);
            Toast.makeText(getApplicationContext(), textView.getText(), Toast.LENGTH_LONG).show();
            String[] item = (String[])imageListAdapter_main.getItem(position);
            fileUuid = item[0];

            // 다이얼로그 생성
            createTransferDialog().show(); //  2) 전송 방법 다이얼 로그
        }
    };

    //  -----------  2) 전송 방법 다이얼 로그  -------------
    private AlertDialog createTransferDialog() {
        /**
         * 선택할 수 있는 전송방법을 나열하는 다이얼로그
         */

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
        builder.setAdapter(trasferListAdapter, transferItemClickListener_main); //  3) 전송 방법 선택 다이얼로그 선택 리스너
        alertDialog = builder.create();
        return alertDialog;
    }

    // ------------  3) 전송 방법 선택 다이얼로그 선택 리스너  ------------
    private DialogInterface.OnClickListener transferItemClickListener_main = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            /**
             * 전송방법에 따라 타켓 설정
             */
            switch (i){
                case TRANSFER_CODE_NFC:
                    // 4-1) NFC 전송
                    break;
                case TRANSFER_CODE_BLUETOOTH:
                    // 4-2) 블루투스 전송
                    break;
                case TRANSFER_CODE_WIFI:
                    // 전송 시작
                    startTransfer(); //   4-3) 소켓 전송
                    break;

            }
        }
    };
    // ---------------   4-3) 소켓 전송 ------------------
    private void startTransfer() {
        /**
         * TCP 소켓 전송
         */
        // 1) 소켓 오픈
        SocketServer socketServer = new SocketServer(SERVERSIDE_WPORT);
        Toast.makeText(getApplicationContext(), filePathManager.get(fileUuid)[1], Toast.LENGTH_LONG).show();
        Log.d("_______", filePathManager.get(fileUuid)[1]);
        // 2) 이미지 파일 설정
        String filename = filePathManager.get(fileUuid)[0];  // [ 0 : filename, 1 : filepath ]
        String filepath = filePathManager.getFilePath(fileUuid);

        // 3) 소캣 시작
        socketServer.setPath(filename, filepath);
        socketServer.start();
        socketServer.setHandler(progressHandler);
    }

    private ProgressDialog onCreateDialogToID(int id) {
        switch (id) {
            case PROGRESS_DIALOG:
                /**
                 *  이미지 전송 로딩바 다이얼로그
                 */
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Transfer... Image");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                return progressDialog;
            default:
                return null;
        }
    }

    public class ProgressHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            // SocketServer로부터 진행상황 보고 수신
            Bundle bundle = msg.getData();
            Set<String> keys = bundle.keySet();
            Iterator iterator =  keys.iterator();
            String key = iterator.hasNext() ? (String)iterator.next() : "";
            Log.d("_______Message",key);
            switch( key ) {
                case "progress":
                    progressDialog.setProgress(Integer.parseInt(bundle.getString("progress")));
                    break;
                case "dismiss":
                    Log.d("ProgressDialog","dismiss");
                    progressDialog.dismiss();
                    break;
                case "show":
                    Log.d("ProgressDialog", "show");
                    progressDialog.show();
                    break;
                default:
                    return;
            }
        }
    }
}
