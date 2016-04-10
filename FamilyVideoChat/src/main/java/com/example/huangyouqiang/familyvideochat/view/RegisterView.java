package com.example.huangyouqiang.familyvideochat.view;

/**
 * Created by huangyouqiang on 2016/4/7.
 */
public interface RegisterView extends BaseView {
	public void initView();
	public String getUserId();
	public String getPassword();
	public void setUsernameError(String msg);
	public void setPasswordError(String msg);
}
