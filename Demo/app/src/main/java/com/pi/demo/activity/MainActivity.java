package com.pi.demo.activity;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.pi.demo.R;
import com.pi.demo.common.ActionBarDrawerToggleDiy;
import com.pi.demo.common.CameraUtils;
import com.pi.demo.common.GlobalParam;
import com.pi.pipanosdk.PiPanoSDK;
import com.pi.pipanosdk.common.PiSourceModeType;
import com.pi.pipanosdk.common.PiViewModeType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.pi.demo.common.GlobalParam.RunMode_E.PICTURE;
import static com.pi.demo.common.GlobalParam.RunMode_E.PREVIEW;
import static com.pi.demo.common.GlobalParam.RunMode_E.VIDEO;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PiPanoSDK.OnPreviewIsReadyListener
{
    private final String TAG = MainActivity.class.getSimpleName();

    private final int CAMERA_FRONT = 0; // 前置摄像头
    private final int CAMERA_BACK = 1;  // 后置摄像头
    private int mWidth = 1088;
    private int mHeight = 1088;
    private Camera mCamera = null;
    private boolean mbConnected = false;

    private PiPanoSDK mPiPanoSDK = null;
    private FrameLayout mLayout = null;
    private View mSDKView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // 隐藏标题
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggleDiy toggle = new ActionBarDrawerToggleDiy(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close,
                new ActionBarDrawerToggleDiy.ActionBarDrawerToggleDiyListener()
                {
                    @Override
                    public void refreshUI()
                    {
                        mSDKView.requestLayout();
                        return;
                    }
                });
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try
        {
            copyMediaToSDCard();    // 第一次启动时，将演示资源拷贝到SDCard中
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            initSDK();  // 初始化SDK
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        initUI();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (null == mPiPanoSDK)
        {
            return super.onOptionsItemSelected(item);
        }

        int id = item.getItemId();
        switch (id)
        {// 切换展开模式
            case R.id.action_view_mode1:
                mPiPanoSDK.setViewMode(PiViewModeType.PIVM_FishEye);    // 鱼眼
                GlobalParam.mViewModetype = PiViewModeType.PIVM_FishEye;
                break;

            case R.id.action_view_mode2:
                mPiPanoSDK.setViewMode(PiViewModeType.PIVM_Asteroid);    // 小行星
                GlobalParam.mViewModetype = PiViewModeType.PIVM_Asteroid;
                break;

            case R.id.action_view_mode3:
                mPiPanoSDK.setViewMode(PiViewModeType.PIVM_Immerse);    // 沉浸式
                GlobalParam.mViewModetype = PiViewModeType.PIVM_Immerse;
                break;

            case R.id.action_view_mode4:
                mPiPanoSDK.setViewMode(PiViewModeType.PIVM_VR);    // VR
                GlobalParam.mViewModetype = PiViewModeType.PIVM_VR;
                break;

            default:
                mPiPanoSDK.setViewMode(PiViewModeType.PIVM_Immerse);    // 沉浸式
                GlobalParam.mViewModetype = PiViewModeType.PIVM_Immerse;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera)
        {// 预览
            if (GlobalParam.mRunMode.equals(VIDEO))
            {
                mPiPanoSDK.stop();
            }
            setUI(PREVIEW);
            if (!mbConnected)
            {
                startVideoStream();
            }
        }
        else if (id == R.id.nav_gallery)
        {// 图片
            if (mbConnected)
            {
                stopVideoStream();
                mbConnected = false;
            }
            if (GlobalParam.mRunMode.equals(VIDEO))
            {
                mPiPanoSDK.stop();
            }
            setUI(PICTURE);
            openPicture(GlobalParam.mInputIamgeSrcType);
        }
        else if (id == R.id.nav_slideshow)
        {// 视频
            if (mbConnected)
            {
                stopVideoStream();
                mbConnected = false;
            }
            if (GlobalParam.mRunMode.equals(VIDEO))
            {
                mPiPanoSDK.stop();
            }
            setUI(VIDEO);
            openVideo(GlobalParam.mInputIamgeSrcType);
        }
        else if (id == R.id.nav_input_oneeye)
        {// 切换到单目源
            GlobalParam.mInputIamgeSrcType = PiSourceModeType.PISM_OneEye;
            onSourceModeTypeChanged();
        }
        else if (id == R.id.nav_input_full21)
        {// 切换到全景2:1源
            GlobalParam.mInputIamgeSrcType = PiSourceModeType.PISM_Full21;
            onSourceModeTypeChanged();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause");
        super.onPause();
        if (null != mPiPanoSDK)
        {
            mPiPanoSDK.onPause();
        }
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume");
        super.onResume();

        if (null != mPiPanoSDK)
        {
            mPiPanoSDK.onResume();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        Log.d(TAG, "onConfigurationChanged");
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
        Log.d(TAG, "onWindowFocusChanged(): hasFocus = " + hasFocus);
        if (null != mPiPanoSDK)
        {
            mPiPanoSDK.onWindowFocusChanged(hasFocus);
        }
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if (null != mPiPanoSDK)
        {
            mPiPanoSDK.onDestroy();
            mPiPanoSDK = null;
        }
    }

    // 预览准备就绪
    @Override
    public void onPreviewIsReady()
    {
        Log.d(TAG, "Preview is ok!!!");

        openCamera(CAMERA_FRONT);

        // 获取SDK的SurfaceTexture
        //Surface sfc = null;
        SurfaceTexture texture = null;
        try
        {
            texture = mPiPanoSDK.getPreviewSurfaceTexture();
            Log.d(TAG, "mPiPanoSDK.getSurfaceTexture() = " + texture);
            //sfc = new Surface(sfct);
            try
            {

                mCamera.setPreviewTexture(texture);
            } catch (IOException ioe)
            {
                throw new RuntimeException(ioe);
            }
            mCamera.startPreview();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        mbConnected = true;
    }

    private void initSDK() throws Exception
    {
        // 创建SDK实例
        mPiPanoSDK = new PiPanoSDK(this, new PiPanoSDK.OnSDKIsReadyListener()
        {
            @Override
            public void onSDKIsReady()
            {
                Log.d(TAG, "SDK is ok!!!");

                openPicture(GlobalParam.mInputIamgeSrcType);  // 打开图片
            }
        });

        // 将SDK的Veiw与UI关联
        mLayout = (FrameLayout) findViewById(R.id.sdk_view);
        mSDKView = mPiPanoSDK.getPlayerView();
        mLayout.addView(mSDKView);

        mPiPanoSDK.setScreenOrientation(5); // 设置跟随设备翻转
    }

    private void initUI()
    {
        // 播放
        Button playVideoButton = (Button)findViewById(R.id.btn_resume);
        playVideoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mPiPanoSDK.resume();
            }
        });

        // 暂停
        Button pauseVideoButton = (Button)findViewById(R.id.btn_pause);
        pauseVideoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mPiPanoSDK.pause();
            }
        });

        // 停止播放
        Button stopVideoButton = (Button)findViewById(R.id.btn_stop);
        stopVideoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mPiPanoSDK.stop();
            }
        });

        // 快进
        Button seekVideoButton = (Button)findViewById(R.id.btn_seek);
        seekVideoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                double seekOffset = mPiPanoSDK.getVideoProgress();  // 获取当前进度
                Log.d(TAG, "mSeekOffset = " + seekOffset);
                seekOffset += 0.1;
                if (seekOffset > 1.0)
                {
                    seekOffset = 0.0;
                }
                mPiPanoSDK.seek(seekOffset);    // 指定跳转进度(实际只能跳转到与指定进度最近的关键帧)
            }
        });

        setUI(PICTURE);
    }

    private void setUI(GlobalParam.RunMode_E runMode )
    {

        LinearLayout parentLayout = (LinearLayout)findViewById(R.id.main_content);
        LinearLayout.LayoutParams sdkLayoutParams = (LinearLayout.LayoutParams) mLayout.getLayoutParams();

        LinearLayout videocontrol = (LinearLayout) findViewById(R.id.video_control);

        switch (runMode)
        {
            case PICTURE:
            case PREVIEW:
                // 将sdk view放大
                sdkLayoutParams.width = parentLayout.getLayoutParams().width;
                sdkLayoutParams.height = parentLayout.getLayoutParams().height;
                mLayout.setLayoutParams(sdkLayoutParams);

                // 隐藏播放控制按钮
                videocontrol.setVisibility(View.GONE);

                break;

            case VIDEO:
                // 恢复sdk view为原来的大小
                sdkLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                sdkLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                // 显示播放控制按钮
                videocontrol.setVisibility(View.VISIBLE);
                break;
        }

        GlobalParam.mRunMode = runMode;
    }

    // 打开图片
    private void openPicture(String type)
    {
        // 根据输入图像源的类型来加载对应的图片
        String path = "/mnt/sdcard/sdk_photo_full21.jpg";
        if (type.equals(PiSourceModeType.PISM_OneEye))
        {
            path = "/mnt/sdcard/sdk_photo_oneeye.jpg";
        }
        mPiPanoSDK.openPhoto(path, type);
        mPiPanoSDK.setViewMode(GlobalParam.mViewModetype);    // 设置展开模式
    }

    // 打开视频文件
    private void openVideo(String type)
    {
        mPiPanoSDK.stop();

        // 根据输入图像源的类型来加载对应的图片
        String path = "/mnt/sdcard/sdk_video_full21.mp4";
        if (type.equals(PiSourceModeType.PISM_OneEye))
        {
            path = "/mnt/sdcard/sdk_video_oneeye.mp4";
        }
        mPiPanoSDK.openVideo(path, type);   // 播放视频文件
        mPiPanoSDK.setViewMode(GlobalParam.mViewModetype);    // 设置展开模式
    }

    // 切换输入图像源
    private void onSourceModeTypeChanged()
    {
        switch (GlobalParam.mRunMode)
        {
            case PICTURE:
                openPicture(GlobalParam.mInputIamgeSrcType);
                break;

            case PREVIEW:
                if (mbConnected)
                {
                    stopVideoStream();
                    mbConnected = false;
                }
                startVideoStream();
                break;

            case VIDEO:
                mPiPanoSDK.stop();
                openVideo(GlobalParam.mInputIamgeSrcType);
                break;
        }
    }

    // 打开手机摄像头
    private void openCamera(int cameraIndex)
    {
        Log.d(TAG, "openCamera(): cameraIndex = " + cameraIndex);
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++)
        {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && cameraIndex == CAMERA_FRONT)
            {
                mCamera = Camera.open(i);
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK && cameraIndex == CAMERA_BACK)
            {
                mCamera = Camera.open(i);
            }
        }
        if (null == mCamera)
        {
            throw new RuntimeException("Unable to open camera");
        }

        Camera.Parameters parms = mCamera.getParameters();
        CameraUtils.choosePreviewSize(parms, mWidth, mHeight);
        // Try to set the frame rate to a constant value.
        int mCameraPreviewThousandFps = CameraUtils.chooseFixedPreviewFps(parms, 30 * 1000);
        // Give the camera a hint that we're recording video.  This can have a big
        // impact on frame rate.
        parms.setRecordingHint(true);
        mCamera.setParameters(parms);
        Camera.Size cameraPreviewSize = parms.getPreviewSize();
        String previewFacts = cameraPreviewSize.width + "x" + cameraPreviewSize.height + " @" + (mCameraPreviewThousandFps / 1000.0f) + "fps";
        Log.i(TAG, "Camera config: " + previewFacts);
    }

    private void startVideoStream()
    {
        Log.d(TAG, "startVideoStream()");

        mPiPanoSDK.setPreviewIsReadyListener(this); // 设置监听预览是否准备就绪
        mPiPanoSDK.setPreviewTextureSize(mWidth, mHeight);  // 设置预览分辨率
        mPiPanoSDK.startPreview(GlobalParam.mInputIamgeSrcType);    // 开始预览
        mPiPanoSDK.setViewMode(GlobalParam.mViewModetype);    // 设置显示模式
    }

    private void stopVideoStream()
    {
        if (mbConnected)
        {
            mPiPanoSDK.stopPreview();
        }

        if (null != mCamera)
        {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void copyMediaToSDCard() throws IOException
    {
        // 判断是否为第一次启动
        SharedPreferences sp = getSharedPreferences("set", MODE_PRIVATE);
        boolean isFirstStartup = sp.getBoolean("isFirstStartup", true);
        if (!isFirstStartup)
        {
            Log.d(TAG, "isFirstStartup = false");
            return;
        }

        AssetManager assetManager = getAssets();
        String[] fileNames = {"sdk_photo_full21.jpg", "sdk_photo_oneeye.jpg", "sdk_video_full21.mp4", "sdk_video_oneeye.mp4"};
        for (String fileName : fileNames)
        {
            //InputStream is = getApplicationContext().getClass().getClassLoader().getResourceAsStream("assets/" + fileName);
            InputStream is = assetManager.open(fileName);
            FileOutputStream fos = new FileOutputStream(new File("/sdcard/" + fileName));
            Log.d(TAG, "copy: " + fileName);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) > 0)
            {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            is.close();
            is = null;
            fos.close();
            fos = null;
            Log.d(TAG, "copy: " + fileName + " over");
        }

        // 记录已经启动过
        SharedPreferences.Editor et = sp.edit();
        et.putBoolean("isFirstStartup", false);
        et.commit();

        Log.d(TAG, "copyMediaToSDCard() over ");
    }
}
