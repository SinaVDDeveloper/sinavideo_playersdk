/**
 * 音量容器，单独弄出来，为了适配OnSoundVisibleListener接口部分
 * 
 * @author seven
 */
package com.sina.sinavideo.sdk.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnSoundVisibleListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

public class VDVideoControlSoundContainer extends LinearLayout implements OnSoundVisibleListener, VDBaseWidget {

    public VDVideoControlSoundContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.addOnSoundVisibleListener(this);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.removeOnSoundVisibleListener(this);
    }

    @Override
    public void onSoundVisible(boolean isVisible) {
        if (isVisible) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    @Override
    public void onSoundSeekBarVisible(boolean isVisible) {
    }

}
