# sinavideo_playersdk


##郑重声明

很多进群的VDPlayerSDK用户第一句话就问，这个项目是否是免费得，针对商业项目是否还是免费得。

**VDPlayerSDK开源且免费，遵循bsd开源协议，无授权费，甚至于，你可以任意修改签名标注等，抹掉VDPlayerSDK的任何痕迹都是允许的**

QQ群号：414552747，作者们都在，有事儿进群说

##项目的由来

大部分时候，在android中集成一个视频播放器是很痛苦的过程，因为google提供的多媒体控件实在距离UI太远（似乎这个框架根本没法用），我们也一样深受其害，所以，我们在自己研发解码器的时候，就顺手也处理了一下UI部分的逻辑。我们封装了大部分的播放器中的UI控件，比如playbutton之类的，把里面的大部分事件用EventBus模式进行了处理。这样，最终的结果是，大部分时候，集成一个播放器只需要三步就可以了。具体的可以参照demo中的例子，代码量确实很少，你懂的。

忘了说了，我们支持广告播放，但只支持多流方式，也就是广告跟正片是分着的。以后会加入广告跟正片动态合并为单流播放方式，以避免，广告播放后，正片还需要加载loading的问题。

##集成步骤
集成一个播放器需要几步？三步<br/>
1. 设置一个播放列表 

        info = new VDVideoInfo();
        info.mTitle = "这就是一个测试视频0";
        info.mPlayUrl = "http://wtv.v.iask.com/player/ovs1_vod_rid_2015052841_br_3_pn_weitv_tn_0_sig_md5.m3u8";
        infoList.addVideoInfo(info);
		// 初始化播放器以及播放列表
        mVDVideoView.open(SinaVideoActivity.this, infoList);
        // 开始播放，直接选择序号即可
        mVDVideoView.play(0);
        
2. 从demo里面copy一份layout出来

    `<com.sina.sinavideo.sdk.VDVideoView
        android:id="@+id/vv1"
        android:layout_width="match_parent"
        android:layout_height="240dp"
        android:layout_alignParentTop="true"
        android:background="#000000"
        app:layerAttrs="@array/sv_videoview_layers2" >
    </com.sina.sinavideo.sdk.VDVideoView>`

3. 设置一下layout中的素材，比如文字、颜色，图片，大小等等

然后，就可以提交上线了
##组件说明：

1. VDVideoView，统一的接口类
2. VDVideoViewController，使用EventBus方式来处理的消息控制中心
3. ***widgets***，自定义的组件库
4. ***Containers***，自定义的容器类，包裹widgets用得

###VDVideoView
VDVideoView是提供给客户端用的，客户端只需使用VDVideoView就能完成视频的播放的功能；VDVideoView封装了视频播放的View和 控制层UI，提供了横竖屏切换和播放视频的接口。
其中 layerAttrs属性用于设置小屏和全屏的播放器控制层UI。

####控制部分

open(Context context, VDVideoListInfo infoList)  该方法用于设置视频列表。

open(Context context, VDVideoInfo path) 简化版，设置单视频

play(int index)， 该方法用于播放视频列表里的第index个视频

play(int index, long position)， 播放第index个视频，并且从position开始，position为毫秒

setIsFullScreen(boolean isFullScreen) 该方法用来设置全屏或小屏

stop() 该方法停止播放视频

release(boolean isOnlyReloadVideo) 该方法销毁播放器

####activity回调部分

基本与activity使用同名函数，默认onResume使用暂停方式提供，即：onPause->onStop->onResume调用链后，当前视频是在暂停状态的

####其他部分，包括广告等

getIsPlaying() 是否播放中

getPlayerStatus() 播放器当前状态[getIsPlaying的复杂版，能得到更多状态]

VDVideoListInfo getListInfo() 拿到当前的播放列表

setLayers(int resourceID) 手动加载layer，用于在一些无法写layout xml的场合，比如列表播放的时候

setExternalFullContainer(ViewGroup vg)
	设置父容器，用来做全屏转换<br />
	有时候，播放器会嵌入到很奇怪的vg中，比如：fragment等<br/>
	默认，SDK会自己寻找decoreView为父类容器<br />
	但在特殊情况下，找到的decoreView不是正确的父容器，这时候需要手工指定
	
refreshInsertADList(List<VDVideoInfo> insertADList,
			VDVideoInfo currInfo)
			
播放中，加入新的广告贴片，一般用在点击播放列表的时候。

###VDVideoInfoList

播放列表，用来设置需要播放的流

###VDVideoViewController

封装了播放器控制行为操作，例如 手势，按钮点击事件。一般情况下，请不要直接调用

###***widgets***

详细的参考：【还没写完】

###***containers***

详细的参考：【还没写完】

###***VDVideoExtListeners***

外部事件封装，app端的activity有时候需要得到播放器的一些事件回调。这个时候就需要用到外部listener类

通过VDVideoView中的set***Listener函数族来注册事件，并且在activity中的implement来进行回调。

具体用法参见demo中的例子。

##一些提示
###我如何安排集成播放器的开发节奏？
我们推荐的开发进程安排如下：
1、	从Demo工程与《快速启动》文档开始，时间控制在0.5Day
2、	在自己的工程中，copy进demo中的layout设计，让它先在自己的项目中跑起来，时间：0.5Day
3、	按照产品经理的产品需求，参照设计师设计的UI，进行layout的编写工作（理论上就是切图，换图，然后运行起来看看），在期间，可以参照《用户手册》文档，熟悉系统提供的widget与container的用法，时间2.5Day
4、	开始自测以及机型适配工作，时间：3Day
5、	提交测试吧。###什么是简单模式与复杂模式？
根据产品的不同呈现，播放器SDK分为：简单模式跟复杂模式。
简单模式：只有一层layer，一般为竖屏，可以自定义组件，但相应扩展能力较弱
复杂模式：可以包含N层layer，一般一层layer竖屏，一层layer横屏，其余各层作为显示层或者广告层，扩展强，容易定制，layer文件的长度也容易控制。###什么是layer？

我们自定义了一个layer概念，用于分别堆放不同用途的container与widget。在复杂模式下，一般一个layer表示一个确定的功能，比如：控件层、显示层。系统默认，最下面一层是Videiview层，用来播放视频。###什么是widget与container？
Wideget是控件的意思，比如：开始按钮，就是一个控件，集成自view。
container是容器的意思，因为播放器中，有很多自定义的消息需要处理，而系统提供的容器无法使用，可以理解为：viewgroup
###可靠性
现在新浪的一部分app正在或者即将使用此款SDK，包括：新浪新闻、新浪体育、新浪视频、新浪游戏。新浪微博也正在接洽中