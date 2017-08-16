package com.songshu.squirrelvideo.mail;

/**
 * Created by yb on 15-11-5.
 */

import java.util.Arrays;

/**
 * 邮件信息Bean
 */
public class MailInfo {

    private String from;
    private String password;
    private String[] toList;
    private String subject;
    private String content;

    public MailInfo() {
    }

    public String getFrom() {
        return this.from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getToList() {
        return this.toList;
    }

    public void setToList(String[] toList) {
        this.toList = toList;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MailInfo{" +
                "from='" + from + '\'' +
                ", password='" + password + '\'' +
                ", toList=" + Arrays.toString(toList) +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
