package com.victor.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.victor.dao.DbDao;
import com.victor.data.MusicData;
import com.victor.util.Constant;
import com.victor.util.SharePreferencesUtil;

public class MusicService extends Service implements OnCompletionListener{
	private String TAG = "MusicService";
	private MediaPlayer mediaPlayer;
	private int totalNum;
	private int current;
	List<MusicData> musicDatas = new ArrayList<>();
//	private String mLastPlayUrl;//上次播放url
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		    // We want this service to continue running until it is explicitly    // stopped, so return sticky.    return START_STICKY;}
		if (intent == null || intent.getAction() == null) {
			return START_NOT_STICKY;
		}

		int action = intent.getIntExtra(Constant.ACTION_KEY,-1);
		current = SharePreferencesUtil.getInt(getApplicationContext(),Constant.CURRENT_POSITION_KEY);
		musicDatas = DbDao.getInstance(getApplicationContext()).queryMusic(Constant.TB.MUSIC_CURRENT);
		if (musicDatas != null && musicDatas.size() > 0) {
			totalNum = musicDatas.size();
			if (current < totalNum) {
				playAction(action);
			}
		}

		return START_STICKY;
	}

	private void next(){
		current++;
		if (current == totalNum) {
			current = 0;
		}
		SharePreferencesUtil.putInt(getApplicationContext(),Constant.CURRENT_POSITION_KEY,current);
		play(false);
	}

	private void prev(){
		current--;
		if (current == -1) {
			current = totalNum - 1;
		}
		SharePreferencesUtil.putInt(getApplicationContext(),Constant.CURRENT_POSITION_KEY,current);
		play(false);
	}
	private void pause(){
		mediaPlayer.pause();
	}
	private void stop(){
		mediaPlayer.stop();
	}


	private void play(boolean isPlayOnline){
		Log.e(TAG,"play()......");
		String playUrl = musicDatas.get(current).file_link;
		if (isPlayOnline) {
			playUrl = SharePreferencesUtil.getString(getApplicationContext(),Constant.PLAY_ONLINE_URL_KEY);
		}
		if (TextUtils.isEmpty(playUrl)) {
			Log.e(TAG,"playUrl is empty!!!!");
			return;
		}
//		if (mLastPlayUrl.equals(playUrl)) {
//			mp.start();
//			return;
//		}
//		mLastPlayUrl = playUrl;
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(playUrl);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@Override
	public void onCompletion(MediaPlayer mp) {
		next();
	}

	private void playAction (int action) {
		switch (action) {
			case Constant.Action.PLAY:
				play(false);
				break;
			case Constant.Action.PLAY_ONLINE:
				play(true);
				break;
			case Constant.Action.PAUSE:
				pause();
				break;
			case Constant.Action.PREV:
				prev();
				break;
			case Constant.Action.NEXT:
				next();
				break;
			case Constant.Action.STOP:
				stop();
				break;
		}
	}
}
