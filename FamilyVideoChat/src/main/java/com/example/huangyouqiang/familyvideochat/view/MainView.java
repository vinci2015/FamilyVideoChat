package com.example.huangyouqiang.familyvideochat.view;

import com.example.huangyouqiang.familyvideochat.UserModel;

import java.util.List;

/**
 * Created by huangyouqiang on 2016/4/8.
 */
public interface MainView extends BaseView {
	void initView(String username,List<UserModel> friends);
}
