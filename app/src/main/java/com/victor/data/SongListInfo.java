package com.victor.data;import java.io.Serializable;/** * 歌单信息 * Created by victor on 2016/07/06. */public class SongListInfo implements Serializable {    public String title;    /**     * #主打榜单     * 1.新歌榜     * 2.热歌榜     * #分类榜单     * 20.华语金曲榜     * 21.欧美金曲榜     * 24.影视金曲榜     * 23.情歌对唱榜     * 25.网络歌曲榜     * 22.经典老歌榜     * 11.摇滚榜     * #媒体榜单     * 6.KTV热歌榜     * 8.Billboard     * 18.Hito中文榜     * 7.叱咤歌曲榜     */    public String type;    public String music1;    public String music2;    public String music3;    public String pic_s640;    public String pic_s444;    public String pic_s260;    public int status;//http响应状态    public int msg;//http请求消息}