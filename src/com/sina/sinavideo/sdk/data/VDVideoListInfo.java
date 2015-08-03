
package com.sina.sinavideo.sdk.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.sina.sinavideo.sdk.utils.VDLog;

/**
 * 视频列表的封装类<br />
 * 对于前贴片广告来说，对外部是隐藏的，app端只需要调用相应的函数进行插入即可，任何对于视频的操作都是基于正片地址的
 * 
 * @author sunxiao
 */
public class VDVideoListInfo {

    private final static String TAG = "VDVideoListInfo";

    /**
     * 没有广告
     */
    public final static int INSERTAD_TYPE_NONE = 0;
    /**
     * 前贴片广告，在多个流里面
     */
    public final static int INSERTAD_TYPE_MULTI = 1;
    /**
     * 前贴片广告，在单个流里面<br>
     * 注：仅针对m3u8有效，其余无效，意思是在m3u8流中，起始部分的一个或者几个ts流为广告
     */
    public final static int INSERTAD_TYPE_SINGLE = 2;

    /**
     * 前贴片广告类型
     */
    public int mInsertADType = INSERTAD_TYPE_NONE;
    /**
     * 是否设置前贴片广告的时间<br>
     * 如果不设置，那么按照单条时间显示
     */
    public boolean mIsSetInsertADTime = true;
    /**
     * 前贴片广告时长，单位：秒，分为一下情况：<br>
     * 1、如果有多个贴片，在m3u8单流时候，系统可自动回填，如果是多流列表方式，则需要手动填写，否则计算长度会有问题<br>
     * 2、如果只有一个贴片，那么这个值可以写空，后期在prepared回调时候，由系统主动填充进来
     */
    public int mInsertADSecNum = 0;
    /**
     * 当前播放的序号
     */
    public int mIndex = 0;

    /**
     * 检查当前的视频参数，看是否可以播放
     * 
     * @return
     */
    @SuppressWarnings("unused")
    public boolean isCanPlay() throws IllegalArgumentException {
        if (mInsertADType == INSERTAD_TYPE_SINGLE) {
            // 单流广告暂时不支持
            throw new IllegalArgumentException("单流广告暂时不支持");
        }
        if (mInfoList == null || mInfoList.size() == 0) {
            // 播放列表不能为空
            throw new IllegalArgumentException("播放列表不能为空");
        }
        if (!mIsSetInsertADTime && mInsertADSecNum > 0) {
            // 不能同时设置mIsSetInsertADTime以及mInsertADSecNum
            throw new IllegalAccessError("不能同时设置mIsSetInsertADTime以及mInsertADSecNum");
        }
        int num = 0;
        int adNum = 0;
        for (VDVideoInfo info : mInfoList) {
            // if ((info.mPlayUrl == null || info.mPlayUrl.equals(""))
            // && (info.getVMSId() == null || info.getVMSId().equals(""))) {
            // throw new IllegalArgumentException("第" + num + "个连接的播放连接有错误," +
            // info.toString());
            // }
            if (info.mIsInsertAD) {
                adNum++;
            }
            num++;
        }
        if (adNum == 0 && (mInsertADSecNum != 0 || mInsertADType != INSERTAD_TYPE_NONE)) {
            throw new IllegalArgumentException("广告部分设置不正确");
        }
        return true;
    }

    /**
     * @hide
     * @param info
     * @return
     */
    public int getVideoInfoKey(VDVideoInfo info) {
        return mInfoList.indexOf(info);
    }

    /**
     * 得到某个视频（包含广告）
     * 
     * @hide
     * @param num
     * @return
     */
    public VDVideoInfo getVideoInfo(int num) {
        try {
            return mInfoList.get(num);
        } catch (Exception ex)
        {
            VDLog.e(TAG, ex.getMessage());
        }
        return null;
    }

