package com.example.huangyouqiang.familyvideochat.presenter;

import com.example.huangyouqiang.familyvideochat.LocalSaveManager;
import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.example.huangyouqiang.familyvideochat.view.MainView;

/**
 * Created by huangyouqiang on 2016/4/8.
 */
public class MainPresenter {

	private MainView mainView;
	public MainPresenter(MainView mainView){
		this.mainView = mainView;
	}
	public void init(){
		String username = LocalSaveManager.getInstance(AndroidApplication.getContext()).getAccountUserId();
		String[] friends = {"Jack","Jim","Curry","vinci","Damn","Trump","Nick","Bruce","Gasol","Nydus"};
		this.mainView.initView(username,friends);

	}

	public void showSomeLoading(){
		mainView.showLoading();
	}
	public void hideSomeLoading(){
		mainView.hideLoading();
	}
}
