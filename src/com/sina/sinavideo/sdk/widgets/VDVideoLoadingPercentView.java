package com.sina.sinavideo.sdk.widgets;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnBufferingUpdateListener;
import com.sina.sinavideo.sdk.VDVideoViewListeners.OnLoadingListener;
import com.sina.sinavideo.sdk.utils.VDLog;
import com.sina.video_playersdkv2.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public final class VDVideoLoadingPercentView extends TextView implements
		VDBaseWidget, OnLoadingListener, OnBufferingUpdateListener {

	private String mLoadingText = "正在缓冲%d%%";
	private String mPreLoadingText = "开始加载";
	private Context mContext = null;
	private boolean mIsVisible = false;

	public VDVideoLoadingPercentView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public VDVideoLoadingPercentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TypedArray typedArr = context.obtainStyledAttributes(attrs,
				R.styleable.VDVideoLoadingPercentView);
		if (typedArr != null) {
			for (int i = 0; i < typedArr.getIndexCount(); i++) {
				if (typedArr.getIndex(i) == R.styleable.VDVideoLoadingPercentView_loadingText) {
					mLoadingText = typedArr
							.getString(R.styleable.VDVideoLoadingPercentView_loadingText);
				} else if (typedArr.getIndex(i) == R.styleable.VDVideoLoadingPercentView_preLoadingText) {
					mPreLoadingText = typedArr
							.getString(R.styleable.VDVideoLoadingPercentView_preLoadingText);
				}
			}
			typedArr.recycle();
		}
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		registerListener();
	}

	private void registerListener() {
		VDVideoViewController controller = VDVideoViewController
				.getInstance(mContext);
		if (controller != null) {
			controller.addOnBufferingUpdateListener(this);
			controller.addOnLoadingListener(this);
		}
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		registerListener();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void showLoading() {
		// TODO Auto-generated method stub
		setText(mPreLoadingText);
		setVisibility(View.VISIBLE);
		mIsVisible = true;
	}

	@Override
	public void hideLoading() {
		// TODO Auto-generated method stub
		setVisibility(View.GONE);
		mIsVisible = false;
	}

	@Override
	public void onBufferingUpdate(int percent) {
		// TODO Auto-generated method stub
		if (mIsVisible) {
			try {
				String formatTxt = String.format(mLoadingText, percent);
				setText(formatTxt);
			} catch (Exception ex) {
				VDLog.e(VIEW_LOG_TAG, ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

}
