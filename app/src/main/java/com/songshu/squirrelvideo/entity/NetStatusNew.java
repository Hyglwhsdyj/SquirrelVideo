package com.songshu.squirrelvideo.entity;

/**
 * Created by yb on 15-7-4.
 */
public class NetStatusNew {

    public int code;
    public String msg;
    public String time;
    public int expires;

    public NetStatusNew() {
    }

    public NetStatusNew(int code, String msg, String time, int expires) {
        this.code = code;
        this.msg = msg;
        this.time = time;
        this.expires = expires;
    }

    @Override
    public String toString() {
        return "NetStatusNew{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", time='" + time + '\'' +
                ", expires=" + expires +
                '}';
    }
}
