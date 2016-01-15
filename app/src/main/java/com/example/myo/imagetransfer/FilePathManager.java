package com.example.myo.imagetransfer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by myo on 2016. 1. 13..
 */
public class FilePathManager {
    /**
     * 디바이스내에서 관리하고 있는 이미지파일 관리
     */

    // filename, filepath
    private HashMap<String, String> filelist = new HashMap<String, String>();
    private String rootPath;

    public FilePathManager(){
        rootPath = "/sdcard";
    }
    public void put(String name, String path) {
        filelist.put(name,path);
    }
    public void remove(String name) {
        filelist.remove(name);
    }
    public void get(String name){
        filelist.get(name);
    }
    public ArrayList<String> ketSet(){
        return new ArrayList<String>(filelist.keySet());
    }

    public HashMap<String, String> getFileList(){
        return new HashMap<String, String>(filelist);
    }
    public void searchFiles(){
        filelist.clear();
        fileTour(new File(rootPath));
    }

    private void fileTour(File file) {
        String[] _filelist = file.list();

        if(_filelist == null || _filelist.length == 0){
            return;
        }

        for(String filename : _filelist){
            File inFile = new File(file.getPath()+"/"+filename);
            if(inFile.isDirectory()) {
                fileTour(inFile);
            }
            else if(filename.matches("/^\\w+\\.(gif|png|jpg|jpeg)$/i")) {
                put(filename, inFile.getPath());
            }
        }
    }
}
