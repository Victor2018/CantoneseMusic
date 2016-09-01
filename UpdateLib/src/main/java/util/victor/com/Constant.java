package util.victor.com;

/**
 * Created by victor on 2016/04/07.
 */
public class Constant {

    public static final String STATUS_KEY                                   = "STATUS_KEY";
    public static final String REASON_KEY                                   = "REASON_KEY";
    public static final String REQUEST_MSG_KEY                              = "REQUEST_MSG_KEY";
    public static final String UPDATE_DATA_KEY                              = "UPDATE_DATA_KEY";

    public static final String UPDATE_URL = "https://raw.githubusercontent.com/Victor2018/FlowFunny/master/docs/update_json.txt";
    public static class Msg {
        public static final int REQUEST_SUCCESS                                 = 0x001;//请求成功
        public static final int REQUEST_SUCCESS_NO_DATA                        = 0x002;//请求成功，没有数据
        public static final int REQUEST_FAILED                                  = 0x003;//请求失败
        public static final int PARSING_EXCEPTION                               = 0x004;//数据解析异常
        public static final int NETWORK_ERROR                                    = 0x005;//网络错误
        public static final int SOCKET_TIME_OUT                                 = 0x006;//访问超时

        public static final int UPDATE_REQUEST                                  = 0x007;
        public static final int DOWNLOAD_OVER                                   = 0x008;
    }
}