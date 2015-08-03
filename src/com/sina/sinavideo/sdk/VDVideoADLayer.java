
package com.sina.sinavideo.sdk;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoInsertADListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

/**
 * 前贴片广告容器，顶层容器，在此容器范围内的组件，全部是广告类型<br>
 * 注：一个播放器只能允许一组，横竖屏各一个，超过会导致播放器出错
 * 
 * @author sunxiao
 */
public class VDVideoADLayer extends VDVideoViewLayer implements VDBaseWidget, OnVideoInsertADListener {
	private Context mContext;
    public VDVideoADLayer(Context context) {
        super(context);
        mContext = context;
    }

    public VDVideoADLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
    	if(controller!=null)
    		controller.addOnVideoInsertADListener(this);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
    	if(controller!=null)
    		controller.removeOnVideoInsertADListener(this);
    }

    /**
     * 插入广告结束的时候，插入广告显示
     */
    @Override
    public void onVideoInsertADBegin() {
        // TODO Auto-generated method stub
        setVisibility(View.VISIBLE);
    }

    /**
     * 插入广告结束的时候，插入广告隐藏
     */
    @Override
    public void onVideoInsertADEnd() {
        // TODO Auto-generated method stub
        setVisibility(View.GONE);
    }

    @Override
    public void onVideoInsertADTicker() {
        // TODO Auto-generated method stub

    }

}
