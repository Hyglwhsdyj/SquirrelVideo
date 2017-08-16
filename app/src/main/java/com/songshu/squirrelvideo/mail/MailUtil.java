package com.songshu.squirrelvideo.mail;

import com.songshu.squirrelvideo.utils.FileUtils;
import com.songshu.squirrelvideo.utils.L;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by yb on 15-11-4.
 */
public class MailUtil {
    private static final String TAG = MailUtil.class.getSimpleName() + ":";

    private MailUtil() {
    }

    private static MailUtil instance;

    public static synchronized MailUtil getInstance() {
        if (instance == null) {
            synchronized (MailUtil.class) {
                if (instance == null)
                    instance = new MailUtil();
            }
        }
        return instance;
    }


    public boolean sendMail(final MailInfo info) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.protocol", "smtp");
            props.put("mail.smtp.host", "smtp.qq.com");
//        props.put("mail.smtp.host", "smtp.exmail.qq.com");
//        props.put("mail.smtp.port", "465");
            props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.socketFactory.fallback", "false");

            Session session = Session.getDefaultInstance(props, new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(info.getFrom(), info.getPassword());
                }
            });

            MimeMessage msg = new MimeMessage(session);


            msg.setFrom(new InternetAddress(info.getFrom()));//发自


            InternetAddress[] address = new InternetAddress[info.getToList().length];
            for (int current = 0; current < info.getToList().length; ++current) {
                address[current] = new InternetAddress(info.getToList()[current]);
            }
            msg.setRecipients(Message.RecipientType.TO, address);//发给

            msg.setSubject(info.getSubject());//标题

            msg.setSentDate(new Date());//添加日期

            MimeMultipart mp = new MimeMultipart();
            MimeBodyPart msgContent = new MimeBodyPart();
            msgContent.setText(info.getContent());
            mp.addBodyPart(msgContent);//添加文字内容
            MimeBodyPart attachContent = new MimeBodyPart();
            FileDataSource fds = new FileDataSource(FileUtils.getRecordFile());
            attachContent.setDataHandler(new DataHandler(fds));
            attachContent.setFileName(fds.getName());
            mp.addBodyPart(attachContent);//添加附件内容
            msg.setContent(mp);//添加内容
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            L.d(TAG, "......e : " + e);
//            throw new RuntimeException();
        }
        return false;
    }


    /**
     * 测试用
     */
    public boolean sendMail() {
        Properties props = new Properties();
        props.put("mail.smtp.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.qq.com");
//        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.socketFactory.fallback", "false");
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("yangbo@songshu.cc", "ybbjlm1215");
            }
        });
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress("yangbo@songshu.cc"));//发自
            msg.setRecipients(Message.RecipientType.TO, "yangbo@songshu.cc");//发给
            msg.setSubject("abc");//标题
            msg.setSentDate(new Date());
            MimeMultipart mp = new MimeMultipart();
            MimeBodyPart msgContent = new MimeBodyPart();
            msgContent.setText("ceshi");
            mp.addBodyPart(msgContent);//添加文字内容
            msg.setContent(mp);//添加内容
            Transport.send(msg);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return false;
    }

}
