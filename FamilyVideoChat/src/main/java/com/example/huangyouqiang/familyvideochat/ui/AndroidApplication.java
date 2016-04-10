package com.example.huangyouqiang.familyvideochat.ui;

import android.app.Activity;
import android.app.Application;

import com.example.huangyouqiang.familyvideochat.AVManager;
import com.example.huangyouqiang.familyvideochat.IMManager;

/**
 * Created by huangyouqiang on 2016/4/5.
 */
public class AndroidApplication extends Application {
	private static AndroidApplication instance;
	private Activity mCurrentActivity;
	private static IMManager mIMManager ;
	private AVManager mAVManager = null;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		mIMManager = IMManager.getInstance(this);
		mIMManager.init();
		mAVManager = AVManager.getInstance(this);
	}


	public static AndroidApplication getContext(){
		return instance;
	}

	public static AndroidApplication getInstance(){
		return instance;
	}

	public void setCurrentActivity(Activity mActivity){
		this.mCurrentActivity = mActivity;
	}

	public Activity getCurrentActivity(){
		return this. mCurrentActivity;
	}

	public static IMManager getIMManager(){
		return mIMManager;
	}
}
