package com.example.huangyouqiang.familyvideochat.ui.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.service.StartContextService;
import com.example.huangyouqiang.familyvideochat.ui.Navigate;

import java.util.List;

public class SplashActivity extends BaseActivity{

	private static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
	private static final String LOGIN_FAILED = "LOGIN_FAILED";


	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(LOGIN_FAILED)){
				Navigate.build().navigateTo(LoginActivity.class).start();
			}else{
				Navigate.build()
								.navigateTo(MainActivity.class)
								.start();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		IntentFilter filter = new IntentFilter();
		filter.addAction(LOGIN_FAILED);
		filter.addAction(LOGIN_SUCCESS);
		registerReceiver(broadcastReceiver,filter);

		if(!isServiceAlive(StartContextService.class.getSimpleName())) {
			startService(new Intent(SplashActivity.this, StartContextService.class));
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

	private boolean isServiceAlive(String serviceName){
		boolean isAlive = false;
		ActivityManager activityManger = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManger.getRunningServices(40);
		if(serviceList.size()<= 0){
			return false;
		}
		for(ActivityManager.RunningServiceInfo info :serviceList){
			if(info.service.getClassName().toString().equals(serviceName)){
				isAlive = true;
				break;
			}
		}
		return isAlive;
	}
}
