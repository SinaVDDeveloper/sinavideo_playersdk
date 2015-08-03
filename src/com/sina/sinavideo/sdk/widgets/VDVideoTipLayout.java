package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sina.sinavideo.coreplayer.splayer.MediaPlayer;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer.OnVideoOpenedListener;
import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnTipListener;
import com.sina.video_playersdkv2.R;

/**
 * 播放器错误信息提醒框
 * 
 * @author liuqun
 * 
 */
public class VDVideoTipLayout extends RelativeLayout implements VDBaseWidget, OnVideoOpenedListener, OnTipListener {

    private TextView mTipMessage;
    private ImageButton mBtnCloseTip;

    public VDVideoTipLayout(Context context) {
        super(context);
        init(context);
    }

    public VDVideoTipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VDVideoTipLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    private void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.tip_layout, null);
        addView(v);
        mTipMessage = (TextView) findViewById(R.id.tipMessage);
        mBtnCloseTip = (ImageButton) findViewById(R.id.btnCloseTip);
        registerListener();
        // postDelayed(new Runnable() {
        //
        // @Override
        // public void run() {
        // onTip("测试");
        // }
        // }, 5000);
    }

    public void registerListener() {
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if(controller!=null)controller.addOnTipListener(this);
        mBtnCloseTip.setOnClickListener(mCloseListener);
        mTipMessage.setOnClickListener(mCloseListener);
        setOnClickListener(mCloseListener);
    }

    private View.OnClickListener mCloseListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            setVisibility(GONE);
            VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoTipLayout.this.getContext());
            if(controller!=null)controller.notifyHideTip();
        }
    };

    @Override
    public void reset() {

    }

    @Override
    public void hide() {
        mTipMessage.setText("");
        setVisibility(GONE);
    }

    @Override
    public void onVideoOpened(MediaPlayer mp) {
        // setValue("缓冲...");
    }

    @Override
    public void onTip(String tip) {
        mTipMessage.setText(tip);
        setVisibility(VISIBLE);
    }

    @Override
    public void onTip(int tipResId) {
        mTipMessage.setText(tipResId);
        setVisibility(VISIBLE);
    }

    @Override
    public void hideTip() {
        mTipMessage.setText("");
        setVisibility(GONE);
    }

}
