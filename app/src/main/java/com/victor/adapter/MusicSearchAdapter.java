package com.victor.adapter;import android.content.Context;import android.support.v7.widget.RecyclerView;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.ImageView;import android.widget.TextView;import com.bumptech.glide.Glide;import com.victor.data.MusicData;import com.victor.music.R;import com.victor.view.CircleImageView;import com.victor.view.DonutProgress;import java.util.List;/** * Created by victor on 2016/6/1. */public class MusicSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{    private String TAG = "MusicSearchAdapter";    private final LayoutInflater mLayoutInflater;    private final Context mContext;    private boolean isHeaderVisible = true;    private boolean isFooterVisible = true;    private boolean isProgressVisible = false;    protected int mHeaderCount = 1;//头部View个数    protected int mBottomCount = 1;//底部View个数    private int ITEM_TYPE_HEADER = 0;    private int ITEM_TYPE_CONTENT = 1;    private int ITEM_TYPE_BOTTOM = 2;    private List<MusicData> musicDatas;    private static OnItemClickListener mOnItemClickListener;    private static OnMoreClickListener mOnMoreClickListener;    public interface OnItemClickListener {        void OnItemClick(int position);    }    public interface OnMoreClickListener {        void OnMoreClick(int position);    }    public MusicSearchAdapter(Context context, OnItemClickListener listener,OnMoreClickListener moreClickListenerlistener) {        mContext = context;        mOnItemClickListener = listener;        mOnMoreClickListener = moreClickListenerlistener;        mLayoutInflater = LayoutInflater.from(mContext);    }    public void setHeaderVisible (boolean visible) {        isHeaderVisible = visible;        mHeaderCount = 1;        if (!isHeaderVisible) {            mHeaderCount = 0;        }        notifyDataSetChanged();    }    public void setFooterVisible (boolean visible) {        isFooterVisible = visible;        mBottomCount = 1;        if (!isFooterVisible) {            mBottomCount = 0;        }        notifyDataSetChanged();    }    public void setProgressVisible (boolean visible) {        isProgressVisible = visible;    }    public void setMusicDatas(List<MusicData> musicDatas) {        this.musicDatas = musicDatas;    }    @Override    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {        if (viewType == ITEM_TYPE_HEADER) {            return onCreateHeaderView(parent);        } else if (viewType == ITEM_TYPE_CONTENT) {            return onCreateContentView(parent);        } else if (viewType == ITEM_TYPE_BOTTOM) {            return onCreateBottomView(parent);        }        return null;    }    public RecyclerView.ViewHolder onCreateHeaderView (ViewGroup parent){        return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.recyclerview_img_item, parent, false));    }    public RecyclerView.ViewHolder onCreateContentView (ViewGroup parent){        return new ContentViewHolder(mLayoutInflater.inflate(R.layout.lv_music_item, parent, false));    }    public RecyclerView.ViewHolder onCreateBottomView (ViewGroup parent) {        return new BottomViewHolder(mLayoutInflater.inflate(R.layout.recyclerview_img_item, parent, false));    }    @Override    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {        if (holder instanceof HeaderViewHolder) {//            ((HeaderViewHolder) holder).mTvTitle.setText(titleList.get(0));        } else if (holder instanceof ContentViewHolder) {            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;            if (isProgressVisible) {                contentViewHolder.mDpProgress.setVisibility(View.VISIBLE);            }            int index = position - mHeaderCount;            if (index < musicDatas.size() && index >=0) {                if (musicDatas.get(index).status != 0) {                    contentViewHolder.mDpProgress.setPrefixText("");                }                contentViewHolder.mDpProgress.setProgress(musicDatas.get(index).current);                contentViewHolder.mTvTitle.setText(musicDatas.get(index).title);                contentViewHolder.mTvAuthor.setText(musicDatas.get(index).artist);                Glide.with(mContext).load(musicDatas.get(index).pic_small).centerCrop().error(R.mipmap.default_cover).into(contentViewHolder.mCivImg);            }        } else if (holder instanceof BottomViewHolder) {            ((BottomViewHolder) holder).mTvTitle.setVisibility(View.GONE);        }    }    @Override    public int getItemViewType(int position) {        int dataItemCount = getContentItemCount();        if (mHeaderCount != 0 && position < mHeaderCount) {//头部View            return ITEM_TYPE_HEADER;        } else if (mBottomCount != 0 && position >= (mHeaderCount + dataItemCount)) {//底部View            return ITEM_TYPE_BOTTOM;        } else {            return ITEM_TYPE_CONTENT;        }    }    public boolean isHeaderView(int position) {        return mHeaderCount != 0 && position < mHeaderCount;    }    public boolean isBottomView(int position) {        return mBottomCount != 0 && position >= (mHeaderCount + getContentItemCount());    }    public void add(String title) {//        titleList.add(title);        notifyItemInserted(1);    }    public void remove(int position) {//        titleList.remove(position);        notifyItemRemoved(position);    }    public int getContentItemCount() {        return musicDatas == null ? 0 : musicDatas.size();    }    @Override    public int getItemCount() {        return mHeaderCount + getContentItemCount() + mBottomCount;    }    public class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{        CircleImageView mCivImg;        TextView mTvTitle,mTvAuthor;        ImageView mIvMore;        DonutProgress mDpProgress;        ContentViewHolder(View view) {            super(view);            mCivImg = (CircleImageView) view.findViewById(R.id.civ_img);            mTvTitle = (TextView) view.findViewById(R.id.tv_title);            mTvAuthor = (TextView) view.findViewById(R.id.tv_author);            mIvMore = (ImageView) view.findViewById(R.id.iv_more);            mDpProgress = (DonutProgress) view.findViewById(R.id.dp_progress);            view.setOnClickListener(this);            mIvMore.setOnClickListener(this);        }        @Override        public void onClick(View v) {            if (v instanceof ImageView) {                if (mOnMoreClickListener != null) {                    mOnMoreClickListener.OnMoreClick(getAdapterPosition() - mHeaderCount);                }            } else {                if(mOnItemClickListener != null) {                    mOnItemClickListener.OnItemClick(getAdapterPosition() - mHeaderCount);                }            }        }    }    public static class HeaderViewHolder extends RecyclerView.ViewHolder {        ImageView mIvImg;        TextView mTvTitle;        public HeaderViewHolder(View itemView) {            super(itemView);            mIvImg = (ImageView) itemView.findViewById(R.id.iv_img);            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);        }    }    public static class BottomViewHolder extends RecyclerView.ViewHolder {        ImageView mIvImg;        TextView mTvTitle;        public BottomViewHolder(View itemView) {            super(itemView);            mIvImg = (ImageView) itemView.findViewById(R.id.iv_img);            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);        }    }}