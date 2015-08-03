package com.sina.sinavideo.sdk.container;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnShowHideADContainerListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoInsertADListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 视频插入式视频广告，了解更多容器<br>
 * 在里面可以插入响应的图片或者汉字
 * 
 * @author sunxiao
 * 
 */
public class VDVideoADGetMoreContainer extends RelativeLayout implements VDBaseWidget, OnVideoInsertADListener,
        OnShowHideADContainerListener {

    public VDVideoADGetMoreContainer(Context context) {
        super(context);
        registerListeners();
    }

    public VDVideoADGetMoreContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        registerListeners();
        setVisibility(View.GONE);
    }

    private void registerListeners() {
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoADGetMoreContainer.this.getContext());
                if (controller!=null && controller.getExtListener() != null) {
                	controller.getExtListener().notifyInsertADListenerClick();
                }
            }
        });
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(null!=controller)controller.addOnVideoInsertADListener(this);
    	if(null!=controller)controller.addOnShowHideADContainerListener(this);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(null!=controller)controller.removeOnVideoInsertADListener(this);
    	if(null!=controller)controller.removeOnShowHideADContainerListener(this);
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
