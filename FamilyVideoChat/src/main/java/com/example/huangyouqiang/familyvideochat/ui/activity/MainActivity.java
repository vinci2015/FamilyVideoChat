package com.example.huangyouqiang.familyvideochat.ui.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.common.DividerItemDecoration;
import com.example.huangyouqiang.familyvideochat.common.adapter.FriendListAdapter;
import com.example.huangyouqiang.familyvideochat.presenter.MainPresenter;
import com.example.huangyouqiang.familyvideochat.ui.fragment.MainFragment;
import com.example.huangyouqiang.familyvideochat.view.MainView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements MainFragment.OnDeliverMessageListener,MainView {

	@InjectView(R.id.toolbar) Toolbar toolbar;
	@InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;
	@InjectView(R.id.navigation) NavigationView navigationView;

	private static final String TAG_MainFragment = "MainFragment";
	private ActionBarDrawerToggle drawerToggle;
	private View headerView;
	private RecyclerView recyclerView;
	private MainPresenter mainPresenter;
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
	public void onDeliverMessage(String msg) {
		Log.i(TAG_MainFragment,msg);
	}

	@Override
	public void initView(String username,String[] friends) {
		TextView drawerUsername = (TextView) navigationView.findViewById(R.id.tv_drawer_username);
		drawerUsername.setText(username);
		setSupportActionBar(toolbar);
		drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close);
		drawerToggle.syncState();
		drawerLayout.addDrawerListener(drawerToggle);

		recyclerView = (RecyclerView) navigationView.findViewById(R.id.recycle_view);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setHasFixedSize(true);
		FriendListAdapter adapter = new FriendListAdapter(friends);
		adapter.setItemClickListener(new FriendListAdapter.RecycleItemClickListener() {
			@Override
			public void onItemClick(View view, String data) {
				drawerLayout.closeDrawers();
				((MainFragment)getFragment(TAG_MainFragment)).setToPerson(data);
			}
		});
		recyclerView.setAdapter(adapter);
		recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
	}
}
