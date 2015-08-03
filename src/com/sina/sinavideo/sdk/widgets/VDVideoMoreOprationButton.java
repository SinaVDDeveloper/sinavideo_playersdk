package com.sina.sinavideo.sdk.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;
import com.sina.video_playersdkv2.R;

/**
 * 更多按钮
 * 
 * @author liuqun
 * 
 */
public class VDVideoMoreOprationButton extends ImageButton implements VDBaseWidget {

    @SuppressLint("nouse")
    private final static String TAG = "VDVideoRelatedButton";
    @SuppressLint("nouse")
    private Context mContext;

    public VDVideoMoreOprationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray typedArr = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.background});
        if (typedArr != null) {
            int resouceID = typedArr.getResourceId(0, -1);
            if (resouceID == -1) {
                setBackgroundResource(R.drawable.more);
            }
            typedArr.recycle();
        } else {
            setBackgroundResource(R.drawable.more);
        }

        registerListeners();
    }

    public VDVideoMoreOprationButton(Context context) {
        super(context);
        mContext = context;
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
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoMoreOprationButton.this
                        .getContext());
                if (controller != null)
                    controller.notifyHideControllerBar(0);
                if (controller != null)
                    controller.notifyShowMoreOprationPanel();
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
