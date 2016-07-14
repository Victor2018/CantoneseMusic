package com.victor.fragments;import android.content.Context;import android.content.Intent;import android.os.Bundle;import android.support.annotation.Nullable;import android.support.v4.app.Fragment;import android.support.v7.widget.GridLayoutManager;import android.support.v7.widget.LinearLayoutManager;import android.support.v7.widget.OrientationHelper;import android.support.v7.widget.RecyclerView;import android.support.v7.widget.StaggeredGridLayoutManager;import android.util.Log;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import com.victor.adapter.MusicOnlineAdapter;import com.victor.module.DataObservable;import com.victor.module.HttpRequestHelper;import com.victor.music.MusicDetailActivity;import com.victor.music.R;import java.util.Observable;import java.util.Observer;/** * Created by victor on 2016/7/6. */public class MusicOnlineFrag extends Fragment implements MusicOnlineAdapter.OnItemClickListener {    private String TAG = "MusicOnlineFrag";    private Context mContext;    private RecyclerView mRecyclerView;    private LinearLayoutManager linearLayoutManager;    private GridLayoutManager gridLayoutManager;    private StaggeredGridLayoutManager staggeredGridLayoutManager;    private MusicOnlineAdapter normalRecyclerViewAdapter;    private HttpRequestHelper mHttpRequestHelper;    @Nullable    @Override    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {        View view = inflater.inflate(R.layout.frag_music_category,container, false);        initialize(view);        return view;    }    private void initialize (View view) {        mContext = getActivity();        mHttpRequestHelper = new HttpRequestHelper(mContext);        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);        linearLayoutManager = new LinearLayoutManager(getContext());//这里用线性显示 类似于listview        gridLayoutManager = new GridLayoutManager(getContext(), 2);//这里用线性宫格显示 类似于grid view        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);//这里用线性宫格显示 类似于瀑布流        //设置头部及底部View占据整行空间        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {            @Override            public int getSpanSize(int position) {                return (normalRecyclerViewAdapter.isHeaderView(position) || normalRecyclerViewAdapter.isBottomView(position)) ? gridLayoutManager.getSpanCount() : 1;            }        });        mRecyclerView.setLayoutManager(linearLayoutManager);        normalRecyclerViewAdapter = new MusicOnlineAdapter(getContext(),this,mHttpRequestHelper);        normalRecyclerViewAdapter.setHeaderVisible(false);        normalRecyclerViewAdapter.setFooterVisible(false);        mRecyclerView.setAdapter(normalRecyclerViewAdapter);    }    @Override    public void OnItemClick(int position,String imgUrl) {        Intent intent = new Intent(getActivity(), MusicDetailActivity.class);        intent.putExtra(MusicDetailActivity.EXTRA_TYPE,normalRecyclerViewAdapter.types[position]);        intent.putExtra(MusicDetailActivity.EXTRA_NAME,normalRecyclerViewAdapter.titles[position]);        intent.putExtra(MusicDetailActivity.EXTRA_IMG,imgUrl);        startActivity(intent);    }    @Override    public void onDestroy() {        if (mHttpRequestHelper != null) {            mHttpRequestHelper.onDestroy();        }        super.onDestroy();    }}