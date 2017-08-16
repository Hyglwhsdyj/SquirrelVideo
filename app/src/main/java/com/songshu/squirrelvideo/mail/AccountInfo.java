package com.songshu.squirrelvideo.mail;

/**
 * Created by yb on 15-11-4.
 */
public class AccountInfo {
    private static final String TAG = AccountInfo.class.getSimpleName() + ":";
    private String account;
    private String password;

    public AccountInfo() {
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
