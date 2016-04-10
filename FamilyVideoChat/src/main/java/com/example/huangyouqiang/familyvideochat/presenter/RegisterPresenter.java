package com.example.huangyouqiang.familyvideochat.presenter;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;

import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.example.huangyouqiang.familyvideochat.view.RegisterView;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by huangyouqiang on 2016/4/7.
 */
public class RegisterPresenter {

	private RegisterView registerView;
	public RegisterPresenter(RegisterView registerView){
		this.registerView = registerView;
	}

	public void init(){
		this.registerView.initView();
	}
	public void register(){
		String username = this.registerView.getUserId();
		String password = this.registerView.getPassword();
		if(TextUtils.isEmpty(username)){
			registerView.setUsernameError("username is not valid");
			return;
		}
		if(TextUtils.isEmpty(password) || password.length() < 6 || TextUtils.isDigitsOnly(password)){
			registerView.setPasswordError("password is not valid,must contains letter ");
			return;
		}
		registerView.showLoading();
		AndroidApplication.getIMManager().register(username, password, regListener);
	}

	TLSStrAccRegListener regListener = new TLSStrAccRegListener() {
		@Override
		public void OnStrAccRegSuccess(final TLSUserInfo tlsUserInfo) {
			registerView.hideLoading();
			registerView.showMessage("register success!", new Snackbar.Callback() {
				@Override
				public void onDismissed(Snackbar snackbar, int event) {
					super.onDismissed(snackbar, event);
					Activity currentActivity = AndroidApplication.getInstance().getCurrentActivity();
					currentActivity.setResult(0, currentActivity.getIntent().putExtra("username", tlsUserInfo.identifier));
					currentActivity.finish();
				}
			});
		}

		@Override
		public void OnStrAccRegFail(TLSErrInfo tlsErrInfo) {
			registerView.hideLoading();
			registerView.showMessage("register failed , " + tlsErrInfo.Title + tlsErrInfo.Msg,null);
		}

		@Override
		public void OnStrAccRegTimeout(TLSErrInfo tlsErrInfo) {
			registerView.hideLoading();
			registerView.showMessage("timeout , " + tlsErrInfo.Title + tlsErrInfo.Msg, null);
		}
	};

}
