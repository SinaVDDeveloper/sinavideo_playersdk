package com.sina.sinavideo.sdk.widgets.playlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.sinavideo.sdk.widgets.VDBaseWidget;
import com.sina.video_playersdkv2.R;

/**
 * 相关视频
 * 
 * @author liuqun
 * 
 */
public class VDVideoRelatedButton extends ImageButton implements VDBaseWidget {

    private final static String TAG = "VDVideoRelatedButton";

    private final static int UICONTROL_NONE = 1;
    private final static int UICONTROL_STATUSBAR = 2;
    private final static int UICONTROL_TOPCONTAINER = 4;
    private final static int UICONTROL_BOTTOMCONTAINER = 8;
    @SuppressLint("nouse")
    private int mListContainerID = -1;
    private int mUIControl = -1;

    public VDVideoRelatedButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        // mContext = context;

        // 如果有自定义属性，加载
        TypedArray typedArr = context.obtainStyledAttributes(attrs, R.styleable.VDVideoRelatedButton);
        if (typedArr != null) {
            for (int i = 0; i < typedArr.getIndexCount(); i++) {
                int i1 = typedArr.getIndex(i);
                if (i1 == R.styleable.VDVideoRelatedButton_listContainer) {
                    mListContainerID = typedArr.getResourceId(R.styleable.VDVideoRelatedButton_listContainer, -1);

                } else if (i1 == R.styleable.VDVideoRelatedButton_uiControl) {
                    mUIControl = typedArr.getInteger(R.styleable.VDVideoRelatedButton_uiControl, -1);

                }
            }
            typedArr.recycle();
        } else {
            VDLog.e(TAG, "listContainer属性必须设置");
            return;
        }

        // 如果没有自定义图片，则默认为新浪视频的素材
        typedArr = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.background});
        if (typedArr != null) {
            int resouceID = typedArr.getResourceId(0, -1);
            if (resouceID == -1) {
                setBackgroundResource(R.drawable.play_ctrl_video_list);
            }
            typedArr.recycle();
        } else {
            setBackgroundResource(R.drawable.play_ctrl_video_list);
        }

        registerListeners();
    }

    public VDVideoRelatedButton(Context context) {
        super(context);
        // mContext = context;
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
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoRelatedButton.this.getContext());
                if(controller==null){
                	return ;
                }
                if (mUIControl == -1 || ((mUIControl & UICONTROL_NONE) == 1)) {
                    controller.notifyHideControllerBar(0);
                } else if ((mUIControl & UICONTROL_STATUSBAR) == 2) {
                    // 显示状态栏
                    controller.hideStatusBar(false);
                } else if ((mUIControl & UICONTROL_TOPCONTAINER) == 4) {
                    // 显示顶部
                    controller.notifyShowTopControllerBar();
                } else if ((mUIControl & UICONTROL_BOTTOMCONTAINER) == 8) {
                    // 显示底部
                    controller.notifyShowBottomControllerBar();
                }
                controller.notifyToogleVideoList();
                // if (mContext != null && mListContainerID != -1) {
                // mPlayerlistContainer = (VDVideoPlaylistContainer) ((Activity)
                // mContext)
                // .findViewById(mListContainerID);
                // }
                // if (mPlayerlistContainer != null) {
                // if (mPlayerlistContainer.getVisibility() == View.GONE) {
                // mPlayerlistContainer.setVisibility(View.VISIBLE);
                // } else {
                // mPlayerlistContainer.setVisibility(View.GONE);
                // }
                // }
            }

        });

    }

    @Override
    public void reset() {
        this.setBackgroundResource(R.drawable.play_ctrl_video_list_bg);
    }

    @Override
    public void hide() {
    }

}
