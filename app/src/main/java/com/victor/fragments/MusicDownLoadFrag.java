package com.victor.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.victor.adapter.MusicSearchAdapter;
import com.victor.data.MusicData;
import com.victor.module.DataObservable;
import com.victor.module.PlayModule;
import com.victor.music.R;
import com.victor.util.Constant;
import com.victor.util.DownLoadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by victor on 2016/7/7.
 */
public class MusicDownLoadFrag extends Fragment implements MusicSearchAdapter.OnItemClickListener,
        MusicSearchAdapter.OnMoreClickListener,Observer{
    private String TAG = "MusicLocalFrag";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    private MusicSearchAdapter musicSearchAdapter;
    private List<MusicData> searchList = new ArrayList<>();

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.Action.UPDATE_DOWNLOAD_LIST:
                    List<MusicData> downloadMusicList = (List<MusicData>) msg.obj;
                    if (downloadMusicList != null && downloadMusicList.size() > 0) {
                        searchList = downloadMusicList;
                    } else {
                        searchList.clear();
                    }
                    musicSearchAdapter.setMusicDatas(searchList);
                    musicSearchAdapter.notifyDataSetChanged();
                    break;
                case Constant.Msg.UPDATE_DOWNLOAD_PROGRESS:
                    if (searchList != null && searchList.size() > 0) {
                        searchList.get(0).current = msg.arg1;
                        searchList.get(0).status = 1;
                        musicSearchAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_download_music,container, false);
        initialize(view);
        initData();
        return view;
    }

    private void initialize (View view) {
        mContext = getActivity();
        DataObservable.getInstance().addObserver(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getContext());//这里用线性显示 类似于listview
        mRecyclerView.setLayoutManager(linearLayoutManager);
        musicSearchAdapter = new MusicSearchAdapter(getContext(),this,this);

        musicSearchAdapter.setHeaderVisible(false);
        musicSearchAdapter.setFooterVisible(false);
        musicSearchAdapter.setProgressVisible(true);
        musicSearchAdapter.setMusicDatas(searchList);
        mRecyclerView.setAdapter(musicSearchAdapter);

    }

    private void initData () {
        Uri uri =  Uri.parse("file://"+ Environment.getExternalStorageDirectory());
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

        List<MusicData> downloadMusicList = DownLoadUtil.getInstance().getDownloadMusicList();
        Message msg = mHandler.obtainMessage(Constant.Action.UPDATE_DOWNLOAD_LIST);
        msg.obj = downloadMusicList;
        mHandler.sendMessage(msg);
    }

    @Override
    public void OnItemClick(int position) {
        if (searchList != null && searchList.size() > 0) {
            if (position < searchList.size()) {
                PlayModule.getInstance(getContext()).playOnline(searchList.get(position));
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof MusicData) {
            MusicData info = (MusicData) data;
            if (info.action == Constant.Action.DOWNLOAD) {
                if (searchList != null && searchList.size() > 0) {
                    if (searchList.get(0).id == info.id) {
                        Message msg = mHandler.obtainMessage(Constant.Msg.UPDATE_DOWNLOAD_PROGRESS);
                        msg.arg1 = info.current;
                        mHandler.sendMessage(msg);
                    }
                }
            }
        } else if (data instanceof Integer) {
            int action = (int) data;
            if (action == Constant.Action.UPDATE_DOWNLOAD_LIST) {
                initData();
            }
        }
    }

    @Override
    public void onDestroy() {
        DataObservable.getInstance().deleteObserver(this);
        super.onDestroy();
    }

    @Override
    public void OnMoreClick(int position) {

    }

}
