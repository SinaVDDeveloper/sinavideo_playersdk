
package com.sina.sinavideo.sdk;

import java.util.ArrayList;
import java.util.List;

/**
 * VDVideoViewLayerContext的包装类
 * 
 * @author sunxiao
 */
public class VDVideoViewLayerContextData {

    public static int LAYER_COMPLEX_ALL = 0;// 复杂模式，横竖全
    public static int LAYER_COMPLEX_NOVERTICAL = 1; // 复杂模式，只横不竖
    public static int LAYER_SIMPLE = 2; // 简单模式，只竖不横

    /**
     * 标识当前页是什么类型，默认是复杂模式，横竖全
     */
    private int mLayerType = LAYER_COMPLEX_ALL;

    private List<VDVideoViewLayerContext> mList = new ArrayList<VDVideoViewLayerContext>();

    public VDVideoViewLayerContextData()
    {
        super();
    }

    public int getLayerType()
    {
        return mLayerType;
    }

    public void setLayerType(int layerType)
    {
        mLayerType = layerType;
    }

    public List<VDVideoViewLayerContext> getLayerList()
    {
        return mList;
    }

    public void setLayerList(List<VDVideoViewLayerContext> layerContextList)
    {
        mList = layerContextList;
    }

    public void addLayerContext(VDVideoViewLayerContext layerContext)
    {
        mList.add(layerContext);
    }

    public void removeLayerContext(VDVideoViewLayerContext layerContext)
    {
        if (mList.contains(layerContext))
        {
            mList.remove(layerContext);
        }
    }

}
