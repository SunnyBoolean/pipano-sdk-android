package com.pi.demo.common;

import com.pi.pipanosdk.common.PiSourceModeType;
import com.pi.pipanosdk.common.PiViewModeType;

/**
 * Created by demon on 2017/10/4.
 */

public class GlobalParam
{
    public enum RunMode_E
    {
        PREVIEW, VIDEO, PICTURE
    }
    public static RunMode_E mRunMode = RunMode_E.PICTURE;
    public static String mInputIamgeSrcType = PiSourceModeType.PISM_Full21;
    public static String mViewModetype = PiViewModeType.PIVM_Immerse;

}
