package com.songshu.squirrelvideo.manager;

import android.content.Context;
import android.view.View;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.entity.NetStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yb on 15-7-4.
 */
public class MessageManager {

    {
        messageMap.put(10, App.getContext().getString(R.string.code_10));
        messageMap.put(401, App.getContext().getString(R.string.code_401));
        messageMap.put(429, App.getContext().getString(R.string.code_429));
        messageMap.put(500, App.getContext().getString(R.string.code_500));
        messageMap.put(702, App.getContext().getString(R.string.code_702));
        messageMap.put(704, App.getContext().getString(R.string.code_704));
        messageMap.put(705, App.getContext().getString(R.string.code_705));
        messageMap.put(708, App.getContext().getString(R.string.code_708));
        messageMap.put(712, App.getContext().getString(R.string.code_712));
        messageMap.put(713, App.getContext().getString(R.string.code_713));
        messageMap.put(801, App.getContext().getString(R.string.code_801));
        messageMap.put(1002, App.getContext().getString(R.string.code_1002));
        messageMap.put(1003, App.getContext().getString(R.string.code_1003));
        messageMap.put(1004, App.getContext().getString(R.string.code_1004));
        messageMap.put(1005, App.getContext().getString(R.string.code_1005));
        messageMap.put(1006, App.getContext().getString(R.string.code_1006));
        messageMap.put(1007, App.getContext().getString(R.string.code_1007));
        messageMap.put(1302, App.getContext().getString(R.string.code_1302));
    }

    private static Map<Integer, String> messageMap = new HashMap<Integer, String>();

    public static String getMessage(int code) {
        String msg = String.valueOf(code);
        if (messageMap.containsKey(code)) {
            msg = messageMap.get(code);
        }
        return msg;
    }

    private static boolean isHandledStatus(int status_code) {
        return messageMap.containsKey(status_code);
    }

    public static void showUIMessage(NetStatus status) {
        if (MessageManager.isHandledStatus(status.status_code)) {
            App.showToast(MessageManager.getMessage(status.status_code));
        } else {
            App.showToast(R.string.code_unknow);
        }
    }
}
