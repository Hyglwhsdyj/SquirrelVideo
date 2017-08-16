package com.songshu.squirrelvideo.utils;

import android.content.Context;

import com.songshu.squirrelvideo.application.App;

/**
 * Created by yb on 15-11-5.
 */
public class SSUtil {

    private static final String TAG = SSUtil.class.getSimpleName() + ":";

    /**
     * 获取松鼠号
     *
     * @return
     */
    public static String getSSCodeNum() {
        return SystemPropertiesProxy.get(App.getContext(), "persist.radio.squirrelno");
    }

    /**
     * VERSION : DEV or RELEASE
     * @return
     */
    public static String getDebugMode() {
        return SystemPropertiesProxy.getBoolean(App.getContext(), "persist.songshu.devmode", false) ? "VERSION : DEV" : "VERSION : RELEASE";
    }

    /**
     * 获取版本号
     * @return
     */
    public static String getSSProductVersion() {
        return SystemPropertiesProxy.get(App.getContext(), "ro.custom.build.version");
    }
}
