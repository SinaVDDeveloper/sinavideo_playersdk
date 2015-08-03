package com.sina.sinavideo.sdk.dlna;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.dlna.DLNAEventListener.OnMediaRenderNumChangedListener;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.video_playersdkv2.R;

/**
 * 当检测到有支持DLNA设备时，播放页面中对应的DLNA设备选择控件
 * 
 * @author sina
 *
 */
public class DLNAButton extends ImageButton implements OnMediaRenderNumChangedListener {

    public DLNAButton(Context context) {
        super(context);
        init(context);
        VDLog.d("DLNAButton","1 context ctt=" + context);
    }

    public DLNAButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        VDLog.d("DLNAButton","2 context ctt=" + context);
    }

    public DLNAButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        VDLog.d("DLNAButton","3 context ctt=" + context);
    }

    private void init(Context context) {
        DLNAEventListener.getInstance().addOnMediaRenderNumChangedListener(this);
        DLNAEventListener.getInstance().notifyDLNASetUp();
        DLNAController.getInstance(getContext()).setUp();
        if (DLNAController.getInstance(context).mData.size() > 0) {
            setDLNAStatus(true);
        } else {
            setDLNAStatus(false);
        }
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (DLNAController.mIsDLNA) {

                } else {
                    VDVideoViewController controller = VDVideoViewController.getInstance(DLNAButton.this.getContext());
                    if(controller!=null)controller.notifyHideControllerBar(0);
                    // onDLNASwitch(true);
                    if(controller!=null)controller.notifyRegisterDLNAListener();
                    DLNAEventListener.getInstance().notifyDLNASetUp();
                    if(controller!=null)controller.notifyHideMoreOprationPanel();
                }
                DLNAEventListener.getInstance().notifyDLNAListToogle();
            }
        });
    }
    
    private void setDLNAStatus(boolean enable){
        setEnabled(enable);
        if(enable){
            setBackgroundResource(R.drawable.dlna_icon);
        } else {
            setBackgroundResource(R.drawable.dlna_unable_icon);
        }
    }

    @Override
    public void onMediaRenderAdded(String uuid, String name) {
        Log.i("DLNA", "DLNAButton onMediaRenderAdded : uuid = " + uuid + " , name = " + name);
        setDLNAStatus(true);
//        if (getVisibility() != VISIBLE) {
//            setVisibility(VISIBLE);
//        }
    }

    @Override
    public void onMediaRenderRemoved(String uuid, String name) {
        if (DLNAController.getInstance(getContext()).mData.size() == 0) {
//            setVisibility(GONE);
            setDLNAStatus(false);
        }
    }

}
