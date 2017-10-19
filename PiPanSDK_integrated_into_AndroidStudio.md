# PiPanSDK deployment

### Import the necessary files

1、Copy the files from the libs directory to the libs directory of the android studio project

![libs](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/libs.png)

The files in the drawings are for reference only and the actual files will vary depending on the previous version.

2、Copy the assets folder to the src / main directory of the android studio project

![assets](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/assets.png)

3、Copy the folder under the jniLibs directory to the src / main / jniLibs directory of the android studio project (if there is no jiniLibs directory, create a new one)

![jniLibs](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/jniLibs.png)

The files in the drawings are for reference only and the actual files will vary depending on the previous version.

### Engineering configuration

Users need to configure the appropriate permissions according to actual needs.

![permission](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/permission.png)

In the need to embed the preview or player in the layout of the text to add a player's parent control, such as LinearLayout, refer to the following figure：

![layout](https://github.com/pisofttech/pipano-sdk-android/blob/master/DocRes/layout.png)

