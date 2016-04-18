package com.example.huangyouqiang.familyvideochat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.example.huangyouqiang.familyvideochat.ui.NavigationListener;
import com.example.huangyouqiang.familyvideochat.ui.activity.LoginActivity;
import com.tencent.TIMCallBack;
import com.tencent.TIMConnListener;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;

import java.util.List;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSHelper;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSRefreshUserSigListener;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by huangyouqiang on 2016/3/30.
 */
public class IMManager {

	private static  volatile IMManager instance = null;

	private static final String TAG = IMManager.class.getSimpleName();
	private static final int APP_ID = 1400007396;

	private TIMManager mTIMManager ;
	private TLSHelper mTlsHelper;
	private Context context;



	private IMManager(Context context){
		this.mTIMManager = TIMManager.getInstance();
		this.mTlsHelper = TLSHelper.getInstance();
		this.context = context;
	}

	public static IMManager getInstance(Context context){
		if(instance == null){
			synchronized (IMManager.class){
				if(instance == null){
					instance = new IMManager(context);
				}
			}
		}
		return instance;
	}

	public void init(){
		mTIMManager.setEnv(1);
		mTIMManager.addMessageListener(new TIMMessageListener() {
			@Override
			public boolean onNewMessages(List<TIMMessage> list) {
				return false;
			}
		});
		mTIMManager.setConnectionListener(new TIMConnListener() {
			@Override
			public void onConnected() {
				Log.i(TAG, "connected");
			}

			@Override
			public void onDisconnected(int i, String s) {
				Log.i(TAG, "disconnected");
			}

			@Override
			public void onWifiNeedAuth(String s) {

			}
		});
		mTIMManager.init(this.context, APP_ID);
		mTlsHelper.init(this.context, APP_ID);
		Log.i(TAG,"init complete ");
	}

	public void login(String username,String password,TLSPwdLoginListener loginListener){
		int ret = mTlsHelper.TLSPwdLogin(username, password.getBytes(), loginListener);
		if(ret == TLSErrInfo.LOGIN_OK){
			Log.i(TAG,"login success");
		}
	}
	public void register(String username,String password,TLSStrAccRegListener regListener){
		if(regListener != null) {
			int ret = mTlsHelper.TLSStrAccReg(username, password,regListener);
			if(ret == TLSErrInfo.INPUT_INVALID){
				Log.i(TAG,"input invalid");
			}
		}
	}

	public String getSignature(String userId){
		return mTlsHelper.getUserSig(userId);
	}
	public void login(final NavigationListener listener){
		final LocalSaveManager saveManager =LocalSaveManager.getInstance(AndroidApplication.getContext());
		if(saveManager.getAccountUserId() == ""){
			Log.e(TAG,"login----account is null");
			listener.onFail();
			return;
		}
		if(mTlsHelper != null){
			TLSUserInfo userInfo = mTlsHelper.getLastUserInfo();
			boolean hasLogin = (userInfo != null && !mTlsHelper.needLogin(userInfo.identifier));
			if(hasLogin){
				mTlsHelper.TLSRefreshUserSig(userInfo.identifier,refreshSigListener);
			}else{
				Log.e(TAG,"login---need login");
					listener.onFail();
					saveManager.clearAccountInfo();
					return;
			}
		}
		final String userId = saveManager.getAccountUserId();
		mTIMManager.login(userId, saveManager.getAccountUserSig(userId), new TIMCallBack() {
			@Override
			public void onError(int i, String s) {
				Log.e(TAG,"login---error"+s);
				listener.onFail();
				saveManager.setAccountInfo(userId,"");
			}

			@Override
			public void onSuccess() {
				Log.i(TAG,"login success");
				listener.onSuccess("username", userId);
			}
		});
	}

	TLSRefreshUserSigListener refreshSigListener = new TLSRefreshUserSigListener() {
		@Override
		public void OnRefreshUserSigSuccess(TLSUserInfo tlsUserInfo) {
			Log.i(TAG, "refresh sig success");
			String sig = mTlsHelper.getUserSig(tlsUserInfo.identifier);
			LocalSaveManager.getInstance(AndroidApplication.getContext())
							.setAccountInfo(tlsUserInfo.identifier, sig);
		}

		@Override
		public void OnRefreshUserSigFail(TLSErrInfo tlsErrInfo) {
			Log.e(TAG, "refresh sig failed," + tlsErrInfo.Msg);
			if(tlsErrInfo.ErrCode == TLSErrInfo.LOGIN_WRONG_PWD){
				Log.e(TAG,"you have changed pwd somewhere");
				//ReLogin
				Activity currentActivity = AndroidApplication.getInstance().getCurrentActivity();
				Intent intent = new Intent(currentActivity,LoginActivity.class);
				currentActivity.startActivity(intent);
				currentActivity.finish();
			}
		}

		@Override
		public void OnRefreshUserSigTimeout(TLSErrInfo tlsErrInfo) {
			Log.e(TAG,"timeout");
		}
	};
}
