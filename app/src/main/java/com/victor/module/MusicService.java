package com.victor.module;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.victor.util.Constant;

public class MusicService extends Service implements OnCompletionListener{
	private String TAG = "MusicService";
	private MediaPlayer mp;
	private Cursor cursor;
	private int totalNum;
	private int current;
//	private String mLastPlayUrl;//上次播放url
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		    // We want this service to continue running until it is explicitly    // stopped, so return sticky.    return START_STICKY;}
		if (intent == null || intent.getAction() == null) {
			return START_NOT_STICKY;
		}
		int playAction = intent.getIntExtra("PLAY_ACTION",-1);
		String playUrl = intent.getStringExtra("PLAY_URL");
		if (playAction == Constant.Action.PLAY) {
			play(playUrl);
		} else if (playAction == Constant.Action.PAUSE) {
			pause();
		} else if (playAction == Constant.Action.STOP) {
			stop();
		} else if (playAction == Constant.Action.NEXT) {
			next();
		} else if (playAction == Constant.Action.PREV) {
			prev();
		}
		return START_STICKY;
	}

	private void next(){
		current++;
		play("");
	}

	private void prev(){
		current--;
		if (current == -1) {
			current = totalNum - 1;
		}
		play("");
	}
	private void pause(){
		mp.pause();
	}
	private void stop(){
		mp.stop();
	}


	private void play(String playUrl){
		Log.e(TAG,"play()%%%%%%%%%%%%%%%%%%%%");
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
			mp.reset();
			mp.setDataSource(playUrl);
			mp.prepare();
			mp.start();
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
}
