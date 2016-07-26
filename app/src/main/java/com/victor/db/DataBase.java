package com.victor.db;import android.content.Context;import android.database.Cursor;import android.database.sqlite.SQLiteDatabase;import android.database.sqlite.SQLiteOpenHelper;import android.text.TextUtils;import com.victor.util.Constant;/** * Created by victor on 2015/12/25. */public class DataBase extends SQLiteOpenHelper{    public DataBase(Context context, String name, SQLiteDatabase.CursorFactory factory,                    int version) {        super(context, Constant.DbConfig.DB_NAME, null, Constant.DbConfig.DB_VERSION);        // TODO Auto-generated constructor stub    }    @Override    public void onCreate(SQLiteDatabase db) {        createMusicTb(db,Constant.TB.MUSIC_ALL);        createMusicTb(db,Constant.TB.MUSIC_ONLINE);        createMusicTb(db,Constant.TB.MUSIC_CURRENT);    }    @Override    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {    	if (tabbleIsExist(Constant.TB.MUSIC_ALL)) {    		db.execSQL("drop table " + Constant.TB.MUSIC_ALL);    	}    	if (tabbleIsExist(Constant.TB.MUSIC_ONLINE)) {    		db.execSQL("drop table " + Constant.TB.MUSIC_ONLINE);    	}    	if (tabbleIsExist(Constant.TB.MUSIC_CURRENT)) {    		db.execSQL("drop table " + Constant.TB.MUSIC_CURRENT);    	}        onCreate(db);    }    private void createMusicTb(SQLiteDatabase db,String musicTbName) {        String sysSql = "create table if not exists " + musicTbName +                "(_id integer primary key autoincrement,title text,duration long," +                "artist text,id integer,displayName text,data text," +                "albumId text,album text,size integer,data_type integer)";        db.execSQL(sysSql);    }    /**     * 判断某张表是否存在     * @param tableName 表名     * @return     */    public boolean tabbleIsExist(String tableName){        boolean result = false;        if(TextUtils.isEmpty(tableName)){                return false;        }        SQLiteDatabase db = null;        Cursor cursor = null;        try {            db = this.getReadableDatabase();            String sql = "select count(1) from "+"sqlite_master "+" where type ='table' and name ='"+tableName.trim()+"' ";            cursor = db.rawQuery(sql, null);            if(cursor.moveToNext()){                int count = cursor.getInt(0);                if(count>0){                        result = true;                }            }        } catch (Exception e) {                // TODO: handle exception        } finally {        	if (cursor != null) {        		cursor.close();        	}        }        return result;    }}