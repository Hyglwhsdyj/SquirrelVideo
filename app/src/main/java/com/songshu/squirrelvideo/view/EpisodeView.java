package com.songshu.squirrelvideo.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.golshadi.majid.core.enums.TaskStates;
import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.adapter.EpisodeGridViewAdapter;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.db.HistoryDaoImpl;
import com.songshu.squirrelvideo.entity.DBHistoryBean;
import com.songshu.squirrelvideo.entity.PlayTeleplayBean;
import com.songshu.squirrelvideo.entity.TeleplaySourceBean;
import com.songshu.squirrelvideo.player.PlayVideoActivity;
import com.songshu.squirrelvideo.utils.DensityUtil;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.NetworkUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yb on 15-7-10.
 */
public class EpisodeView {
    private static final String TAG = EpisodeView.class.getSimpleName() + ":";

    private Context mContext;
    private Activity mActivity;
    private String mChannel;

    private View mView;
    private MyGridView gv_episode;
    private EpisodeGridViewAdapter mEpisodeGridViewAdapter;
    private MyHorizontalScrollView hsv_episode;
    private RadioGroup rg_episode;


    public EpisodeView(Context ctx, View view, String channel) {
        mContext = ctx;
        mView = view;
        mActivity = ((Activity) mContext);
        mChannel = channel;

        initViewControllers(mView);
        setListener();
        initData();
    }

    private void initViewControllers(View v) {
        gv_episode = (MyGridView) v.findViewById(R.id.gv_episode);
        hsv_episode = (MyHorizontalScrollView) v.findViewById(R.id.hsv_episode);
        rg_episode = (RadioGroup) v.findViewById(R.id.rg_episode);
    }

