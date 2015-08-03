package com.sina.sinavideo.sdk.container;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

/**
 * 控制条容器类，单击隐藏等操作
 * 
 * @author seven
 */
public class VDVideoSimpleLinearContainer extends LinearLayout implements VDBaseWidget {

    public VDVideoSimpleLinearContainer(Context context) {
        super(context);
    }

    public VDVideoSimpleLinearContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void reset() {
    }

    @Override
    public void hide() {
    }

}
