package com.victor.module;

import android.content.Context;
import android.content.Intent;

import com.victor.dao.DbDao;
import com.victor.data.MusicData;
import com.victor.util.Constant;
import com.victor.util.SharePreferencesUtil;

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

    public void playOnline (String url) {
        SharePreferencesUtil.putString(mContext,Constant.PLAY_ONLINE_URL_KEY,url);
        Intent intent = new Intent();
        intent.setAction(Constant.SERVICE_ACTION);
        intent.putExtra(Constant.ACTION_KEY, Constant.Action.PLAY_ONLINE);
        intent.putExtra(Constant.DATA_TYPE_KEY, Constant.DateType.ONLINE);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
    }

    public void play (int position) {
        if (checkData(position)) {
            Intent intent = new Intent();
            intent.setAction(Constant.SERVICE_ACTION);
            intent.putExtra(Constant.ACTION_KEY, Constant.Action.PLAY);
            intent.putExtra(Constant.DATA_TYPE_KEY, Constant.DateType.LOCAL);
            intent.setPackage(mContext.getPackageName());
            mContext.startService(intent);
        }
    }

    public void pause () {
        Intent intent = new Intent();
        intent.setAction(Constant.SERVICE_ACTION);
        intent.putExtra(Constant.ACTION_KEY, Constant.Action.PAUSE);
        intent.putExtra(Constant.DATA_TYPE_KEY, Constant.DateType.LOCAL);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
    }

    public void prev () {
        Intent intent = new Intent();
        intent.setAction(Constant.SERVICE_ACTION);
        intent.putExtra(Constant.ACTION_KEY, Constant.Action.PREV);
        intent.putExtra(Constant.DATA_TYPE_KEY, Constant.DateType.LOCAL);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
    }

    public void next () {
        Intent intent = new Intent();
        intent.setAction(Constant.SERVICE_ACTION);
        intent.putExtra(Constant.ACTION_KEY, Constant.Action.NEXT);
        intent.putExtra(Constant.DATA_TYPE_KEY, Constant.DateType.LOCAL);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
    }

    public void stop () {
        Intent intent = new Intent();
        intent.setAction(Constant.SERVICE_ACTION);
        intent.putExtra(Constant.ACTION_KEY, Constant.Action.STOP);
        intent.putExtra(Constant.DATA_TYPE_KEY, Constant.DateType.LOCAL);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
    }

    public boolean checkData (int position) {
        boolean enable = false;
        List<MusicData> musicDatas = DbDao.getInstance(mContext).queryMusic(Constant.TB.MUSIC_CURRENT);
        if (musicDatas != null && musicDatas.size() > 0) {
            if (position < musicDatas.size()) {
                enable = true;
                SharePreferencesUtil.putInt(mContext,Constant.CURRENT_POSITION_KEY,position);
            }
        }
        return enable;
    }

}
