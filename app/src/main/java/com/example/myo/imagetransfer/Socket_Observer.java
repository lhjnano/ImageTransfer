package com.example.myo.imagetransfer;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by myo on 2016. 1. 12..
 */
public class Socket_Observer {
    private ArrayList<Bundle> bundleList = new ArrayList<Bundle>();
    public Socket_Observer(){}
    public void update(Bundle message){
        bundleList.add(message);
    }
    public boolean hasMessage(){
        if(bundleList.isEmpty())
            return false;
        return true;
    }
    public Bundle getMessage() {
        if (bundleList.isEmpty())
            return null;
        Bundle retMessage = new Bundle(bundleList.get(0));
        bundleList.remove(0);
        return retMessage;
    }

    public ArrayList<Bundle> getMessages() {
        ArrayList<Bundle> bundleList = new ArrayList<Bundle>(this.bundleList);
        this.bundleList.clear();
        return bundleList;
    }
}
