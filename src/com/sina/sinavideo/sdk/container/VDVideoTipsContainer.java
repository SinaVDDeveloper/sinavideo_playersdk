/**
 * 播放器新手引导View容器类，单纯的容器类，只是用来标识隐藏与显示tips而已。
 * 
 * @author sunxiao
 * 
 */
package com.sina.sinavideo.sdk.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVideoGuideTipsListener;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

public class VDVideoTipsContainer extends RelativeLayout implements VDBaseWidget, OnVideoGuideTipsListener {

    private Context mContext = null;
    private final static String TAG = "VDVideoTipsLayout";

    public VDVideoTipsContainer(Context context) {
        super(context);

        setVisibility(View.GONE);
        init(context);
    }

    public VDVideoTipsContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        // 默认为View.GONE
        TypedArray typedArr = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.visible});
        if (typedArr != null) {
            int visible = typedArr.getInt(0, -1);
            if (visible == -1) {
                setVisibility(View.GONE);
            }
        } else {
            setVisibility(View.GONE);
        }
        typedArr.recycle();

        init(context);
    }

    private void init(Context context) {
        mContext = context;
        registerOnclick();
    }

    private void registerOnclick() {
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mContext == null) {
                    VDLog.e(VDVideoTipsContainer.TAG, "mContext is null");
                    return;
                }
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoTipsContainer.this.getContext());
                if(null!=controller){
                	controller.setFirstFullScreen(mContext, false);
                	controller.notifyGuideTips(false);
                }
            }
        });
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        ViewGroup vg = (ViewGroup) getParent();
        if (!(vg.getChildAt(0) instanceof VDVideoTipsContainer)) {
            // 确保，这个ViewGroup在父View的最上面
            vg.bringChildToFront(this);
        }
        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if(controller!=null)controller.addOnVideoGuideTipsListener(this);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    	 VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
         if(controller!=null)controller.removeOnVideoGuideTipsListener(this);
    }

    @Override
    public void onVisible(boolean isVisible) {
        // TODO Auto-generated method stub
        // 根据具体的操作实现与隐藏新手教程
        if (isVisible) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

}
