package com.victor.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.victor.dao.DbDao;
import com.victor.data.MusicData;
import com.victor.data.SongListInfo;
import com.victor.interfaces.HttpRequestListener;
import com.victor.module.DataObservable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 2016/1/21.
 */
public class HttpActions {

    private static String TAG = "HttpActions";

    public static void requestCategory (String url,Context context){
        Loger.e(TAG,"requestCategory()......url = " + url);
        int status = 0;
        SongListInfo info  = new SongListInfo();
        if (HttpUtil.isNetEnable(context)){
            try {
                String result = HttpUtil.HttpGetRequest(url);
                info = JsonParser.parseCategory(result.toString());
                if (info != null) {
                    status = Constant.Msg.REQUEST_SUCCESS;
                } else {
                    status = Constant.Msg.REQUEST_FAILED;
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                status = Constant.Msg.SOCKET_TIME_OUT;
            } catch (IOException e) {
                e.printStackTrace();
                status = Constant.Msg.REQUEST_FAILED;
            } catch (JSONException e) {
                e.printStackTrace();
                status = Constant.Msg.PARSING_EXCEPTION;
            }
        } else {
            status = Constant.Msg.NETWORK_ERROR;
        }
        info.status = status;
        info.msg = Constant.Msg.REQUEST_CATEGORY;
        DataObservable.getInstance().setData(info);
    }

    public static void requestMusics (String url,Context context){
        Loger.e(TAG,"requestMusics()......url = " + url);
        int status = 0;
        Bundle responseData = new Bundle();
        if (HttpUtil.isNetEnable(context)){
            try {
                String result = HttpUtil.HttpGetRequest(url);
                List<MusicData> musicDatas = JsonParser.parseMusics(result.toString());
                if (musicDatas != null && musicDatas.size() > 0) {
                    status = Constant.Msg.REQUEST_SUCCESS;
                    responseData.putSerializable(Constant.INTENT_DATA, (Serializable) musicDatas);
                    DbDao.getInstance(context).insertMusics(musicDatas,Constant.TB.MUSIC_ALL);
                    DbDao.getInstance(context).insertMusics(musicDatas,Constant.TB.MUSIC_CURRENT);
                } else {
                    status = Constant.Msg.REQUEST_FAILED;
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                status = Constant.Msg.SOCKET_TIME_OUT;
            } catch (IOException e) {
                e.printStackTrace();
                status = Constant.Msg.REQUEST_FAILED;
            } catch (JSONException e) {
                e.printStackTrace();
                status = Constant.Msg.PARSING_EXCEPTION;
            }
        } else {
            status = Constant.Msg.NETWORK_ERROR;
        }
        responseData.putInt(Constant.STATUS_KEY,status);
        responseData.putInt(Constant.REQUEST_MSG_KEY,Constant.Msg.REQUEST_MUSICS);
        DataObservable.getInstance().setData(responseData);
    }
    public static void requestMusic (String url,Context context){
        Loger.e(TAG,"requestMusic()......url = " + url);
        int status = 0;
        MusicData musicData = new MusicData();
        if (HttpUtil.isNetEnable(context)){
            try {
                String result = HttpUtil.HttpGetRequest(url);
                Loger.e(TAG,"result = " + result);
                musicData = JsonParser.parseMusic(result.toString());
                if (musicData != null) {
                    status = Constant.Msg.REQUEST_SUCCESS;
                } else {
                    status = Constant.Msg.REQUEST_FAILED;
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                status = Constant.Msg.SOCKET_TIME_OUT;
            } catch (IOException e) {
                e.printStackTrace();
                status = Constant.Msg.REQUEST_FAILED;
            } catch (JSONException e) {
                e.printStackTrace();
                status = Constant.Msg.PARSING_EXCEPTION;
            }
        } else {
            status = Constant.Msg.NETWORK_ERROR;
        }
        musicData.status = status;
        musicData.msg = Constant.Msg.REQUEST_MUSIC;
        DataObservable.getInstance().setData(musicData);
    }

    public static void searchLocalMusic (Context context) {
        Loger.e(TAG,"searchLocalMusic()......");
        int status = 0;
        Bundle responseData = new Bundle();
        List<MusicData> musicDatas = MusicUtil.getAllSongs(context);
        if (musicDatas != null && musicDatas.size() > 0) {
            status = Constant.Msg.SEARCH_SUCCESS;
        } else {
            status = Constant.Msg.SEARCH_ERROR;
        }

        responseData.putSerializable(Constant.INTENT_DATA, (Serializable) musicDatas);
        responseData.putInt(Constant.STATUS_KEY, status);
        responseData.putInt(Constant.REQUEST_MSG_KEY, Constant.Msg.SEARCH_LOCAL_MUSIC);
        DataObservable.getInstance().setData(responseData);
    }


}
