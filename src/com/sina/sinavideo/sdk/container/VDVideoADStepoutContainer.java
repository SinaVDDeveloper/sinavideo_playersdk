package com.sina.sinavideo.sdk.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideADContainerListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoInsertADListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

/**
 * 插入式视频广告，『跳过广告按钮』
 * 
 * @author sunxiao
 * 
 */
public class VDVideoADStepoutContainer extends RelativeLayout implements VDBaseWidget, OnVideoInsertADListener,
        OnShowHideADContainerListener {

    public VDVideoADStepoutContainer(Context context) {
        super(context);
        registerListeners();
        // TODO Auto-generated constructor stub
    }

    public VDVideoADStepoutContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        registerListeners();
        setVisibility(View.GONE);
        // TODO Auto-generated constructor stub
    }

    private void registerListeners() {
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // 当前容器被点击，则触发外部的事件响应
            	VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoADStepoutContainer.this.getContext());
                if (controller!=null && controller.getExtListener() != null) {
                	controller.getExtListener().notifyInsertADListenerStepout();
                }
            }
        });
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.addOnVideoInsertADListener(this);
    	if(controller!=null)controller.addOnShowHideADContainerListener(this);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.removeOnVideoInsertADListener(this);
    	if(controller!=null)controller.removeOnShowHideADContainerListener(this);
    }

    @Override
    public void onVideoInsertADBegin() {
        // TODO Auto-generated method stub
        setVisibility(View.VISIBLE);
    }

    @Override
    public void onVideoInsertADEnd() {
        // TODO Auto-generated method stub
        setVisibility(View.GONE);
    }

    @Override
    public void onVideoInsertADTicker() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hideADContainerBar() {
        // TODO Auto-generated method stub
        setVisibility(View.GONE);
    }

    @Override
    public void showADContainerBar() {
        // TODO Auto-generated method stub
        setVisibility(View.VISIBLE);
    }

}
