
package com.sina.sinavideo.sdk.container;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnLoadingListener;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

public class VDVideoControlLoadingContainer extends RelativeLayout implements VDBaseWidget, OnLoadingListener {

    private Context mContext = null;

    public VDVideoControlLoadingContainer(Context context) {
        super(context);
        init(context);
    }

    public VDVideoControlLoadingContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VDVideoControlLoadingContainer(Context context, AttributeSet attrs, int defStyles) {
        super(context, attrs, defStyles);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    public void showLoading() {
        // TODO Auto-generated method stub
        setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        // TODO Auto-generated method stub
        setVisibility(View.GONE);
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
        if (controller != null) {
            controller.addOnLoadingListener(this);
        }
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
        if (controller != null) {
            controller.removeOnLoadingListener(this);
        }
    }

}
