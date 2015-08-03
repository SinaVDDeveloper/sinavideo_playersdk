
package com.sina.sinavideo.sdk.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnDecodingTypeListener;
import com.sina.sinavideo.sdk.utils.VDSharedPreferencesUtil;
import com.sina.video_playersdkv2.R;

/**
 * 解码选择界面，这个是单独的一个按钮，一般要用两个按钮一起使用才行
 * 
 * @author sunxiao
 */
public class VDVideoDecodingView extends ImageButton implements VDBaseWidget, OnDecodingTypeListener {

    private final static int DECODING_TYPE_FFMPEG = 1;
    private final static int DECODING_TYPE_HARDWARE = 2;

    private int mDecodingType = DECODING_TYPE_FFMPEG;

    private Context mContext = null;

    public VDVideoDecodingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        mContext = context;
        TypedArray typedArr = context.obtainStyledAttributes(attrs, R.styleable.VDVideoDecodingView);
        if (typedArr != null) {
            for (int i = 0; i < typedArr.getIndexCount(); i++) {
                if (typedArr.getIndex(i) == R.styleable.VDVideoDecodingView_decodingType) {
                    int flag = typedArr.getInt(i, -1);
                    if (flag != -1) {
                        mDecodingType = flag;
                    }
                }
                // switch (typedArr.getIndex(i)) {
                // default :
                // break;
                // case R.styleable.VDVideoDecodingView_decodingType :
                // int flag = typedArr.getInt(i, -1);
                // if (flag != -1) {
                // mDecodingType = flag;
                // }
                // break;
                // }
            }
            typedArr.recycle();
        }

        registerListener();
    }

    public boolean getDecodingTypeIsFFMpeg() {
        return VDSharedPreferencesUtil.isDecodingTypeFFMpeg(mContext);
    }

    public void setDecodingType(boolean isFFMpeg) {
        VDSharedPreferencesUtil.setDecodingType(mContext, isFFMpeg);
    }

    private void registerListener() {
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (mDecodingType == DECODING_TYPE_FFMPEG && !getDecodingTypeIsFFMpeg()) {
                    setDecodingType(true);
                    // VDVideoViewController.getInstance().notifyDecodingTypeChange(true);
                } else if (mDecodingType == DECODING_TYPE_HARDWARE && getDecodingTypeIsFFMpeg()) {
                    setDecodingType(false);
                    // VDVideoViewController.getInstance().notifyDecodingTypeChange(false);
                }
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoDecodingView.this.getContext());
                if(controller==null){
                	return;
                }
                long position = controller.getCurrentPosition();
                controller.reset(position);
            }
        });
    }

    private void refreshClickable() {
        if ((mDecodingType == DECODING_TYPE_FFMPEG && getDecodingTypeIsFFMpeg())
                || (mDecodingType == DECODING_TYPE_HARDWARE && !getDecodingTypeIsFFMpeg())) {
            setClickable(true);
        } else {
            setClickable(false);
        }
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(null!=controller)controller.addOnDecodingTypeListener(this);
        refreshClickable();
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
    	VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
    	if(null!=controller)controller.removeOnDecodingTypeListener(this);
    }

    @Override
    public void onChange(boolean isFFMpeg) {
        // TODO Auto-generated method stub
        if ((mDecodingType == DECODING_TYPE_FFMPEG && isFFMpeg)
                || (mDecodingType == DECODING_TYPE_HARDWARE && !isFFMpeg)) {
            setClickable(true);
        } else {
            setClickable(false);
        }
    }

}
