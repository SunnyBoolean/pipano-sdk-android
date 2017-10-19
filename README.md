# Brief

PiPanoSDK is a set of software development kits designed to process panoramic images with the following features:

1, Browse pictures, play video and preview video stream;

2, Support source mode includeing oneeye, twoeye and full21;

3, Support various view modes: immerse, fisheye, asteroid, cylinder, VR, twoscroll, etc .;

4, Support  various filter effects: bleach, charcoal, contours, blueberry, pixelated, etc .;

5, Support a set of transition animations: flip, fade, gate, circle, fold , etc .;





![沉浸](http://fortylin-image.oss-cn-shenzhen.aliyuncs.com/doc/2017-10-13-%E6%B2%89%E6%B5%B8.gif)![小行星](http://fortylin-image.oss-cn-shenzhen.aliyuncs.com/doc/2017-10-13-%E5%B0%8F%E8%A1%8C%E6%98%9F.gif)

![综合](http://fortylin-image.oss-cn-shenzhen.aliyuncs.com/doc/2017-10-13-%E7%BB%BC%E5%90%88.gif)![坠入](http://fortylin-image.oss-cn-shenzhen.aliyuncs.com/doc/2017-10-13-%E5%9D%A0%E5%85%A5.gif)


# Example

![Scan QR code to download app](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/Scan QR code to download app.png)

Download link ：[Google Play](https://play.google.com/store/apps/details?id=com.pi.testing.sdktesting)


# integrate

[PiPanSDK集成到AndroidStudio](https://github.com/pisofttech/pipano-sdk-android/blob/master/PiPanSDK集成到AndroidStudio.md)



PiPanSDK integrated into AndroidStudio

# Basic usage

### initialization

1、Create an SDK object in the activity onCreate ()

```java
 mPiPanoSDK = new PiPanoSDK(this, this);
```

2、Associate the SDK instance with the UI

```java
// Associate the SDK's Veiw with the UI
mLayout = (FrameLayout) findViewById(R.id.sdk_view);
mSDKView = mPiPanoSDK.getPlayerView();  // Get the view of the SDK
mLayout.addView(mSDKView);
```

3、Overload related response function

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

4、Implement the SDK to initialize the completion of the callback interface

```java
public void onSDKIsReady()
{
    Log.d(TAG, "SDK is ok");
}

```

### Load the photo

1、Call openPhoto () to open the local photo and display it

```java
mPiPanoSDK.openPhoto(path, type);	// path is the photo path, type is the image source type (monocular or panorama 2: 1)
```

2、After the photo is loaded successfully, you can switch the expand mode with setViewMode ()

```java
mPiPanoSDK.setViewMode(PiViewModeType.PIVM_Immerse);    // Set the expansion mode
```

### Play video

1、Call openVideo () to open the local mp4 file and play it

```java
mPiPanoSDK.openVideo(path, type);   // path is the MP4 file path, type is the image source type (monocular or panorama 2: 1)
```

2、During video playback, you can call setViewMode () to switch the expansion mode

```java
mPiPanoSDK.setViewMode(PiViewModeType.PIVM_Immerse);    // Set the expansion mode
```

3、Video playback control

```java
mPiPanoSDK.pause();	// Pause playback
mPiPanoSDK.resume();	// Continue to play
mPiPanoSDK.seek(0.3);    // Specify the progress of jumping to 30% of the video (actually only to the specified progress of the recent key frame)
mPiPanoSDK.stop();	// Stop play
```

### Preview video stream

1、Set whether the monitor preview is ready

```java
mPiPanoSDK.setPreviewIsReadyListener(this);
```

2、Implement the preview callback interface ready

```java
@Override
public void onPreviewIsReady()
{
  Log.d(TAG, "Preview is ok!!!");

  openCamera(CAMERA_FRONT);	// Turn on the camera lens

  // 获取SDK的SurfaceTexture
  SurfaceTexture texture = null;
  texture = mPiPanoSDK.getPreviewSurfaceTexture();
  
  mCamera.setPreviewTexture(texture);
  mCamera.startPreview();
}
```

3、Set the resolution of the input image

```java
mPiPanoSDK.setPreviewTextureSize(mWidth, mHeight);
```

4、Start previewing

```java
mPiPanoSDK.startPreview(PiSourceModeType.PISM_Full21);
```

