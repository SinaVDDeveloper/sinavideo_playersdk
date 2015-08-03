package com.sina.sinavideo.sdk.widgets;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.sinavideo.coreplayer.splayer.MediaPlayer;
import com.sina.sinavideo.sdk.VDVideoConfig;
import com.sina.sinavideo.sdk.VDVideoConfig.eVDDecodingType;
import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.container.VDVideoControlContainer;
import com.sina.sinavideo.sdk.dlna.DLNAController;
import com.sina.sinavideo.sdk.utils.VDSharedPreferencesUtil;
import com.sina.video_playersdkv2.R;

/**
 * 解码方式选择自定义控件，硬解软解
 * 
 * @author sunxiao
 */
public class VDVideoDecodingButton extends ImageButton implements VDBaseWidget {

    private int mContainerID = -1;
    private int mDialogAdapterID = -1;
    private VDVideoControlContainer mContainer = null;
    private Context mContext = null;
    // private final static String TAG = "VDVideoDecodingButton";
    private LayoutInflater mLayoutInflater = null;
    private AlertDialog mDialog = null;

    private static class ViewHolder {

        TextView tv1;
        RadioButton rb1;
    }

    private class VDVideoDecodingDialogAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 3;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mLayoutInflater.inflate(mDialogAdapterID, null);
                viewHolder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                viewHolder.rb1 = (RadioButton) convertView.findViewById(R.id.rb1);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String desc = "";
            if (position == 0) {
                desc = VDVideoConfig.mDecodingDesc;
            } else if (position == 1) {
                desc = VDVideoConfig.mDecodingPlayerDesc[0];
            } else if (position == 2) {
                desc = VDVideoConfig.mDecodingPlayerDesc[1];
            }

