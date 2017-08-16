package com.songshu.squirrelvideo.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build.VERSION;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.NetStatus;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.RetrofitError;

/**
 * @author Administrator
 *         检查网络工具类
 */
public class NetworkUtil {

    private static final String TAG = NetworkUtil.class.getSimpleName() + ":";

    /**
     * 判断网络连接是否可用
     */
    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 检查网络类型
     */
    public static int checkNetworkType(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return Const.TYPE_NO_NETWORK;
        }
        State mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
            return Const.TYPE_MOBBLE_NETWORK;
        }
        if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
            return Const.TYPE_WIFI_NETWORK;
        }
        return Const.TYPE_NO_NETWORK;
    }

    /**
     * 手机网络
     */
    public static boolean isMobileNetwork(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    /**
     * wifi网络
     */
    public static boolean isWifiNetwork(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 跳转去网络设置页面
     */
    public static void startNetworkSettingActivity(Context mContext) {
        Intent intent = new Intent();
        int sdkVersion = VERSION.SDK_INT;
        if (sdkVersion >= 14) {
            intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        } else if (sdkVersion > 10 && sdkVersion < 14) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else if (sdkVersion < 10) {
            // android4.0系统找不到此Activity
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        mContext.startActivity(intent);
    }


    public static NetStatus getNetStatus(SpiceException e) {
        Throwable t = e.getCause();
        L.d(TAG, "getNetStatus:t:" + t);
        NetStatus status = new NetStatus(-1, e.toString());
        if (e instanceof NoNetworkException) {
            status = new NetStatus(10, App.getContext().getString(R.string.code_10));
        } else if (t != null) {
            if (t instanceof RetrofitError) {
                status = getNetStatus((RetrofitError) t);
            } else {
                status = getNetStatus(t);
            }
        }
        return status;
    }

    public static NetStatus getNetStatus(RetrofitError e) {
        Throwable t = e.getCause();
        NetStatus status = new NetStatus(-1, e.toString());
        L.d(TAG, "getNetStatus:RetrofitError:" + e);
        if (e.getResponse() != null) {
            L.d(TAG, "failure:resp:reason:" + e.getResponse().getReason());
            L.d(TAG, "failure:resp:body:" + e.getResponse().getBody());
            if (e.getResponse().getBody() != null) {
                L.d(TAG, "failure:body:string:" + new String(e.getResponse().getBody().toString()));
                L.d(TAG, "failure:body:mimeType:" + new String(e.getResponse().getBody().mimeType()));
                L.d(TAG, "failure:body:length:" + e.getResponse().getBody().length());
            }
            try {
                status = inStream2NetStatus(e);
            } catch (Exception ex) {
                ex.printStackTrace();
                L.d(TAG, "Exception:" + ex);
            }
        } else if (t != null) {
            status = getNetStatus(t);
        }
        return status;
    }

    private static NetStatus inStream2NetStatus(RetrofitError retrofitError) {

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(retrofitError.getResponse().getBody().in()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStatus2NetStatus(buffer.toString(), retrofitError.toString());
    }

    private static NetStatus jsonStatus2NetStatus(String jsonStatus, String error_reason) {
        L.e(TAG, "jsonStatus2NetStatus:----body:" + jsonStatus);
        NetStatus status = new NetStatus(-1, error_reason);
        if (!TextUtils.isEmpty(jsonStatus)) {
            try {
                status = new Gson().fromJson(jsonStatus, NetStatus.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    private static NetStatus getNetStatus(Throwable t) {
        NetStatus status = new NetStatus(-1, t.toString());
        if (t instanceof java.net.ConnectException) {
            status = new NetStatus(10, App.getContext().getString(R.string.code_10));
        } else if (t instanceof java.net.UnknownHostException) {
            status = new NetStatus(10, App.getContext().getString(R.string.code_10));
        } else if (t instanceof java.net.SocketTimeoutException) {
            status = new NetStatus(10, App.getContext().getString(R.string.code_10));
        } else if (t instanceof java.net.SocketException) {
            status = new NetStatus(10, App.getContext().getString(R.string.code_10));
        } else if (t instanceof NoNetworkException) {
            status = new NetStatus(10, App.getContext().getString(R.string.code_10));
        } else if (t instanceof EOFException) {
            status = new NetStatus(10, App.getContext().getString(R.string.code_10));
        }
        return status;
    }


    private static final String BYTES = "B/s";
    private static final String KILOBYTES = "KB/s";
    private static final String MEGABYTES = "MB/s";
    private static final String GIGABYTES = "GB/s";
    private static final long KILO = 1024;
    private static final long MEGA = KILO * 1024;
    private static final long GIGA = MEGA * 1024;

    // 格式化数据
    public static String forNetSize(final long bytes) {
        if (bytes < KILO) {
            return bytes + BYTES;
        } else if (bytes < MEGA) {
            return (int) (0.5 + (bytes / (double) KILO)) + KILOBYTES;
        } else if (bytes < GIGA) {
            return (int) (0.5 + (bytes / (double) MEGA)) + MEGABYTES;
        } else {
            return (int) (0.5 + (bytes / (double) GIGA)) + GIGABYTES;
        }
    }

}