    /**
     * 是否是贴片广告播放结束<br />
     * 判断标识：如果下一个视频为正片且当前是广告就表示广告结束，其余都判错
     * 
     * @return
     */
    public boolean isInsertADEnd() {
        if (mInsertADType == INSERTAD_TYPE_NONE) {
            // 如果是非广告类型，直接返回
            return false;
        }
        int nextIndex = mIndex + 1;
        if (nextIndex > (getVideoListSize() - 1) || nextIndex < 0 || mIndex < 0) {
            // 最后一个视频？那估计是播放列表设置有错误
            return false;
        }

        if (!getVideoInfo(nextIndex).mIsInsertAD && getVideoInfo(mIndex).mIsInsertAD) {
            return true;
        }
        return false;
    }

    /**
     * 播放列表具体内容
     */
    private LinkedList<VDVideoInfo> mInfoList = new LinkedList<VDVideoInfo>();

    /**
     * 整片的列表内容
     */
    // private LinkedList<VDVideoInfo> mRealInfoList = new
    // LinkedList<VDVideoInfo>();

    /**
     * 在多流环境中，从当前流开始，获得还剩余的广告数量
     * 
     * @return
     */
    public int getADNumOfRemain() {
        int adNum = 0;
        if (mInfoList != null) {
            try {
                for (int i = mIndex + 1; i < mInfoList.size(); i++) {
                    if (mInfoList.get(i).mIsInsertAD) {
                        adNum++;
                    }
                }
            } catch (Exception ex) {

            }
        }
        return adNum;
    }

    /**
     * 得到前贴片广告数量
     * 
     * @return
     */
    public int getADNum() {
        int adNum = 0;
        if (mInfoList != null) {
            for (VDVideoInfo info : mInfoList) {
                if (info.mIsInsertAD) {
                    adNum++;
                }
            }
        }

        return adNum;
    }

    /**
     * 转译正片下标到实际的播放列表的下标
     * 
     * @param realNum
     * @return
     */
    public int getRealVideoKey(int realNum) {
        try {
            int realVideoStep = 0;
            int step = 0;
            for (VDVideoInfo value : mInfoList) {
                if (!value.mIsInsertAD) {
                    if (realVideoStep == realNum) {
                        return step;
                    }
                    realVideoStep++;
                }
                step++;
            }
        } catch (Exception ex) {

        }
        return -1;
    }

    /**
     * 得到某个正片<br />
     * 用于视频列表里面
     * 
     * @param num 按照整片的需要排列，去掉广告后的序号
     * @return
     */
    public VDVideoInfo getRealVideoInfo(int num) {
        try {
            int realVideoStep = 0;
            int step = 0;
            for (VDVideoInfo value : mInfoList) {
                if (!value.mIsInsertAD) {
                    if (realVideoStep == num) {
                        return mInfoList.get(step);
                    }
                    realVideoStep++;
                }
                step++;
            }
        } catch (Exception ex) {

        }
        return null;
    }

    /**
     * 得到正片的视频列表，去掉广告后的
     * 
     * @return
     */
    public List<VDVideoInfo> getRealVideoList() {
        List<VDVideoInfo> infoList = new ArrayList<VDVideoInfo>();
        if (mInfoList != null) {
            for (VDVideoInfo value : mInfoList) {
                if (!value.mIsInsertAD) {
                    infoList.add(value);
                }
            }
        }
        return infoList;
    }

    /**
     * 刷新insertad列表，考虑到广告不是必要信息，如果插入失败，那么就按照没有贴片广告的逻辑处理了
     * 
     * @param insertADList 新的贴片广告地址，如果为null，就是删除贴片广告
     * @param preVideoInfo 在当前播放列表的这个视频前面添加前贴片<br />
     *            如果当前视频的前部有贴片地址，那么直接去掉源地址更换新的
     */
    public synchronized int refreshInsertADList(List<VDVideoInfo> insertADList, VDVideoInfo currInfo) {
        if (currInfo == null) {
            VDLog.e(TAG, "currInfo为null，无法添加前贴片广告");
            return 0;
        }

        // 先清理当前给定的info前的所有贴片广告
        removeInsertADBeforeWithVDVideoInfo(currInfo);
        // 加入新的贴片广告
        return insertVideoInfoListBeforeWithVideoInfo(currInfo, insertADList);
    }

