package com.sina.sinavideo.sdk.dlna;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * DLNA播放页中关闭按钮控件
 * 
 * @author sina
 *
 */
public class DLNACloseButton extends TextView {

    public DLNACloseButton(Context context) {
        super(context);
        init(context);
    }

    public DLNACloseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DLNACloseButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DLNAEventListener.getInstance().notifyDLNAClose();
                // DLNAController.getInstance(getContext()).pause();
                DLNAController.getInstance(getContext()).stop();
                DLNAController.getInstance(getContext()).release();
            }
        });
    }

}
