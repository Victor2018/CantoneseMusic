package com.victor.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.victor.data.MusicData;
import com.victor.music.R;
import com.victor.view.CircleImageView;

/**
 * Created by victor on 2016/1/9.
 */

public class MusicLocalAdapter extends BaseAdapter implements SectionIndexer{
	private List<MusicData> datas;
	private Context mContext;

	public MusicLocalAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public List<MusicData> getMusicDatas() {
		return datas;
	}

	public void setMusicDatas(List<MusicData> musicDatas) {

		this.datas = musicDatas;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<MusicData> list){
		this.datas = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.datas.size();
	}

	public Object getItem(int position) {
		return datas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final MusicData info = datas.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.lv_local_music_item, null);
			viewHolder.mCivImg = (CircleImageView) view.findViewById(R.id.civ_img);
			viewHolder.mTvTitle = (TextView) view.findViewById(R.id.tv_title);
			viewHolder.mTvAuthor = (TextView) view.findViewById(R.id.tv_author);
			viewHolder.mTvLetter = (TextView) view.findViewById(R.id.catalog);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.mTvLetter.setVisibility(View.VISIBLE);
			viewHolder.mTvLetter.setText(info.sortLetters);
		}else{
			viewHolder.mTvLetter.setVisibility(View.GONE);
		}
		viewHolder.mTvTitle.setText(info.title);
		viewHolder.mTvAuthor.setText(info.artist);
		Glide.with(mContext).load(info.pic_small).centerCrop().error(R.mipmap.default_cover).into(viewHolder.mCivImg);

		return view;

	}



	final static class ViewHolder {
		CircleImageView mCivImg;
		TextView mTvTitle;
		TextView mTvAuthor;
		TextView mTvLetter;
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		if (datas != null && datas.size() > 0) {
			if (!TextUtils.isEmpty(datas.get(position).sortLetters)) {
				return datas.get(position).sortLetters.charAt(0);
			}
		}
		return -1;
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = datas.get(i).sortLetters;
			if (!TextUtils.isEmpty(sortStr)) {
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 *
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}