package com.songshu.squirrelvideo.manager;

import android.content.Context;

import com.songshu.squirrelvideo.R;
import com.songshu.squirrelvideo.application.App;
import com.songshu.squirrelvideo.entity.VideoTypeTransformBean;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by yb on 15-7-7.
 */
public class TitleManager {

    private static Context mContext;

    static {
        mContext = App.getContext();

    }

    private static final String TAG = TitleManager.class.getSimpleName() + ":";

    public static final String TITLE_MOVIE = mContext.getString(R.string.character_movie);
    public static final String TITLE_TELEPLAY = mContext.getString(R.string.character_teleplay);
    public static final String TITLE_CHILD = mContext.getString(R.string.character_child);
    public static final String TITLE_OPERA = mContext.getString(R.string.character_opera);
    public static final String TITLE_HEALTH = mContext.getString(R.string.character_health);
    public static final String TITLE_LOCAL = mContext.getString(R.string.local_media_title);

    public static final String SUB_TITLE_RECOMMOND = mContext.getString(R.string.character_recommend);
    public static final String SUB_TITLE_WHOLE = mContext.getString(R.string.character_all_category);

    public static final String MOVIE_SUB_TITLE_XIJU = mContext.getString(R.string.character_movie_xiju);
    public static final String MOVIE_SUB_TITLE_AIQING = mContext.getString(R.string.character_movie_aiqing);
    public static final String MOVIE_SUB_TITLE_DONGZUO = mContext.getString(R.string.character_movie_dongzuo);
    public static final String MOVIE_SUB_TITLE_ZHANZHENG = mContext.getString(R.string.character_movie_zhanzheng);
    public static final String MOVIE_SUB_TITLE_WUXIA = mContext.getString(R.string.character_movie_wuxia);
    public static final String MOVIE_SUB_TITLE_DONGHUA = mContext.getString(R.string.character_movie_donghua);
    public static final String MOVIE_SUB_TITLE_JUQING = mContext.getString(R.string.character_movie_juqing);
    public static final String MOVIE_SUB_TITLE_ZHUANJI = mContext.getString(R.string.character_movie_zhuanji);
    public static final String MOVIE_SUB_TITLE_LISHI = mContext.getString(R.string.character_movie_lishi);
    public static final String MOVIE_SUB_TITLE_KEHUAN = mContext.getString(R.string.character_movie_kehuan);
    public static final String MOVIE_SUB_TITLE_QITA = mContext.getString(R.string.character_movie_qita);

    public static final String TELEPLAY_SUB_TITLE_JIATING = mContext.getString(R.string.character_teleplay_jiating);
    public static final String TELEPLAY_SUB_TITLE_GUZHUANG = mContext.getString(R.string.character_teleplay_guzhuang);
    public static final String TELEPLAY_SUB_TITLE_XIJU = mContext.getString(R.string.character_teleplay_xiju);
    public static final String TELEPLAY_SUB_TITLE_YANQING = mContext.getString(R.string.character_teleplay_yanqing);
    public static final String TELEPLAY_SUB_TITLE_ZHANZHENG = mContext.getString(R.string.character_teleplay_zhanzheng);
    public static final String TELEPLAY_SUB_TITLE_JINGFEI = mContext.getString(R.string.character_teleplay_jingfei);
    public static final String TELEPLAY_SUB_TITLE_QITA = mContext.getString(R.string.character_teleplay_qita);

    public static final String CHILD_SUB_TITLE_YIZHIQINZI = mContext.getString(R.string.character_child_yizhiqinzi);
    public static final String CHILD_SUB_TITLE_ERTONGZAOJIAO = mContext.getString(R.string.character_child_ertongzaojiao);
    public static final String CHILD_SUB_TITLE_ERTONGYINYUE = mContext.getString(R.string.character_child_ertongyinyue);
    public static final String CHILD_SUB_TITLE_QIHUANMOFA = mContext.getString(R.string.character_child_qihuanmofa);
    public static final String CHILD_SUB_TITLE_LIXIANQIYU = mContext.getString(R.string.character_child_lixianqiyu);
    public static final String CHILD_SUB_TITLE_GAOXIAOLEYUAN = mContext.getString(R.string.character_child_gaoxiaoleyuan);
    public static final String CHILD_SUB_TITLE_QITA = mContext.getString(R.string.character_child_qita);

