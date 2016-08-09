package com.victor.util;/** * Created by victor on 2015/12/25. */public class Constant {    public static final boolean isDebug = true;    public static final String BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting?";    public static final String METHOD_MUSIC_PLAY                    = "baidu.ting.song.play";    public static final String METHOD_MUSIC_LRC                    = "baidu.ting.song.lry";    public static final String METHOD_MUSIC_LIST                    = "baidu.ting.billboard.billList";    public static final String METHOD_MUSIC_SEARCH                  = "baidu.ting.search.catalogSug";    public static final String CATEGORY_URL = BASE_URL + "method=%s&type=%s&size=%s";    public static final String MUSIC_URL = CATEGORY_URL + "&offset=%s";    public static final String MUSIC_PLAY_URL = BASE_URL + "method="+ METHOD_MUSIC_PLAY +"&songid=%s";    public static final String MUSIC_LRC_URL = BASE_URL + "method="+ METHOD_MUSIC_LRC +"&songid=%s";    public static final String MUSIC_SEARCH_URL = BASE_URL + "method="+ METHOD_MUSIC_SEARCH +"&query=%s";    public static final int PAGE_SIZE                                = 20;    public static final String INTENT_DATA                           = "INTENT_DATA";    public static final String DOWNLOAD_SIDE_PLAY_KEY               = "DOWNLOAD_SIDE_PLAY_KEY";    public static final String STATUS_KEY                            = "STATUS_KEY";    public static final String LOOP_MODE_KEY                         = "LOOP_MODE_KEY";    public static final String IS_PLAY_ONLINE_KEY                   = "IS_PLAY_ONLINE_KEY";    public static final String REQUEST_MSG_KEY                      = "REQUEST_MSG_KEY";    public static final String MUSIC_TYPE_KEY                       = "MUSIC_TYPE_KEY";    public static final String PAGE_SIZE_KEY                        = "PAGE_SIZE_KEY";    public static final String OFF_SET_KEY                           = "OFF_SET_KEY";    public static final String FORMAT_MP3                            = ".mp3";    public static final String MA_DATA                              = "madata";    public static final String CURRENT_POSITION_KEY                = "CURRENT_POSITION_KEY";    public static final String PLAY_ONLINE_URL_KEY                    = "PLAY_ONLINE_URL_KEY";    public static final String ACTION_KEY                              = "ACTION_KEY";    public static final String POSITION_KEY                              = "POSITION_KEY";    public static final String SEEK_DURATION_KEY                       = "SEEK_DURATION_KEY";    public static final String SERVICE_ACTION                          = "com.service.musicservice";    public static class Action {        public static final int PLAY                                                = 0x202;        public static final int PLAY_ONLINE                                        = 0x203;        public static final int PAUSE                                               = 0x204;        public static final int PREV                                                = 0x205;        public static final int NEXT                                                = 0x206;        public static final int STOP                                                = 0x207;        public static final int SEEK                                                = 0x208;        public static final int DOWNLOAD                                            = 0x209;        public static final int DOWNLOAD_OVER                                      = 0x210;        public static final int UPDATE_DOWNLOAD_LIST                              = 0x211;        public static final int CLEAR_ALL_MUSIC                                    = 0x212;        public static final int SHOW_CURRENT_PLAY                                 = 0x213;        public static final int CLEAR_LIST_FOCUS                                  = 0x214;        public static final int UPDATE_CURRENT_POSITION                          = 0x215;        public static final int SEARCH_MUSIC                                      = 0x216;    }    public static class PlayStatus {        public static final int PLAY                                                = 0x301;        public static final int PAUSE                                               = 0x302;        public static final int PREV                                                = 0x303;        public static final int NEXT                                                = 0x304;        public static final int STOP                                                = 0x305;    }    public static class Msg {        public static final int UPDATE_MUSIC_LIST                         = 0x101;        public static final int SEARCH_LOCAL_MUSIC                        = 0x102;        public static final int SEARCH_SUCCESS                            = 0x103;        public static final int SEARCH_ERROR                              = 0x104;        public static final int REQUEST_SUCCESS                           = 0x105;        public static final int REQUEST_FAILED                            = 0x106;        public static final int PARSING_EXCEPTION                         = 0x107;        public static final int SOCKET_TIME_OUT                           = 0x108;        public static final int NETWORK_ERROR                             = 0x109;        public static final int REQUEST_CATEGORY                          = 0x110;        public static final int REQUEST_MUSICS                            = 0x111;        public static final int REQUEST_MUSIC                             = 0x112;        public static final int UPDATE_DOWNLOAD_PROGRESS                 = 0x113;        public static final int SEARCH_MUSIC_ERROR                        = 0x114;        public static final int UPDATE_PLAY_PROGRESS                      = 0x115;        public static final int SHOW_PLAY_HOOLDE                          = 0x116;        public static final int PLAY_MUSIC                                 = 0x117;        public static final int EXIT_APP                                   = 0x118;    }    /**     * 数据库配置信息     * @author victor     * @date 2016-2-24     */    public static class DbConfig {        public static final String DB_NAME 						= "music_db";        public static final String SCHEME 						= "content://";        public static final String AUTHORITY 					= "content.music.content";        public static final String URI_PATH 					   = SCHEME + AUTHORITY + "/";        public static final int DB_VERSION 						= 6;    }    public static class TB {        public static final String MUSIC_ALL 					   = "music_all";        public static final String MUSIC_ONLINE 				       = "music_online";        public static final String MUSIC_CURRENT				       = "music_current";    }    public static class DataType {        public static final int MUSIC_LOCAL 						   = 0;        public static final int MUSIC_ONLINE 				       = 1;    }    public static class LoopMode {        public static final int LOOP_ALL 						   = 0;        public static final int LOOP_ONE 		   		           = 1;        public static final int LOOP_RANDOM  				       = 2;    }}