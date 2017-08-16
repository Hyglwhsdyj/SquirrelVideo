package com.songshu.squirrelvideo.manager;

import android.app.Activity;

import com.songshu.squirrelvideo.utils.L;

import java.util.Stack;

/**
 * Created by yb on 15-7-15.
 */
public class MyActivityManager {

    private static final String TAG = MyActivityManager.class.getSimpleName() + ":";

    private static Stack<Activity> activityStack;
    private static MyActivityManager instance;

    private MyActivityManager() {
    }

    public static MyActivityManager getScreenManager() {
        if (instance == null) {
            instance = new MyActivityManager();
        }
        return instance;
    }

    //将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        L.d(TAG, ".........pushActivity........." + activity);
        activityStack.push(activity);
    }

    /**
     * 清空除了MainActivity以外的act
     */
    public void emptyStackExceptMainAct() {
        while (activityStack != null && !activityStack.empty()) {
            removePopActivity();
        }
    }

    //退出栈定Activity
    public void removePopActivity() {
        //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
        Activity activity = activityStack.pop();
        if (activity != null) {
            L.d(TAG, ".........removePopActivity........." + activity);
            activity.finish();
            activity = null;
        }
    }


    //退出指定Activity
    public void removeActivity(Activity activity) {
        if (activity != null) {
            //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    // 清空栈
    public void emptyStack() {
        if (activityStack != null) {
            activityStack.clear();
        }
    }

    //获得当前栈顶Activity
    public Activity currentActivity() {
        Activity activity = null;
        if (!activityStack.empty())
            activity = activityStack.firstElement();
        return activity;
    }
}