    /**
     * 从UI的播放列表中的index，转译出实际的infolist地址<br />
     * 包含前面的前贴片地址
     * 
     * @param index
     * @return
     */
    public synchronized int getCurrKeyFromRealInfo(int index) {
        int ret = index;
        int num = getRealVideoKey(index);
        if (num == -1) {
            VDLog.e(TAG, "realNum:" + index + ",播放列表中找不到这个下标");
            return ret;
        }
        if (num == 0) {
            // 当前视频就是第一个，那么直接返回就可以了
            return ret;
        }
        for (int i = (num - 1); i >= 0; i--) {
            if (!getVideoInfo(i).mIsInsertAD) {
                ret = i + 1;
                break;
            }
        }
        return ret;
    }

    /**
     * 得到当前正在播放的视频信息
     * 
     * @return
     */
    public VDVideoInfo getCurrInfo() {
        return mInfoList.get(mIndex);
    }

    /**
     * 初始化变量
     */
    public synchronized void clean() {
        mInsertADSecNum = 0;
        mIsSetInsertADTime = true;
        mInfoList.clear();
    }

    /**
     * 全部的视频数量
     * 
     * @return
     */
    public int getVideoListSize() {
        try {
            return mInfoList.size();
        } catch (Exception ex) {

        }
        return 0;
    }

    /**
     * 得到当前列表的数量
     * 
     * @return
     */
    public synchronized int getRealVideoListSize() {
        if (mInfoList != null) {
            int num = 0;
            for (VDVideoInfo value : mInfoList) {
                if (!value.mIsInsertAD) {
                    num++;
                }
            }
            return num;
        }
        return 0;
    }

    /**
     * 在列表中增加一个视频
     * 
     * @param info
     */
    public synchronized void addVideoInfo(VDVideoInfo info) {
        mInfoList.add(info);
    }

    /**
     * 在列表中增加一堆视频
     * 
     * @param info
     */
    public synchronized void addVideoList(List<VDVideoInfo> infoList) {
        if (infoList == null) {
            return;
        }
        mInfoList.addAll(infoList);
    }

    /**
     * 清理实际的infolist下标前的广告
     * 
     * @hide
     * @param num
     */
    public synchronized void removeInsertADBeforeWithNum(int num) {
        if (num == 0) {
            // 第一个，无需清理
            return;
        }
        for (int i = (num - 1); i >= 0; i--) {
            if (!getVideoInfo(i).mIsInsertAD) {
                // 走到前一个非前贴片广告的地址时候，跳出即可
                break;
            }
            mInfoList.remove(i);
        }
    }

    /**
     * 给定的UI播放下标往前清理这个下标视频的所有前贴片广告
     * 
     * @param realNum
     */
    public synchronized void removeInsertADBeforeWithRealnum(int realNum) {
        int num = getRealVideoKey(realNum);
        if (num == -1) {
            VDLog.e(TAG, "realNum:" + realNum + ",播放列表中找不到这个下标");
            return;
        }
        removeInsertADBeforeWithNum(num);
    }

    /**
     * 清理给定的info前的所有贴片广告
     * 
     * @param info
     */
    public synchronized void removeInsertADBeforeWithVDVideoInfo(VDVideoInfo info) {
        int num = mInfoList.indexOf(info);
        if (num == -1) {
            VDLog.e(TAG, "找不到当前视频，info:" + info);
            return;
        }
        removeInsertADBeforeWithNum(num);
    }

    /**
     * 在当前num前插入一个视频，目标info
     * 
     * @param sourceInfo
     * @param info
     */
    public synchronized void insertVideoInfoBeforeWithVideoInfo(VDVideoInfo sourceInfo, VDVideoInfo info) {
        int num = mInfoList.indexOf(sourceInfo);
        if (num == -1) {
            VDLog.e(TAG, "mInfoList不包含当前当前info:" + sourceInfo);
            return;
        }
        insertVideoInfoBefore(num, info);
    }

