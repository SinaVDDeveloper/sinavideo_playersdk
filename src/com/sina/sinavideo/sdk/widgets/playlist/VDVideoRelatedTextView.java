package com.sina.sinavideo.sdk.widgets.playlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

/**
 * 相关视频
 * 
 * @author liuqun
 * 
 */
public class VDVideoRelatedTextView extends TextView implements VDBaseWidget {
    @SuppressLint("nouse")
    private final static String TAG = "VDVideoRelatedButton";

    public VDVideoRelatedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        registerListeners();
    }

    public VDVideoRelatedTextView(Context context) {
        super(context);
        registerListeners();
    }

    /**
     * 为自己注册事件
     */
    private void registerListeners() {
        // click事件
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoRelatedTextView.this.getContext());
                if(null==controller){
                	return;
                }
                controller.notifyHideControllerBar(0);
                controller.notifyToogleVideoList();
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
