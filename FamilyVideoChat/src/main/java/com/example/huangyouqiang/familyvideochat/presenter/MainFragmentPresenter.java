package com.example.huangyouqiang.familyvideochat.presenter;

import android.util.Log;

import com.example.huangyouqiang.familyvideochat.AVManager;
import com.example.huangyouqiang.familyvideochat.avsdk.Util;
import com.example.huangyouqiang.familyvideochat.avsdk.control.QavsdkControl;
import com.example.huangyouqiang.familyvideochat.common.ResultMessageListener;
import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.example.huangyouqiang.familyvideochat.ui.fragment.MainFragment.OnFragmentInteractionListener;
import com.example.huangyouqiang.familyvideochat.view.MainFragmentView;
import com.tencent.av.sdk.AVError;

/**
 * Created by huangyouqiang on 2016/4/13.
 */
public class MainFragmentPresenter {
	private static final String TAG =  MainFragmentPresenter.class.getSimpleName();
	private MainFragmentView fragmentView;
	private OnFragmentInteractionListener listener;
	private AVManager mAVManager;
	private String toPerson = "";

	public MainFragmentPresenter(MainFragmentView view,OnFragmentInteractionListener listener){
		this.fragmentView = view;
		this.listener = listener;
		this.mAVManager = AVManager.getInstance(AndroidApplication.getContext());
	}

	public void init(){
		fragmentView.initView();
		mAVManager.getqavsdkControl().setIsSupportMultiView(true);
		mAVManager.getqavsdkControl().setIsOpenBackCameraFirst(false);
	}

	public void setToPerson(String toPerson){
		this.toPerson = toPerson;
		this.fragmentView.showToPerson(toPerson);
	}
	public String getToPerson(){
		return toPerson;
	}
	public void invite(){
		if(getToPerson() != "") {
			if(Util.isNetworkAvailable(AndroidApplication.getContext())) {
				listener.showLoading(true);
				AVManager.getInstance(AndroidApplication.getContext()).invite(getToPerson(), Util.getRandomNumber(), new ResultMessageListener() {
					@Override
					public void onResult(boolean isOk,String msg) {
						listener.showMessage(msg);
						if(!isOk){
							listener.hideLoading(true);
						}
					}
				});
			}else{
				listener.showMessage("network is not available");
			}
		}else{
			Log.e(TAG,"to person is null");
		}
	}
	public void accept(){
		AVManager.getInstance(AndroidApplication.getContext()).accept();
	}
	public void refuse(){
		AVManager.getInstance(AndroidApplication.getContext()).refuse();
	}


	public void onActivityResult(){
		Log.i(TAG,"on activity result");
		QavsdkControl mQavsdkControl = AVManager.getInstance(AndroidApplication.getContext()).getqavsdkControl();
		if ((mQavsdkControl != null) && (mQavsdkControl.getAVContext() != null) && (mQavsdkControl.getAVContext().getAudioCtrl() != null)) {
			Log.i(TAG,"stop traeservice");
			mQavsdkControl.getAVContext().getAudioCtrl().stopTRAEService();
		}
		int closeRoomResultCode = mQavsdkControl.exitRoom();
		if (closeRoomResultCode != AVError.AV_OK) {
			listener.showMessage(closeRoomResultCode + "");
			if (mQavsdkControl != null) {
				mQavsdkControl.setCloseRoomStatus(false);
			}
			return;
		}
		listener.showLoading(true);
	}
}
