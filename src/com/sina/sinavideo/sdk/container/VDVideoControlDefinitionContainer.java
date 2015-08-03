package com.sina.sinavideo.sdk.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnVMSResolutionListener;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

/**
 * 清晰度容器
 * 
 * @author GengHongchao
 *
 */
public class VDVideoControlDefinitionContainer extends RelativeLayout implements OnVMSResolutionListener, VDBaseWidget {

    public VDVideoControlDefinitionContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void reset() {
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.addOnVMSResolutionListener(this);
    }

    @Override
    public void hide() {
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.removeOnVMSResolutionListener(this);
    }

    private Runnable mHideAction = new Runnable() {

        @Override
        public void run() {
            setVisibility(View.GONE);
        }
    };

    @Override
    public void onVMSResolutionContainerVisible(boolean isVisible) {
        VDLog.i("VDVideoControlDefinitionContainer", "onDefinitionVisible : " + isVisible);
        if (isVisible) {
            setVisibility(View.VISIBLE);
            removeCallbacks(mHideAction);
            postDelayed(mHideAction, 3000);
        } else {
            setVisibility(View.GONE);
        }
    }

    @Override
    public void onVMSResolutionChanged() {
        setVisibility(View.GONE);
    }

}
