package com.example.huangyouqiang.familyvideochat.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.avsdk.Util;
import com.example.huangyouqiang.familyvideochat.avsdk.activity.AvActivity;
import com.example.huangyouqiang.familyvideochat.presenter.MainFragmentPresenter;
import com.example.huangyouqiang.familyvideochat.ui.Navigate;
import com.example.huangyouqiang.familyvideochat.view.MainFragmentView;
import com.tencent.av.sdk.AVError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainFragment extends BaseFragment implements  MainFragmentView {

	private static final String TAG = MainFragment.class.getSimpleName();
	Context context;
	AlertDialog alertDialog;
	@InjectView(R.id.toPerson)
	TextView toPerson;
	@InjectView(R.id.iv_video)
	ImageView ivVideo;
	@InjectView(R.id.cv_video)
	CardView cvVideo;
	@InjectView(R.id.iv_audio)
	ImageView ivAudio;
	@InjectView(R.id.cv_audio)
	CardView cvAudio;
	@InjectView(R.id.iv_message)
	ImageView ivMessage;
	@InjectView(R.id.cv_message)
	CardView cvMessage;
	private MainFragmentPresenter mainFragmentPresenter;
	//与activity的通信接口
	private OnFragmentInteractionListener messageListener = null;


	public static MainFragment newInstance() {
		return new MainFragment();
	}

	public MainFragment() {
	}

	@Override
	public void onAttach(Context context) {
		Log.i(TAG,"on attach");
		super.onAttach(context);
		if (context != null) {
			this.context = context;
			this.messageListener = (OnFragmentInteractionListener) context;
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.inject(this,view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mainFragmentPresenter = new MainFragmentPresenter(this,messageListener);
		mainFragmentPresenter.init();
	}


	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		mainFragmentPresenter.onActivityResult();
	}

	//接收从activity传送过来的broadcast，并做处理，相当于一个broadcastReceiver
	public void deliverBroadcast(final Intent intent) {
		String action = intent.getAction();
		if (action.equals(Util.ACTION_ROOM_CREATE_COMPLETE)) {
			messageListener.hideLoading(true);
			int createRoomErrorCode = intent.getIntExtra(Util.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
			if (createRoomErrorCode == AVError.AV_OK) {
				Navigate.build().navigateTo(AvActivity.class).setBack(true).start();
			} else {
				if (messageListener != null) {
					messageListener.showMessage(getString(R.string.create_room_failed) + getString(R.string.error_code_prefix) + createRoomErrorCode);
				}
			}
		} else if (action.equals(Util.ACTION_CLOSE_ROOM_COMPLETE)) {
			messageListener.hideLoading(true);
		} else if (action.equals(Util.ACTION_RECV_INVITE)) {
			alertDialog = new AlertDialog.Builder(context).setTitle("receive invite").setMessage("if accept invite ?")
							.setPositiveButton("yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									mainFragmentPresenter.accept();
									context.removeStickyBroadcast(intent);
									dialog.dismiss();
								}
							})
							.setNegativeButton("no", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									mainFragmentPresenter.refuse();
									dialog.dismiss();
								}
							}).setCancelable(false).show();
		} else if (action.equals(Util.ACTION_INVITE_CANCELED)) {
			if (alertDialog != null) {
				alertDialog.dismiss();
				messageListener.showMessage("peer cancel the invite");
			}
		} else if (action.equals(Util.ACTION_CHAT_HANGUP)) {
			if (alertDialog != null) {
				alertDialog.dismiss();
				messageListener.showMessage("peer hangup the call");
			}
		} else if (action.equals(Util.ACTION_START_CONTEXT_COMPLETE)) {

		}
	}

	public void setToPerson(String person) {
		mainFragmentPresenter.setToPerson(person);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		messageListener = null;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}

	@OnClick(R.id.cv_video)
	void invite(){
		mainFragmentPresenter.invite();
	}

	@Override
	public void initView() {

	}

	@Override
	public void showToPerson(String name) {
		toPerson.setText("talk to "+name);
	}

	public interface OnFragmentInteractionListener {
		void onDeliverMessage(String msg);

		void showMessage(String msg);

		void showLoading(boolean isShow);

		void hideLoading(boolean isHide);
	}
}
