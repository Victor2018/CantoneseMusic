package com.victor.interfaces;

import com.victor.data.MusicData;

import java.util.List;

/**
 * Created by victor on 2016/6/13.
 */
public interface DbInterface {

    /**
     * @param tbName
     * 清空表数据
     */
    void clearTb(String tbName);


    /**
     * @param musicDatas
     * @param tbName
     * 添加音乐列表到本地
     */
    void insertMusics(List<MusicData> musicDatas,String tbName);

    /**
     * @param tbName
     * @param id
     * 删除音乐
     */
    void removeMusic(String tbName,int id);

    /**
     * @return
     * 查询音乐
     */
    List<MusicData> queryMusic(String tbName);


}
