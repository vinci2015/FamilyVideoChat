package com.example.huangyouqiang.familyvideochat.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.UserModel;
import com.example.huangyouqiang.familyvideochat.common.DividerItemDecoration;
import com.example.huangyouqiang.familyvideochat.common.adapter.SearchUserListAdapter;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddFriendActivity extends BaseActivity {
	@InjectView(R.id.edt_user)
	EditText edt_user;
	@InjectView(R.id.lv_users)
	RecyclerView lv_users;
	@InjectView(R.id.toolbar)
	Toolbar toolbar;
	private SearchUserListAdapter searchUserListAdapter;
	private List<UserModel> userModelList = new ArrayList<>();
	private ArrayList<UserModel> friendList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		ButterKnife.inject(this);

		toolbar.setTitle("添加好友");
		setSupportActionBar(toolbar);

		lv_users.setLayoutManager(new LinearLayoutManager(this));
		searchUserListAdapter = new SearchUserListAdapter(this,userModelList);
		lv_users.setAdapter(searchUserListAdapter);
		lv_users.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

	}

	@OnClick(R.id.btn_search)
	public void searchUser(View v) {
		String s = edt_user.getText().toString();
		if (TextUtils.isEmpty(s)) {
			showMessage("请输入想要添加的用户名", null);
			return;
		}
		TIMFriendshipManager.getInstance().searchFriend(s, new TIMValueCallBack<TIMUserProfile>() {
			@Override
			public void onError(int i, String s) {

			}

			@Override
			public void onSuccess(TIMUserProfile userProfile) {
				userModelList.add(UserModel.transformFrom(userProfile));
				searchUserListAdapter.updateDatas(userModelList);
			}
		});
	}

	@Override
	public void onBackPressed() {
		if(this.friendList.size() == 0){
			setResult(9);//
		}else {
			setResult(1, getIntent().putParcelableArrayListExtra("friends", this.friendList));
		}
		finish();
	}

	public void addFriend(UserModel userModel){
		this.friendList.add(userModel);
	}
}
