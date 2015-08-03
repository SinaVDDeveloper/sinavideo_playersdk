package com.sina.sinavideo.sdk.dlna;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sina.sinavideo.sdk.VDVideoViewController;
import com.sina.sinavideo.sdk.dlna.DLNAEventListener.OnDLNAListSwitchListener;
import com.sina.sinavideo.sdk.dlna.DLNAEventListener.OnDLNASwitchListener;
import com.sina.sinavideo.sdk.dlna.DLNAEventListener.OnDLNAVisibleChangeListener;
import com.sina.sinavideo.sdk.dlna.DLNAEventListener.OnMediaRenderNumChangedListener;

/**
 * 搜索到支持DLNA的设置列表
 * 
 * @author sina
 * 
 */
public class DLNAListView extends ListView implements
		OnMediaRenderNumChangedListener, OnDLNAListSwitchListener,
		OnDLNASwitchListener, OnDLNAVisibleChangeListener {

	private MRContentAdapter mAdapter;
	private boolean mShowDLNA = false;

	private MRContent mMRContent;

	public DLNAListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public DLNAListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DLNAListView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context DLNALinearLayout) {
		DLNAEventListener.getInstance().addOnDLNAListSwitchListener(this);
		DLNAEventListener.getInstance().addOnDLNASwitchListener(this);
		DLNAEventListener.getInstance().addOnDLNAVisiableChangeListener(this);
	}

	@Override
	public void onMediaRenderAdded(String uuid, String name) {
		Log.i("DLNA", "onMediaRenderAdded : uuid = " + uuid + " , name = "
				+ name);
		mAdapter.addMR(uuid, name);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onMediaRenderRemoved(String uuid, String name) {
		mAdapter.removeMR(uuid, name);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDLNAListSwitch(boolean open) {
		Log.i("DLNA", "onSwitch : open = " + open);
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void setUp() {
		if (mAdapter == null) {
			setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mMRContent = (MRContent) mAdapter.getItem(position);
					if (0 == DLNAController.getInstance(getContext())
							.setMediaRender(mMRContent.getUuid())) {
						mShowDLNA = true;
						DLNAEventListener.getInstance().notifyOnPreOpenDLNA();
						Toast.makeText(getContext(), "正在连接设备",
								Toast.LENGTH_SHORT).show();
						mAdapter.setSelectPosition(position);
						DLNAController.mIsDLNA = true;
						DLNAEventListener.getInstance()
								.notifyDLNAMediaRenderSelected(
										mMRContent.getName(),
										mMRContent.getUuid());
					} else {
						DLNAController.mIsDLNA = false;
					}
				}
			});

			mAdapter = new MRContentAdapter(getContext());
			setAdapter(mAdapter);
			DLNAEventListener.getInstance().addOnMediaRenderNumChangedListener(
					this);
		}
	}

	@Override
	public void toggle() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDLNASwitch(boolean open) {
		if (mAdapter != null && !open) {
			mAdapter.removeAll();
		}
	}

	@Override
	public void onDLNAIndicaterVisibleChange(boolean visible) {
		if (visible) {
			if (mShowDLNA /* && mDLNALinearLayout != null */) {
				// mDLNALinearLayout.setVisibility(VISIBLE);
				VDVideoViewController controller = VDVideoViewController
						.getInstance(this.getContext());
				if (controller != null)
					controller.notifySetDLNALayoutVisible(true);
				mShowDLNA = false;
			}
		} else {
			VDVideoViewController controller = VDVideoViewController
					.getInstance(this.getContext());
			if (controller != null)
				controller.notifySetDLNALayoutVisible(false);
			// if(mDLNALinearLayout != null){
			// mDLNALinearLayout.setVisibility(GONE);
			// }
		}
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}
}
