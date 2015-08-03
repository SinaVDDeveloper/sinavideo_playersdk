/**
 * 全屏按钮部分，点击后，横屏显示
 * 
 * @author sunxiao
 */

package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.utils.VDVideoFullModeController;
import com.sina.video_playersdkv2.R;

public class VDVideoFullScreenButton extends ImageButton implements VDBaseWidget {

    public VDVideoFullScreenButton(Context context) {
        super(context);
        init();
    }

    public VDVideoFullScreenButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 判断是否有新定义的背景图片，没有则使用sina视频的默认图片
        TypedArray typedArr = context.obtainStyledAttributes(attrs, new int[] {
                android.R.attr.background
        });
        if (typedArr != null) {
            int resouceID = typedArr.getResourceId(0, -1);
            if (resouceID == -1) {
                setBackgroundResource(R.drawable.play_ctrl_fullscreen);
            }
            typedArr.recycle();
        } else {
            setBackgroundResource(R.drawable.play_ctrl_fullscreen);
        }

        init();
    }

    private void init() {
        registerListeners();
    }

    private void registerListeners() {
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!VDVideoFullModeController.getInstance().getIsFullScreen())
                {
                	VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoFullScreenButton.this.getContext());
                	if(null!=controller)controller.setIsFullScreen(true);
                }
            }
        });
    }

    @Override
    public void reset() {
    }

    @Override
    public void hide() {
    }
}
