package com.songshu.squirrelvideo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.common.AppEvent;
import com.songshu.squirrelvideo.common.Const;
import com.songshu.squirrelvideo.db.SearchHistoryDaoImpl;
import com.songshu.squirrelvideo.entity.DBSearchHistoryBean;
import com.songshu.squirrelvideo.entity.HotSearchBean;
import com.songshu.squirrelvideo.entity.SearchResultSingleVideoBean;
import com.songshu.squirrelvideo.fragment.SearchDefaultFragment;
import com.songshu.squirrelvideo.fragment.SearchHasResultFragment;
import com.songshu.squirrelvideo.fragment.SearchNoResultFragment;
import com.songshu.squirrelvideo.fragment.SearchSearchingFragment;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.view.swipe.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by yb on 15-7-20.
 */
public class SearchActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final String TAG = SearchActivity.class.getSimpleName() + ":";
    private Context mContext;

    private RelativeLayout rl_search_short;
    private RelativeLayout rl_search_long;
    private ImageView iv_go_back;
    private ImageView iv_search_action_short;
    private ImageView iv_search_action_long;
    private EditText et_search_content_short;
    private EditText et_search_content_long;
    private TextView tv_cancel;
    private FrameLayout fl_for_replace;

    private SearchHistoryDaoImpl mSearchHistoryDaoImpl;
    private boolean isAutoSetTextForEditText = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;
        mSearchHistoryDaoImpl = new SearchHistoryDaoImpl(mContext);
        initView();
        setListener();
        initFragment();
        initFragmentManager();
        initFragmentListener();
        changeContentTopView(TOP_VIEW_SHORT);
        hideSoftKeyBoard(getCurrentStyleSearchBar());
        switchFragment(FRAGMENT_DEFAULT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initView() {
        rl_search_short = (RelativeLayout) findViewById(R.id.rl_search_short);
        rl_search_long = (RelativeLayout) findViewById(R.id.rl_search_long);
        fl_for_replace = (FrameLayout) findViewById(R.id.fl_for_replace);

        iv_go_back = (ImageView) findViewById(R.id.iv_go_back);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);

        iv_search_action_short = (ImageView) findViewById(R.id.iv_search_action_short);
        et_search_content_short = (EditText) findViewById(R.id.et_search_content_short);

        iv_search_action_long = (ImageView) findViewById(R.id.iv_search_action_long);
        et_search_content_long = (EditText) findViewById(R.id.et_search_content_long);

    }


    private void setListener() {
        iv_go_back.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        iv_search_action_short.setOnClickListener(searchActionClickListener);
        iv_search_action_long.setOnClickListener(searchActionClickListener);

        et_search_content_short.setOnTouchListener(searchEditTouchListener);
        et_search_content_long.setOnTouchListener(searchEditTouchListener);

        et_search_content_short.setOnEditorActionListener(editorActionListener);// 监听软键盘上的搜索
        et_search_content_long.setOnEditorActionListener(editorActionListener);// 监听软键盘上的搜索

        et_search_content_short.addTextChangedListener(textWatcher);// 监听输入框的变化
        et_search_content_long.addTextChangedListener(textWatcher);// 监听输入框的变化

    }


    private Fragment[] fragments;

    private void initFragment() {
        fragments = new Fragment[4];
        fragments[0] = new SearchDefaultFragment();
        fragments[1] = new SearchHasResultFragment();
        fragments[2] = new SearchNoResultFragment();
        fragments[3] = new SearchSearchingFragment();
    }


    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private void initFragmentManager() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            mFragmentTransaction.add(R.id.fl_for_replace, fragments[i]);
        }
        mFragmentTransaction.commitAllowingStateLoss();
    }


    private void initFragmentListener() {
        ((SearchDefaultFragment) fragments[0]).setOnSearchDefaultFragmentItemClickListener(new SearchDefaultFragment.SearchDefaultFragmentItemClickListener() {
            @Override
            public void hotSearchItemClick(HotSearchBean bean) {
                L.d(TAG, "hotSearchItemClick --> " + bean);
                changeContentTopView(TOP_VIEW_LONG);
                isAutoSetTextForEditText = true;
                et_search_content_long.setText(bean.content);
                et_search_content_long.setSelection(bean.content.trim().length());
                hideSoftKeyBoard(getCurrentStyleSearchBar());
                switchFragment(FRAGMENT_RESULT);
                ((SearchHasResultFragment) fragments[1]).join(bean.content);
            }

            @Override
            public void SearchHistoryItemClick(DBSearchHistoryBean bean) {
                L.d(TAG, "SearchHistoryItemClick --> " + bean);
                changeContentTopView(TOP_VIEW_LONG);
                isAutoSetTextForEditText = true;
                et_search_content_long.setText(bean.historyTitle);
                et_search_content_long.setSelection(bean.historyTitle.trim().length());
                hideSoftKeyBoard(getCurrentStyleSearchBar());
                switchFragment(FRAGMENT_RESULT);
                ((SearchHasResultFragment) fragments[1]).join(bean.historyTitle);
            }
        });


        ((SearchHasResultFragment) fragments[1]).setOnSearchHasResultFragmentListener(new SearchHasResultFragment.SearchHasResultFragmentListener() {
            @Override
            public void noSearchResult(String search_name) {
                L.d(TAG, "noSearchResult --> 搜索的关键字 : " + search_name);
                if (current_fragment == FRAGMENT_RESULT) {
                    changeContentTopView(TOP_VIEW_SHORT);
                    isAutoSetTextForEditText = true;
                    et_search_content_short.setText(search_name);
                    et_search_content_short.setSelection(search_name.trim().length());
                    hideSoftKeyBoard(getCurrentStyleSearchBar());
                    switchFragment(FRAGMENT_NO_RESULT);
                }
            }
        });


        ((SearchSearchingFragment) fragments[3]).setOnSearchSearchingFragmentItemClickListener(new SearchSearchingFragment.SearchSearchingFragmentItemClickListener() {
            @Override
            public void quicklySearchItemClick(SearchResultSingleVideoBean bean) {
                L.d(TAG, "quicklySearchItemClick --> " + bean);
                setSearchDataToDB(bean.title);
                ((SearchDefaultFragment) fragments[0]).join();
                changeContentTopView(TOP_VIEW_LONG);
                isAutoSetTextForEditText = true;
                et_search_content_long.setText(bean.title);
                et_search_content_long.setSelection(bean.title.trim().length());
                hideSoftKeyBoard(getCurrentStyleSearchBar());
                switchFragment(FRAGMENT_RESULT);
                ((SearchHasResultFragment) fragments[1]).join(bean.title);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_go_back:
                hideSoftKeyBoard(getCurrentStyleSearchBar());
                finish();
                break;
            case R.id.tv_cancel:
                changeContentTopView(TOP_VIEW_SHORT);
                isAutoSetTextForEditText = true;
                et_search_content_short.setText("");
                et_search_content_short.setSelection("".trim().length());
                hideSoftKeyBoard(getCurrentStyleSearchBar());
                switchFragment(FRAGMENT_DEFAULT);
                break;
        }
    }


    /**
     * 搜索框中字数的变化
     */
    private TextWatcher textWatcher = new TextWatcher() {

        // 缓存上一次文本框内是否为空
        private boolean isnull = true;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            L.d(TAG, "afterTextChanged --> isAutoSetTextForEditText : " + isAutoSetTextForEditText);
            if (isAutoSetTextForEditText) {
                isAutoSetTextForEditText = false;
                return;
            }

            if (getCurrentStyleSearchBarMark()) {//短
                EditText currentStyleSearchBar = getCurrentStyleSearchBar();
                String searchName = currentStyleSearchBar.getText().toString().trim();
                changeContentTopView(TOP_VIEW_LONG);
                et_search_content_long.setText(searchName);
                et_search_content_long.setSelection(searchName.trim().length());
            } else {//长
                if (current_fragment != FRAGMENT_SEARCHING) {
                    switchFragment(FRAGMENT_SEARCHING);
                }
                et_search_content_long.requestFocus();
                et_search_content_long.setSelection(et_search_content_long.getText().toString().trim().length());
                EditText currentStyleSearchBar = getCurrentStyleSearchBar();
                String searchName = currentStyleSearchBar.getText().toString().trim();
                ((SearchSearchingFragment) fragments[3]).join(searchName);
            }
        }
    };


    /**
     * 软件盘上搜索按键的监听
     */
    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                EditText currentStyleSearchBar = getCurrentStyleSearchBar();
                String searchName = currentStyleSearchBar.getText().toString().trim();
                if (TextUtils.isEmpty(searchName)) {
                    App.showToast("请输入要搜索的关键字");
                    return true;
                }
                setSearchDataToDB(searchName);
                ((SearchDefaultFragment) fragments[0]).join();
                changeContentTopView(TOP_VIEW_LONG);
                isAutoSetTextForEditText = true;
                et_search_content_long.setText(searchName);
                et_search_content_long.setSelection(searchName.trim().length());
                hideSoftKeyBoard(getCurrentStyleSearchBar());
                switchFragment(FRAGMENT_RESULT);
                ((SearchHasResultFragment) fragments[1]).join(searchName);
                return true;
            }
            return false;
        }
    };


    /**
     * 搜索框的触摸监听事件
     */
    private View.OnTouchListener searchEditTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            EditText et = (EditText) v;
            et.setFocusable(true);
            et.requestFocus();
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    int curX = (int) event.getX();
                    if (curX > v.getWidth() - 38 && !TextUtils.isEmpty(et.getText())) {
                        isAutoSetTextForEditText = true;
                        et.setText("");
                        et.setSelection("".trim().length());
                        int cacheInputType = et.getInputType();
                        et.setInputType(InputType.TYPE_NULL);
                        et.onTouchEvent(event);
                        et.setInputType(cacheInputType);
                        return true;
                    }
                    break;
            }
            return false;
        }
    };


    /**
     * 点击搜索图标的监听
     */
    private View.OnClickListener searchActionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText currentStyleSearchBar = getCurrentStyleSearchBar();
            String searchName = currentStyleSearchBar.getText().toString().trim();
            if (TextUtils.isEmpty(searchName)) {
                App.showToast("请输入要搜索的关键字");
                return;
            }
            setSearchDataToDB(searchName);
            ((SearchDefaultFragment) fragments[0]).join();
            changeContentTopView(TOP_VIEW_LONG);
            isAutoSetTextForEditText = true;
            et_search_content_long.setText(searchName);
            et_search_content_long.setSelection(searchName.trim().length());
            hideSoftKeyBoard(getCurrentStyleSearchBar());
            switchFragment(FRAGMENT_RESULT);
            ((SearchHasResultFragment) fragments[1]).join(searchName);
        }
    };


    private static final int FRAGMENT_DEFAULT = 0;
    private static final int FRAGMENT_RESULT = 1;
    private static final int FRAGMENT_NO_RESULT = 2;
    private static final int FRAGMENT_SEARCHING = 3;
    private int current_fragment = FRAGMENT_DEFAULT;
    private int last_fragment = FRAGMENT_DEFAULT;

    /**
     * 更改Fragment对象
     *
     * @param index
     */
    private void switchFragment(int index) {
        last_fragment = current_fragment;
        current_fragment = index;
        L.d(TAG, "switchFragment --> last_fragment : " + last_fragment + " , current_fragment : " + current_fragment);
        mFragmentTransaction = mFragmentManager.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (index == i) {
                mFragmentTransaction.show(fragments[index]);
            } else {
                mFragmentTransaction.hide(fragments[i]);
            }
        }
        mFragmentTransaction.commit();
    }


    private static final boolean TOP_VIEW_SHORT = true;
    private static final boolean TOP_VIEW_LONG = false;

    /**
     * 修改顶部长短搜索条的布局
     *
     * @param isShort
     */
    private void changeContentTopView(boolean isShort) {
        L.d(TAG, "changeContentTopView --> " + isShort);
        if (isShort) {
            rl_search_short.setVisibility(View.VISIBLE);
            rl_search_long.setVisibility(View.GONE);
            setSwipeBackEnable(true);
        } else {
            rl_search_short.setVisibility(View.GONE);
            rl_search_long.setVisibility(View.VISIBLE);
            setSwipeBackEnable(false);
        }
    }

    /**
     * 获取当前是长搜索条还是短搜索条
     */
    private EditText getCurrentStyleSearchBar() {
        if (rl_search_short.getVisibility() == View.VISIBLE) {
            L.d(TAG, "......短搜索条......");
            return et_search_content_short;
        } else {
            L.d(TAG, "......长搜索条......");
            return et_search_content_long;
        }
    }


    /**
     * 返回当前长短搜索条的标记
     *
     * @return true 短搜索条 false 长搜索条
     */
    private boolean getCurrentStyleSearchBarMark() {
        if (rl_search_short.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 向数据库添加数据
     *
     * @param historyTitle
     */
    private void setSearchDataToDB(String historyTitle) {
        DBSearchHistoryBean bean = new DBSearchHistoryBean(historyTitle, Const.CHANNEL, String.valueOf(System.currentTimeMillis()));
        L.d(TAG, "setSearchDataToDB --> DBSearchHistoryBean : " + bean);
        boolean add = mSearchHistoryDaoImpl.addSearchHistory(bean);
        L.d(TAG, "是否成功存入数据库 : " + add);
        if (!add) {
            boolean updata = mSearchHistoryDaoImpl.updataSearchHistory(bean);
            L.d(TAG, "是否成功更新数据库 : " + updata);
        }
    }


    /**
     * 打开软件盘
     */
    private void openSoftKeyBoard(EditText editText) {
        InputMethodManager m = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * 隐藏软键盘
     */
    private void hideSoftKeyBoard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
