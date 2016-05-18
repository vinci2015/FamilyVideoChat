package com.example.huangyouqiang.familyvideochat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.huangyouqiang.familyvideochat.LocalSaveManager;
import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.UserModel;
import com.example.huangyouqiang.familyvideochat.avsdk.Util;
import com.example.huangyouqiang.familyvideochat.common.DividerItemDecoration;
import com.example.huangyouqiang.familyvideochat.common.adapter.FriendListAdapter;
import com.example.huangyouqiang.familyvideochat.presenter.MainPresenter;
import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.example.huangyouqiang.familyvideochat.ui.Navigate;
import com.example.huangyouqiang.familyvideochat.ui.fragment.MainFragment;
import com.example.huangyouqiang.familyvideochat.ui.fragment.MainFragment.OnFragmentInteractionListener;
import com.example.huangyouqiang.familyvideochat.view.MainView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements OnFragmentInteractionListener,MainView {

	@InjectView(R.id.toolbar) Toolbar toolbar;
	@InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;
	@InjectView(R.id.navigation) NavigationView navigationView;

	private static final String TAG_MainFragment = "MainFragment";
	private static final String TAG = MainActivity.class.getSimpleName();
	private ActionBarDrawerToggle drawerToggle;
	private RecyclerView recyclerView;
	private MainPresenter mainPresenter;
	private FriendListAdapter friendListAdapter;

	@Override
	public void onDeliverMessage(String msg) {

	}

	@Override
	public void showMessage(String msg) {
		showMessage(msg,null);
	}

	@Override
	public void showLoading(boolean isShow) {
		if(isShow){
			mainPresenter.showSomeLoading();
		}
	}

	@Override
	public void hideLoading(boolean isHide) {
		if(isHide){
			mainPresenter.hideSomeLoading();
		}
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			((MainFragment)getFragment(TAG_MainFragment)).deliverBroadcast(intent);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);

		mainPresenter = new MainPresenter(this);
		mainPresenter.init();
		addFragment(R.id.fl_fragment, MainFragment.newInstance(), TAG_MainFragment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main,menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG,"on activity result, requestCode :"+requestCode+" resultCode :"+resultCode);
		if(requestCode == 0) {
			getFragment(TAG_MainFragment).onActivityResult(requestCode, resultCode, data);
		}else if(requestCode == 1){
			if(resultCode == 9){
				return;
			}else if(resultCode == 1){
				//// TODO: 2016/5/17  update friend list adapter t
				ArrayList<UserModel> friendList = data.getParcelableArrayListExtra("friends");
				for (UserModel friend : friendList) {
					friendListAdapter.addItem(friend);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void initView(String username,List<UserModel> friends) {
		TextView drawerUsername = (TextView) navigationView.findViewById(R.id.tv_drawer_username);
		drawerUsername.setText(username);
		Button clearAccountButton = (Button) navigationView.findViewById(R.id.btn_clear_account);
		clearAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LocalSaveManager.getInstance(AndroidApplication.getContext()).clearAccountInfo();
				mainPresenter.logout();
				Navigate.build().navigateTo(LoginActivity.class).start();
			}
		});
		setSupportActionBar(toolbar);
		toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()){
					case R.id.action_add_friend:
						startActivityForResult(new Intent(MainActivity.this,AddFriendActivity.class),1);
						break;
				}
				return false;
			}
		});
		drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close);
		drawerToggle.syncState();
		drawerLayout.addDrawerListener(drawerToggle);

		recyclerView = (RecyclerView) navigationView.findViewById(R.id.recycle_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setHasFixedSize(true);
		friendListAdapter = new FriendListAdapter(friends);
		friendListAdapter.setItemClickListener(new FriendListAdapter.RecycleItemClickListener() {
			@Override
			public void onItemClick(View view, String data) {
				drawerLayout.closeDrawers();
				((MainFragment)getFragment(TAG_MainFragment)).setToPerson(data);
			}
		});
		recyclerView.setAdapter(friendListAdapter);
		recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

		IntentFilter filter = new IntentFilter();
		filter.addAction(Util.ACTION_ROOM_CREATE_COMPLETE);
		filter.addAction(Util.ACTION_CLOSE_ROOM_COMPLETE);
		filter.addAction(Util.ACTION_RECV_INVITE);
		filter.addAction(Util.ACTION_INVITE_CANCELED);
		filter.addAction(Util.ACTION_CHAT_HANGUP);
		filter.addAction(Util.ACTION_START_CONTEXT_COMPLETE);
		registerReceiver(broadcastReceiver,filter);
	}
}