    public static final String OPERA_SUB_TITLE_JINGJU = mContext.getString(R.string.character_opera_jingju);
    public static final String OPERA_SUB_TITLE_PINGJU = mContext.getString(R.string.character_opera_pingju);
    public static final String OPERA_SUB_TITLE_YUJU = mContext.getString(R.string.character_opera_yuju);
    public static final String OPERA_SUB_TITLE_YUEJU = mContext.getString(R.string.character_opera_yueju);
    public static final String OPERA_SUB_TITLE_HUANGMEIXI = mContext.getString(R.string.character_opera_huangmeixi);
    public static final String OPERA_SUB_TITLE_XIANGSHENGXIAOPIN = mContext.getString(R.string.character_opera_xiangshengxiaopin);
    public static final String OPERA_SUB_TITLE_PINGSHUXIAOHUA = mContext.getString(R.string.character_opera_pingshuxiaohua);
    public static final String OPERA_SUB_TITLE_MOSHUZAJI = mContext.getString(R.string.character_opera_moshuzaji);
    public static final String OPERA_SUB_TITLE_HEBEIBANGZI = mContext.getString(R.string.character_opera_hebeibangzi);
    public static final String OPERA_SUB_TITLE_CHUANJU = mContext.getString(R.string.character_opera_chuanju);
    public static final String OPERA_SUB_TITLE_ERRENZHUAN = mContext.getString(R.string.character_opera_errenzhuan);
    public static final String OPERA_SUB_TITLE_QINQIANG = mContext.getString(R.string.character_opera_qinqiang);
    public static final String OPERA_SUB_TITLE_JINJU = mContext.getString(R.string.character_opera_jinju);
    public static final String OPERA_SUB_TITLE_HUAGUXI = mContext.getString(R.string.character_opera_huaguxi);
    public static final String OPERA_SUB_TITLE_KUNQU = mContext.getString(R.string.character_opera_kunqu);
    public static final String OPERA_SUB_TITLE_PUXIANXI = mContext.getString(R.string.character_opera_puxianxi);
    public static final String OPERA_SUB_TITLE_XIJU = mContext.getString(R.string.character_opera_xiju);
    public static final String OPERA_SUB_TITLE_MINJIANXIAODIAO = mContext.getString(R.string.character_opera_minjianxiaodiao);
    public static final String OPERA_SUB_TITLE_CHAOJU = mContext.getString(R.string.character_opera_chaoju);
    public static final String OPERA_SUB_TITLE_QUJU = mContext.getString(R.string.character_opera_quju);
    public static final String OPERA_SUB_TITLE_PUJU = mContext.getString(R.string.character_opera_puju);
    public static final String OPERA_SUB_TITLE_QITA = mContext.getString(R.string.character_opera_qita);

    public static final String HEALTH_SUB_TITLE_YANGSHENG = mContext.getString(R.string.character_health_yangsheng);
    public static final String HEALTH_SUB_TITLE_TAIJIQUAN = mContext.getString(R.string.character_health_taijiquan);
    public static final String HEALTH_SUB_TITLE_YILIAO = mContext.getString(R.string.character_health_yiliao);
    public static final String HEALTH_SUB_TITLE_ZHONGYI = mContext.getString(R.string.character_health_zhongyi);
    public static final String HEALTH_SUB_TITLE_LIANGXING = mContext.getString(R.string.character_health_liangxing);
    public static final String HEALTH_SUB_TITLE_CHANGSHI = mContext.getString(R.string.character_health_changshi);
    public static final String HEALTH_SUB_TITLE_ERKE = mContext.getString(R.string.character_health_erke);
    public static final String HEALTH_SUB_TITLE_LAONIAN = mContext.getString(R.string.character_health_laonian);
    public static final String HEALTH_SUB_TITLE_XINLI = mContext.getString(R.string.character_health_xinli);
    public static final String HEALTH_SUB_TITLE_ZHIYEBING = mContext.getString(R.string.character_health_zhiyebing);
    public static final String HEALTH_SUB_TITLE_QITA = mContext.getString(R.string.character_health_qita);

