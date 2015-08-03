package com.sina.sinavideo.sdk.dlna;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sina.sinavideo.sdk.dlna.DLNAEventListener.OnDLNASelectedListener;
import com.sina.video_playersdkv2.R;

/**
 * 显示通过DLNA链接的设备名称控件
 * 
 * @author sina
 *
 */
public class DLNANameTextView extends TextView implements OnDLNASelectedListener {

    private String mName;
    // private String mValue;
    private String mPrefix;

    public DLNANameTextView(Context context) {
        super(context);
        init(context);
    }

    public DLNANameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DLNANameTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mPrefix = context.getString(R.string.dlna_connect_to_device);
        DLNAEventListener.getInstance().addOnDLNASelectedListener(this);
    }

    @Override
    public void onMediaRenderSelect(String name, String value) {
        mName = name;
        // mValue = value;
    }

    @Override
    public void onMediaRenderOpened(boolean opened) {
        if (opened) {
            if (mName != null) {
                setText(mPrefix + mName);
            }
        } else {
            setText(mPrefix);
        }
    }

}
