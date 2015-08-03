package com.sina.sinavideo.sdk.widgets.playlist;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.sina.sinavideo.sdk.VDVideoViewController;

/**
 * 相关视频列表滑动监听类
 * 
 * @author sina
 *
 */
public class VDPlayListScrollListener implements OnScrollListener {
	private Context mContext;
    public VDPlayListScrollListener(Context context) {
    	mContext = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {//5 秒后隐藏
        	VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
        	if(null!=controller)
        		controller.notifyRemoveAndHideDelayVideoList();
        } else if (scrollState == SCROLL_STATE_FLING) {
            
        } else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {//移除
        	VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
        	if(null!=controller)
        		controller.notifyRemoveAndHideDelayVideoList();
        }
//        VDVideoViewController.getInstance().notifyHideControllerBar(VDVideoViewController.DEFAULT_DELAY);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        VDVideoViewController.getInstance().notifyHideControllerBar(VDVideoViewController.DEFAULT_DELAY);
    	VDVideoViewController controller = VDVideoViewController.getInstance(mContext);
    	if(null!=controller)
    		controller.notifyRemoveAndHideDelayVideoList();
    }

}
