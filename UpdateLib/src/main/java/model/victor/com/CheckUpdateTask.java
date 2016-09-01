package model.victor.com;

import java.net.SocketTimeoutException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.content.Context;

import util.victor.com.Constant;
import util.victor.com.HttpUtil;


public class CheckUpdateTask {
	private String TAG = "CheckUpdateTask";
	private int requestCount;
	private Context mContext;
	private OnUpdateCompleteListener OnUpdateCompleteListener;

	public interface OnUpdateCompleteListener {
		void onUpdateCompelete(Bundle result);
	}

	public CheckUpdateTask(Context context,OnUpdateCompleteListener listener) {
		mContext = context;
		OnUpdateCompleteListener = listener;
	}

	public void requestUpdateData() {
		requestCount++;
		Log.e(TAG, "requestCount=" + requestCount);
		if(requestCount > 5){
			return;
		}
		new UpdateTask().execute(Constant.UPDATE_URL);
	}

	class UpdateTask extends AsyncTask<String, Integer, Bundle>{

		@Override
		protected Bundle doInBackground(String... params) {
			// TODO Auto-generated method stub
			int status = 0;
			Bundle responseData = new Bundle();
			if (HttpUtil.isNetEnable(mContext)){
				String result = null;
				try {
					result = HttpUtil.HttpGetRequest(params[0]);
					Log.e(TAG, "HttpGetRequest-result=" + result);
					if (!TextUtils.isEmpty(result)) {
						Log.e(TAG, "HttpGetRequest-REQUEST_SUCCESS");
						status = Constant.Msg.REQUEST_SUCCESS;
						responseData.putString(Constant.UPDATE_DATA_KEY,result);
					} else {
						Log.e(TAG, "HttpGetRequest-REQUEST_FAILED");
						status = Constant.Msg.REQUEST_FAILED;
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					Log.e(TAG, "HttpGetRequest-SOCKET_TIME_OUT");
					status = Constant.Msg.SOCKET_TIME_OUT;
				}
			} else {
				Log.e(TAG, "HttpGetRequest-NETWORK_ERROR");
				status = Constant.Msg.NETWORK_ERROR;
			}
			responseData.putInt(Constant.STATUS_KEY, status);
			responseData.putInt(Constant.REQUEST_MSG_KEY, Constant.Msg.UPDATE_REQUEST);
			return responseData;
		}

		protected void onPostExecute(Bundle result) {
			if(result != null){
//				DataObservable.getInstance().setData(result);
				if (OnUpdateCompleteListener != null) {
					OnUpdateCompleteListener.onUpdateCompelete(result);
				}
			}else{
				requestUpdateData();
			}
		}
	}

}