package com.victor.util;import android.os.Environment;import android.text.TextUtils;import java.io.File;import java.util.regex.Matcher;import java.util.regex.Pattern;/** * Created by victor on 2016/7/8. */public class FileUtil {    public static String getMp3FileName(String artist, String title) {        artist = stringFilter(artist);        title = stringFilter(title);        if (TextUtils.isEmpty(artist)) {            artist = "未知";        }        if (TextUtils.isEmpty(title)) {            title = "未知";        }        return artist + " - " + title + Constant.FORMAT_MP3;    }    private static String getAppDir() {        return Environment.getExternalStorageDirectory() + "/CantoneseMusic/";    }    public static String getMusicDir() {        String dir = getAppDir() + "Music/";        return mkdirs(dir);    }    private static String mkdirs(String dir) {        File file = new File(dir);        if (!file.exists()) {            file.mkdirs();        }        return dir;    }    /**     * 过滤特殊字符(\/:*?"<>|)     */    private static String stringFilter(String str) {        if (str == null) {            return null;        }        String regEx = "[\\/:*?\"<>|]";        Pattern p = Pattern.compile(regEx);        Matcher m = p.matcher(str);        return m.replaceAll("").trim();    }    public static void delete(File file) {        if (file.isFile()) {            file.delete();            return;        }       if(file.isDirectory()){                File[] childFiles = file.listFiles();                if (childFiles == null || childFiles.length == 0) {                       file.delete();                        return;                    }                for (int i = 0; i < childFiles.length; i++) {                        delete(childFiles[i]);                    }                file.delete();            }        }}