            if (position == 0) {
                viewHolder.rb1.setVisibility(View.GONE);
            } else {
                viewHolder.rb1.setVisibility(View.VISIBLE);
                viewHolder.rb1.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        if (position == 1) {
                            setDecodingType(true);
                        } else if (position == 2) {
                            setDecodingType(false);
                        }
                        mDialog.dismiss();
                    }
                });
            }
            viewHolder.tv1.setText(desc);

            convertView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundColor(0xFF5CACEE);
                    } else if (event.getAction() == MotionEvent.ACTION_UP
                            || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        v.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return false;
                }
            });

            boolean isFFMpeg = VDSharedPreferencesUtil.isDecodingTypeFFMpeg(mContext);
            int index = isFFMpeg ? 0 : 1;
            if ((index == 0 && position == 1) || (index == 1 && position == 2)) {
                viewHolder.rb1.setChecked(true);
            } else {
                viewHolder.rb1.setChecked(false);
            }

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if ((VDSharedPreferencesUtil.isDecodingTypeFFMpeg(mContext) && position == 1)
                            || (!VDSharedPreferencesUtil.isDecodingTypeFFMpeg(mContext) && position == 2)) {
                        // 相等，不设置，直接退出
                        return;
                    }
                    ViewHolder viewHolder = (ViewHolder) arg0.getTag();
                    viewHolder.rb1.setChecked(true);
                    if (position == 1) {
                        setDecodingType(true);
                    } else if (position == 2) {
                        setDecodingType(false);
                    }
                    mDialog.dismiss();
                }
            });

            return convertView;
        }
    }

    /**
     * 设置解码类型，eg：软解、硬解
     * 
     * @param isFFMpeg
     *            true：表示软解码；false：表示硬解码
     */
    private void setDecodingType(boolean isFFMpeg) {
        if (isFFMpeg == VDSharedPreferencesUtil.isDecodingTypeFFMpeg(mContext)) {
            return;
        }
        VDSharedPreferencesUtil.setDecodingType(mContext, isFFMpeg);

        VDVideoViewController controller = VDVideoViewController.getInstance(this.getContext());
        if (controller == null) {
            return;
        }
        long sec = controller.getCurrentPosition();
        VDVideoViewController.getInstance(this.getContext()).reset(sec);
    }

    /**
     * 构造函数
     * 
     * @param context
     * @param attrs
     */
    public VDVideoDecodingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        // boolean supportHard = MediaPlayer.supportHardDecode();
        // if (supportHard) {
        // setVisibility(VISIBLE);
        // } else {
        // setVisibility(GONE);
        // }

        TypedArray typedArr = context.obtainStyledAttributes(attrs, R.styleable.VDVideoDecodingButton);
        if (typedArr != null) {
            for (int i = 0; i < typedArr.getIndexCount(); i++) {
                if (typedArr.getIndex(i) == R.styleable.VDVideoDecodingButton_decodingTypeContainer) {
                    mContainerID = typedArr.getResourceId(i, -1);
                } else if (typedArr.getIndex(i) == R.styleable.VDVideoDecodingButton_decodingTypeDialogAdapter) {
                    mDialogAdapterID = typedArr.getResourceId(i, -1);
                }
            }
            typedArr.recycle();
        }

        if (mDialogAdapterID == -1) {
            mDialogAdapterID = R.layout.default_decodingtype_adapter;
        }

        typedArr = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.background});
        if (typedArr != null) {
            int resouceID = typedArr.getResourceId(0, -1);
            if (resouceID == -1) {
                setBackgroundResource(R.drawable.decoding_setting);
            }
            typedArr.recycle();
        } else {
            setBackgroundResource(R.drawable.decoding_setting);
        }

    }

    /**
     * 初始化数据
     * 
     * @param context
     */
    private void init(Context context) {
        mContext = context;

        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * 注册点击事件监听
     */
    private void registerClickListener() {
        setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean supportHard = MediaPlayer.supportHardDecode();
                VDVideoViewController controller = VDVideoViewController.getInstance(VDVideoDecodingButton.this
                        .getContext());
                if (null == controller) {
                    return;
                }
                if (!supportHard) {
                    Toast.makeText(getContext(), R.string.sorry_cannot_switch_player, 0).show();
                    controller.notifyHideMoreOprationPanel();
                    return;
                }
                if (DLNAController.mIsDLNA) {
                    // TODO
                    Toast.makeText(getContext(), R.string.cannot_switch_player, Toast.LENGTH_LONG).show();
                    return;
                }
                controller.pause();
                if (mContainer != null) {
                    if (mContainer.getVisibility() == View.VISIBLE) {
                        mContainer.setVisibility(View.GONE);
                    } else {
                        mContainer.setVisibility(View.VISIBLE);
                    }
                } else {
                    // 找不到容器，调用系统提供的alertView进行切换
                    if (mDialog != null) {
                        mDialog.show();
                    }

                }
                controller.notifyHideMoreOprationPanel();
            }
        });
    }

    /**
     * 获取对应的容器对象
     */
    private void getContainer() {
        if (mContainerID != -1) {
            mContainer = (VDVideoControlContainer) ((Activity) mContext).findViewById(mContainerID);
        }
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        if (VDVideoConfig.mDecodingType == eVDDecodingType.eVDDecodingTypeSoft) {
            // 如果选择了纯软环境，那么，选择按钮直接不出现
            setVisibility(View.GONE);
        }
        if (mContainer == null) {
            getContainer();
            if (mContainer != null) {
                mContainer.setVisibility(View.GONE);
            } else if (mDialog == null) {
                mDialog = new AlertDialog.Builder(mContext).setTitle(VDVideoConfig.mDecodingPlayerTitle)
                        .setAdapter(new VDVideoDecodingDialogAdapter(), null)
                        .setNegativeButton(VDVideoConfig.mDecodingCancelButton, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                VDVideoViewController controller = VDVideoViewController
                                        .getInstance(VDVideoDecodingButton.this.getContext());
                                if (null != controller) {
                                    controller.resume();
                                    controller.start();
                                }
                            }
                        }).create();
                mDialog.setCanceledOnTouchOutside(false);
            }
        }
        registerClickListener();
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        if (mDialog != null) {
            mDialog.hide();
        }
    }

}
