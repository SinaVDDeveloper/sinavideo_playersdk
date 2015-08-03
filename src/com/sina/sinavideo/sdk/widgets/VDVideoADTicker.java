package com.sina.sinavideo.sdk.widgets;

import java.util.ArrayList;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoInsertADListener;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.video_playersdkv2.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 视频插入广告的时钟跳数widget，规则如下：<br>
 * 1、当时钟跳数为0，则不管当前广告是播放完毕，则直接进入整片<br>
 * 2、当前广告因为编码、网络等原因导致无法播放，不进跳数过程，按照错误处理，直接跳入正片
 * 
 * @author sunxiao
 */
public class VDVideoADTicker extends LinearLayout implements VDBaseWidget, OnVideoInsertADListener {

    private ArrayList<Integer> mTickerImgList = new ArrayList<Integer>();
    private ImageView mImg1 = null;
    private ImageView mImg2 = null;
    private TextView mTV1 = null;
    private TextView mTV2 = null;
    // 字体默认为白色
    private int mTVTextColor = 0xFFFFFF;
    // 字体大小默认为12sp
    private float mTVTextSize = 12;
    @SuppressLint("nouse")
    private Context mContext = null;

    private final String TAG = "VDVideoADTicker";

    private void refreshCurrSecNum(final int tickerNum) throws Exception {
        int units = 9;
        int decade = 9;
        if (tickerNum < 100) {
            units = tickerNum % 10;
            decade = tickerNum / 10;
        }

        // TODO Auto-generated method stub
        if (mTickerImgList == null || mTickerImgList.size() == 0) {
            mTV1.setText(decade + "");
            mTV2.setText(units + "");
        } else {
            mImg1.setImageDrawable(getResources().getDrawable(mTickerImgList.get(decade)));
            mImg2.setImageDrawable(getResources().getDrawable(mTickerImgList.get(units)));
        }
    }

    public VDVideoADTicker(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;

        initLayout(context);
    }

    public VDVideoADTicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // TODO Auto-generated constructor stub
        int tickerImgListRes = -1;
        TypedArray typedArr = context.obtainStyledAttributes(attrs, R.styleable.VDVideoADTicker);
        if (typedArr != null) {
            for (int i = 0; i < typedArr.getIndexCount(); i++) {
                if (typedArr.getIndex(i) == R.styleable.VDVideoADTicker_tickerImgList) {
                    int resID = typedArr.getResourceId(R.styleable.VDVideoADTicker_tickerImgList, -1);
                    if (resID != -1) {
                        tickerImgListRes = resID;
                    }
                } else if (typedArr.getIndex(i) == R.styleable.VDVideoADTicker_tickerTextColor) {
                    int txtColor = typedArr.getColor(R.styleable.VDVideoADTicker_tickerTextColor, -1);
                    if (txtColor != -1) {
                        mTVTextColor = txtColor;
                    }
                } else if (typedArr.getIndex(i) == R.styleable.VDVideoADTicker_tickerTextSize) {
                    float txtSize = typedArr.getDimension(R.styleable.VDVideoADTicker_tickerTextSize, -1);
                    if (txtSize != -1) {
                        mTVTextSize = txtSize;
                    }
                }
            }
        }
        mTickerImgList.clear();
        if (tickerImgListRes != -1) {
            // 判断是否是一个array，如果不是，报错
            String type = getResources().getResourceTypeName(tickerImgListRes);
            if (type != null && type.equals("array")) {
                TypedArray imgList = getResources().obtainTypedArray(tickerImgListRes);
                if (imgList == null || imgList.length() != 10) {
                    imgList.recycle();
                    throw new IllegalArgumentException("数字图片数组必须为10个");
                }
                for (int i = 0; i < imgList.length(); i++) {
                    mTickerImgList.add(imgList.getResourceId(i, -1));
                }
                imgList.recycle();
            }
        }
        typedArr.recycle();
        initLayout(context);
    }

    private void initLayout(Context context) {
        if (mImg1 == null)
            mImg1 = new ImageView(context);
        if (mImg2 == null)
            mImg2 = new ImageView(context);
        if (mTV1 == null) {
            mTV1 = new TextView(context);
            mTV1.setTextColor(mTVTextColor);
            mTV1.setTextSize(mTVTextSize);
        }
        if (mTV2 == null) {
            mTV2 = new TextView(context);
            mTV2.setTextColor(mTVTextColor);
            mTV2.setTextSize(mTVTextSize);
        }

        // 设置下层的两个图片的属性
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        if (mTickerImgList == null || mTickerImgList.size() == 0) {
            addView(mTV1, params1);
            addView(mTV2, params2);
        } else {
            addView(mImg1, params1);
            addView(mImg2, params2);
        }

    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (null != controller)
            controller.addOnVideoInsertADListener(this);
        // 需要同步一下，当前的时间值，可能会有变化
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (null != controller)
            controller.removeOnVideoInsertADListener(this);
    }

    @Override
    public void onVideoInsertADBegin() {
        // TODO Auto-generated method stub
        setVisibility(View.VISIBLE);
        try {
            VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
            if (controller == null) {
                return;
            }
            refreshCurrSecNum(controller.getADTickerSec());
        } catch (Exception ex) {
            VDLog.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onVideoInsertADTicker() {
        // TODO Auto-generated method stub
        try {
            VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
            if (controller == null) {
                return;
            }
            refreshCurrSecNum(controller.refreshADTickerSec());
        } catch (Exception ex) {
            VDLog.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onVideoInsertADEnd() {
        // TODO Auto-generated method stub
        setVisibility(View.GONE);
    }

}
