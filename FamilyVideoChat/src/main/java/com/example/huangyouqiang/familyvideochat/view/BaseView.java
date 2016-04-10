package com.example.huangyouqiang.familyvideochat.view;

import android.support.design.widget.Snackbar;

/**
 * Created by huangyouqiang on 2016/4/7.
 */
public interface BaseView {
	public void showMessage(String msg,Snackbar.Callback callback);
	public void showLoading();
	public void hideLoading();
}
