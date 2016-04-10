package com.example.huangyouqiang.familyvideochat.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import com.example.huangyouqiang.familyvideochat.presenter.LoginPresenter;
import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.UserModel;
import com.example.huangyouqiang.familyvideochat.ui.Navigate;
import com.example.huangyouqiang.familyvideochat.view.LoginView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements LoginView{

	@InjectView(R.id.ttl_username_wrapper) TextInputLayout usernameWrapper;
	@InjectView(R.id.ttl_password_wrapper) TextInputLayout passwordWrapper;
	@InjectView(R.id.toolbar) Toolbar toolbar;

	private LoginPresenter loginPresent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		ButterKnife.inject(this);

		loginPresent = new LoginPresenter(this);
		loginPresent.init();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 0){
			usernameWrapper.getEditText().setText(data.getStringExtra("username"));
		}
	}

	@OnClick(R.id.btn_login)
	public void login(){
		loginPresent.login();
	}
	@OnClick(R.id.fab)
	public void toRegister(){
		loginPresent.toRegister();
	}

	@Override
	public void initView(UserModel userModel) {
		toolbar.setTitle("LOGIN");
		setSupportActionBar(toolbar);
		String userId = userModel.getUserId();
		if(userId != ""){
			usernameWrapper.getEditText().setText(userId);
		}
		usernameWrapper.setHint("username");
		passwordWrapper.setHint("password");

	}

	@Override
	public String getUserId() {
		return usernameWrapper.getEditText().getText().toString().trim();
	}

	@Override
	public String getPassword() {
		return passwordWrapper.getEditText().getText().toString().trim();
	}

	@Override
	public void ToRegister() {
		Navigate.build()
						.navigateTo(RegisterActivity.class)
						.setBack(true)
						.start();
	}


}
