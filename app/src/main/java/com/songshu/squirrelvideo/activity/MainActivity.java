package com.songshu.squirrelvideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.snappydb.SnappydbException;
import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.VideoTypeBean;
import com.songshu.squirrelvideo.manager.TitleManager;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.Util;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName() + ":";

    private static final int VIDEO_TYPE_MOVIE = 0;
    private static final int VIDEO_TYPE_TELEPLAY = 1;
    private static final int VIDEO_TYPE_CHILD = 2;
    private static final int VIDEO_TYPE_OPERA = 3;
    private static final int VIDEO_TYPE_HEALTH = 4;
    private static final int VIDEO_TYPE_LOCAL = 5;

    public static final String VIDEO_TYPE_OBJECT = "video_type_object";

    public static boolean isDiffEntrance = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        L.d(TAG, "...... MainActivity --> onCreate ......");
        super.onCreate(savedInstanceState);
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this)) return;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        L.d(TAG, "...... MainActivity --> onNewIntent ...... intent : " + intent);
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        L.d(TAG, "...... MainActivity --> onResume ......");
        super.onResume();
        MobclickAgent.onPageStart("MainActivity"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
        MobclickAgent.onResume(this);          //统计时长
        getIntentData(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainActivity"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    private void getIntentData(Intent intent) {
        L.d(TAG, "MainActivity --> intent ...... " + intent);
        if (intent.getIntExtra("video_type", 0) != Const.CURRENT_INTENT_VIDEO_TYPE) {
            L.d(TAG, "不同的入口进入 --> 旧 : " + Const.CURRENT_INTENT_VIDEO_TYPE + " , 新 : " + intent.getIntExtra("video_type", 0));
            isDiffEntrance = true;
            Util.queryAllRunningAppActivitys();
            App.getActivityManager().emptyStackExceptMainAct();
            // 因为playvideoactivity在不同的进程中,所以上面的栈里没有它,它在另一个新的变量栈里,所以用广播来处理,eventbus也不能跨进程
            sendBroadcast(new Intent("com.songshu.different.entrance.broadcast.receiver"));
            Const.CURRENT_INTENT_VIDEO_TYPE = intent.getIntExtra("video_type", 0);
        }
        switch (Const.CURRENT_INTENT_VIDEO_TYPE) {
            case VIDEO_TYPE_MOVIE:
                initData(TitleManager.TITLE_MOVIE, TitleManager.SUBTITLE_DEFAULT_MOVIE);
                break;
            case VIDEO_TYPE_TELEPLAY:
                initData(TitleManager.TITLE_TELEPLAY, TitleManager.SUBTITLE_DEFAULT_TELEPLAY);
                break;
            case VIDEO_TYPE_CHILD:
                initData(TitleManager.TITLE_CHILD, TitleManager.SUBTITLE_DEFAULT_CHILD);
                break;
            case VIDEO_TYPE_OPERA:
                initData(TitleManager.TITLE_OPERA, TitleManager.SUBTITLE_DEFAULT_OPERA);
                break;
            case VIDEO_TYPE_HEALTH:
                initData(TitleManager.TITLE_HEALTH, TitleManager.SUBTITLE_DEFAULT_HEALTH);
                break;
            case VIDEO_TYPE_LOCAL:
                initLocalMediaData();
                break;
        }
    }

    private void initLocalMediaData() {
        Const.TITLE = TitleManager.TITLE_LOCAL;
        startActivity(new Intent(this, LocalMediaHomeActivity.class));
    }

    public static Intent homeIntent = null;

    private void initData(String title, String[] subtitle_default) {
        Const.TITLE = title;
        Const.CHANNEL = TitleManager.getEngNameMap().get(Const.TITLE);
        L.d(TAG, "initData --> Const.TITLE : " + Const.TITLE + " , Const.CHANNEL : " + Const.CHANNEL);
        try {
            VideoTypeBean mVideoTypeBean = new VideoTypeBean();
            Intent mIntent = new Intent(MainActivity.this, HomeActivity.class);
            mVideoTypeBean.mTitle = title;

            boolean isExist = App.getSnappyDb().exists(title);
            App.closeSnappyDb();
            if (isExist) {
                L.d(TAG, "有缓存");
                String[] list = App.getSnappyDb().getArray(title, String.class);
                App.closeSnappyDb();
                mVideoTypeBean.mVideoSubTypeList = Arrays.asList(list);

                checkSexExist(title, subtitle_default, mVideoTypeBean);
                checkMagicAndLaughtExist(title, subtitle_default, mVideoTypeBean);

            } else {
                L.d(TAG, "没有缓存");
                mVideoTypeBean.mVideoSubTypeList = Arrays.asList(subtitle_default);
                App.getSnappyDb().put(title, subtitle_default);
                App.closeSnappyDb();
            }
            mIntent.putExtra(VIDEO_TYPE_OBJECT, mVideoTypeBean);
            L.d(TAG, "start activity --> HomeActivity --> VideoTypeBean : " + mVideoTypeBean);
            homeIntent = mIntent;
            startActivity(mIntent);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查健康中是否有两性分类
     *
     * @param title
     * @param subtitle_default
     * @param mVideoTypeBean
     * @throws SnappydbException
     */
    private void checkSexExist(String title, String[] subtitle_default, VideoTypeBean mVideoTypeBean) throws SnappydbException {
        if (title.equals(TitleManager.TITLE_HEALTH)) {
            if (mVideoTypeBean.mVideoSubTypeList.contains(getString(R.string.character_health_liangxing))) {
                L.d(TAG, "存在两性的分类...只能拜拜了...");
                mVideoTypeBean.mVideoSubTypeList = Arrays.asList(subtitle_default);
                App.getSnappyDb().put(title, subtitle_default);
                App.closeSnappyDb();
            }
        }
    }

    /**
     * 检查戏曲里的魔术杂技/评书笑话
     *
     * @param title
     * @param subtitle_default
     * @param mVideoTypeBean
     * @throws SnappydbException
     */
    private void checkMagicAndLaughtExist(String title, String[] subtitle_default, VideoTypeBean mVideoTypeBean) throws SnappydbException {
        if (title.equals(TitleManager.TITLE_OPERA)) {
            if (mVideoTypeBean.mVideoSubTypeList.contains(getString(R.string.character_opera_moshuzaji))
                    || mVideoTypeBean.mVideoSubTypeList.contains(getString(R.string.character_opera_pingshuxiaohua))) {
                L.d(TAG, "存在魔术杂技/评书笑话的分类...只能拜拜了...");
                mVideoTypeBean.mVideoSubTypeList = Arrays.asList(subtitle_default);
                App.getSnappyDb().put(title, subtitle_default);
                App.closeSnappyDb();
            }
        }
    }


    /**
     * 空方法
     *
     * @param e
     */
    public void onEventMainThread(AppEvent.NothingHappen e) {
        L.d(TAG, "... onEvent --> NothingHappen ...");
    }
}
