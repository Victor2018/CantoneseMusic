package com.victor.data;import java.io.Serializable;/** * Created by victor on 2016/7/6. */public class MusicData implements Serializable{    public int id;    public long duration;    public String displayName;    public String data;    public String path;    public String albumId;    public String album;    public String pic_big;//海报大图片    public String pic_small;//海报小图片    public String lrclink;//歌词播放地址    public String song_id;//歌曲id    public String title;//歌曲名称    public String artist;//歌唱家    public int file_size;//文件大小    public String file_extension;//格式    public String show_link;//播放地址    public String file_link;//播放地址    public String artist_id;//艺术家id    public int action;//操作action    public int playStatus;//播放状态    public int dataType;//数据类型 0 本地，1在线    public int status;//http响应状态    public int msg;//http请求消息    public int current;//当前下载进度或当前播放进度    public String sortLetters = "";  //显示数据拼音的首字母}