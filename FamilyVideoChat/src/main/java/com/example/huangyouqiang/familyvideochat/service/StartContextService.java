package com.example.huangyouqiang.familyvideochat.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.huangyouqiang.familyvideochat.AVManager;
import com.example.huangyouqiang.familyvideochat.IMManager;
import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.example.huangyouqiang.familyvideochat.ui.NavigationListener;

public class StartContextService extends Service {
	private static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
	private static final String LOGIN_FAILED = "LOGIN_FAILED";
	public StartContextService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		IMManager.getInstance(AndroidApplication.getContext()).login(new NavigationListener() {
			@Override
			public void onFail() {
				sendBroadcast(new Intent(LOGIN_FAILED));
			}

			@Override
			public void onSuccess(String params, String value) {
				sendBroadcast(new Intent(LOGIN_SUCCESS).putExtra(params,value));
				AVManager.getInstance(AndroidApplication.getContext()).init();
			}
		});
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
	throw new UnsupportedOperationException("Not yet implemented");
}
}
