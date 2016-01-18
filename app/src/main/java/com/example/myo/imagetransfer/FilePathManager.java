package com.example.myo.imagetransfer;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by myo on 2016. 1. 13..
 */
public class FilePathManager {
    /**
     * 디바이스내에서 관리하고 있는 이미지파일 관리
     */

    HashMap<String, String[]> fileMap;
    // ^ UUID, filename, filepath 파일 이름은 같을 수 있으므로 UUID를 통하여 각각의 파일을 구분

    public FilePathManager() {
        Log.d("FilePathManager","Create");
        fileMap = new HashMap<String, String[]>();
        setFileMap();
    }

    public String getFilePath(String uuid) {
        /**
         * UUID를 통해 filepath 반환
         */
        return fileMap.get(uuid)[1];
    }
    public ArrayList<String> getFileNameList() {
        /**
         * file 이름만 문자열로 반환
         */
        ArrayList<String> list = new ArrayList<String>(fileMap.keySet());
        ArrayList<String> fileNameList = new ArrayList<String>();
        for(String UUID : list) {
            fileNameList.add(fileMap.get(UUID)[0]);
        }
        return fileNameList;
    }

    public void put(String[] fileData) {
        /**
         * 파일 추가
         */
        fileMap.put(UUID.randomUUID().toString(), fileData);
    }

    public void get(String uuid) {
        /**
         * 파일 반환
         */
        fileMap.get(uuid);
    }

    public void remove(String uuid) {
        /**
         * 파일 제거
         */
        fileMap.remove(uuid);
    }

    public void setFileMap() {
        /**
         * 디렉토리를 순회하면서
         * 이미지 파일 데이터만을 수신
         */
        Log.d("FilePathManager", "setFileMap");
        File root = getRoot();
        Log.d("FilePathManager", "setFileMap_" + root.getPath());
        fileMap.clear();
        traverseFile(root);
    }

    public HashMap<String, String[]> getFileMap(){
        return new HashMap<String, String[]>(fileMap);
    }

    private void traverseFile(File root){
        /**
         * File 순회하며  fileMap에 정보 전달
         */
        // 1) 파일 안의 리스트
        String[] infiles = root.list();
        // 2) 파일이 없다면 복귀
        if( infiles == null || infiles.length == 0 ){
            Log.d("FilePathManager","/" + root.list());
            return;
        }

        for(String infile : infiles ) {
            // 3) 통과
            Log.d("FilePathManager",infile);
            if(infile.equals(".") || infile.equals("..")) continue;

            // 4) 파일이 디렉터리라면
            File fInfile = new File(root.getPath()+"/"+infile);
            if( fInfile.isDirectory() ) {
                // 4-1) 재귀
                traverseFile(fInfile);
            }
            // 5) 파일이 그림파일이라면
            else if(infile.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)" )) {
                // 5-1) 추가
                put(new String[]{infile, fInfile.getAbsolutePath()});
            }
        }
    }

    public File getRoot(){
        /**
         * Root 파일 포인터(sdcard) 반환
         */
        File returnFile = new File("/storage/extSdCard");
        return returnFile;
    }
}
