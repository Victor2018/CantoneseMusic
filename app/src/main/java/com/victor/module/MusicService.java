package com.victor.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.victor.dao.DbDao;
import com.victor.data.MusicData;
import com.victor.music.R;
import com.victor.util.Constant;
import com.victor.util.Loger;
import com.victor.util.SharePreferencesUtil;

public class MusicService extends Service implements OnCompletionListener,OnInfoListener,
		OnPreparedListener,MediaPlayer.OnErrorListener {
	private String TAG = "MusicService";
	private MediaPlayer mediaPlayer;
	private int totalNum;
	private int current;
	private int action;
	private int msec;
	public static boolean isPlaying;
	List<MusicData> musicDatas = new ArrayList<>();
	private int loopMode = Constant.LoopMode.LOOP_ALL;

	private MusicPlayHelper mMusicPlayHelper;
	private MusicData currentPlayMusic;
	private String mLastPlayUrl = "";//上次播放url

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Loger.e(TAG,"onCreate()......");
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnErrorListener(this);
		mMusicPlayHelper = new MusicPlayHelper(this,mediaPlayer);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		    // We want this service to continue running until it is explicitly    // stopped, so return sticky.    return START_STICKY;}
		Loger.e(TAG,"onStartCommand()......");
		if (intent == null || intent.getAction() == null) {
			return START_NOT_STICKY;
		}
		loopMode = PlayModule.getInstance(this).getPlayMode();
		action = intent.getIntExtra(Constant.ACTION_KEY,-1);
		msec = intent.getIntExtra(Constant.SEEK_DURATION_KEY,-1);
		current = SharePreferencesUtil.getInt(getApplicationContext(),Constant.CURRENT_POSITION_KEY);
		musicDatas = DbDao.getInstance(getApplicationContext()).queryMusic(Constant.TB.MUSIC_ALL);
		playAction();

		return START_STICKY;
	}

	/**
	 * @param isManual 是否是手动操作
	 */
	private void next(boolean isManual){
		Loger.e(TAG,"next()......");
		isPlaying = true;
		if (musicDatas == null || musicDatas.size() == 0) {
			Loger.e(TAG,"not found music to next!");
			return;
		}
		getPlayPosition(Constant.PlayStatus.NEXT,isManual);
		SharePreferencesUtil.putInt(getApplicationContext(),Constant.CURRENT_POSITION_KEY,current);
		PlayModule.getInstance(getApplicationContext()).updateCurrentPlay(musicDatas.get(current),Constant.PlayStatus.PLAY);
		play(false);

		Bundle bundle = new Bundle();
		bundle.putInt(Constant.ACTION_KEY,Constant.Action.UPDATE_CURRENT_POSITION);
		bundle.putInt(Constant.POSITION_KEY,current);
		DataObservable.getInstance().setData(bundle);
	}

	/**
	 * @param isManual 是否是手动操作
	 */
	private void prev(boolean isManual){
		Loger.e(TAG,"prev()......");
		isPlaying = true;
		if (musicDatas == null || musicDatas.size() == 0) {
			Loger.e(TAG,"not found music to prev!");
			return;
		}
		getPlayPosition(Constant.PlayStatus.PREV,isManual);
		SharePreferencesUtil.putInt(getApplicationContext(),Constant.CURRENT_POSITION_KEY,current);
		PlayModule.getInstance(getApplicationContext()).updateCurrentPlay(musicDatas.get(current),Constant.PlayStatus.PLAY);
		play(false);

		Bundle bundle = new Bundle();
		bundle.putInt(Constant.ACTION_KEY,Constant.Action.UPDATE_CURRENT_POSITION);
		bundle.putInt(Constant.POSITION_KEY,current);
		DataObservable.getInstance().setData(bundle);
	}
	private void pause(){
		Loger.e(TAG,"pause()......");
		isPlaying = false;
		mediaPlayer.pause();
		mHandler.removeMessages(Constant.Msg.UPDATE_PLAY_PROGRESS);
		mHandler.removeCallbacks(updateProgressRunnable);
	}
	private void stop(){
		Loger.e(TAG,"stop()......");
		mHandler.removeMessages(Constant.Msg.UPDATE_PLAY_PROGRESS);
		mHandler.removeCallbacks(updateProgressRunnable);
		isPlaying = false;
		mediaPlayer.stop();
	}

	private void seekTo (int msec) {
		Loger.e(TAG,"seekTo()......");
		isPlaying = true;
		mediaPlayer.seekTo(msec);
	}

	private void play(boolean isPlayOnline){
		Log.e(TAG,"play()......isPlayOnline = " + isPlayOnline);
		mHandler.removeMessages(Constant.Msg.UPDATE_PLAY_PROGRESS);
		mHandler.removeCallbacks(updateProgressRunnable);
		isPlaying = true;
		if (musicDatas == null || musicDatas.size() == 0) {
			Loger.e(TAG,"not found music to play!");
			return;
		}
		String playUrl = "";
		if (current < musicDatas.size()) {
			playUrl = musicDatas.get(current).data;
		}
		if (isPlayOnline) {
			playUrl = SharePreferencesUtil.getString(getApplicationContext(),Constant.PLAY_ONLINE_URL_KEY);
		}
		if (mediaPlayer == null) {
			Loger.e(TAG,"mediaPlayer is null");
			return;
		}
		if (TextUtils.isEmpty(playUrl)) {
			Loger.e(TAG,"playUrl is null");
			return;
		}
		mMusicPlayHelper.sendRequestWithParms(Constant.Msg.PLAY_MUSIC,playUrl);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Loger.e(TAG,"onCompletion()......");
		next(false);
	}

	private void playAction () {
		if (musicDatas != null && musicDatas.size() > 0) {
			totalNum = musicDatas.size();
		}
		switch (action) {
			case Constant.Action.PLAY:
				if (totalNum > 0) {
					play(false);
				}
				break;
			case Constant.Action.PLAY_ONLINE:
				play(true);
				break;
			case Constant.Action.PAUSE:
				pause();
				break;
			case Constant.Action.PREV:
				if (totalNum > 0) {
					prev(true);
				}
				break;
			case Constant.Action.NEXT:
				if (totalNum > 0) {
					next(true);
				}
				break;
			case Constant.Action.STOP:
				stop();
				break;
			case Constant.Action.SEEK:
				if (msec != -1) {
					seekTo(msec);
				}
				break;

		}
	}

	private Runnable updateProgressRunnable = new Runnable() {
		@Override
		public void run() {
			if (currentPlayMusic != null) {
				currentPlayMusic.action = Constant.Msg.UPDATE_PLAY_PROGRESS;
				currentPlayMusic.current = mediaPlayer.getCurrentPosition();
				if (currentPlayMusic.dataType == Constant.DataType.MUSIC_ONLINE) {
					currentPlayMusic.duration = mediaPlayer.getDuration();
				}
				DataObservable.getInstance().setData(currentPlayMusic);
			}
		}
	};

	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		Loger.e(TAG,"onInfo()......");
		switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				break;
			case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
				break;
			case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
				break;
			case MediaPlayer.MEDIA_INFO_UNKNOWN:
				break;
			case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
				break;
			case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
				break;
			case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
				break;
			case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
				break;
			case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
				break;
		}
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		Loger.e(TAG,"onPrepared()......");
		//duration,title,artist,album,data
		List<MusicData> currentList = new ArrayList<>();
		MusicData info = getCurrentMusic();
		if (info != null) {
			currentPlayMusic = info;
			currentList.add(info);
			DbDao.getInstance(getApplicationContext()).insertMusics(currentList,Constant.TB.MUSIC_CURRENT);
			mHandler.sendEmptyMessage(Constant.Msg.UPDATE_PLAY_PROGRESS);
		}
	}

	private MusicData getCurrentMusic () {
		MusicData musicData;
		if (action == Constant.Action.PLAY_ONLINE) {
			List<MusicData> onlineList = DbDao.getInstance(getApplicationContext()).queryMusic(Constant.TB.MUSIC_ONLINE);
			musicData = onlineList.get(0);
			musicData.dataType = Constant.DataType.MUSIC_ONLINE;
		} else {
			musicData = musicDatas.get(current);
			musicData.dataType = Constant.DataType.MUSIC_LOCAL;
		}
		return musicData;
	}

	Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Constant.Msg.UPDATE_PLAY_PROGRESS:
					mHandler.post(updateProgressRunnable);
					mHandler.sendEmptyMessageDelayed(Constant.Msg.UPDATE_PLAY_PROGRESS,200);
					break;
			}
		}
	};

	private void getPlayPosition (int action,boolean isManual) {
		switch (loopMode) {
			case Constant.LoopMode.LOOP_ALL:
				if (action == Constant.PlayStatus.PREV) {
					current--;
					if (current == -1) {
						current = totalNum - 1;
					}
				} else if (action == Constant.PlayStatus.NEXT) {
					current++;
					if (current == totalNum) {
						current = 0;
					}
				}
				break;
			case Constant.LoopMode.LOOP_ONE:
				if (!isManual) return;
				if (action == Constant.PlayStatus.PREV) {
					current--;
					if (current == -1) {
						current = totalNum - 1;
					}
				} else if (action == Constant.PlayStatus.NEXT) {
					current++;
					if (current == totalNum) {
						current = 0;
					}
				}
				break;
			case Constant.LoopMode.LOOP_RANDOM:
				if (totalNum > 0) {
					int minIndex = 0;
					int maxIndex = totalNum - 1;
					current = (int) (Math.random() * (maxIndex - minIndex + 1)) + minIndex;
				}
				break;
			default:
				if (action == Constant.PlayStatus.PREV) {
					current--;
					if (current == -1) {
						current = totalNum - 1;
					}
				} else if (action == Constant.PlayStatus.NEXT) {
					current++;
					if (current == totalNum) {
						current = 0;
					}
				}
				break;
		}
	}

	private void releaseMediaPlayer() {
		Log.d(TAG, "releaseMediaPlayer()......");
		synchronized (this) {
			if (mediaPlayer != null) {
				mediaPlayer.stop(); // mMediaPlayer.reset();
				mediaPlayer.release();
			}
			mediaPlayer = null;
		}
	}

	@Override
	public void onDestroy() {
		if (mMusicPlayHelper != null) {
			mMusicPlayHelper.onDestroy();
		}
		if (mHandler != null) {
			mHandler.removeCallbacks(updateProgressRunnable);
		}
		releaseMediaPlayer();
		super.onDestroy();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Loger.e(TAG,"onError()......");
		play(PlayModule.getInstance(getApplicationContext()).isPlayOnline());
		return false;
	}
}
