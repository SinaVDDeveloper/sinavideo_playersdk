package com.sina.sinavideo.sdk.widgets.playlist;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sina.sinavideo.sdk.data.VDVideoInfo;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;
import com.sina.video_playersdkv2.R;

/**
 * TV模式中全屏播放时相关视频列表中数据列中的文字内容
 * @author liuqun
 */
public class VDVideoPlayListGridItemTextView extends TextView implements VDBaseWidget, VDVideoPlaylistBase {

    protected VDVideoInfo mInfo = null;
    protected int mVideoInfoIndex = -1;
    private int mCurPlayColor;
    private int mNoPlayColor;

    public VDVideoPlayListGridItemTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ResolutionBackGround);
        int otherTextColor = Color.parseColor("#d1d1d1");
        int curBgColor = Color.parseColor("#0078db");
        mNoPlayColor = a.getColor(R.styleable.PlayListTextViewColor_NoPlayColor, otherTextColor);
        mCurPlayColor = a.getColor(R.styleable.PlayListTextViewColor_CurPlayColor, curBgColor);
        a.recycle();
    }

    @Override
    public void reset() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void setData(VDVideoInfo info) {
        mInfo = info;
        // setText(info.mTitle);
    }

    @Override
    public void setVideoInfo(int infoIndex, int curPlayIndex) {
        mVideoInfoIndex = infoIndex;
        if (mVideoInfoIndex == curPlayIndex) {
            setTextColor(mCurPlayColor);
        } else {
            setTextColor(mNoPlayColor);
        }
        setText(String.valueOf(infoIndex + 1));
    }

}
