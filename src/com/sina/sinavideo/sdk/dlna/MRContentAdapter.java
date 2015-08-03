package com.sina.sinavideo.sdk.dlna;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sina.video_playersdkv2.R;

/**
 * 搜索到支持DLNA的设备适配器
 * 
 * @author sina
 *
 */
public class MRContentAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    public ArrayList<MRContent> mData;
    private int mSelectPosition = -1;
    private int mSelectColor;

    public void setSelectPosition(int p) {
        mSelectPosition = p;
        notifyDataSetChanged();
    }

    public MRContentAdapter(Context context) {
        super();
        mInflater = LayoutInflater.from(context);
        // mData = new ArrayList<MRContent>();
        mSelectColor = Color.parseColor("#0662ad");
        mData = DLNAController.getInstance(context).mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mData.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        if (view == null) {
            ViewGroup group = null;
            view = mInflater.inflate(R.layout.dlna_device_list_item, group);
        }
        final TextView name = (TextView) view.findViewById(R.id.mr_name);
        name.setText(mData.get(position).getName());
        if (position == mSelectPosition) {
            name.setBackgroundColor(mSelectColor);
        } else {
            name.setBackgroundColor(0);
        }
        return view;
    }

    public void addMR(String uuid, String name) {
        // MRContent mr = new MRContent();
        // mr.setUuid(uuid);
        // mr.setName(name);
        // mData.add(mr);

        // TODO
        // mr = new MRContent();
        // mr.setUuid(uuid);
        // mr.setName("测试DLNA");
        // mData.add(mr);
        notifyDataSetChanged();
    }

    public void removeMR(String uuid, String name) {
        // for (int i = 0; i < mData.size(); i++) {
        // if (mData.get(i) != null && mData.get(i).getUuid().equals(uuid)) {
        // mData.remove(mData.get(i));
        // break;
        // }
        // }
        notifyDataSetChanged();
    }

    public String getMR(int position) {
        if (mData.get(position) != null) {
            return mData.get(position).getUuid();
        }
        return null;
    }

    public void removeAll() {
        // if(mData != null){
        // mData.clear();
        // }
        mSelectPosition = -1;
        notifyDataSetChanged();
    }
}
