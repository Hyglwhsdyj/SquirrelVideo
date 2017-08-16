package com.songshu.squirrelvideo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.songshu.squirrelvideo.common.Const;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by yb on 15-7-4.
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName() + ":";

    /**
     * 获取每一个存储卡的位置
     *
     * @param context
     * @return
     */
    public static String getEverySDPath(Context context) {
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            String[] paths = (String[]) sm.getClass().getMethod("getVolumePaths", new  Class[ 0 ]).invoke(sm, new  Object[]{});
            StringBuilder builder = new StringBuilder();
            for (String str : paths) {
                File sdFile = new File(str);
                if (sdFile.canWrite() && sdFile.isDirectory() && sdFile.listFiles().length > 0) {
                    builder.append(str).append("&");
                }
            }
            return builder.toString();
        } catch (IllegalAccessException e) {
            L.d(TAG, "e:" + e);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            L.d(TAG, "e:" + e);
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            L.d(TAG, "e:" + e);
            e.printStackTrace();
        } catch (Exception e) {
            L.d(TAG, "e:" + e);
            e.printStackTrace();
        }
        return "";
    }

    private static File getSDCardRootFile() {
        File sdRoot = null;
        try {
            sdRoot = Environment.getExternalStorageDirectory().getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sdRoot;
    }

    private static File getAppRootFile() {
        File sdRoot = getSDCardRootFile();
        if (sdRoot != null) {
            File appRoot = new File(sdRoot, Const.APP_NAME);
            if (!appRoot.exists()) {
                appRoot.mkdirs();
            }
            return appRoot;
        } else {
            return null;
        }
    }

    private static File getAppUnderFile(String str) {
        File appRoot = getAppRootFile();
        if (appRoot != null) {
            File pRoot = new File(appRoot, str);
            if (!pRoot.exists()) {
                pRoot.mkdirs();
            }
            return pRoot;
        } else {
            return null;
        }
    }

    public static File getCacheRootFile() {
        return getAppUnderFile(Const.CACHE_NAME);
    }

    public static File getRetrofitSpiceCacheFile() {
        File cFile = getCacheRootFile();
        if (cFile != null) {
            File cache = new File(cFile, Const.CACHE_RETROFIT_NAME);
            if (!cache.exists()) {
                cache.mkdirs();
            }
            return cache;
        } else {
            return null;
        }
    }

    public static File getImageLoaderCacheFile() {
        File cFile = getCacheRootFile();
        if (cFile != null) {
            File cache = new File(cFile, Const.CACHE_IMAGE_LOADER_NAME);
            if (!cache.exists()) {
                cache.mkdirs();
            }
            return cache;
        } else {
            return null;
        }
    }

    public static File getDownloadManagerCacheFile() {
        File cFile = getCacheRootFile();
        if (cFile != null) {
            File cache = new File(cFile, Const.CACHE_DOWN_LOAD_MANAGER);
            if (!cache.exists()) {
                cache.mkdirs();
            }
            return cache;
        } else {
            return null;
        }
    }


    public static File getLocalMediaThumbnailCacheFile() {
        File cFile = getCacheRootFile();
        if (cFile != null) {
            File cache = new File(cFile, Const.CACHE_LOCAL_MEDIA_THUMBNAIL);
            if (!cache.exists()) {
                cache.mkdirs();
            }
            return cache;
        } else {
            return null;
        }
    }


    public static File getLogCacheFile() {
        File cFile = getCacheRootFile();
        if (cFile != null) {
            File cache = new File(cFile, Const.CACHE_LOG);
            if (!cache.exists()) {
                cache.mkdirs();
            }
            return cache;
        } else {
            return null;
        }
    }

    public static String getRecordFile() {
        try {
            String recordFilePath = getLogCacheFile().getAbsolutePath()
                    + "/squirrel_v_log_"
                    + TimeUtils.getCurrentTimeInString(TimeUtils.DATE_FORMAT_DATE);
            File newRecordFile = new File(recordFilePath);
            if (!newRecordFile.exists()) {
                return newRecordFile.createNewFile() ? recordFilePath : "";
            } else {
                return recordFilePath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void recordMessage(String strMsg) {
        strMsg = TimeUtils.getCurrentTimeInString(TimeUtils.DEFAULT_DATE_FORMAT_NOMILL) + " [ " + strMsg + " ] \r\n";
        try {
            FileWriter writer = new FileWriter(getRecordFile(), true);
            writer.write(strMsg);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ===================================================================================================
    // ===================================================================================================
    // ===================================================================================================


    private static HashMap<String, String> mMimeType = new HashMap<String, String>();

    static {
        mMimeType.put("M1V", "video/mpeg");
        mMimeType.put("MP2", "video/mpeg");
        mMimeType.put("MPE", "video/mpeg");
        mMimeType.put("MPG", "video/mpeg");
        mMimeType.put("MPEG", "video/mpeg");
        mMimeType.put("MP4", "video/mp4");
        mMimeType.put("M4V", "video/mp4");
        mMimeType.put("3GP", "video/3gpp");
        mMimeType.put("3GPP", "video/3gpp");
        mMimeType.put("3G2", "video/3gpp2");
        mMimeType.put("3GPP2", "video/3gpp2");
        mMimeType.put("MKV", "video/x-matroska");
        mMimeType.put("WEBM", "video/x-matroska");
        mMimeType.put("MTS", "video/mp2ts");
        mMimeType.put("TS", "video/mp2ts");
        mMimeType.put("TP", "video/mp2ts");
        mMimeType.put("WMV", "video/x-ms-wmv");
        mMimeType.put("ASF", "video/x-ms-asf");
        mMimeType.put("ASX", "video/x-ms-asf");
        mMimeType.put("FLV", "video/x-flv");
        mMimeType.put("MOV", "video/quicktime");
        mMimeType.put("QT", "video/quicktime");
        mMimeType.put("RM", "video/x-pn-realvideo");
        mMimeType.put("RMVB", "video/x-pn-realvideo");
        mMimeType.put("VOB", "video/dvd");
        mMimeType.put("DAT", "video/dvd");
        mMimeType.put("AVI", "video/x-divx");
        mMimeType.put("OGV", "video/ogg");
        mMimeType.put("OGG", "video/ogg");
        mMimeType.put("VIV", "video/vnd.vivo");
        mMimeType.put("VIVO", "video/vnd.vivo");
        mMimeType.put("WTV", "video/wtv");
        mMimeType.put("AVS", "video/avs-video");
        mMimeType.put("SWF", "video/x-shockwave-flash");
        mMimeType.put("YUV", "video/x-raw-yuv");
    }

    /**
     * 获取文件MIME
     */
    public static String getMimeType(String path) {
        int lastDot = path.lastIndexOf(".");
        if (lastDot < 0)
            return null;

        return mMimeType.get(path.substring(lastDot + 1).toUpperCase());
    }

    /**
     * 获取文件canonical路径
     *
     * @param f
     * @return
     */
    public static String getCanonical(File f) {
        if (f == null)
            return null;
        try {
            return f.getCanonicalPath();
        } catch (IOException e) {
            return f.getAbsolutePath();
        }
    }

    private static final double KB = 1024.0;
    private static final double MB = KB * KB;
    private static final double GB = KB * KB * KB;

    /**
     * 获取文件大小
     *
     * @param size
     * @return
     */
    public static String showFileSize(long size) {
        String fileSize;
        if (size < KB)
            fileSize = size + "B";
        else if (size < MB)
            fileSize = String.format("%.1f", size / KB) + "KB";
        else if (size < GB)
            fileSize = String.format("%.1f", size / MB) + "MB";
        else
            fileSize = String.format("%.1f", size / GB) + "GB";

        return fileSize;
    }

    /**
     * 如果不存在就创建
     */
    public static boolean createIfNoExists(String path) {
        File file = new File(path);
        boolean mk = false;
        if (!file.exists()) {
            mk = file.mkdirs();
        }
        return mk;
    }

    /**
     * 获取不带后缀的文件名
     *
     * @param filename
     * @return
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * 获取文件后缀
     */
    public static String getFileExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase();
            }
        }
        return null;
    }

    // http://www.fileinfo.com/filetypes/video , "dat" , "bin" , "rms"
    public static final String[] VIDEO_EXTENSIONS = {"264", "3g2", "3gp",
            "3gp2", "3gpp", "3gpp2", "3mm", "3p2", "60d"/*, "aep"*/, /*"ajp",*/ "amv",
            "amx", "arf", "asf", "asx", "avb", "avd", "avi", "avs", "avs",
            "axm", "bdm", "bdmv", "bik", "bix", "bmk", "box", "bs4", "bsf",
            "byu", "camre", "clpi", "cpi", "cvc", "d2v", "d3v", "dav", "dce",
            "dck", "ddat", "dif", "dir", "divx", "dlx", "dmb", "dmsm", "dmss",
            "dnc", "dpg", "dream", "dsy", "dv", "dv-avi", "dv4", "dvdmedia",
            "dvr-ms", "dvx", "dxr", "dzm", "dzp", "dzt", "evo", "eye", "f4p",
            "f4v", "fbr", "fbr", "fbz", "fcp", "flc", "flh", "fli", "flv",
            "flx", "gl", "grasp", "gts", "gvi", "gvp", "hdmov", "hkm", "ifo",
            "imovi", "imovi", "iva", "ivf", "ivr", "ivs", "izz", "izzy", "jts",
            "lsf", "lsx", "m15", "m1pg", "m1v", "m21", "m21", "m2a", "m2p",
            "m2t", "m2ts", "m2v", "m4e", "m4u", "m4v", "m75", "meta", "mgv",
            "mj2", "mjp", "mjpg", "mkv", "mmv", "mnv", "mod", "modd", "moff",
            "moi", "moov", "mov", "movie", "mp21", "mp21", "mp2v", "mp4",
            "mp4v", "mpe", "mpeg", "mpeg4", "mpf", "mpg", "mpg2", "mpgin",
            "mpl", "mpls", "mpv", "mpv2", "mqv", "msdvd", "msh", "mswmm",
            "mts", "mtv", "mvb", "mvc", "mvd", "mve", "mvp", "mxf", "mys",
            "ncor", "nsv", "nvc", "ogm", "ogv", "ogx", "osp", "par", "pds",
            "pgi", "piv", "playlist", "pmf", "prel", "pro", "prproj", "psh",
            "pva", "pvr", "pxv", "qt", "qtch", "qtl", "qtm", "qtz",
            "rcproject", "rdb", "rec", "rm", "rmd", "rmp", "rmvb", "roq", "rp",
            "rts", "rts", "rum", "rv", "sbk", "sbt", "scm", "scm", "scn",
            "sec", "seq", "sfvidcap", "smil", "smk", "sml", "smv", "spl",
            "ssm", "str", "stx", "svi", "swf", "swi", "swt", "tda3mt", "tivo",
            "tix", "tod", "tp", "tp0", "tpd", "tpr", "trp", "ts", "tvs", "vc1",
            "vcr", "vcv", "vdo", "vdr", "veg", "vem", "vf", "vfw", "vfz",
            "vgz", "vid", "viewlet", "viv", "vivo", "vlab", "vob", "vp3",
            "vp6", "vp7", "vpj", "vro", "vsp", "w32", "wcp", "webm", "wm",
            "wmd", "wmmp", "wmv", "wmx", "wp3", "wpl", "wtv", "wvx", "xfl",
            "xvid", "yuv", "zm1", "zm2", "zm3", "zmv"};
    // http://www.fileinfo.com/filetypes/audio , "spx" , "mid" , "sf"
    public static final String[] AUDIO_EXTENSIONS = {"4mp", "669", "6cm",
            "8cm", "8med", "8svx", "a2m", "aa", "aa3", "aac", "aax", "abc",
            "abm", "ac3", "acd", "acd-bak", "acd-zip", "acm", "act", "adg",
            "afc", "agm", "ahx", "aif", "aifc", "aiff", "ais", "akp", "al",
            "alaw", "all", "amf", "amr", "ams", "ams", "aob", "ape", "apf",
            "apl", "ase", "at3", "atrac", "au", "aud", "aup", "avr", "awb",
            "band", "bap", "bdd", "box", "bun", "bwf", "c01", "caf", "cda",
            "cdda", "cdr", "cel", "cfa", "cidb", "cmf", "copy", "cpr", "cpt",
            "csh", "cwp", "d00", "d01", "dcf", "dcm", "dct", "ddt", "dewf",
            "df2", "dfc", "dig", "dig", "dls", "dm", "dmf", "dmsa", "dmse",
            "drg", "dsf", "dsm", "dsp", "dss", "dtm", "dts", "dtshd", "dvf",
            "dwd", "ear", "efa", "efe", "efk", "efq", "efs", "efv", "emd",
            "emp", "emx", "esps", "f2r", "f32", "f3r", "f4a", "f64", "far",
            "fff", "flac", "flp", "fls", "frg", "fsm", "fzb", "fzf", "fzv",
            "g721", "g723", "g726", "gig", "gp5", "gpk", "gsm", "gsm", "h0",
            "hdp", "hma", "hsb", "ics", "iff", "imf", "imp", "ins", "ins",
            "it", "iti", "its", "jam", "k25", "k26", "kar", "kin", "kit",
            "kmp", "koz", "koz", "kpl", "krz", "ksc", "ksf", "kt2", "kt3",
            "ktp", "l", "la", "lqt", "lso", "lvp", "lwv", "m1a", "m3u", "m4a",
            "m4b", "m4p", "m4r", "ma1", "mdl", "med", "mgv", "midi", "miniusf",
            "mka", "mlp", "mmf", "mmm", "mmp", "mo3", "mod", "mp1", "mp2",
            "mp3", "mpa", "mpc", "mpga", "mpu", "mp_", "mscx", "mscz", "msv",
            "mt2", "mt9", "mte", "mti", "mtm", "mtp", "mts", "mus", "mws",
            "mxl", "mzp", "nap", "nki", "nra", "nrt", "nsa", "nsf", "nst",
            "ntn", "nvf", "nwc", "odm", "oga", "ogg", "okt", "oma", "omf",
            "omg", "omx", "ots", "ove", "ovw", "pac", "pat", "pbf", "pca",
            "pcast", "pcg", "pcm", "peak", "phy", "pk", "pla", "pls", "pna",
            "ppc", "ppcx", "prg", "prg", "psf", "psm", "ptf", "ptm", "pts",
            "pvc", "qcp", "r", "r1m", "ra", "ram", "raw", "rax", "rbs", "rcy",
            "rex", "rfl", "rmf", "rmi", "rmj", "rmm", "rmx", "rng", "rns",
            "rol", "rsn", "rso", "rti", "rtm", "rts", "rvx", "rx2", "s3i",
            "s3m", "s3z", "saf", "sam", "sb", "sbg", "sbi", "sbk", "sc2", "sd",
            "sd", "sd2", "sd2f", "sdat", "sdii", "sds", "sdt", "sdx", "seg",
            "seq", "ses", "sf2", "sfk", "sfl", "shn", "sib", "sid", "sid",
            "smf", "smp", "snd", "snd", "snd", "sng", "sng", "sou", "sppack",
            "sprg", "sseq", "sseq", "ssnd", "stm", "stx", "sty", "svx", "sw",
            "swa", "syh", "syw", "syx", "td0", "tfmx", "thx", "toc", "tsp",
            "txw", "u", "ub", "ulaw", "ult", "ulw", "uni", "usf", "usflib",
            "uw", "uwf", "vag", "val", "vc3", "vmd", "vmf", "vmf", "voc",
            "voi", "vox", "vpm", "vqf", "vrf", "vyf", "w01", "wav", "wav",
            "wave", "wax", "wfb", "wfd", "wfp", "wma", "wow", "wpk", "wproj",
            "wrk", "wus", "wut", "wv", "wvc", "wve", "wwu", "xa", "xa", "xfs",
            "xi", "xm", "xmf", "xmi", "xmz", "xp", "xrns", "xsb", "xspf", "xt",
            "xwb", "ym", "zvd", "zvr"};

    private static final HashSet<String> mHashVideo;
    private static final HashSet<String> mHashAudio;

    static {
        mHashVideo = new HashSet<String>(Arrays.asList(VIDEO_EXTENSIONS));
        mHashAudio = new HashSet<String>(Arrays.asList(AUDIO_EXTENSIONS));
    }

    public static String getUrlFileName(String url) {
        int slashIndex = url.lastIndexOf('/');
        int dotIndex = url.lastIndexOf('.');
        String filenameWithoutExtension;
        if (dotIndex == -1) {
            filenameWithoutExtension = url.substring(slashIndex + 1);
        } else {
            filenameWithoutExtension = url.substring(slashIndex + 1, dotIndex);
        }
        return filenameWithoutExtension;
    }

    public static String getUrlExtension(String url) {
        if (!StringUtils.isEmpty(url)) {
            int i = url.lastIndexOf('.');
            if (i > 0 && i < url.length() - 1) {
                return url.substring(i + 1).toLowerCase();
            }
        }
        return "";
    }

    /**
     * 是否是音频或者视频
     */
    public static boolean isVideoOrAudio(File f) {
        final String ext = getFileExtension(f);
        return mHashVideo.contains(ext) || mHashAudio.contains(ext);
    }

    public static boolean isVideoOrAudio(String f) {
        final String ext = getUrlExtension(f);
        return mHashVideo.contains(ext) || mHashAudio.contains(ext);
    }

    public static boolean isVideo(File f) {
        final String ext = getFileExtension(f);
        return mHashVideo.contains(ext);
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        FileInputStream fis = null;
        if (file.exists()) {
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            L.d(TAG, "获取文件大小 , 文件不存在!");
        }
        fis.close();
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.d(TAG, "获取文件大小 , 获取失败!");
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 调用此方法自动计算指定文件或指定文件夹的大小
     *
     * @param filePath 文件路径
     * @return 计算好的带B、KB、MB、GB的字符串
     */
    public static String getAutoFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.d(TAG, "获取文件大小 , 获取失败!");
        }
        return FormetFileSize(blockSize);
    }


    // ==============================================================================
    // ==============================================================================
    // ==============================================================================


    public final static String FILE_EXTENSION_SEPARATOR = ".";

    /**
     * read file
     *
     * @param filePath
     * @param charsetName The device of a supported {@link java.nio.charset.Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static StringBuilder readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * write file
     *
     * @param filePath
     * @param content
     * @param append   is append, if true, write to the end of file, else clear content of file and write into it
     * @return return false if content is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * write file
     *
     * @param filePath
     * @param contentList
     * @param append      is append, if true, write to the end of file, else clear content of file and write into it
     * @return return false if contentList is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, List<String> contentList, boolean append) {
        if (ListUtils.isEmpty(contentList)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            int i = 0;
            for (String line : contentList) {
                if (i++ > 0) {
                    fileWriter.write("\r\n");
                }
                fileWriter.write(line);
            }
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * write file, the string will be written to the begin of the file
     *
     * @param filePath
     * @param content
     * @return
     */
    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    /**
     * write file, the string list will be written to the begin of the file
     *
     * @param filePath
     * @param contentList
     * @return
     */
    public static boolean writeFile(String filePath, List<String> contentList) {
        return writeFile(filePath, contentList, false);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param filePath
     * @param stream
     * @return
     * @see {@link #writeFile(String, InputStream, boolean)}
     */
    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    /**
     * write file
     *
     * @param filePath the file to be opened for writing.
     * @param stream   the input stream
     * @param append   if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream, append);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param file
     * @param stream
     * @return
     * @see {@link #writeFile(File, InputStream, boolean)}
     */
    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (o != null) {
                try {
                    o.close();
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * copy file
     *
     * @param sourceFilePath
     * @param destFilePath
     * @return
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean copyFile(String sourceFilePath, String destFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        }
        return writeFile(destFilePath, inputStream);
    }

    /**
     * read file to string list, a element of list is a line
     *
     * @param filePath
     * @param charsetName The device of a supported {@link java.nio.charset.Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static List<String> readFileToList(String filePath, String charsetName) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * get file device from path, not include suffix
     * <p/>
     * <pre>
     *      getFileNameWithoutExtension(null)               =   null
     *      getFileNameWithoutExtension("")                 =   ""
     *      getFileNameWithoutExtension("   ")              =   "   "
     *      getFileNameWithoutExtension("abc")              =   "abc"
     *      getFileNameWithoutExtension("a.mp3")            =   "a"
     *      getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
     *      getFileNameWithoutExtension("c:\\")              =   ""
     *      getFileNameWithoutExtension("c:\\a")             =   "a"
     *      getFileNameWithoutExtension("c:\\a.b")           =   "a"
     *      getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
     *      getFileNameWithoutExtension("/home/admin")      =   "admin"
     *      getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
     * </pre>
     *
     * @param filePath
     * @return file device from path, not include suffix
     * @see
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
    }

    /**
     * get file device from path, include suffix
     * <p/>
     * <pre>
     *      getFileName(null)               =   null
     *      getFileName("")                 =   ""
     *      getFileName("   ")              =   "   "
     *      getFileName("a.mp3")            =   "a.mp3"
     *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
     *      getFileName("abc")              =   "abc"
     *      getFileName("c:\\")              =   ""
     *      getFileName("c:\\a")             =   "a"
     *      getFileName("c:\\a.b")           =   "a.b"
     *      getFileName("c:a.txt\\a")        =   "a"
     *      getFileName("/home/admin")      =   "admin"
     *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
     * </pre>
     *
     * @param filePath
     * @return file device from path, include suffix
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    /**
     * get folder device from path
     * <p/>
     * <pre>
     *      getFolderName(null)               =   null
     *      getFolderName("")                 =   ""
     *      getFolderName("   ")              =   ""
     *      getFolderName("a.mp3")            =   ""
     *      getFolderName("a.b.rmvb")         =   ""
     *      getFolderName("abc")              =   ""
     *      getFolderName("c:\\")              =   "c:"
     *      getFolderName("c:\\a")             =   "c:"
     *      getFolderName("c:\\a.b")           =   "c:"
     *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
     *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
     *      getFolderName("/home/admin")      =   "/home"
     *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
     * </pre>
     *
     * @param filePath
     * @return
     */
    public static String getFolderName(String filePath) {

        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    /**
     * get suffix of file from path
     * <p/>
     * <pre>
     *      getFileExtension(null)               =   ""
     *      getFileExtension("")                 =   ""
     *      getFileExtension("   ")              =   "   "
     *      getFileExtension("a.mp3")            =   "mp3"
     *      getFileExtension("a.b.rmvb")         =   "rmvb"
     *      getFileExtension("abc")              =   ""
     *      getFileExtension("c:\\")              =   ""
     *      getFileExtension("c:\\a")             =   ""
     *      getFileExtension("c:\\a.b")           =   "b"
     *      getFileExtension("c:a.txt\\a")        =   ""
     *      getFileExtension("/home/admin")      =   ""
     *      getFileExtension("/home/admin/a.txt/b")  =   ""
     *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
     * </pre>
     *
     * @param filePath
     * @return
     */
    public static String getFileExtension(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    /**
     * Creates the directory named by the trailing filename of this file, including the complete directory path required
     * to create this directory. <br/>
     *
     * @param filePath
     * @return true if the necessary directories have been created or the target directory already exists, false one of
     * the directories can not be created.
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (StringUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    /**
     * @param filePath
     * @return
     * @see #makeDirs(String)
     */
    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    /**
     * Indicates if this file represents a file on the underlying file system.
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    public static boolean isFileAvailable(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile() && file.length() > 0);
    }

    /**
     * Indicates if this file represents a directory on the underlying file system.
     *
     * @param directoryPath
     * @return
     */
    public static boolean isFolderExist(String directoryPath) {
        if (StringUtils.isBlank(directoryPath)) {
            return false;
        }

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * delete file or directory
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        if (StringUtils.isBlank(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    /**
     * get file size
     *
     * @param path
     * @return returns the length of this file in bytes. returns -1 if the file does not exist.
     */
    public static long getFileSize(String path) {
        if (StringUtils.isBlank(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    public static void fileScan(Context mContext, String img_save_path) {
        Uri data = Uri.parse("file://" + img_save_path);
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }

}
