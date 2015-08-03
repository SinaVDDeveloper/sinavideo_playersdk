package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sina.sinavideo.coreplayer.ISinaVideoView;
import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnScreenOrientationSwitchListener;
import com.sina.sinavideo.sdk.data.VDVideoInfo;
import com.sina.sinavideo.sdk.utils.VDSharedPreferencesUtil;
import com.sina.video_playersdkv2.R;

/**
 * 播放器错误信息提醒框
 * 
 * @author liuqun
 * 
 */
public class VDVideoIndicaterLayout extends RelativeLayout implements VDBaseWidget, OnScreenOrientationSwitchListener {

    private Context mContext = null;

    public VDVideoIndicaterLayout(Context context) {
        super(context);
        init(context);
    }

    public VDVideoIndicaterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VDVideoIndicaterLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);

    }

    private void init(Context context) {
        mContext = context;
        ViewGroup group = null;
        View v = LayoutInflater.from(context).inflate(R.layout.indicater_layer_include, group);
        addView(v);
        registerListener();
    }

    public void registerListener() {
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if(controller!=null)controller.addOnScreenOrientationSwitchListener(this);
        setOnClickListener(mCloseListener);
    }

    private View.OnClickListener mCloseListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            setVisibility(GONE);
            VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoIndicaterLayout.this.getContext());
            if(controller==null)
            	return;
            ISinaVideoView videoViewe = controller.getVideoView();
            if (videoViewe != null && !videoViewe.isPlaying()) {
                videoViewe.start();
            }
        }
    };

    @Override
    public void reset() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void onScreenOrientationSwitch(boolean fullScreen) {
        Log.i("VDVideoIndicaterLayout", "onScreenOrientationSwitch --> fullScreen = " + fullScreen);
        if (fullScreen) {
            boolean first_full = VDSharedPreferencesUtil.isFirstFullScreen(mContext);
            Log.i("VDVideoIndicaterLayout", "onScreenOrientationSwitch --> fullScreen = " + fullScreen
                    + " , first_full = " + first_full);
            VDVideoViewController contorlloer = VDVideoViewController.getInstance(this.getContext());
            if(contorlloer==null){
            	return ;
            }
            VDVideoInfo mVideoInfo = contorlloer.getCurrentVideo();
            if (first_full && mVideoInfo != null && !mVideoInfo.mIsLive) {
                setVisibility(VISIBLE);
                ISinaVideoView videoViewe = contorlloer.getVideoView();
                if (videoViewe != null && videoViewe.isPlaying()) {
                    videoViewe.pause();
                }
                VDSharedPreferencesUtil.setFirstFullScreen(mContext, false);
            }
        } else {
            if (getVisibility() == VISIBLE) {
                setVisibility(GONE);
            }
        }

    }

}
