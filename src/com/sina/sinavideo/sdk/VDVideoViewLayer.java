
package com.sina.sinavideo.sdk;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 视频控件层
 * 
 * @author sina
 */
public class VDVideoViewLayer extends RelativeLayout {

    // 标明当前layer是否是竖屏组件
    public boolean mIsVertical = false;

    public VDVideoViewLayer(Context context) {
        super(context);
        setBackgroundColor(0x0);
    }

    public VDVideoViewLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x0);
    }

}
