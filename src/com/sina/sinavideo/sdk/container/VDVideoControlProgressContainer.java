/**
 * 调节亮度的容器，单独出来，为了适配OnLightingVisibleListener接口
 * 
 * @author seven
 */
package com.sina.sinavideo.sdk.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnProgressViewVisibleListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

public class VDVideoControlProgressContainer extends LinearLayout implements OnProgressViewVisibleListener,
        VDBaseWidget {

    public VDVideoControlProgressContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void reset() {
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.addOnProgressViewVisibleListener(this);
    }

    @Override
    public void hide() {
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.removeOnProgressViewVisibleListener(this);
        setVisibility(GONE);
    }

    @Override
    public void onProgressVisible(boolean isVisible) {
        if (isVisible) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

}
