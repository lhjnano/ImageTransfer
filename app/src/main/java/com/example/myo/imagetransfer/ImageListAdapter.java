package com.example.myo.imagetransfer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by myo on 2016. 1. 12..
 */
public class ImageListAdapter extends BaseAdapter {

    private ArrayList<String> list = null;
    private ArrayList<String> pathList = null;

    public ImageListAdapter(){
        list = new ArrayList<String>();
        pathList = new ArrayList<String>();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        /**
         *  리스트의 내용을 구현하는 메소드
         */

        // 1. 위치와 뷰그룹 컨텍스트 설정
        final Context context = (Context) viewGroup.getContext();

        TextView textview = null;
        ImageView imageView = null;
        Holder holder = null;

        // 2. 생성할 것이 생기면 생성
        if(view == null) {

            // 3. 연결할 layout 설정
            LayoutInflater inflater =  (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.imagelist_custom,viewGroup, false);

            // 4. 각 속성뷰 불러오기
            imageView = (ImageView) view.findViewById(R.id.image_imagelist_custom);
            textview = (TextView) view.findViewById(R.id.text_imagelist_custom);

            // 5. Holder 설정  -  // 스크롤 시 데이터가 변경 되는 것과 findViewById()를 사용을 줄여 속도 향상
            holder = new Holder();
            holder.imageView = imageView;
            holder.textView = textview;

            view.setTag(holder);
        }
        else {
            holder = (Holder)view.getTag();
            imageView = holder.imageView;
            textview = holder.textView;
        }
        // 6. 값 설정
        //imageView.setImageBitmap(new Bitemap());
        textview.setText(list.get(i));

        return view;
    }

    public void add(String name) {
        list.add(name);
    }

    public void remove(int i) {
        list.remove(i);
    }

    private class Holder {
        TextView textView;
        ImageView imageView;
    }
}
