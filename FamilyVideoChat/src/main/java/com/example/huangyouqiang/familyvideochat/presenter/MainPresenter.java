package com.example.huangyouqiang.familyvideochat.presenter;

import android.util.Log;

import com.example.huangyouqiang.familyvideochat.IMManager;
import com.example.huangyouqiang.familyvideochat.LocalSaveManager;
import com.example.huangyouqiang.familyvideochat.UserModel;
import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.example.huangyouqiang.familyvideochat.view.MainView;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.List;

/**
 * Created by huangyouqiang on 2016/4/8.
 */
public class MainPresenter {

	private MainView mainView;
	public MainPresenter(MainView mainView){
		this.mainView = mainView;
	}
	public void init(){
		final String username = LocalSaveManager.getInstance(AndroidApplication.getContext()).getAccountUserId();
		TIMFriendshipManager.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>() {
			@Override
			public void onError(int i, String s) {
				Log.i("mainPresenter","error  "+s);
			}

			@Override
			public void onSuccess(List<TIMUserProfile> timUserProfileList) {
				Log.i("mainPresenter","success"+timUserProfileList.size());
				List<UserModel> userModelList = UserModel.transformFromList(timUserProfileList);
				mainView.initView(username,userModelList);
			}
		});

	}
	public void logout(){
		IMManager.getInstance(AndroidApplication.getContext()).logout();
	}
	public void showSomeLoading(){
		mainView.showLoading();
	}
	public void hideSomeLoading(){
		mainView.hideLoading();
	}
}
