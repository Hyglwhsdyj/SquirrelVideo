package com.songshu.squirrelvideo.entity;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.songshu.squirrelvideo.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yb on 15-7-9.
 */
public class TeleplaySourceBean implements Serializable {

    private static final String TAG = TeleplaySourceBean.class.getSimpleName() + ":";

    public String name;
    public String title;
    public String icon_url;
    public JsonObject values;
    public List<PlayTeleplayBean> mPlayTeleplayBeanList;


    @Override
    public String toString() {
        return "TeleplaySourceBean{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", icon_url='" + icon_url + '\'' +
                ", values=" + values +
                '}';
    }

    public void transform() {
        try {
            JSONObject obj = new JSONObject(values.toString());
            mPlayTeleplayBeanList = new LinkedList<PlayTeleplayBean>();
            Iterator iter = obj.keys();
            List<String> tempList = new ArrayList<String>();
            while (iter.hasNext()) {
                tempList.add((String) iter.next());
            }
            int size = tempList.size();
            String[] episodes = tempList.toArray(new String[size]);
            int[] episodesNum = new int[size];
            for (int j = 0; j < episodes.length; j++) {
                String str = episodes[j];
                String newStr = str.substring(str.indexOf("p") + 1, str.length());
                episodesNum[j] = Integer.parseInt(newStr);
            }
            // 排序剧集
            Arrays.sort(episodesNum);
            for (int k = 0; k < episodesNum.length; k++) {
                String videoUrl = null;
                videoUrl = obj.getString("p" + episodesNum[k]);
                PlayTeleplayBean bean = new PlayTeleplayBean(episodesNum[k], videoUrl);
                mPlayTeleplayBeanList.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public boolean isEqual(String valueStr) {
        if (!TextUtils.isEmpty(valueStr) && valueStr.equals(values.toString())) {
            return true;
        }
        return false;
    }
}
