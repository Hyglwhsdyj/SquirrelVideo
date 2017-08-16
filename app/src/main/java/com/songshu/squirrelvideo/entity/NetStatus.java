package com.songshu.squirrelvideo.entity;

/**
 * Created by yb on 15-7-4.
 */
public class NetStatus {

    public int status_code;
    public String detail;

    public NetStatus() {
    }

    public NetStatus(int status_code, String detail) {
        this.status_code = status_code;
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "NetStatus{" +
                "status_code=" + status_code +
                ", detail='" + detail + '\'' +
                '}';
    }

}
