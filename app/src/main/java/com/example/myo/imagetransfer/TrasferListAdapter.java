package com.example.myo.imagetransfer;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
public class TrasferListAdapter extends BaseAdapter {

    private ArrayList<String> list = null;
    private ArrayList<Drawable> imgList = null;

    public TrasferListAdapter(){
        list = new ArrayList<String>();
        imgList = new ArrayList<Drawable>();
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
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        // 1. 위치와 뷰그룹 컨텍스트 설정
        final Context context = (Context) viewGroup.getContext();

        TextView textview = null;
        ImageView imageView = null;
        Holder holder = null;

        // 2. 생성할 것이 생기면 생성
        if(view == null) {

            // 3. 연결할 layout 설정
            LayoutInflater inflater =  (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.transferlist_custom,viewGroup, false);

            // 4. 각 속성뷰 불러오기
            imageView = (ImageView) view.findViewById(R.id.image_transferlist_custom);
            textview = (TextView) view.findViewById(R.id.text_transferlist_custom);

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
        imageView.setImageDrawable(imgList.get(i));
        textview.setText(list.get(i));

        return view;
    }


    public void add(String name,Drawable drawable)
    {
        imgList.add(drawable);
        list.add(name);
    }

    public void remove(int i)
    {
        imgList.remove(i);
        list.remove(i);
    }

    private class Holder {
        TextView textView;
        ImageView imageView;
    }
}
