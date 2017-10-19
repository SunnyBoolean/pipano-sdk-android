# PiPanSDK部署

### 导入必要文件

1、将libs目录下的文件复制到android studio工程的libs目录中

![libs](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/libs.png)

图中文件仅供参考，实际文件会根据使用前版本有所差异。

2、将assets文件夹复制到android studio工程的src/main目录中

![assets](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/assets.png)

3、将jniLibs目录下的文件夹复制到android studio工程的src/main/jniLibs目录(如果没有jiniLibs目录，则新建一个)

![jniLibs](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/jniLibs.png)

图中文件仅供参考，实际文件会根据使用前版本有所差异。

### 工程配置

用户须要根据实际需求，配置相应的权限。

![permission](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/permission.png)

在需要嵌入预览或播放器的layout文中加入一个播放器的父控件，如LinearLayout，参考下图：

![layout](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/layout.png)

