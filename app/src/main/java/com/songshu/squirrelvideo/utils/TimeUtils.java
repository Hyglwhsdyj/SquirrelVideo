package com.songshu.squirrelvideo.utils;

import android.content.Context;

import com.songshu.squirrelvideo.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TimeUtils
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT_NOMILL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT_PHOTOCOMMENT = new SimpleDateFormat("MM.dd  HH:mm");
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT_FILE = new SimpleDateFormat("yyyy_MM_dd_HHmmss_SSS");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_MY_RECORD = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    public static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L;

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }


    public static String getLocalTime(Context context, long time) {
        String timeStr = null;
        long diff = new Date().getTime() / 1000 - time;
        if (diff < 1) {
            diff = 1;
        }
        if (diff < 60) {
            timeStr = diff + context.getString(R.string.time_second);
        } else if (diff >= 60 && diff < 3600) {
            timeStr = diff / 60 + context.getString(R.string.time_minute);
        } else if (diff >= 3600 && diff < (3600 * 24)) {
            timeStr = diff / 3600 + context.getString(R.string.time_hour);
        } else {
            timeStr = (diff / (3600 * 24)) + context.getString(R.string.time_day);
        }
        return timeStr;
    }

    public static String getLocal2Time(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        if (h == 0) {
            return d + ":" + s;
        } else {
            return h + ":" + d + ":" + s;
        }
    }

    public static String generateTime2(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02d时%02d分%02d秒", hours, minutes,
                seconds) : String.format("%02d分%02d秒", minutes, seconds);
    }
}
