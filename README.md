# 简述

PiPanoSDK 是一套用于处理全景图像的二次开发工具包，包含以下功能功能：

  1、支持 浏览图片、播放视频、预览视频流；

  2、支持图像输入源包含：单鱼眼，双鱼眼，全景2:1等；

  3、 支持多种展开模式：沉浸、鱼眼、小行星、圆柱、VR、双画卷等；

  4、 支持多种滤镜效果：锐化、木炭笔、轮廓、蓝莓、像素化等；

  5、 支持多种过场动画：翻转、渐变、开门、光圈、折叠等。


![沉浸](http://fortylin-image.oss-cn-shenzhen.aliyuncs.com/doc/2017-10-13-%E6%B2%89%E6%B5%B8.gif)![小行星](http://fortylin-image.oss-cn-shenzhen.aliyuncs.com/doc/2017-10-13-%E5%B0%8F%E8%A1%8C%E6%98%9F.gif)

![综合](http://fortylin-image.oss-cn-shenzhen.aliyuncs.com/doc/2017-10-13-%E7%BB%BC%E5%90%88.gif)![坠入](http://fortylin-image.oss-cn-shenzhen.aliyuncs.com/doc/2017-10-13-%E5%9D%A0%E5%85%A5.gif)


# 示例

![事务Demo下载地址](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/商务Demo下载地址.png)

下载链接 ：[Google Play](https://play.google.com/store/apps/details?id=com.pi.testing.sdktesting)


# 集成

[PiPanSDK集成到AndroidStudio](https://github.com/pisofttech/pipano-sdk-android/blob/master/PiPanSDK集成到AndroidStudio.md)



# 基本用法

### 初始化

1、在Activity中onCreate()中创建SDK对象

```java
 mPiPanoSDK = new PiPanoSDK(this, this);
```

2、将SDK实例与UI关联

```java
// 将SDK的Veiw与UI关联
mLayout = (FrameLayout) findViewById(R.id.sdk_view);
mSDKView = mPiPanoSDK.getPlayerView();  // 获取SDK的View
mLayout.addView(mSDKView);
```

3、重载相关响应函数

```java
@Override
public void onPause()
{
  super.onPause();
  
  if (null != mPiPanoSDK)
  {
    mPiPanoSDK.onPause();
  }
}

@Override
public void onResume()
{
  super.onResume();

  if (null != mPiPanoSDK)
  {
    mPiPanoSDK.onResume();
  }
}

@Override
public void onConfigurationChanged(Configuration newConfig)
{
  super.onConfigurationChanged(newConfig);
  
  if (null != mPiPanoSDK)
  {
    mPiPanoSDK.onConfigurationChanged(newConfig);
  }
}

@Override
public void onWindowFocusChanged(boolean hasFocus)
{
  super.onWindowFocusChanged(hasFocus);
  
  if (null != mPiPanoSDK)
  {
    mPiPanoSDK.onWindowFocusChanged(hasFocus);
  }
}

@Override
public void onDestroy()
{
  super.onDestroy();

  if (null != mPiPanoSDK)
  {
    mPiPanoSDK.onDestroy();
    mPiPanoSDK = null;
  }
}
```

4、实现SDK初始化完成的回调接口

```java
public void onSDKIsReady()
{
    Log.d(TAG, "SDK is ok");
}

```

### 加载照片

1、调用openPhoto()打开本地照片，并显示

```java
mPiPanoSDK.openPhoto(path, type);	// path是照片路径，type是图像源类型（单目 或 全景2:1）
```

2、照片加载成功后，可以通过setViewMode()可以切换展开模式

```java
mPiPanoSDK.setViewMode(PiViewModeType.PIVM_Immerse);    // 设置展开模式
```

### 播放视频

1、调用openVideo()打开本地mp4文件，并播放

```java
mPiPanoSDK.openVideo(path, type);   // path是MP4文件路径，type是图像源类型（单目 或 全景2:1）
```

2、视频播放过程中，可以调用setViewMode()切换展开模式

```java
mPiPanoSDK.setViewMode(PiViewModeType.PIVM_Immerse);    // 设置展开模式
```

3、视频播放的控制

```java
mPiPanoSDK.pause();	// 暂停播放
mPiPanoSDK.resume();	// 继续播放
mPiPanoSDK.seek(0.3);    // 指定跳转到视频30%的进度(实际只能跳转到与指定进度最近的关键帧)
mPiPanoSDK.stop();	// 停止播放
```

### 预览视频流

1、设置监听预览是否准备就绪

```java
mPiPanoSDK.setPreviewIsReadyListener(this);
```

2、实现预览准备就绪的回调接口

```java
@Override
public void onPreviewIsReady()
{
  Log.d(TAG, "Preview is ok!!!");

  openCamera(CAMERA_FRONT);	// 打开相机镜头

  // 获取SDK的SurfaceTexture
  SurfaceTexture texture = null;
  texture = mPiPanoSDK.getPreviewSurfaceTexture();
  
  mCamera.setPreviewTexture(texture);
  mCamera.startPreview();
}
```

3、设置输入图像的分辨率

```java
mPiPanoSDK.setPreviewTextureSize(mWidth, mHeight);
```

4、开始预览

```java
mPiPanoSDK.startPreview(PiSourceModeType.PISM_Full21);
```

