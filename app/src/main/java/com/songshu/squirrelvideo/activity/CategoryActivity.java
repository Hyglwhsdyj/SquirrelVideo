package com.songshu.squirrelvideo.activity;

/**
 * Created by yb on 15-7-8.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.snappydb.SnappydbException;
import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.adapter.CategroyAdapter;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.entity.CategoryBean;
import com.songshu.squirrelvideo.entity.VideoTypeBean;
import com.songshu.squirrelvideo.entity.VideoTypeTransformBean;
import com.songshu.squirrelvideo.manager.TitleManager;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.Util;
import com.songshu.squirrelvideo.view.swipe.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by yb on 15-7-7.
 */
public class CategoryActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final String TAG = CategoryActivity.class.getSimpleName() + ":";

    private Context mContext;
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setAdapter();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.checkAndFinishOtherAct(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mContext = this;
        initParams();
        initData();
        initView();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CategoryActivity"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
        MobclickAgent.onResume(this);          //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CategoryActivity"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    private VideoTypeBean videoTypeBean;

    private void initParams() {
        Intent mIntent = getIntent();
        videoTypeBean = (VideoTypeBean) mIntent.getSerializableExtra("video_type");
        L.d(TAG, "initParam --> VideoTypeBean : " + videoTypeBean);
    }

    private List<CategoryBean> categoryBeanList;

    private void initData() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                List<CategoryBean> tempCategoryBeanList = new LinkedList<CategoryBean>();
                for (VideoTypeTransformBean bean : TitleManager.getSubTitleMap().get(videoTypeBean.mTitle)) {
                    CategoryBean categoryBean = new CategoryBean();
                    categoryBean.title = videoTypeBean.mTitle;
                    categoryBean.channel = TitleManager.getEngNameMap().get(videoTypeBean.mTitle);
                    categoryBean.poster_url = bean.poster_url;
                    categoryBean.sub_title = bean.sub_title;
                    categoryBean.is_selected = false;
                    for (String str : videoTypeBean.mVideoSubTypeList) {
                        if ((!TitleManager.SUB_TITLE_RECOMMOND.equals(str)) && (!TitleManager.SUB_TITLE_WHOLE.equals(str))) {
                            if (str.equals(categoryBean.sub_title)) {
                                categoryBean.is_selected = true;
                            }
                        }
                    }
                    tempCategoryBeanList.add(categoryBean);
                }
                categoryBeanList = tempCategoryBeanList;
                L.d(TAG, "initData --> CategoryBeanList : " + categoryBeanList);
                uiHandler.sendEmptyMessage(0);
            }
        });
    }

    private ImageView iv_go_back;
    private ListView lv_category;

    private void initView() {
        iv_go_back = (ImageView) findViewById(R.id.iv_go_back);
        lv_category = (ListView) findViewById(R.id.lv_category);
        lv_category.setVerticalScrollBarEnabled(true);
    }

    private void setListener() {
        iv_go_back.setOnClickListener(this);
    }

    private CategroyAdapter mCategroyAdapter;

    private void setAdapter() {
        mCategroyAdapter = new CategroyAdapter(this, categoryBeanList);
        lv_category.setAdapter(mCategroyAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_go_back:
                finish();
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 点击了添加或者已添加后的回调,为了及时存储数据
     * 组装数据,返回到首页选择页
     */
    public void itemCheckOrUncheckCallBack() {
        LinkedList<String> lists = new LinkedList<String>();
        lists.add(TitleManager.SUB_TITLE_RECOMMOND);
        for (CategoryBean bean : categoryBeanList) {
            if (bean.is_selected) {
                lists.add(bean.sub_title);
            }
        }
        lists.add(TitleManager.SUB_TITLE_WHOLE);
        try {
            App.getSnappyDb().put(Const.TITLE, lists.toArray(new String[lists.size()]));
            App.closeSnappyDb();
        } catch (SnappydbException e) {
            L.d(TAG, ".........................updateSubTitleList..................ERROR...........................");
            e.printStackTrace();
            App.closeSnappyDb();
        }

        VideoTypeBean newVideoTypeBean = new VideoTypeBean();
        newVideoTypeBean.mTitle = Const.TITLE;
        newVideoTypeBean.mVideoSubTypeList = lists;
        EventBus.getDefault().post(new AppEvent.SubTitleListOccusChanging(newVideoTypeBean));
    }


    public void onEventMainThread(AppEvent.NothingHappen e) {
        L.d(TAG, "... onEvent --> NothingHappen ...");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d(TAG, "onActivityResult --> requestCode : " + requestCode + " , resultCode : " + resultCode);
        if (requestCode == 100) {
            if (resultCode == 0) {
                CategoryBean bean = (CategoryBean) data.getSerializableExtra("category_bean");
                for (CategoryBean b : categoryBeanList) {
                    if (b.sub_title.equals(bean.sub_title)) {
                        if (b.is_selected == bean.is_selected) {
                            break;
                        } else {
                            b.is_selected = bean.is_selected;
                            mCategroyAdapter.notifyData(categoryBeanList);// TODO 优化只更新一个位置
                            itemCheckOrUncheckCallBack();
                        }
                        break;
                    }
                }

            }
        }
    }
}
