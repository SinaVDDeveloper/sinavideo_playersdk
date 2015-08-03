
package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideControllerListener;
import com.sina.sinavideo.sdk.data.VDVideoInfo;
import com.sina.sinavideo.sdk.dlna.DLNAController;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.utils.VDPlayPauseHelper;
import com.sina.video_playersdkv2.R;

/**
 * 播放\暂停视频按钮
 * 
 * @author liuqun
 */
public class VDVideoPlayButton extends ImageView implements VDBaseWidget, VDVideoViewListeners.OnPlayVideoListener,
        OnShowHideControllerListener {

    private Context mContext;

    private int mPlayRes = R.drawable.play_ctrl_pause;
    private int mPauseRes = R.drawable.play_ctrl_play;

    private VDPlayPauseHelper mVDPlayPauseHelper;

    public VDVideoPlayButton(Context context) {
        super(context);
        mContext = context;
        VDLog.d("VDVideoPlayButton","context ctt=" + context);
        setImageResource(mPauseRes);
        registerListeners();// 注册事件监听
        init();
    }

    public VDVideoPlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        VDLog.d("VDVideoPlayButton","context ctt=" + context);
        TypedArray typedArr = context.obtainStyledAttributes(attrs, R.styleable.VDVideoPlayButton);

        if (typedArr != null) {
            for (int i = 0; i < typedArr.getIndexCount(); i++) {
                int resID = -1;
                if (typedArr.getIndex(i) == R.styleable.VDVideoPlayButton_pausedRes) {
                    resID = typedArr.getResourceId(R.styleable.VDVideoPlayButton_pausedRes, -1);
                    if (resID != -1) {
                        mPauseRes = resID;
                    }
                } else if (typedArr.getIndex(i) == R.styleable.VDVideoPlayButton_playingRes) {
                    resID = typedArr.getResourceId(R.styleable.VDVideoPlayButton_playingRes, -1);
                    if (resID != -1) {
                        mPlayRes = resID;
                    }
                }
                // switch (typedArr.getIndex(i)) {
                // case R.styleable.VDVideoPlayButton_pausedRes :
                // resID =
                // typedArr.getResourceId(R.styleable.VDVideoPlayButton_pausedRes,
                // -1);
                // if (resID != -1) {
                // mPauseRes = resID;
                // }
                // break;
                // case R.styleable.VDVideoPlayButton_playingRes :
                // resID =
                // typedArr.getResourceId(R.styleable.VDVideoPlayButton_playingRes,
                // -1);
                // if (resID != -1) {
                // mPlayRes = resID;
                // }
                // break;
                // }
            }
            typedArr.recycle();
        }
        init();
        registerListeners();// 注册事件监听
        VDVideoViewController.getInstance(context).addOnPlayVideoListener(this);
    }

    private void init() {
        mVDPlayPauseHelper = new VDPlayPauseHelper(getContext());
        setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                	VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoPlayButton.this.getContext());
                	if(controller!=null)controller.notifyShowControllerBar(true);
                }
            }
        });
    }

    @Override
    public void reset() {
        // if (VDVideoViewController.getInstance().mVDPlayerInfo.mPlayStatus ==
        // VDPlayerInfo.PLAYER_ISPLAYING) {
        // setSelected(false);
        // } else {
        // setSelected(true);
        // }
        onPlayStateChanged();
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if(null!=controller)controller.addOnPlayVideoListener(this);
    }

    @Override
    public void hide() {
    	 VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
         if(null!=controller)controller.removeOnPlayVideoListener(this);
    }

    /**
     * 为自己注册事件
     */
    private void registerListeners() {
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.addOnShowHideControllerListener(this);
        // click事件
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (DLNAController.mIsDLNA) {
                    DLNAController.getInstance(mContext).onClickPlay();
                    return;
                }
                doClick();
            }

        });

    }

    private void doClick() {
        mVDPlayPauseHelper.doClick();
    }

    @Override
    public void onVideoInfo(VDVideoInfo info) {
    }

    @Override
    public void onShowLoading(boolean show) {
    }

    @Override
    public void onVideoPrepared(boolean prepare) {
    }

    @Override
    public void onPlayStateChanged() {
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller!=null && controller.mVDPlayerInfo.mIsPlaying) {
            setImageResource(mPlayRes);
        } else {
            setImageResource(mPauseRes);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        VDLog.e("VDVideoPlayButton", " onKeyDown ");
        // if(keyCode == KeyEvent.KEYCODE_ENTER || keyCode ==
        // KeyEvent.KEYCODE_DPAD_CENTER){
        // doClick();
        // return true;
        // }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        VDLog.e("VDVideoPlayButton", " onKeyUp ");
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            doClick();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            VDLog.e("VDVideoPlayButton", " onKeyUp KEYCODE_DPAD_RIGHT 111111111");
            int id = getNextFocusRightId();
            View v = ((ViewGroup) getParent()).findViewById(id);
            if (v != null) {
                VDLog.e("VDVideoPlayButton", " onKeyUp KEYCODE_DPAD_RIGHT");
                v.requestFocus();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void doNotHideControllerBar() {

    }

    @Override
    public void hideControllerBar(long delay) {

    }

    @Override
    public void onPostHide() {
        Log.i("VDVideoPlayButton", "onPostHide key--> 失去焦点");
        clearFocus();
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller!=null && controller.getVideoView() != null) {
            ((View) controller.getVideoView()).requestFocus();
        }
    }

    @Override
    public void onPostShow() {
        postDelayed(new Runnable() {

            @Override
            public void run() {
                // setFocusable(true);
                requestFocus();
                Log.i("VDVideoPlayButton", "key onPostShow --> " + isFocused());
            }
        }, 50);
    }

    @Override
    public void onPreHide() {

    }

    @Override
    public void showControllerBar(boolean delayHide) {

    }

    @Override
    public void onPreShow() {

    }

}
