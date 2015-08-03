package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnProgressUpdateListener;
import com.sina.video_playersdkv2.R;

/**
 * 快进或快退图标
 * 
 * @author liuqun
 */
public class VDVideoProgressIndicaterView extends ImageButton implements VDBaseWidget, OnProgressUpdateListener {

//	private Context mContext;
	private long mPosition = 0;

	public VDVideoProgressIndicaterView(Context context) {
		super(context);
//		mContext = context;
		setBackgroundResource(R.drawable.playbutton);
	}

	public VDVideoProgressIndicaterView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		mContext = context;

        TypedArray typedArr = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.background});

        if (typedArr != null) {
            int resouceID = typedArr.getResourceId(0, -1);
            if (resouceID == -1) {
                setBackgroundResource(R.drawable.playbutton);
            }
            typedArr.recycle();
        } else {
            setBackgroundResource(R.drawable.playbutton);
        }
        VDVideoViewController controller = VDVideoViewController.getInstance(context);
        if(controller!=null)controller.addOnProgressUpdateListener(this);
    }

    @Override
    public void reset() {
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.addOnProgressUpdateListener(this);
    }

    @Override
    public void hide() {
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(controller!=null)controller.removeOnProgressUpdateListener(this);
    }

    @Override
    public void onProgressUpdate(long current, long duration) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDragProgess(long progress, long duration) {
        if (mPosition < progress) {
            setBackgroundResource(R.drawable.play_ctrl_seek_forward);
            // setBackgroundResource(R.drawable.play_ctrl_seek_forward);
        } else {
            // setBackgroundResource(R.drawable.play_ctrl_seek_backward);
            setBackgroundResource(R.drawable.play_ctrl_seek_backward);
        }
        mPosition = progress;
    }
}
