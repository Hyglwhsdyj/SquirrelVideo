package com.songshu.squirrelvideo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.VideoTypeBean;
import com.songshu.squirrelvideo.entity.VideoTypeTransformBean;
import com.songshu.squirrelvideo.fragment.BaseFragment;
import com.songshu.squirrelvideo.fragment.RecommondFragment;
import com.songshu.squirrelvideo.fragment.SelectionFragment;
import com.songshu.squirrelvideo.manager.TitleManager;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.Util;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yb on 15-7-6.
 */
public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName() + ":";

    private Context mContext;
    public static boolean isHomeActivityOnResume = false;
    private boolean dosedUserInAppFirstTimeAfterCleaningOldApp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(TAG, "...... HomeActivity --> onCreate");
        mContext = HomeActivity.this;
        setContentView(R.layout.activity_main);

        initData();
        initFragment();
        initFragmentManager();
        initView();
        setView();
        setSubTitleListener();
        syncSubTitleList();

        dosedUserInAppFirstTimeAfterCleaningOldApp = true;
        isHomeActivityOnResume = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        L.d(TAG, "...... HomeActivity --> onNewIntent");
        super.onNewIntent(intent);
        if (dosedUserInAppFirstTimeAfterCleaningOldApp) {
            App.getActivityManager().emptyStackExceptMainAct();
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        L.d(TAG, "...... HomeActivity --> onResume");
        super.onResume();
        MobclickAgent.onResume(this);          //统计时长
        dosedUserInAppFirstTimeAfterCleaningOldApp = false;
        isHomeActivityOnResume = true;
    }

    @Override
    protected void onPause() {
        L.d(TAG, "...... HomeActivity --> onPause");
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private VideoTypeBean mVideoTypeBean;

    private void initData() {
        Intent intent = getIntent();
        mVideoTypeBean = (VideoTypeBean) intent.getSerializableExtra(MainActivity.VIDEO_TYPE_OBJECT);
        L.d(TAG, "initData --> VideoTypeBean : " + mVideoTypeBean);
    }


    private Map<String, Fragment> fragmentMap = new HashMap<String, Fragment>();

    private void initFragment() {
        Fragment recommondFragment = new RecommondFragment();
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("video_type", mVideoTypeBean);
        recommondFragment.setArguments(mBundle);
        fragmentMap.put(getString(R.string.character_recommend), recommondFragment);

        for (VideoTypeTransformBean bean : TitleManager.getSubTitleMap().get(Const.TITLE)) {
            Fragment tempFragment = new SelectionFragment();
            Bundle selection_bundle = new Bundle();
            selection_bundle.putSerializable("video_type", mVideoTypeBean);
            selection_bundle.putString("sub_title", bean.sub_title);
            tempFragment.setArguments(selection_bundle);
            fragmentMap.put(bean.sub_title, tempFragment);
        }
    }

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private void initFragmentManager() {
        fragmentManager = this.getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        for (String sub_title : fragmentMap.keySet()) {
            transaction.add(R.id.rl_for_replace, fragmentMap.get(sub_title));
            transaction.hide(fragmentMap.get(sub_title));
        }
        transaction.show(fragmentMap.get(getString(R.string.character_recommend)));
        transaction.commitAllowingStateLoss();
    }

    private TextView tv_title;
    private LinearLayout ll_sub_title;

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(mVideoTypeBean.mTitle);
        ll_sub_title = (LinearLayout) findViewById(R.id.ll_sub_title);
    }

    private Map<String, TextView> mTextViews = new HashMap<String, TextView>();

    private void setView() {
        ll_sub_title.removeAllViews();

        TextView recommondView = getTextView(getString(R.string.character_recommend));
        mTextViews.put(getString(R.string.character_recommend), recommondView);
        ll_sub_title.addView(recommondView);
        recommondView.setBackgroundResource(R.drawable.left_arraw);
        currentTextView = recommondView;

        for (String sub_title : fragmentMap.keySet()) {
            if (!sub_title.equals(getString(R.string.character_recommend))) {
                TextView item = getTextView(sub_title);
                item.setBackgroundResource(0);
                mTextViews.put(sub_title, item);
                ll_sub_title.addView(item);
            }
        }

        TextView allCategoryView = getTextView(getString(R.string.character_all_category));
        mTextViews.put(getString(R.string.character_all_category), allCategoryView);
        ll_sub_title.addView(allCategoryView);
    }

    /**
     * 获取制定名称的textview对象
     *
     * @param sub_title
     * @return
     */
    private TextView getTextView(String sub_title) {
        TextView item = new TextView(mContext);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 80);
        item.setLayoutParams(lp);
        item.setGravity(Gravity.CENTER);
        item.setTextSize(28);
        item.setTextColor(Color.parseColor("#ffffff"));
        item.setText(sub_title);
        return item;
    }

    private TextView currentTextView;

    private void setSubTitleListener() {
        if (mTextViews.size() > 0) {
            for (String sub_title : mTextViews.keySet()) {
                mTextViews.get(sub_title).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView item = (TextView) v;
                        L.d(TAG, "click : " + item.getText());

                        if (currentTextView.getText().toString().trim().equals(item.getText().toString().trim())) {
                            return;
                        }

                        changeSubTitleBackground(item);

                        if (TitleManager.SUB_TITLE_WHOLE.equals(item.getText().toString().trim())) {
                            Intent mIntent = new Intent(mContext, CategoryActivity.class);
                            mIntent.putExtra("video_type", mVideoTypeBean);
                            startActivity(mIntent);

                            uiHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    uiHandler.sendEmptyMessage(0);
                                }
                            }, 1000);

                        } else {
                            currentTextView = item;
                            switchFragment(item.getText().toString().trim());
                        }

                    }
                });
            }
        }
    }


    /**
     * 更新左侧子标题的状态
     */
    private void syncSubTitleList() {
        for (String sub_title : mTextViews.keySet()) {
            mTextViews.get(sub_title).setVisibility(View.GONE);
            for (String sub_title_str : mVideoTypeBean.mVideoSubTypeList) {
                if (mTextViews.get(sub_title).getText().toString().trim().equals(sub_title_str)) {
                    mTextViews.get(sub_title).setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    /**
     * 处理子列表发生变化的通知
     *
     * @param e
     */
    public void onEventMainThread(AppEvent.SubTitleListOccusChanging e) {
        L.d(TAG, "... onEvent --> SubTitleListOccusChanging ..." + e.getVideoTypeBean());
        mVideoTypeBean = e.getVideoTypeBean();
        syncSubTitleList();
        if (!mVideoTypeBean.mVideoSubTypeList.contains(currentTextView.getText().toString().trim())) {
            L.d(TAG, "..............currentTextView.getText().toString().trim()............." + currentTextView.getText().toString().trim());
            currentTextView = mTextViews.get(getString(R.string.character_recommend));
            changeSubTitleBackground(currentTextView);
        }
        switchFragment(currentTextView.getText().toString().trim());
    }

    /**
     * 更改Fragment对象
     *
     * @param sub_title
     */
    private void switchFragment(String sub_title) {
        L.d(TAG, "..........switchFragment........." + sub_title);
        transaction = fragmentManager.beginTransaction();
        for (String str : fragmentMap.keySet()) {
            if (str.equals(sub_title)) {
                L.d(TAG, "...............show................" + str);
                if (!sub_title.equals(getString(R.string.character_recommend))) {
                    ((BaseFragment) fragmentMap.get(sub_title)).join();
                }
                transaction.show(fragmentMap.get(sub_title));
            } else {
                L.d(TAG, "...............hide................" + str);
                transaction.hide(fragmentMap.get(str));
            }
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 修改被选子标题的背景
     *
     * @param item
     */
    private void changeSubTitleBackground(TextView item) {
        for (String sub_title : mTextViews.keySet()) {
            if (mTextViews.get(sub_title).getText().toString().trim().equals(item.getText().toString().trim())) {
                mTextViews.get(sub_title).setBackgroundResource(R.drawable.left_arraw);
            } else {
                mTextViews.get(sub_title).setBackgroundResource(0);
            }
        }
    }

    public void onEventMainThread(AppEvent.NothingHappen e) {
        L.d(TAG, "... onEvent --> NothingHappen ...");
    }

//    private long startTime;

    @Override
    public void onBackPressed() {
        exitApp();
    }

    /**
     * 退出app
     */
    private void exitApp() {
        App.getMyDownloadManager().pauseAllDownloadingTask();
        Util.exitThisApp(mContext);
//        long stopTime = System.currentTimeMillis();
//        if (stopTime - startTime < 2000) {
//            Util.exitThisApp(mContext);
//        } else {
//            startTime = stopTime;
//            App.showToast("再按一次退出程序");
//        }
    }

    /**
     * 处理点击"全部"子标题后的响应事件-->界面变化
     */
    private final Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            changeSubTitleBackground(currentTextView);
        }
    };
}
