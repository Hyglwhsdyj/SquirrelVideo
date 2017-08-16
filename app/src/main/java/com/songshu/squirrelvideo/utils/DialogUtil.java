package com.songshu.squirrelvideo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

/**
 * 对话框工具类
 */
public class DialogUtil {
    /**
     * 显示对话框
     *
     * @param context
     * @param title
     * @param message
     * @param okMsg
     * @param cancelMsg
     * @param onOkListener
     * @param onCancelListener
     */
    public static void showConformDialog(Context context, String title,
                                         String message, String okMsg, String cancelMsg, String neutralMsg,
                                         OnClickListener onOkListener, OnClickListener onCancelListener,
                                         OnClickListener onNeutralListener) {

        if (context.isRestricted()) {
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle(title).setMessage(message)
                .setPositiveButton(okMsg, onOkListener)
                .setNegativeButton(cancelMsg, onCancelListener)
                .setNeutralButton(neutralMsg, onNeutralListener).create()
                .show();
    }

    public static void showConformDialog(Context context, String title,
                                         String message, String okMsg, String cancelMsg,
                                         OnClickListener onOkListener, OnClickListener onCancelListener) {

        if (context.isRestricted()) {
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle(title).setMessage(message)
                .setPositiveButton(okMsg, onOkListener)
                .setNegativeButton(cancelMsg, onCancelListener).create().show();
    }
}
