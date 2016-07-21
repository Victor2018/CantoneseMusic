package com.victor.module;import android.content.Context;import android.content.Intent;import com.victor.dao.DbDao;import com.victor.data.MusicData;import com.victor.util.Constant;import com.victor.util.SharePreferencesUtil;import java.util.ArrayList;import java.util.List;/** * Created by victor on 2016/7/15. */public class PlayModule {    private static Context mContext;    private static PlayModule mPlayModule;//    private boolean isPlayOnline;    public PlayModule (Context context) {        mContext = context;    }    public static PlayModule getInstance (Context context) {        if (mPlayModule == null) {            mPlayModule = new PlayModule(context);        }        return mPlayModule;    }    public void updateCurrentPlay(MusicData info,int playStatus) {        info.playStatus = playStatus;        DataObservable.getInstance().setData(info);    }    public boolean isPlayOnline () {        boolean isPlayOnline = SharePreferencesUtil.getBoolean(mContext,Constant.IS_PLAY_ONLINE_KEY);        return isPlayOnline;    }    public int getPositionById (int id) {        int position = 0;        List<MusicData> musicDatas = DbDao.getInstance(mContext).queryMusic(Constant.TB.MUSIC_ALL);        if (musicDatas != null && musicDatas.size() > 0) {            for (int i=0;i<musicDatas.size();i++) {                if (musicDatas.get(i).id == id) {                    position = i;                    break;                }            }        }        return position;    }    public void playOnline (MusicData data) {        SharePreferencesUtil.putBoolean(mContext,Constant.IS_PLAY_ONLINE_KEY,true);        List<MusicData> onlineList = new ArrayList<>();        onlineList.add(data);        DbDao.getInstance(mContext).insertMusics(onlineList,Constant.TB.MUSIC_ONLINE);        updateCurrentPlay(data,Constant.PlayStatus.PLAY);        SharePreferencesUtil.putString(mContext,Constant.PLAY_ONLINE_URL_KEY,data.data);        Intent intent = new Intent();        intent.setAction(Constant.SERVICE_ACTION);        intent.putExtra(Constant.ACTION_KEY, Constant.Action.PLAY_ONLINE);        intent.setPackage(mContext.getPackageName());        mContext.startService(intent);    }    public void play (int position) {        SharePreferencesUtil.putBoolean(mContext,Constant.IS_PLAY_ONLINE_KEY,false);        if (checkData(position)) {            Intent intent = new Intent();            intent.setAction(Constant.SERVICE_ACTION);            intent.putExtra(Constant.ACTION_KEY, Constant.Action.PLAY);            intent.setPackage(mContext.getPackageName());            mContext.startService(intent);        }    }    public void pause () {        Intent intent = new Intent();        intent.setAction(Constant.SERVICE_ACTION);        intent.putExtra(Constant.ACTION_KEY, Constant.Action.PAUSE);        intent.setPackage(mContext.getPackageName());        mContext.startService(intent);    }    public void prev () {        Intent intent = new Intent();        intent.setAction(Constant.SERVICE_ACTION);        intent.putExtra(Constant.ACTION_KEY, Constant.Action.PREV);        intent.setPackage(mContext.getPackageName());        mContext.startService(intent);    }    public void next () {        Intent intent = new Intent();        intent.setAction(Constant.SERVICE_ACTION);        intent.putExtra(Constant.ACTION_KEY, Constant.Action.NEXT);        intent.setPackage(mContext.getPackageName());        mContext.startService(intent);    }    public void stop () {        Intent intent = new Intent();        intent.setAction(Constant.SERVICE_ACTION);        intent.putExtra(Constant.ACTION_KEY, Constant.Action.STOP);        intent.setPackage(mContext.getPackageName());        mContext.startService(intent);    }    public boolean checkData (int position) {        boolean enable = false;        List<MusicData> musicDatas = DbDao.getInstance(mContext).queryMusic(Constant.TB.MUSIC_ALL);        if (musicDatas != null && musicDatas.size() > 0) {            if (position < musicDatas.size()) {                enable = true;                SharePreferencesUtil.putInt(mContext,Constant.CURRENT_POSITION_KEY,position);                updateCurrentPlay(musicDatas.get(position),Constant.PlayStatus.PLAY);            }        }        return enable;    }    public boolean isDownloadSidePlay () {        return SharePreferencesUtil.getBoolean(mContext,Constant.DOWNLOAD_SIDE_PLAY_KEY);    }}