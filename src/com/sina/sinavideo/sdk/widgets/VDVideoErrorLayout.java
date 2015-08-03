package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.sina.sinavideo.coreplayer.splayer.MediaPlayer;
import com.sina.sinavideo.coreplayer.splayer.MediaPlayer.OnVideoOpenedListener;
import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnClickRetryListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnErrorListener;
import com.sina.video_playersdkv2.R;

/**
 * 播放器错误信息提醒框
 * 
 * @author liuqun
 * 
 */
public class VDVideoErrorLayout extends RelativeLayout implements VDBaseWidget, OnVideoOpenedListener, OnErrorListener, OnClickRetryListener{

    private View mBtnReplay;

    public VDVideoErrorLayout(Context context) {
        super(context);
        init(context);
    }

    public VDVideoErrorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VDVideoErrorLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    private void init(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.video_play_error_layout, null);
        addView(v, new LayoutParams(-1, -1));
        mBtnReplay = findViewById(R.id.btn_replay);
        registerListener();
    }

    public void registerListener() {
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if(controller!=null)controller.addOnRetryErrorListener(this);
        mBtnReplay.setOnClickListener(mRetryListener);
        if(controller!=null)controller.addOnClickRetryListener(this);
    }

    public void setValue(String value) {

    }

    @Override
    public void reset() {
    }

    @Override
    public void hide() {
//        VDVideoViewController.getInstance().removeOnClickRetryListener(this);
    }

    @Override
    public void onVideoOpened(MediaPlayer mp) {
        setVisibility(GONE);
        setValue("缓冲...");
    }


    private View.OnClickListener mRetryListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoErrorLayout.this.getContext());
            if(controller==null)
            	return;
            controller.playVideoWithInfo(controller.getCurrentVideo());
            controller.notifyClickRetry();
            setVisibility(GONE);
        }
    };

    @Override
    public boolean onError(int what, int extra) {
        setVisibility(VISIBLE);
        return false;
    }

    @Override
    public void onClickRetry() {
        setVisibility(GONE);
    }

}
