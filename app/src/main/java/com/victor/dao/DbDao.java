package com.victor.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.victor.data.MusicData;
import com.victor.db.DataBase;
import com.victor.interfaces.DbInterface;
import com.victor.music.R;
import com.victor.util.Constant;
import com.victor.util.Loger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 2016/6/13.
 */
public class DbDao implements DbInterface{
    private String TAG = "DbDao";
    private Context mContext;
    private DataBase db;
    private static DbDao dbDao;

    public DbDao (Context context){
        mContext = context;
        db = new DataBase(mContext, null, null, 0);
    }
    public static DbDao getInstance (Context context) {
        if (dbDao == null) {
            dbDao = new DbDao(context);
        }
        return dbDao;
    }


    @Override
    public void clearTb(String tbName) {
        Loger.e(TAG, "clearTb()......tbName = " + tbName);
        if (!db.tabbleIsExist(tbName)){
            return;
        }
        SQLiteDatabase sdb = db.getReadableDatabase();
        try {
            sdb.delete(tbName, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sdb != null) {
                sdb.close();
            }
        }
    }

    @Override
    public void insertMusics(List<MusicData> musicDatas,String tbName) {
        Loger.e(TAG, "insertMusics()......tbName = " + tbName);
        if (!db.tabbleIsExist(tbName)){
            return;
        }
        clearTb(tbName);//清空数据
        long startTime = System.currentTimeMillis();
        if (musicDatas != null && musicDatas.size() > 0){
            SQLiteDatabase sdb = db.getWritableDatabase();
            sdb.beginTransaction();
            try {
                for(MusicData info:musicDatas){
                    ContentValues values = new ContentValues();
                    values.put("title", info.title);
                    values.put("duration", info.duration);
                    values.put("artist", info.artist);
                    values.put("id", info.id);
                    values.put("displayName", info.displayName);
                    values.put("data", info.data);
                    values.put("path", info.path);
                    values.put("albumId", info.albumId);
                    values.put("album", info.album);
                    values.put("size", info.file_size);

                    sdb.insert(tbName, null, values);
                }
                sdb.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (sdb != null) {
                    sdb.endTransaction();
                    sdb.close();
                }
            }

        }
    }

    @Override
    public void removeMusic(String tbName,int id) {
        Loger.e(TAG, "removeMusic()......tbName = " + tbName);
        Loger.e(TAG, "removeMusic()......id = " + id);
        if (!db.tabbleIsExist(tbName)){
            return;
        }
        SQLiteDatabase sdb = db.getReadableDatabase();
        try {
            sdb.delete(tbName, "id = ?",  new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (sdb != null) {
                sdb.close();
            }
        }
    }

    @Override
    public List<MusicData> queryMusic(String tbName) {
        Loger.e(TAG, "queryMusic()......tbName = " + tbName);
        List<MusicData> musicDatas = new ArrayList<>();
        if (!db.tabbleIsExist(tbName)){
            return musicDatas;
        }
        Uri uri = Uri.parse(Constant.DbConfig.URI_PATH + tbName);

        Cursor cursor = mContext.getContentResolver().query(uri,
                null, null , null, null);
        try {
            int row = cursor.getCount();
            if (row > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    MusicData info = new MusicData();
                    info.title = cursor.getString(cursor.getColumnIndex("title"));
                    info.duration = cursor.getLong(cursor.getColumnIndex("duration"));
                    info.artist = cursor.getString(cursor.getColumnIndex("artist"));
                    info.id = cursor.getInt(cursor.getColumnIndex("id"));
                    info.displayName = cursor.getString(cursor.getColumnIndex("displayName"));
                    info.data = cursor.getString(cursor.getColumnIndex("data"));
                    info.path = cursor.getString(cursor.getColumnIndex("path"));
                    info.albumId = cursor.getString(cursor.getColumnIndex("albumId"));
                    info.album = cursor.getString(cursor.getColumnIndex("album"));
                    info.file_size = cursor.getInt(cursor.getColumnIndex("size"));

                    musicDatas.add(info);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return musicDatas;
    }
}
