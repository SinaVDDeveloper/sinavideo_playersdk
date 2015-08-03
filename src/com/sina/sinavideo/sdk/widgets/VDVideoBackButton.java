package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.video_playersdkv2.R;

/**
 * 返回按键，从全屏返回半屏。从横屏到竖屏部分的点击按钮显示
 * 
 * @author sunxiao
 * 
 */
public class VDVideoBackButton extends ImageButton implements VDBaseWidget {

    // private Context mContext = null;
    // private final static String TAG = "VDVideoBackButton";

    public VDVideoBackButton(Context context) {
        super(context);
        // mContext = context;
        registerListeners();
    }

    public VDVideoBackButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // mContext = context;

        // 判断是否有新定义的背景图片，没有则使用sina视频的默认图片
        TypedArray typedArr = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.background});
        if (typedArr != null) {
            int resouceID = typedArr.getResourceId(0, -1);
            if (resouceID == -1) {
                setBackgroundResource(R.drawable.play_ctrl_back);
            }
            typedArr.recycle();
        } else {
            setBackgroundResource(R.drawable.play_ctrl_back);
        }

        registerListeners();
    }

    @Override
    public void reset() {
    }

    @Override
    public void hide() {
    }

    /**
     * 为自己注册事件
     */
    private void registerListeners() {

        // click事件
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoBackButton.this.getContext());
                if(null!=controller)controller.setIsFullScreen(false);
            }

        });

    }

}