    public static final String LOCAL_SUB_TITLE_ENTAIR = mContext.getString(R.string.local_media_entire);
    public static final String LOCAL_SUB_TITLE_FILM = mContext.getString(R.string.local_media_movie);
    public static final String LOCAL_SUB_TITLE_TELEPLAY = mContext.getString(R.string.local_media_teleplay);
    public static final String LOCAL_SUB_TITLE_OPERA = mContext.getString(R.string.local_media_opera);
    public static final String LOCAL_SUB_TITLE_DANCE = mContext.getString(R.string.local_media_dance);
    public static final String LOCAL_SUB_TITLE_CHILD = mContext.getString(R.string.local_media_child);
    public static final String LOCAL_SUB_TITLE_HEALTH = mContext.getString(R.string.local_media_health);


    public static final String[] SUBTITLE_DEFAULT_MOVIE = {
            SUB_TITLE_RECOMMOND,
            MOVIE_SUB_TITLE_XIJU,
            MOVIE_SUB_TITLE_AIQING,
            MOVIE_SUB_TITLE_DONGZUO,
            MOVIE_SUB_TITLE_ZHANZHENG,
            MOVIE_SUB_TITLE_WUXIA,
            SUB_TITLE_WHOLE
    };
    public static final String[] SUBTITLE_DEFAULT_TELEPLAY = {
            SUB_TITLE_RECOMMOND,
            TELEPLAY_SUB_TITLE_JIATING,
            TELEPLAY_SUB_TITLE_GUZHUANG,
            TELEPLAY_SUB_TITLE_XIJU,
            TELEPLAY_SUB_TITLE_YANQING,
            TELEPLAY_SUB_TITLE_ZHANZHENG,
            SUB_TITLE_WHOLE
    };
    public static final String[] SUBTITLE_DEFAULT_CHILD = {
            SUB_TITLE_RECOMMOND,
            CHILD_SUB_TITLE_YIZHIQINZI,
            CHILD_SUB_TITLE_ERTONGZAOJIAO,
            CHILD_SUB_TITLE_ERTONGYINYUE,
            CHILD_SUB_TITLE_QIHUANMOFA,
            CHILD_SUB_TITLE_LIXIANQIYU,
            SUB_TITLE_WHOLE
    };
    public static final String[] SUBTITLE_DEFAULT_OPERA = {
            SUB_TITLE_RECOMMOND,
            OPERA_SUB_TITLE_JINGJU,
            OPERA_SUB_TITLE_PINGJU,
            OPERA_SUB_TITLE_YUJU,
            OPERA_SUB_TITLE_YUEJU,
            OPERA_SUB_TITLE_HUANGMEIXI,
            SUB_TITLE_WHOLE
    };
    public static final String[] SUBTITLE_DEFAULT_HEALTH = {
            SUB_TITLE_RECOMMOND,
            HEALTH_SUB_TITLE_YANGSHENG,
            HEALTH_SUB_TITLE_TAIJIQUAN,
            HEALTH_SUB_TITLE_YILIAO,
            HEALTH_SUB_TITLE_ZHONGYI,
            HEALTH_SUB_TITLE_CHANGSHI,
            SUB_TITLE_WHOLE
    };
    public static final String[] SUBTITLE_DEFAULT_LOCAL = {
            LOCAL_SUB_TITLE_ENTAIR,
            LOCAL_SUB_TITLE_FILM,
            LOCAL_SUB_TITLE_TELEPLAY,
            LOCAL_SUB_TITLE_OPERA,
            LOCAL_SUB_TITLE_DANCE,
            LOCAL_SUB_TITLE_CHILD,
            LOCAL_SUB_TITLE_HEALTH
    };


    private static Map<String, List<VideoTypeTransformBean>> map1;
    private static Map<String, String> map2;

    private static void initEngNameMap() {
        map2 = new LinkedHashMap<String, String>();

        map2.put(TITLE_MOVIE, "movie");
        map2.put(TITLE_TELEPLAY, "teleplay");
        map2.put(TITLE_CHILD, "child");
        map2.put(TITLE_OPERA, "opera");
        map2.put(TITLE_HEALTH, "health");
    }

