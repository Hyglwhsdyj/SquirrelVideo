package com.songshu.squirrelvideo.mail;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

import com.songshu.squirrelvideo.utils.FileUtils;
import com.songshu.squirrelvideo.utils.L;
import com.songshu.squirrelvideo.utils.NetworkUtil;
import com.songshu.squirrelvideo.utils.SSUtil;
import com.songshu.squirrelvideo.utils.Util;

import org.xml.sax.Attributes;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 15-11-4.
 */
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = GlobalExceptionHandler.class.getSimpleName() + ":";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static Thread.UncaughtExceptionHandler defaultHandler;// 系统默认异常处理器
    private boolean caughtException = false; //是否使用自己的异常处理器 开关
    private static String clientInfo;
    private Context mContext;
    private static List<AccountInfo> fromEmailList = new ArrayList();
    private static List<String> toEmailList = new ArrayList();
    private static AccountInfo mailInfo = null;


    private static GlobalExceptionHandler instance;

    public static synchronized GlobalExceptionHandler init(Context mContext) {
        if (instance == null) {
            synchronized (GlobalExceptionHandler.class) {
                if (instance == null)
                    instance = new GlobalExceptionHandler(mContext);
            }
        }
        return instance;
    }


    private GlobalExceptionHandler(Context context) {
        mContext = context;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        clientInfo = this.collectClientInfo();
        loadEmails();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    /**
     * 获取xml里的账户信息
     */
    private void loadEmails() {
        try {
            InputStream e = mContext.getAssets().open("emails.xml");
            RootElement document = new RootElement("addrs");
            Element fromList = document.getChild("from-list");
            Element itemFrom = fromList.getChild("item");
            itemFrom.setElementListener(new ElementListener() {
                public void end() {
                    fromEmailList.add(mailInfo);
                }

                public void start(Attributes attributes) {
                    mailInfo = new AccountInfo();
                }
            });
            itemFrom.getChild("account").setEndTextElementListener(new EndTextElementListener() {
                public void end(String body) {
                    mailInfo.setAccount(body);
                }
            });
            itemFrom.getChild("password").setEndTextElementListener(new EndTextElementListener() {
                public void end(String body) {
                    mailInfo.setPassword(body);
                }
            });
            Element toList = document.getChild("to-list");
            Element itemTo = toList.getChild("item");
            itemTo.setEndTextElementListener(new EndTextElementListener() {
                public void end(String body) {
                    toEmailList.add(body);
                }
            });
            Xml.parse(new InputStreamReader(e), document.getContentHandler());
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }


    /**
     * 发生异常后的回调
     *
     * @param thread
     * @param throwable
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable throwable) {
        L.d(TAG, "......thread : " + thread + " , throwable : " + throwable);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        throwable.printStackTrace(ps);
        String errorMsg = new String(baos.toByteArray());
        final String mailContent = errorMsg + LINE_SEPARATOR + LINE_SEPARATOR + clientInfo;
        final String mailTitle = GlobalExceptionHandler.this.getPackageName() + "_v" + GlobalExceptionHandler.this.getVerName() + " Crash Report";
        L.d(TAG, "......mailContent......" + mailContent);
        L.d(TAG, "......mailTitle......" + mailTitle);
        if (NetworkUtil.isNetworkAvailable(mContext)) {
            send(mailContent, mailTitle);
        } else {
            L.d(TAG, "......发生异常......没有网......保存信息......");
            L.d(TAG, "....................................");
            L.d(TAG, "....................................");
            L.d(TAG, "....................................");
            L.d(TAG, "....................................");
            L.d(TAG, "....................................");
            Util.exitThisApp(mContext);
        }
    }

    /**
     * 发送邮件
     *
     * @param mailContent
     * @param mailTitle
     */
    private void send(final String mailContent, final String mailTitle) {
        new Thread() {
            @Override
            public void run() {
                MailInfo mailInfo = new MailInfo();
                int index = (int) Math.floor(Math.random() * (double) fromEmailList.size());
                AccountInfo info = fromEmailList.get(index);
                mailInfo.setFrom(info.getAccount());
                mailInfo.setPassword(info.getPassword());
                String[] tos = new String[toEmailList.size()];
                toEmailList.toArray(tos);
                mailInfo.setToList(tos);
                mailInfo.setSubject(mailTitle);
                mailInfo.setContent(mailContent);
                boolean result = MailUtil.getInstance().sendMail(mailInfo);
                if (result) {
                    FileUtils.deleteFile(FileUtils.getRecordFile());
                    Util.exitThisApp(mContext);
                } else {
                    send(mailContent, mailTitle);
                }
            }
        }.start();
    }


    //_____________________________________________________________________________


    /**
     * 获取版本信息
     *
     * @return
     */
    public String getVerName() {
        String verName = "";

        try {
            verName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException var3) {
            var3.printStackTrace();
        }

        return verName;
    }

    /**
     * 获取包名
     *
     * @return
     */
    public String getPackageName() {
        return mContext.getPackageName();
    }


    /**
     * 搜集问题机器的相关参数信息
     *
     * @return
     */
    private String collectClientInfo() {
        StringBuilder systemInfo = new StringBuilder();
        systemInfo.append("SS Code: ");
        systemInfo.append(SSUtil.getSSCodeNum());
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("SS Debug Mode: ");
        systemInfo.append(SSUtil.getDebugMode());
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("SS Product Version: ");
        systemInfo.append(SSUtil.getSSProductVersion());
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("CLIENT-INFO");
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Id: ");
        systemInfo.append(Build.ID);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Display: ");
        systemInfo.append(Build.DISPLAY);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Product: ");
        systemInfo.append(Build.PRODUCT);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Device: ");
        systemInfo.append(Build.DEVICE);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Board: ");
        systemInfo.append(Build.BOARD);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("CpuAbility: ");
        systemInfo.append(Build.CPU_ABI);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Manufacturer: ");
        systemInfo.append(Build.MANUFACTURER);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Brand: ");
        systemInfo.append(Build.BRAND);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Model: ");
        systemInfo.append(Build.MODEL);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Type: ");
        systemInfo.append(Build.TYPE);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Tags: ");
        systemInfo.append(Build.TAGS);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("FingerPrint: ");
        systemInfo.append(Build.FINGERPRINT);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Version.Incremental: ");
        systemInfo.append(Build.VERSION.INCREMENTAL);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Version.Release: ");
        systemInfo.append(Build.VERSION.RELEASE);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("SDK: ");
        systemInfo.append(Build.VERSION.SDK);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("SDKInt: ");
        systemInfo.append(Build.VERSION.SDK_INT);
        systemInfo.append(LINE_SEPARATOR);
        systemInfo.append("Version.CodeName: ");
        systemInfo.append(Build.VERSION.CODENAME);
        systemInfo.append(LINE_SEPARATOR);
        String clientInformation = systemInfo.toString();
        systemInfo.delete(0, systemInfo.length());
        return clientInformation;
    }
}
