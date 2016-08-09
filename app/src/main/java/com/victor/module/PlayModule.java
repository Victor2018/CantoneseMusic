package com.victor.module;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.victor.dao.DbDao;
import com.victor.data.MusicData;
import com.victor.util.Constant;
import com.victor.util.NetWorkUtils;
import com.victor.util.SharePreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 2016/7/15.
 */
public class PlayModule {
    private static Context mContext;
    private static PlayModule mPlayModule;

    public PlayModule (Context context) {
        mContext = context;
    }

    public static PlayModule getInstance (Context context) {
        if (mPlayModule == null) {
            mPlayModule = new PlayModule(context);
        }
        return mPlayModule;
    }

    /**
     * @return
     * 获取当前播放是否是在线音乐标示
     */
    public boolean isPlayOnline () {
        boolean isPlayOnline = SharePreferencesUtil.getBoolean(mContext,Constant.IS_PLAY_ONLINE_KEY);
        return isPlayOnline;
    }

    /**
     * @return
     * 获取当前播放器播放状态
     */
    public boolean isPlaying () {
        return MusicService.isPlaying;
    }

    /**
     * @param id
     * @return
     * 获取当前播放音乐的position
     */
    public int getPositionById (int id) {
        int position = 0;
        List<MusicData> musicDatas = DbDao.getInstance(mContext).queryMusic(Constant.TB.MUSIC_ALL);
        if (musicDatas != null && musicDatas.size() > 0) {
            for (int i=0;i<musicDatas.size();i++) {
                if (musicDatas.get(i).id == id) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    /**
     * @param data
     * 播放在线音乐
     */
    public void playOnline (MusicData data) {
        if (!NetWorkUtils.isConnected(mContext)) {
            Toast.makeText(mContext,"当前网络不可用，请检查网络是否连接！",Toast.LENGTH_SHORT).show();
            return;
        }
        SharePreferencesUtil.putBoolean(mContext,Constant.IS_PLAY_ONLINE_KEY,true);
        List<MusicData> onlineList = new ArrayList<>();
        onlineList.add(data);
        DbDao.getInstance(mContext).insertMusics(onlineList,Constant.TB.MUSIC_ONLINE);

        updateCurrentPlay(data,Constant.PlayStatus.PLAY);
        SharePreferencesUtil.putString(mContext,Constant.PLAY_ONLINE_URL_KEY,data.data);

        Intent intent = new Intent();
        intent.setAction(Constant.SERVICE_ACTION);
        intent.putExtra(Constant.ACTION_KEY, Constant.Action.PLAY_ONLINE);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
        showListFocus(-1);

    }

    /**
     * @param position
     * 播放本地歌曲
     */
    public void play (int position) {
        SharePreferencesUtil.putBoolean(mContext,Constant.IS_PLAY_ONLINE_KEY,false);
        if (checkData(position)) {
            Intent intent = new Intent();
            intent.setAction(Constant.SERVICE_ACTION);
            intent.putExtra(Constant.ACTION_KEY, Constant.Action.PLAY);
            intent.setPackage(mContext.getPackageName());
            mContext.startService(intent);
            showListFocus(position);
        }
    }

    public void pause () {
        Intent intent = new Intent();
        intent.setAction(Constant.SERVICE_ACTION);
        intent.putExtra(Constant.ACTION_KEY, Constant.Action.PAUSE);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
        showListFocus(-1);
    }

    /**
     * 上一曲
     */
    public void prev () {
        Intent intent = new Intent();
        intent.setAction(Constant.SERVICE_ACTION);
        intent.putExtra(Constant.ACTION_KEY, Constant.Action.PREV);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
    }

    /**
     * 下一曲
     */
    public void next () {
        Intent intent = new Intent();
        intent.setAction(Constant.SERVICE_ACTION);
        intent.putExtra(Constant.ACTION_KEY, Constant.Action.NEXT);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
    }

    /**
     * 停止播放
     */
    public void stop () {
        Intent intent = new Intent();
        intent.setAction(Constant.SERVICE_ACTION);
        intent.putExtra(Constant.ACTION_KEY, Constant.Action.STOP);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
    }

    /**
     * @param msec
     * 快进
     */
    public void seekTo (int msec) {
        Intent intent = new Intent();
        intent.setAction(Constant.SERVICE_ACTION);
        intent.putExtra(Constant.ACTION_KEY, Constant.Action.SEEK);
        intent.putExtra(Constant.SEEK_DURATION_KEY, msec);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
    }

    /**
     * @param position
     * @return
     * 校验播放数据
     */
    public boolean checkData (int position) {
        boolean enable = false;
        List<MusicData> musicDatas = DbDao.getInstance(mContext).queryMusic(Constant.TB.MUSIC_ALL);
        if (musicDatas != null && musicDatas.size() > 0) {
            if (position < musicDatas.size()) {
                enable = true;
                SharePreferencesUtil.putInt(mContext,Constant.CURRENT_POSITION_KEY,position);
                updateCurrentPlay(musicDatas.get(position),Constant.PlayStatus.PLAY);
            }
        }
        return enable;
    }

    /**
     * @return
     * 获取是否边下边播标示
     */
    public boolean isDownloadSidePlay () {
        return SharePreferencesUtil.getBoolean(mContext,Constant.DOWNLOAD_SIDE_PLAY_KEY);
    }

    /**
     * @param loopMode
     * 保存播放模式
     */
    public void putPlayMode (int loopMode) {
        SharePreferencesUtil.putInt(mContext,Constant.LOOP_MODE_KEY,loopMode);
    }

    /**
     * @return
     * 获取当前播放模式
     */
    public int getPlayMode () {
        return SharePreferencesUtil.getInt(mContext,Constant.LOOP_MODE_KEY);
    }


    /**
     * @param position
     * 刷新音乐列表播放位置
     */
    private void showListFocus (int position) {
        if (position == -1) {
            DataObservable.getInstance().setData(Constant.Action.CLEAR_LIST_FOCUS);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.ACTION_KEY,Constant.Action.UPDATE_CURRENT_POSITION);
            bundle.putInt(Constant.POSITION_KEY,position);
            DataObservable.getInstance().setData(bundle);
        }
    }

    /**
     * @param info
     * @param playStatus
     * 刷新当前播放音乐显示信息
     */
    public void updateCurrentPlay(MusicData info,int playStatus) {
        info.playStatus = playStatus;
        info.action = Constant.Action.SHOW_CURRENT_PLAY;
        DataObservable.getInstance().setData(info);
    }

}