    private void setListener() {
        gv_episode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mEpisodeGridViewAdapter.setSelectItem(position);
                mEpisodeGridViewAdapter.notifyDataSetChanged();
//                PlayTeleplayBean bean = mPlayTeleplayBeanList.get(position);
                final PlayTeleplayBean bean = mEpisodeGridViewAdapter.getCurrentDataList().get(position);
                final String url = bean.videoUrl;
                L.d(TAG, "播放集数=" + bean.videoNumber + " , 播放地址=" + bean.videoUrl);
                if (NetworkUtil.isNetworkAvailable(mContext)) {
                    if (NetworkUtil.isMobileNetwork(mContext)) {
                        new CustomDialog.Builder(mContext)
                                .setMessage(mContext.getResources().getString(R.string.dialog_custom_message_3g))
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        jumpPlayer(url, bean.videoNumber - 1);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create(R.layout.dlg_custom_ori)
                                .show();
                    } else {
                        jumpPlayer(url, bean.videoNumber - 1);
                    }
                } else {
                    App.showToast(mContext.getString(R.string.please_check_network));
                }
            }
        });
    }

    private List<PlayTeleplayBean> mPlayTeleplayBeanList;
    private int screenWidthHalf;

    private void initData() {
        Display d = mActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        d.getMetrics(dm);
        screenWidthHalf = dm.widthPixels / 2;

        mPlayTeleplayBeanList = new ArrayList<PlayTeleplayBean>();
        mEpisodeGridViewAdapter = new EpisodeGridViewAdapter(mContext, mPlayTeleplayBeanList);
        gv_episode.setAdapter(mEpisodeGridViewAdapter);
    }


    private String mSource;
    private String id;

    public void addTabItemAndData(TeleplaySourceBean sourceBean, String id) {
        this.id = id;
        addTabItemAndData(sourceBean);
    }

    /**
     * 为外部提供的接口
     * 添加选集tab和页面的数据。
     */
    public void addTabItemAndData(TeleplaySourceBean sourceBean) {
        mSource = sourceBean.name;
        mPlayTeleplayBeanList = sourceBean.mPlayTeleplayBeanList;
        L.d(TAG, "集数=" + mPlayTeleplayBeanList.size());
        if (mPlayTeleplayBeanList.size() <= 16) { // 如果少于16集，则隐藏掉分段剧的tab
            hsv_episode.setVisibility(View.GONE);
            dataBind(mPlayTeleplayBeanList);
        } else {
            hsv_episode.setVisibility(View.VISIBLE);
            hsv_episode.smoothScrollTo(0, 0);
            splitData();
            rg_episode.removeAllViews();
            for (int i = 0; i < mTabTitles.size(); i++) {
                RadioButton rbtn = (RadioButton) View.inflate(mContext, R.layout.view_episode_radio_group_btn, null);
                rbtn.setId(i);
                rbtn.setText(mTabTitles.get(i));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (i != 0) {
                    params.leftMargin = DensityUtil.px2dip(mContext, 20);
                }
                rg_episode.addView(rbtn, params);
            }
            setEventListener();
            // 先绑定RadioGroup的事件，后选中RadioButton，会触发他的点击
            ((RadioButton) rg_episode.getChildAt(0)).setChecked(true);
        }
    }

    private void dataBind(List<PlayTeleplayBean> list) {
        mEpisodeGridViewAdapter.updataAdapterData(list);
//        mEpisodeGridViewAdapter.notifyDataSetChanged();
    }

    private List<String> mTabTitles;// 1-16 17-32 ...
    private Map<String, List<PlayTeleplayBean>> dataMap; // <(1-16) , (p1-p16)> ...

    /**
     * 分割剧集
     */
    private void splitData() {

        mTabTitles = new ArrayList<String>();
        dataMap = new HashMap<String, List<PlayTeleplayBean>>();
        // 以16集为单位，分割数据集合(注：可以修改它的大小，来决定每个集合的大小)
        final int avg = 16;
        float size = mPlayTeleplayBeanList.size();
        float segmentSize = size / avg;
        L.d(TAG, "分割集合总数为=" + segmentSize);

        String str = String.valueOf(segmentSize);
        // 小数点位置
        int dotPosition = str.indexOf(".");
        // 截取小数点后面的数字
        String newStr = str.substring(dotPosition + 1, str.length());
        L.d(TAG, "小数点后面的数字为=" + newStr);
        int newSegmentSize = (int) segmentSize;
        // 判断是否除尽，如果没有除尽，就在原有“分割集合总数”上+1
        for (int i = 0; i < newStr.length(); i++) {
            char cc = newStr.charAt(i);
            if (!String.valueOf(cc).equals("0")) {
                newSegmentSize += 1;
                break;
            }
        }
        L.d(TAG, "最终要分割集合总数为=" + newSegmentSize);
        int fromIndex = 0;
        int toIndex = avg;
        for (int i = 0; i < newSegmentSize; i++) {
            if (toIndex > size) {
                toIndex = (int) size;
            }
            List<PlayTeleplayBean> tempList = mPlayTeleplayBeanList.subList(fromIndex, toIndex);
            // 倒序输出
            // Collections.reverse(tempList);
            String tabTitle = (fromIndex + 1) + "-" + toIndex;
            dataMap.put(tabTitle, tempList);
            mTabTitles.add(tabTitle);

            fromIndex += avg;
            toIndex += avg;
        }
    }

    /**
     * 设置控件的响应事件
     */
    private void setEventListener() {
        rg_episode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 实现点击RadioButton时自动滑动到屏幕的中央
                int scrollX = hsv_episode.getScrollX();
                RadioButton rbtn = (RadioButton) mActivity.findViewById(checkedId);
                if (rbtn != null) {
                    int left = rbtn.getLeft();
                    int leftScreen = left - scrollX;
                    hsv_episode.smoothScrollBy((leftScreen - screenWidthHalf), 0);
                }
                // 切换分段的剧集
                dataBind(dataMap.get(mTabTitles.get(checkedId)));
            }
        });

    }

    /**
     * 跳转播放器页面
     */
    private void jumpPlayer(String url, int pos) {
        L.d(TAG, "......jumpPlayer...... history id : " + (id + Const.MYWIFEANDIBIRTH + (pos + 1)));
        App.closeSnappyDb();
        HistoryDaoImpl mHistoryDaoImpl = new HistoryDaoImpl(mContext);
        DBHistoryBean historyBean = mHistoryDaoImpl.findHistory(id + Const.MYWIFEANDIBIRTH + (pos + 1), mChannel);
        boolean isHistory = mHistoryDaoImpl.isCheckExist(id + Const.MYWIFEANDIBIRTH + (pos + 1), mChannel);
        L.d(TAG, "......jumpPlayer......isHistory : " + isHistory);
        Intent intent = new Intent(mContext, PlayVideoActivity.class);
        intent.putExtra("cached", App.getMyDownloadManager().getStatusByHtmlUrl(url) == TaskStates.END);
        intent.putExtra("url", url);
        intent.putExtra("channel", mChannel);
        intent.putExtra("source", mSource);
        intent.putExtra("list", (Serializable) mPlayTeleplayBeanList);
        intent.putExtra("pos", pos);
        intent.putExtra("isHistory", isHistory);
        if (isHistory && historyBean != null) { // 有播放记录
            intent.putExtra("lastPosition", historyBean.getTime());
        }
        ((FragmentActivity) mContext).startActivityForResult(intent, 10);
        L.d(TAG, "startActivityForResult --> PlayVideoActivity --> url : " + url + " , channel : " + mChannel + " , source : " + mSource + " , pos : " + pos);
    }

    /**
     * 返回View实例
     *
     * @return
     */
    public View getView() {
        return mView;
    }
}