    /**
     * 在当前num前插入一个视频，正片地址
     * 
     * @param realNum 正片序列（不计算）
     * @param info
     */
    public synchronized void insertVideoInfoBeforeWithRealnum(int realNum, VDVideoInfo info) {
        int num = getRealVideoKey(realNum);
        if (num == -1) {
            VDLog.e(TAG, "realNum:" + realNum + ",播放列表中找不到这个下标");
            return;
        }
        insertVideoInfoBefore(num, info);
    }

    /**
     * 在当前num前插入一个视频（不限，可以是贴片广告也可以是正片）
     * 
     * @param num 整个的视频序列
     * @param info
     */
    public synchronized void insertVideoInfoBefore(int num, VDVideoInfo info) {
        if (num < 0 || num >= mInfoList.size()) {
            VDLog.e(TAG, "num:" + num + ",下标越界");
            return;
        }
        mInfoList.add(num, info);
    }

    public synchronized int insertVideoInfoListBeforeWithVideoInfo(VDVideoInfo sourceInfo, List<VDVideoInfo> infoList) {
        int num = mInfoList.indexOf(sourceInfo);
        if (num == -1) {
            VDLog.e(TAG, "mInfoList不包含当前当前info:" + sourceInfo);
            return 0;
        }
        return insertVideoInfoListBeforeWithNum(num, infoList);
    }

    /**
     * 在当前num前插入一组视频，全部地址
     * 
     * @param num
     * @param infoList
     */
    public synchronized int insertVideoInfoListBeforeWithNum(int num, List<VDVideoInfo> infoList) {
        int retNum = num;
        if (num < 0 || num >= mInfoList.size()) {
            VDLog.e(TAG, "num:" + num + ",越界");
            return retNum;
        }
        if (mInfoList.addAll(num, infoList)) {
            retNum = num - infoList.size();
            if (retNum < 0) {
                retNum = 0;
            }
        }
        return retNum;
    }

    /**
     * 在当前num前插入一组视频，正片地址
     * 
     * @param realNum
     * @param infoList
     */
    public synchronized void insertVideoInfoListBeforeWithRealnum(int realNum, List<VDVideoInfo> infoList) {
        int num = getRealVideoKey(realNum);
        if (num == -1) {
            VDLog.e(TAG, "realNum:" + realNum + ",播放列表中找不到这个下标");
            return;
        }
        insertVideoInfoListBeforeWithNum(num, infoList);
    }

    /**
     * 删掉某个视频
     * 
     * @param realNum 正片序列号
     */
    public synchronized void removeVideoInfoWithRealnum(int realNum) {
        try {
            int num = getRealVideoKey(realNum);
            if (num == -1) {
                VDLog.e(TAG, "realNum:" + realNum + ",播放列表中找不到这个下标");
                return;
            }
            mInfoList.remove(num);
        } catch (Exception ex) {
            VDLog.e(TAG, ex.getMessage());
        }
    }

    /**
     * 删掉某个视频
     * 
     * @param num 整个的视频序列
     */
    public synchronized void removeVideoInfo(int num) {
        try {
            mInfoList.remove(num);
        } catch (Exception ex) {
            VDLog.e(TAG, ex.getMessage());
        }
    }

    /**
     * 删掉某个视频
     * 
     * @param info
     */
    public synchronized void removeVideoInfo(VDVideoInfo info) {
        try {
            mInfoList.remove(info);
        } catch (Exception ex) {
            VDLog.e(TAG, ex.getMessage());
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        StringBuffer sb = new StringBuffer();
        sb.append("ad type:" + mInsertADType + ",ad sec num:" + mInsertADSecNum + "\n");
        if (mInfoList != null) {
            int num = 0;
            for (VDVideoInfo info : mInfoList) {
                sb.append("key:" + num + ",,,," + info.toString() + "\n");
                num++;
            }
        }
        return sb.toString();
    }

}
