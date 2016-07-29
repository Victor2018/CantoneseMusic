package com.victor.music;import android.graphics.Color;import android.os.Build;import android.os.Handler;import android.os.Message;import android.support.v7.app.AppCompatActivity;import android.os.Bundle;import android.support.v7.widget.Toolbar;import android.view.View;import android.view.Window;import android.view.WindowManager;import android.view.animation.Animation;import android.view.animation.AnimationUtils;import android.view.animation.LinearInterpolator;import android.widget.ImageButton;import android.widget.TextView;import com.bumptech.glide.Glide;import com.victor.dao.DbDao;import com.victor.data.MusicData;import com.victor.module.DataObservable;import com.victor.module.PlayModule;import com.victor.module.SearchLrc;import com.victor.util.Constant;import com.victor.util.DateUtil;import com.victor.util.FileUtil;import com.victor.util.Loger;import com.victor.util.SharePreferencesUtil;import com.victor.view.CircleImageView;import com.victor.view.CircularSeekBar;import com.victor.view.LrcView;import java.io.File;import java.util.List;import java.util.Observable;import java.util.Observer;public class MusicPlayActivity extends AppCompatActivity implements View.OnClickListener,CircularSeekBar.OnCircularSeekBarChangeListener,Observer {    private String TAG = "MusicPlayActivity";    private Toolbar toolbar;    private ImageButton mIbPrev,mIbPlay,mIbNext;    private CircularSeekBar mCsbProgress;    private CircleImageView mCIvAlbum;    private TextView mTvCurrent,mTvDuration;    private LrcView mLrcViewSingle;    private MusicData musicData;    private Animation albumRotateAnim;//音乐光盘旋转动画    Handler mHandler = new Handler(){        @Override        public void handleMessage(Message msg) {            switch (msg.what) {                case Constant.Msg.UPDATE_PLAY_PROGRESS:                    MusicData info = (MusicData) msg.obj;                    mCsbProgress.setMax((int) info.duration);                    mCsbProgress.setProgress(info.current);                    mTvCurrent.setText(DateUtil.formatTime((long) info.current));                    mTvDuration.setText(DateUtil.formatTime(info.duration));                    toolbar.setTitle(info.title);                    toolbar.setTitleTextColor(Color.WHITE);                    toolbar.setSubtitle(info.artist);                    if (info.artist.equals("<unknown>")) {                        toolbar.setSubtitle("未知艺术家");                    }                    toolbar.setSubtitleTextColor(Color.WHITE);                    if (mLrcViewSingle.hasLrc()) {                        mLrcViewSingle.updateTime(info.current);                    }                    break;                case Constant.Action.SHOW_CURRENT_PLAY:                    searchLrc(musicData);                    searchAlbumImg();                    break;            }        }    };    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_music_play);        initialize();        initData();    }    private void initialize () {        DataObservable.getInstance().addObserver(this);        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {            Window window = getWindow();            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);            window.setStatusBarColor(Color.TRANSPARENT);        }        toolbar = (Toolbar) findViewById(R.id.toolbar);        mIbPrev = (ImageButton) findViewById(R.id.ib_prev);        mIbPlay = (ImageButton) findViewById(R.id.ib_play);        mIbNext = (ImageButton) findViewById(R.id.ib_next);        mCsbProgress = (CircularSeekBar) findViewById(R.id.csb_progress);        mCIvAlbum = (CircleImageView) findViewById(R.id.civ_album);        mTvCurrent = (TextView) findViewById(R.id.tv_current);        mTvDuration = (TextView) findViewById(R.id.tv_duration);        mLrcViewSingle = (LrcView) findViewById(R.id.lrc_view_single);        mIbPrev.setOnClickListener(this);        mIbPlay.setOnClickListener(this);        mIbNext.setOnClickListener(this);        mCsbProgress.setOnSeekBarChangeListener(this);        //初始化光盘旋转动画        albumRotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);        albumRotateAnim.setInterpolator(new LinearInterpolator());//重复播放不停顿        albumRotateAnim.setFillAfter(true);//停在最后    }    private void initData () {        List<MusicData> currentList = DbDao.getInstance(this).queryMusic(Constant.TB.MUSIC_CURRENT);        if (currentList != null && currentList.size() > 0) {            musicData = currentList.get(0);            toolbar.setTitle(musicData.title);            toolbar.setTitleTextColor(Color.WHITE);            toolbar.setSubtitle(musicData.artist);            if (musicData.artist.equals("<unknown>")) {                toolbar.setSubtitle("未知艺术家");            }            toolbar.setSubtitleTextColor(Color.WHITE);            mCsbProgress.setMax((int) musicData.duration);            mCsbProgress.setProgress(musicData.current);            mTvCurrent.setText(DateUtil.formatTime((long) musicData.current));            mTvDuration.setText(DateUtil.formatTime(musicData.duration));            searchLrc(musicData);            if (PlayModule.getInstance(this).isPlaying()) {                mCIvAlbum.startAnimation(albumRotateAnim);                mIbPlay.setImageResource(R.drawable.btn_pause_selector);            }            searchAlbumImg();        }    }    private void searchAlbumImg () {        if (musicData == null) return;        Glide.with(this).load(musicData.pic_small).centerCrop().error(R.mipmap.default_cover).into(mCIvAlbum);    }    private void searchLrc (final MusicData data) {        Loger.e(TAG,"searchLrc()...");        if (data == null) {            Loger.e(TAG,"searchLrc()...data == null");            return;        }        String lrcPath = FileUtil.getLrcFilePath(data);        File file = new File(lrcPath);        if (file.exists()) {            loadLrc(lrcPath);        } else {            loadLrc(lrcPath);            // 设置tag防止歌词下载完成后已切换歌曲            mLrcViewSingle.setTag(data);            //从网络搜索lrc歌词            SearchLrc.getInstance().searchLrc(data.artist + "-" + data.title);            SearchLrc.getInstance().setOnCompleteListener(new SearchLrc.OnCompleteListener() {                @Override                public void onComplete(int status) {                    if (status == 1) {                        if (mLrcViewSingle.getTag() == data) {                            String lrcPath = FileUtil.getLrcFilePath(data);                            loadLrc(lrcPath);                        }                    }                }            });        }    }    private void loadLrc(String path) {        mLrcViewSingle.loadLrc(path);        // 清除tag        mLrcViewSingle.setTag(null);    }    @Override    public void onClick(View v) {        switch (v.getId()) {            case R.id.ib_prev:                mCIvAlbum.startAnimation(albumRotateAnim);                PlayModule.getInstance(this).prev();                break;            case R.id.ib_play:                if (PlayModule.getInstance(this).isPlaying()) {                    mCIvAlbum.clearAnimation();                    mIbPlay.setImageResource(R.drawable.btn_play_selector);                    PlayModule.getInstance(this).pause();                } else {                    mCIvAlbum.startAnimation(albumRotateAnim);                    mIbPlay.setImageResource(R.drawable.btn_pause_selector);                    if (PlayModule.getInstance(this).isPlayOnline()) {                        List<MusicData> onlineList = DbDao.getInstance(this).queryMusic(Constant.TB.MUSIC_ONLINE);                        if (onlineList != null && onlineList.size() > 0) {                            PlayModule.getInstance(this).playOnline(onlineList.get(0));                        }                    } else {                        int current = SharePreferencesUtil.getInt(this, Constant.CURRENT_POSITION_KEY);                        PlayModule.getInstance(this).play(current);                    }                }                break;            case R.id.ib_next:                mCIvAlbum.startAnimation(albumRotateAnim);                PlayModule.getInstance(this).next();                break;        }    }    @Override    public void update(Observable observable, Object data) {        if (data instanceof MusicData) {            MusicData info = (MusicData) data;            int action = info.action;            if (action == Constant.Msg.UPDATE_PLAY_PROGRESS) {                Message msg = mHandler.obtainMessage(Constant.Msg.UPDATE_PLAY_PROGRESS,info);                msg.sendToTarget();            } else if (action == Constant.Action.SHOW_CURRENT_PLAY) {                musicData = info;                mHandler.sendEmptyMessage(Constant.Action.SHOW_CURRENT_PLAY);            }        }    }    @Override    protected void onDestroy() {        DataObservable.getInstance().deleteObserver(this);        super.onDestroy();    }    @Override    public void onProgressChanged(CircularSeekBar circularSeekBar, long progress, boolean fromUser) {        if (fromUser) {            PlayModule.getInstance(this).seekTo((int) progress);            mLrcViewSingle.onDrag((int) progress);            mCsbProgress.setProgress(progress);        }    }    @Override    public void onStopTrackingTouch(CircularSeekBar seekBar) {    }    @Override    public void onStartTrackingTouch(CircularSeekBar seekBar) {    }}