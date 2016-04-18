package com.example.huangyouqiang.familyvideochat.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.example.huangyouqiang.familyvideochat.view.BaseView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by huangyouqiang on 2016/4/5.
 */
public class BaseActivity extends AppCompatActivity implements BaseView{
	private static final String TAG = BaseActivity.class.getSimpleName();
	@InjectView(R.id.rl_progress)
	RelativeLayout progress;
	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onCreate(savedInstanceState, persistentState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidApplication.getInstance().setCurrentActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clearReference();
		ButterKnife.reset(this);
	}
	protected void addFragment(int containerViewId, Fragment fragment, String tag){
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(containerViewId,fragment,tag);
		fragmentTransaction.commit();
	}

	public < T extends Fragment> T getFragment(String tag){
		return (T)getSupportFragmentManager().findFragmentByTag(tag);
	}

	private void clearReference(){
		Activity currentActivity = AndroidApplication.getContext().getCurrentActivity();
		if(this.equals(currentActivity)){
			AndroidApplication.getInstance().setCurrentActivity(null);
		}
	}

	@Override
	public void showMessage(String msg, Snackbar.Callback callback) {
		Snackbar snackbar = Snackbar.make(((ViewGroup)this.findViewById(android.R.id.content)).getChildAt(0),msg,Snackbar.LENGTH_LONG);
		if(callback != null) {
			snackbar.setCallback(callback);
		}
		snackbar.show();
	}

	@Override
	public void showLoading() {
		progress.setVisibility(View.VISIBLE);
		progress.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;//屏蔽触摸事件
			}
		});
	}

	@Override
	public void hideLoading() {
		progress.setVisibility(View.GONE);
	}
}
