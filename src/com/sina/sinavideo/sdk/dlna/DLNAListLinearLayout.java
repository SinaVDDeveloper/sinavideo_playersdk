package com.sina.sinavideo.sdk.dlna;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sina.sinavideo.sdk.dlna.DLNAEventListener.OnDLNAListSwitchListener;

/**
 * DLNA中间占位图
 * 
 * @author liuqun1
 * 
 */
public class DLNAListLinearLayout extends LinearLayout implements OnDLNAListSwitchListener {

    public DLNAListLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public DLNAListLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        DLNAEventListener.getInstance().addOnDLNAListSwitchListener(this);
    }

    @Override
    public void setUp() {

    }

    @Override
    public void onDLNAListSwitch(boolean open) {
        if (open) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void toggle() {
        if (getVisibility() == VISIBLE) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }

    }
    
    @Override
    public void hide() {
    	if(getVisibility() == VISIBLE){
    		setVisibility(GONE);
    	}
    }

}