    private static void initSubTitleMap() {
        map1 = new LinkedHashMap<String, List<VideoTypeTransformBean>>();


        List<VideoTypeTransformBean> movie_list = new LinkedList<VideoTypeTransformBean>();
        movie_list.add(new VideoTypeTransformBean(MOVIE_SUB_TITLE_XIJU, "movie", "13150", R.drawable.movie_pic_fantastic_comedy));
        movie_list.add(new VideoTypeTransformBean(MOVIE_SUB_TITLE_AIQING, "movie", "13151", R.drawable.movie_pic_love));
        movie_list.add(new VideoTypeTransformBean(MOVIE_SUB_TITLE_DONGZUO, "movie", "13195", R.drawable.movie_pic_action));
        movie_list.add(new VideoTypeTransformBean(MOVIE_SUB_TITLE_ZHANZHENG, "movie", "13213", R.drawable.movie_pic_war));
        movie_list.add(new VideoTypeTransformBean(MOVIE_SUB_TITLE_WUXIA, "movie", "13281", R.drawable.movie_pic_biography));
        movie_list.add(new VideoTypeTransformBean(MOVIE_SUB_TITLE_DONGHUA, "movie", "13113", R.drawable.movie_pic_animation));
        movie_list.add(new VideoTypeTransformBean(MOVIE_SUB_TITLE_JUQING, "movie", "13145", R.drawable.movie_pic_plot));
        movie_list.add(new VideoTypeTransformBean(MOVIE_SUB_TITLE_ZHUANJI, "movie", "13234", R.drawable.movie_pic_suspense));
        movie_list.add(new VideoTypeTransformBean(MOVIE_SUB_TITLE_LISHI, "movie", "13232", R.drawable.movie_pic_history));
        movie_list.add(new VideoTypeTransformBean(MOVIE_SUB_TITLE_KEHUAN, "movie", "13126", R.drawable.movie_pic_science_fiction));
        movie_list.add(new VideoTypeTransformBean(MOVIE_SUB_TITLE_QITA, "movie", "13448", R.drawable.movie_pic_thriller));
        map1.put(TITLE_MOVIE, movie_list);

        List<VideoTypeTransformBean> tele_play_list = new LinkedList<VideoTypeTransformBean>();
        tele_play_list.add(new VideoTypeTransformBean(TELEPLAY_SUB_TITLE_JIATING, "teleplay", "13196", R.drawable.teleplay_pic_family));
        tele_play_list.add(new VideoTypeTransformBean(TELEPLAY_SUB_TITLE_GUZHUANG, "teleplay", "13168", R.drawable.teleplay_pic_ancient_costume));
        tele_play_list.add(new VideoTypeTransformBean(TELEPLAY_SUB_TITLE_XIJU, "teleplay", "13139", R.drawable.teleplay_pic_comedy));
        tele_play_list.add(new VideoTypeTransformBean(TELEPLAY_SUB_TITLE_YANQING, "teleplay", "13142", R.drawable.teleplay_pic_romance));
        tele_play_list.add(new VideoTypeTransformBean(TELEPLAY_SUB_TITLE_ZHANZHENG, "teleplay", "13200", R.drawable.teleplay_pic_war));
        tele_play_list.add(new VideoTypeTransformBean(TELEPLAY_SUB_TITLE_JINGFEI, "teleplay", "13164", R.drawable.teleplay_pic_police));
        tele_play_list.add(new VideoTypeTransformBean(TELEPLAY_SUB_TITLE_QITA, "teleplay", "195", R.drawable.teleplay_pic_other));
        map1.put(TITLE_TELEPLAY, tele_play_list);

        List<VideoTypeTransformBean> child_list = new LinkedList<VideoTypeTransformBean>();
        child_list.add(new VideoTypeTransformBean(CHILD_SUB_TITLE_YIZHIQINZI, "child", "20", R.drawable.children_pic_puzzle));
        child_list.add(new VideoTypeTransformBean(CHILD_SUB_TITLE_ERTONGZAOJIAO, "child", "21", R.drawable.children_pic_education));
        child_list.add(new VideoTypeTransformBean(CHILD_SUB_TITLE_ERTONGYINYUE, "child", "22", R.drawable.children_pic_music));
        child_list.add(new VideoTypeTransformBean(CHILD_SUB_TITLE_QIHUANMOFA, "child", "23", R.drawable.children_pic_fantasy));
        child_list.add(new VideoTypeTransformBean(CHILD_SUB_TITLE_LIXIANQIYU, "child", "24", R.drawable.children_pic_adventure));
        child_list.add(new VideoTypeTransformBean(CHILD_SUB_TITLE_GAOXIAOLEYUAN, "child", "25", R.drawable.children_pic_funny));
        child_list.add(new VideoTypeTransformBean(CHILD_SUB_TITLE_QITA, "child", "197", R.drawable.children_pic_other));
        map1.put(TITLE_CHILD, child_list);

        List<VideoTypeTransformBean> opera_list = new LinkedList<VideoTypeTransformBean>();
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_JINGJU, "opera", "200", R.drawable.opera_pic_beijing_opera));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_PINGJU, "opera", "201", R.drawable.opera_pic_pingju));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_YUJU, "opera", "202", R.drawable.opera_pic_henan_opera));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_YUEJU, "opera", "203", R.drawable.opera_pic_shaoxing_opera));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_HUANGMEIXI, "opera", "204", R.drawable.opera_pic_huangmei_opera));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_XIANGSHENGXIAOPIN, "opera", "205", R.drawable.opera_pic_crosstalk_sketch));
//        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_PINGSHUXIAOHUA, "opera", "206", R.drawable.opera_pic_ballad_singing));
//        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_MOSHUZAJI, "opera", "207", R.drawable.opera_pic_magic));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_HEBEIBANGZI, "opera", "208", R.drawable.opera_pic_hebei_bangzi));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_CHUANJU, "opera", "209", R.drawable.opera_pic_ccj));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_ERRENZHUAN, "opera", "210", R.drawable.opera_pic_men));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_QINQIANG, "opera", "211", R.drawable.opera_pic_qq));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_JINJU, "opera", "212", R.drawable.opera_pic_jj));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_HUAGUXI, "opera", "213", R.drawable.opera_pic_hgx));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_KUNQU, "opera", "214", R.drawable.opera_pic_kq));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_PUXIANXI, "opera", "215", R.drawable.opera_pic_pxx));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_XIJU, "opera", "216", R.drawable.opera_pic_xj));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_MINJIANXIAODIAO, "opera", "217", R.drawable.opera_pic_minor));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_CHAOJU, "opera", "218", R.drawable.opera_pic_cj));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_QUJU, "opera", "219", R.drawable.opera_pic_qj));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_PUJU, "opera", "220", R.drawable.opera_pic_pj));
        opera_list.add(new VideoTypeTransformBean(OPERA_SUB_TITLE_QITA, "opera", "198", R.drawable.opera_pic_other));
        map1.put(TITLE_OPERA, opera_list);

        List<VideoTypeTransformBean> health_list = new LinkedList<VideoTypeTransformBean>();
        health_list.add(new VideoTypeTransformBean(HEALTH_SUB_TITLE_YANGSHENG, "health", "2", R.drawable.health_pic_health));
        health_list.add(new VideoTypeTransformBean(HEALTH_SUB_TITLE_TAIJIQUAN, "health", "4", R.drawable.health_pic_fitness));
        health_list.add(new VideoTypeTransformBean(HEALTH_SUB_TITLE_YILIAO, "health", "5", R.drawable.health_pic_medical));
        health_list.add(new VideoTypeTransformBean(HEALTH_SUB_TITLE_ZHONGYI, "health", "6", R.drawable.health_pic_medicine));
