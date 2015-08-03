package com.sina.sinavideo.sdk.dlna;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnDLNALinearLayoutListener;

/**
 * DLNA最外层布局控件
 * 
 * @author sina
 *
 */
public class DLNALinearLayout extends LinearLayout implements OnDLNALinearLayoutListener {

    public DLNALinearLayout(Context context) {
        super(context);
        init(context);
    }

    public DLNALinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
    	VDVideoViewController controller = VDVideoViewController.getInstance(context);
    	if(controller!=null)controller.addOnDLNALinearLayoutListener(this);
    }

    @Override
    public void setLayoutVisiable(boolean visiable) {
        setVisibility(visiable ? VISIBLE : GONE);
    }

}
