package com.example.huangyouqiang.familyvideochat.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.example.huangyouqiang.familyvideochat.IMManager;
import com.example.huangyouqiang.familyvideochat.LocalSaveManager;
import com.example.huangyouqiang.familyvideochat.ui.Navigate;
import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.ui.NavigationListener;

public class SplashActivity extends BaseActivity implements NavigationListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (LocalSaveManager.getInstance(AndroidApplication.getContext()).getAccountUserId() == "") {
					Navigate.build().navigateTo(LoginActivity.class).start();
				} else {
					IMManager.getInstance(AndroidApplication.getContext()).login(SplashActivity.this);
				}
			}
		},1 * 1000);

	}

	@Override
	public void onFail() {
		Navigate.build().navigateTo(LoginActivity.class).start();
	}

	@Override
	public void onSuccess(String params, String value) {
		Navigate.build()
						.navigateTo(MainActivity.class)
						.start();
		finish();
	}
}