//        health_list.add(new VideoTypeTransformBean(HEALTH_SUB_TITLE_LIANGXING, "health", "7", R.drawable.health_pic_sex));
        health_list.add(new VideoTypeTransformBean(HEALTH_SUB_TITLE_CHANGSHI, "health", "8", R.drawable.health_pic_common));
        health_list.add(new VideoTypeTransformBean(HEALTH_SUB_TITLE_ERKE, "health", "9", R.drawable.health_pic_children));
        health_list.add(new VideoTypeTransformBean(HEALTH_SUB_TITLE_LAONIAN, "health", "10", R.drawable.health_pic_old_man));
        health_list.add(new VideoTypeTransformBean(HEALTH_SUB_TITLE_XINLI, "health", "11", R.drawable.health_pic_psychological));
        health_list.add(new VideoTypeTransformBean(HEALTH_SUB_TITLE_ZHIYEBING, "health", "12", R.drawable.health_pic_occupational_disease));
//        health_list.add(new VideoTypeTransformBean(HEALTH_SUB_TITLE_QITA, "health", "196", R.drawable.movie_pic_fantastic_comedy));
        map1.put(TITLE_HEALTH, health_list);
    }

    public static Map<String, String> getEngNameMap() {
        if (map2 == null) {
            initEngNameMap();
        }
        return map2;
    }

    public static Map<String, List<VideoTypeTransformBean>> getSubTitleMap() {
        if (map1 == null) {
            initSubTitleMap();
        }
        return map1;
    }
}
