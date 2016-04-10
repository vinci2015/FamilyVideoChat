package com.example.huangyouqiang.familyvideochat.view;

import com.example.huangyouqiang.familyvideochat.UserModel;

/**
 * Created by huangyouqiang on 2016/4/7.
 */
public interface LoginView extends BaseView{
	public void initView(UserModel userModel);
	public String getUserId();
	public String getPassword();
	public void ToRegister();
}
