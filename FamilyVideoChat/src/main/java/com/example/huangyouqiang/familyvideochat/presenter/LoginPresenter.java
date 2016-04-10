package com.example.huangyouqiang.familyvideochat.presenter;

import android.support.design.widget.Snackbar;
import android.text.TextUtils;

import com.example.huangyouqiang.familyvideochat.IMManager;
import com.example.huangyouqiang.familyvideochat.LocalSaveManager;
import com.example.huangyouqiang.familyvideochat.UserModel;
import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.example.huangyouqiang.familyvideochat.ui.Navigate;
import com.example.huangyouqiang.familyvideochat.ui.activity.MainActivity;
import com.example.huangyouqiang.familyvideochat.view.LoginView;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by huangyouqiang on 2016/4/7.
 */
public class LoginPresenter {
	private LocalSaveManager localSaveManager;
	private LoginView loginView;
	private UserModel userModel;
	private IMManager mIMManager;

	public LoginPresenter(LoginView loginView){
		this.loginView = loginView;
		this.localSaveManager = LocalSaveManager.getInstance(AndroidApplication.getContext());
	}
	public void init(){
		String userId = localSaveManager.getAccountUserId();
		userModel = new UserModel(userId);
		this.loginView.initView(userModel);

		mIMManager = IMManager.getInstance(AndroidApplication.getContext());
		if(mIMManager == null){
			this.loginView.showMessage("IMManager is null",null);
		}else {
			mIMManager.init();
		}

	}

	public void login(){
			final String username = loginView.getUserId();
			String password = loginView.getPassword();
			if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
				loginView.showLoading();
				mIMManager.login(username, password, new TLSPwdLoginListener() {
					@Override
					public void OnPwdLoginSuccess(TLSUserInfo tlsUserInfo) {
						loginView.hideLoading();
						loginView.showMessage("login success !", new Snackbar.Callback() {
							@Override
							public void onDismissed(Snackbar snackbar, int event) {
								super.onDismissed(snackbar, event);
								Navigate.build().navigateTo(MainActivity.class).add("username", username).start();

							}
						});
						localSaveManager.setAccountInfo(username, mIMManager.getSignature(username));
					}

					@Override
					public void OnPwdLoginReaskImgcodeSuccess(byte[] bytes) {
						loginView.hideLoading();
						loginView.showMessage("OnPwdLoginReaskImgcodeSuccess", null);
					}

					@Override
					public void OnPwdLoginNeedImgcode(byte[] bytes, TLSErrInfo tlsErrInfo) {
						loginView.hideLoading();
						loginView.showMessage("OnPwdLoginNeedImgcode", null);
					}

					@Override
					public void OnPwdLoginFail(TLSErrInfo tlsErrInfo) {
						loginView.hideLoading();
						loginView.showMessage("login failed  " + tlsErrInfo.Title + tlsErrInfo.Msg, null);
					}

					@Override
					public void OnPwdLoginTimeout(TLSErrInfo tlsErrInfo) {
						loginView.hideLoading();
						loginView.showMessage("timeout  " + tlsErrInfo.Msg + tlsErrInfo.Title, null);
					}
				});
			}
	}

	public void toRegister(){
		loginView.ToRegister();
	}
}
