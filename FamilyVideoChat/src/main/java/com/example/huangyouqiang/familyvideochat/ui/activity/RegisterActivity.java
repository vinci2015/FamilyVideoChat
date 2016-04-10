package com.example.huangyouqiang.familyvideochat.ui.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.presenter.RegisterPresenter;
import com.example.huangyouqiang.familyvideochat.view.RegisterView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity implements RegisterView{
	@InjectView(R.id.rootview) CoordinatorLayout rootview;
	@InjectView(R.id.rl_progress) RelativeLayout progressWrapper;
	@InjectView(R.id.ttl_username_wrapper) TextInputLayout usernameWrapper;
	@InjectView(R.id.ttl_password_wrapper) TextInputLayout passwordWrapper;
	@InjectView(R.id.btn_register) Button register;
	@InjectView(R.id.toolbar) Toolbar toolbar;

	private RegisterPresenter registerPresent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ButterKnife.inject(this);

		registerPresent = new RegisterPresenter(this);
		registerPresent.init();

	}

	@OnClick(R.id.btn_register)
	public void register(){
		registerPresent.register();
	}


	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void initView() {
		toolbar.setTitle("REGISTER");
		setSupportActionBar(toolbar);

		usernameWrapper.setHint("username");
		passwordWrapper.setHint("password");
		usernameWrapper.setErrorEnabled(true);
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
	public void setUsernameError(String msg) {
		usernameWrapper.setError(msg);
	}

	@Override
	public void setPasswordError(String msg) {
		passwordWrapper.setError(msg);
	}
}
