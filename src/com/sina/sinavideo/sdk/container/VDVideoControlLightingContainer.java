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
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnLightingVisibleListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

public class VDVideoControlLightingContainer extends LinearLayout implements OnLightingVisibleListener, VDBaseWidget {

    public VDVideoControlLightingContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.addOnLightingVisibleListener(this);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.removeOnLightingVisibleListener(this);
    }

    @Override
    public void onLightingVisible(boolean isVisible) {
        // TODO Auto-generated method stub
        if (isVisible) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

}
