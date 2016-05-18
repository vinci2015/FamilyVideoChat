package com.example.huangyouqiang.familyvideochat.avsdk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.huangyouqiang.familyvideochat.AVManager;
import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.avsdk.DemoConstants;
import com.example.huangyouqiang.familyvideochat.avsdk.ExternalCaptureThread;
import com.example.huangyouqiang.familyvideochat.avsdk.MyCheckable;
import com.example.huangyouqiang.familyvideochat.avsdk.Util;
import com.example.huangyouqiang.familyvideochat.avsdk.control.QavsdkControl;
import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVConstants;
import com.tencent.av.sdk.AVError;
import com.tencent.av.sdk.AVView;
import com.tencent.av.utils.PhoneStatusTools;

public class AvActivity extends Activity implements OnClickListener {
	private static final String TAG = "AvActivity";
	private static final int DIALOG_INIT = 0;
	private static final int DIALOG_AT_ON_CAMERA = DIALOG_INIT + 1;
	private static final int DIALOG_ON_CAMERA_FAILED = DIALOG_AT_ON_CAMERA + 1;
	private static final int DIALOG_AT_OFF_CAMERA = DIALOG_ON_CAMERA_FAILED + 1;
	private static final int DIALOG_OFF_CAMERA_FAILED = DIALOG_AT_OFF_CAMERA + 1;
	private static final int DIALOG_AT_SWITCH_FRONT_CAMERA = DIALOG_OFF_CAMERA_FAILED + 1;
	private static final int DIALOG_SWITCH_FRONT_CAMERA_FAILED = DIALOG_AT_SWITCH_FRONT_CAMERA + 1;
	private static final int DIALOG_AT_SWITCH_BACK_CAMERA = DIALOG_SWITCH_FRONT_CAMERA_FAILED + 1;
	private static final int DIALOG_SWITCH_BACK_CAMERA_FAILED = DIALOG_AT_SWITCH_BACK_CAMERA + 1;
	
	private static final int DIALOG_AT_ON_EXTERNAL_CAPTURE = DIALOG_SWITCH_BACK_CAMERA_FAILED + 1;
	private static final int DIALOG_AT_ON_EXTERNAL_CAPTURE_FAILED = DIALOG_AT_ON_EXTERNAL_CAPTURE + 1;
	private static final int DIALOG_AT_OFF_EXTERNAL_CAPTURE = DIALOG_AT_ON_EXTERNAL_CAPTURE_FAILED + 1;
	private static final int DIALOG_AT_OFF_EXTERNAL_CAPTURE_FAILED = DIALOG_AT_OFF_EXTERNAL_CAPTURE + 1;
	private static final int DIALOG_CHANGE_AUTHRITY_OK = DIALOG_AT_OFF_EXTERNAL_CAPTURE_FAILED + 1;
	private static final int DIALOG_CHANGE_AUTHRITY_FAILED = DIALOG_CHANGE_AUTHRITY_OK + 1;
	private static final int DIALOG_QUIT = DIALOG_CHANGE_AUTHRITY_FAILED+1;
	private boolean mIsPaused = false;
	private int mOnOffCameraErrorCode = AVError.AV_OK;
	private int mSwitchCameraErrorCode = AVError.AV_OK;
	private int mEnableExternalCaptureErrorCode = AVError.AV_OK;
	private ProgressDialog mDialogInit = null;
	private ProgressDialog mDialogAtOnCamera = null;
	private ProgressDialog mDialogAtOffCamera = null;
	
	private ProgressDialog mDialogAtOnExternalCapture = null;
	private ProgressDialog mDialogAtOffExternalCapture = null;
	
	private ProgressDialog mDialogAtSwitchFrontCamera = null;
	private ProgressDialog mDialogAtSwitchBackCamera = null;
	private AlertDialog mDialogQuit = null;
	private QavsdkControl mQavsdkControl;
	private AVManager mAVmanager;
	private String mRecvIdentifier = "";
	private String mSelfIdentifier = "";
	OrientationEventListener mOrientationEventListener = null;
	int mRotationAngle = 0;	
	private static final int TIMER_INTERVAL = 2000; //2s检查一次
	private Context ctx;
	
	private ExternalCaptureThread inputStreamThread;
	private boolean isUserRendEnable = false;
	private Button recordButton;;

	
	private BroadcastReceiver connectionReceiver = new BroadcastReceiver() { 
		@Override 
		public void onReceive(Context context, Intent intent) { 
			int netType = Util.getNetWorkType(ctx);
			Log.e(TAG, "WL_DEBUG connectionReceiver getNetWorkType = " + netType);
			mQavsdkControl.setNetType(netType);	
			

		} 
	};	
	private MyCheckable mMuteCheckable = new MyCheckable(true) {
		@Override
		protected void onCheckedChanged(boolean checked) {
			Button button = (Button) findViewById(R.id.qav_bottombar_mute);
			AVAudioCtrl avAudioCtrl = mQavsdkControl.getAVContext().getAudioCtrl();

			if (checked) {
				button.setSelected(false);
				button.setText(R.string.gaudio_close_mic_acc_txt);
				avAudioCtrl.enableMic(true);
			} else {
				button.setSelected(true);
				button.setText(R.string.gaudio_open_mic_acc_txt);
				avAudioCtrl.enableMic(false);
			}
		}
	};

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "WL_DEBUG onReceive action = " + action);
			if (action.equals(Util.ACTION_SURFACE_CREATED)) {
				locateCameraPreview();
				boolean isEnable = mAVmanager.getIsEnableCamera();
				mOnOffCameraErrorCode = mAVmanager.toggleEnableCamera();
				refreshCameraUI();
				if (mOnOffCameraErrorCode != AVError.AV_OK) {
					showDialog(isEnable ? DIALOG_OFF_CAMERA_FAILED : DIALOG_ON_CAMERA_FAILED);
					mQavsdkControl.setIsInOnOffCamera(false);
					refreshCameraUI();
				}
			//	mMuteCheckable.toggle();
			} else if (action.equals(Util.ACTION_VIDEO_CLOSE)) {
				String identifier = intent.getStringExtra(Util.EXTRA_IDENTIFIER);
				int videoSrcType = intent.getIntExtra(Util.EXTRA_VIDEO_SRC_TYPE, AVView.VIDEO_SRC_TYPE_NONE);
				mRecvIdentifier = identifier;
				if (!TextUtils.isEmpty(mRecvIdentifier) && videoSrcType != AVView.VIDEO_SRC_TYPE_NONE) {
					mQavsdkControl.setRemoteHasVideo(false, mRecvIdentifier);
				}
													
			} else if (action.equals(Util.ACTION_VIDEO_SHOW)) {
				String identifier = intent.getStringExtra(Util.EXTRA_IDENTIFIER);
				int videoSrcType = intent.getIntExtra(Util.EXTRA_VIDEO_SRC_TYPE, AVView.VIDEO_SRC_TYPE_NONE);
				mRecvIdentifier = identifier;	
				if (!TextUtils.isEmpty(mRecvIdentifier) && videoSrcType != AVView.VIDEO_SRC_TYPE_NONE) {			
					mQavsdkControl.setRemoteHasVideo(true, mRecvIdentifier);
				}				
			} else if (action.equals(Util.ACTION_ENABLE_CAMERA_COMPLETE)) {
				refreshCameraUI();

				mOnOffCameraErrorCode = intent.getIntExtra(Util.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
				boolean isEnable = intent.getBooleanExtra(Util.EXTRA_IS_ENABLE, false);
						
				if (mOnOffCameraErrorCode == AVError.AV_OK) {
					if (!mIsPaused) {
						mQavsdkControl.setSelfId(mSelfIdentifier);
						mQavsdkControl.setLocalHasVideo(isEnable, mSelfIdentifier);							
					}
				} else {
					showDialog(isEnable ? DIALOG_ON_CAMERA_FAILED : DIALOG_OFF_CAMERA_FAILED);
				}
				//开启渲染回调的接口
				//mQavsdkControl.setRenderCallback();
			} else if (action.equals(Util.ACTION_ENABLE_EXTERNAL_CAPTURE_COMPLETE)) {
				refreshCameraUI();
				mOnOffCameraErrorCode = intent.getIntExtra(Util.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
				boolean isEnable = intent.getBooleanExtra(Util.EXTRA_IS_ENABLE, false);
						
				if (mOnOffCameraErrorCode == AVError.AV_OK) {
					//打开外部摄像头之后就开始传输，用户可以实现自己的逻辑
					//test
					
					if (isEnable) {
						inputStreamThread = new ExternalCaptureThread(getApplicationContext());
						inputStreamThread.start();
					} else {
						if (inputStreamThread != null) {
							inputStreamThread.canRun = false;
							inputStreamThread = null;
						}
					} 
				} else {
					showDialog(isEnable ? DIALOG_AT_ON_EXTERNAL_CAPTURE_FAILED : DIALOG_AT_OFF_EXTERNAL_CAPTURE_FAILED);
				}
			} else if (action.equals(Util.ACTION_SWITCH_CAMERA_COMPLETE)) {
				refreshCameraUI();

				mSwitchCameraErrorCode = intent.getIntExtra(Util.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
				boolean isFront = intent.getBooleanExtra(Util.EXTRA_IS_FRONT, false);
				if (mSwitchCameraErrorCode != AVError.AV_OK) {
					showDialog(isFront ? DIALOG_SWITCH_FRONT_CAMERA_FAILED : DIALOG_SWITCH_BACK_CAMERA_FAILED);
				}
			} else if (action.equals(Util.ACTION_MEMBER_CHANGE)) {
				mQavsdkControl.onMemberChange();
			} else if (action.equals(Util.ACTION_CHANGE_AUTHRITY)) {
				int result = intent.getIntExtra(Util.EXTRA_AV_ERROR_RESULT, AVError.AV_OK);
				if (result == AVError.AV_OK) {
					showDialog(DIALOG_CHANGE_AUTHRITY_OK);
				}
				else {
					showDialog(DIALOG_CHANGE_AUTHRITY_FAILED);
				}
			} else if (action.equals(Util.ACTION_OUTPUT_MODE_CHANGE)) {
				updateHandfreeButton();
			} else if (action.equals(Util.ACTION_CLOSE_ROOM_COMPLETE)) {
				if (isFinishing()) {
					return;
				} else {
					AvActivity.this.setResult(DemoConstants.AUTO_EXIT_ROOM);
					finish();
				}
			}else if(action.equals(Util.ACTION_ACCEPT_COMPLETE)){
				Toast.makeText(AvActivity.this, "accept", Toast.LENGTH_LONG).show();
			}else if(action.equals(Util.ACTION_REFUSE_COMPLETE)){
				Toast.makeText(AvActivity.this, "refuse", Toast.LENGTH_LONG).show();
				finish();
			}else if(action.equals(Util.ACTION_INVITE_CONFLICT)){
				Toast.makeText(AvActivity.this, "对方正在通话中", Toast.LENGTH_LONG).show();
				removeStickyBroadcast(intent);
				finish();
			}else if(action.equals(Util.ACTION_CHAT_HANGUP)){
				Toast.makeText(AvActivity.this, "对方挂断了视频", Toast.LENGTH_LONG).show();
				finish();
			}else if(action.equals(Util.ACTION_PEER_CAMERA_OPEN)){
				AVManager.getInstance(AndroidApplication.getContext()).setSelfId(mSelfIdentifier);
				AVManager.getInstance(AndroidApplication.getContext()).setRemoteHasVideo(true, mRecvIdentifier);
				removeStickyBroadcast(intent);
			}else if(action.equals(Util.ACTION_PEER_CAMERA_CLOSE)){
				AVManager.getInstance(AndroidApplication.getContext()).setRemoteHasVideo(false, mRecvIdentifier);
				removeStickyBroadcast(intent);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "WL_DEBUG onCreate start");
		super.onCreate(savedInstanceState);
		ctx = this;
		setContentView(R.layout.av_activity);
		findViewById(R.id.qav_bottombar_handfree).setOnClickListener(this);
		findViewById(R.id.qav_bottombar_mute).setOnClickListener(this);
		findViewById(R.id.qav_bottombar_camera).setOnClickListener(this);
		findViewById(R.id.qav_bottombar_hangup).setOnClickListener(this);
		findViewById(R.id.qav_bottombar_switchcamera).setOnClickListener(this);

		

		// 注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Util.ACTION_SURFACE_CREATED);
		intentFilter.addAction(Util.ACTION_VIDEO_SHOW);
		intentFilter.addAction(Util.ACTION_VIDEO_CLOSE);
		intentFilter.addAction(Util.ACTION_ENABLE_CAMERA_COMPLETE);
		intentFilter.addAction(Util.ACTION_ENABLE_EXTERNAL_CAPTURE_COMPLETE);
		intentFilter.addAction(Util.ACTION_SWITCH_CAMERA_COMPLETE);
		intentFilter.addAction(Util.ACTION_MEMBER_CHANGE);
		intentFilter.addAction(Util.ACTION_OUTPUT_MODE_CHANGE);
		intentFilter.addAction(Util.ACTION_REFUSE_COMPLETE);
		intentFilter.addAction(Util.ACTION_ACCEPT_COMPLETE);
		intentFilter.addAction(Util.ACTION_INVITE_CANCELED);
		intentFilter.addAction(Util.ACTION_INVITE_CONFLICT);
		intentFilter.addAction(Util.ACTION_CHAT_HANGUP);
		intentFilter.addAction(Util.ACTION_PEER_CAMERA_CLOSE);
		intentFilter.addAction(Util.ACTION_PEER_CAMERA_OPEN);
		registerReceiver(mBroadcastReceiver, intentFilter);
		
		IntentFilter netIntentFilter = new IntentFilter(); 
		netIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
		registerReceiver(connectionReceiver, netIntentFilter);				

		//showDialog(DIALOG_INIT);

		mAVmanager = AVManager.getInstance(AndroidApplication.getContext());
		mQavsdkControl = AVManager.getInstance(AndroidApplication.getContext()).getqavsdkControl();
	
		int netType = Util.getNetWorkType(ctx);
		Log.e(TAG, "WL_DEBUG connectionReceiver onCreate = " + netType);
		if (netType != AVConstants.NETTYPE_NONE) {
			mQavsdkControl.setNetType(Util.getNetWorkType(ctx));		
		}
		mRecvIdentifier = AVManager.getInstance(this).getPeerId();
		mSelfIdentifier = AVManager.getInstance(AndroidApplication.getContext()).getSelfId();
		if (mQavsdkControl.getAVContext() != null) {
			mQavsdkControl.onCreate(getApplication(), findViewById(android.R.id.content));
			findViewById(R.id.qav_bottombar_camera).setVisibility(View.VISIBLE);
			updateHandfreeButton();
		} else {
			finish();
		}
		registerOrientationListener();
		AVManager.getInstance(AndroidApplication.getContext()).setIsInRoom(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		mIsPaused = false;
		mQavsdkControl.onResume();
		refreshCameraUI();
		if (mOnOffCameraErrorCode != AVError.AV_OK) {
			showDialog(DIALOG_ON_CAMERA_FAILED);
		}
		startOrientationListener();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mIsPaused = true;
		mQavsdkControl.onPause();
		refreshCameraUI();
		if (mOnOffCameraErrorCode != AVError.AV_OK) {
			showDialog(DIALOG_OFF_CAMERA_FAILED);
		}
        stopOrientationListener();	
	}
	@Override
	public void onBackPressed() {
		showDialog(DIALOG_QUIT);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("memoryLeak", "memoryLeak avactivity onDestroy");			
		mQavsdkControl.onDestroy();
		// 注销广播
		if (mBroadcastReceiver != null) {
			unregisterReceiver(mBroadcastReceiver);	
		}
		if (connectionReceiver != null) { 
			unregisterReceiver(connectionReceiver); 
		}	
		Log.e("memoryLeak", "memoryLeak avactivity onDestroy end");
		Log.d(TAG, "WL_DEBUG onDestroy");
		
		if (inputStreamThread != null) {
			inputStreamThread.canRun = false;
			inputStreamThread = null;
		}
		AVManager.getInstance(AndroidApplication.getContext()).setIsInRoom(false);
	}

	private void locateCameraPreview() {
//		SurfaceView localVideo = (SurfaceView) findViewById(r.id.av_video_surfaceView);
//		MarginLayoutParams params = (MarginLayoutParams) localVideo.getLayoutParams();
//		params.leftMargin = -3000;
//		localVideo.setLayoutParams(params);

		if (mDialogInit != null && mDialogInit.isShowing()) {
			mDialogInit.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.qav_bottombar_handfree:
			mQavsdkControl.getAVContext().getAudioCtrl()
							.setAudioOutputMode(mQavsdkControl.getHandfreeChecked() ? AVAudioCtrl.OUTPUT_MODE_SPEAKER : AVAudioCtrl.OUTPUT_MODE_HEADSET);
			break;
		case R.id.qav_bottombar_mute:
			mMuteCheckable.toggle();
			break;
		case R.id.qav_bottombar_camera:
			boolean isEnable = mQavsdkControl.getIsEnableCamera();
			mOnOffCameraErrorCode = mQavsdkControl.toggleEnableCamera();
			refreshCameraUI();
			if (mOnOffCameraErrorCode != AVError.AV_OK) {
				showDialog(isEnable ? DIALOG_OFF_CAMERA_FAILED : DIALOG_ON_CAMERA_FAILED);
				mQavsdkControl.setIsInOnOffCamera(false);
				refreshCameraUI();
			}
			break;
		case R.id.qav_bottombar_hangup:
			mAVmanager.hangUp(mRecvIdentifier);
			finish();
			break;
		case R.id.qav_bottombar_switchcamera:
			boolean isFront = mQavsdkControl.getIsFrontCamera();
			mSwitchCameraErrorCode = mQavsdkControl.toggleSwitchCamera();
			refreshCameraUI();
			if (mSwitchCameraErrorCode != AVError.AV_OK) {
				showDialog(isFront ? DIALOG_SWITCH_BACK_CAMERA_FAILED : DIALOG_SWITCH_FRONT_CAMERA_FAILED);
				mQavsdkControl.setIsInSwitchCamera(false);
				refreshCameraUI();
			}
			break;

			

		default:
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		switch (id) {
		case DIALOG_INIT:
			dialog = mDialogInit = Util.newProgressDialog(this, R.string.interface_initialization);
			break;
		case DIALOG_AT_ON_CAMERA:
			dialog = mDialogAtOnCamera = Util.newProgressDialog(this, R.string.at_on_camera);
			break;
		case DIALOG_ON_CAMERA_FAILED:
			dialog = Util.newErrorDialog(this, R.string.on_camera_failed);
			break;
		case DIALOG_AT_OFF_CAMERA:
			dialog = mDialogAtOffCamera = Util.newProgressDialog(this, R.string.at_off_camera);
			break;
		case DIALOG_OFF_CAMERA_FAILED:
			dialog = Util.newErrorDialog(this, R.string.off_camera_failed);
			break;
			
		case DIALOG_AT_ON_EXTERNAL_CAPTURE:
			dialog = mDialogAtOnExternalCapture = Util.newProgressDialog(this, R.string.at_on_external_capture);
			break;
		case DIALOG_AT_ON_EXTERNAL_CAPTURE_FAILED:
			dialog = Util.newErrorDialog(this, R.string.on_external_capture_failed);
			break;
		case DIALOG_AT_OFF_EXTERNAL_CAPTURE:
			dialog = mDialogAtOffExternalCapture = Util.newProgressDialog(this, R.string.at_off_external_capture);
			break;
		case DIALOG_AT_OFF_EXTERNAL_CAPTURE_FAILED:
			dialog = Util.newErrorDialog(this, R.string.off_external_capture_failed);
			break;
			
		case DIALOG_AT_SWITCH_FRONT_CAMERA:
			dialog = mDialogAtSwitchFrontCamera = Util.newProgressDialog(this, R.string.at_switch_front_camera);
			break;
		case DIALOG_SWITCH_FRONT_CAMERA_FAILED:
			dialog = Util.newErrorDialog(this, R.string.switch_front_camera_failed);
			break;
		case DIALOG_AT_SWITCH_BACK_CAMERA:
			dialog = mDialogAtSwitchBackCamera = Util.newProgressDialog(this, R.string.at_switch_back_camera);
			break;
		case DIALOG_SWITCH_BACK_CAMERA_FAILED:
			dialog = Util.newErrorDialog(this, R.string.switch_back_camera_failed);
			break;
		case DIALOG_QUIT:
				mDialogQuit = new AlertDialog.Builder(this)
								.setTitle(R.string.quit_tittle)
								.setPositiveButton(android.R.string.ok,
												new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog,
													                    int whichButton) {
														AVManager.getInstance(AndroidApplication.getContext()).hangUp(mRecvIdentifier);
														//	finish();
													}
												})
								.setNegativeButton(android.R.string.cancel,
												new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog,
													                    int whichButton) {
														dialog.dismiss();
													}
												})
								.setOnCancelListener(
												new DialogInterface.OnCancelListener() {

													@Override
													public void onCancel(DialogInterface dialog) {
														Log.e(TAG, "WL_DEBUG onCancel");
														dialog.dismiss();
													}
												}).create();
				dialog = mDialogQuit;
			break;
		default:
			break;
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_ON_CAMERA_FAILED:
		case DIALOG_OFF_CAMERA_FAILED:
			((AlertDialog) dialog).setMessage(getString(R.string.error_code_prefix) + mOnOffCameraErrorCode);
			break;
		case DIALOG_SWITCH_FRONT_CAMERA_FAILED:
		case DIALOG_SWITCH_BACK_CAMERA_FAILED:
			((AlertDialog) dialog).setMessage(getString(R.string.error_code_prefix) + mSwitchCameraErrorCode);
			break;
			
		case DIALOG_AT_ON_EXTERNAL_CAPTURE_FAILED:
		case DIALOG_AT_OFF_EXTERNAL_CAPTURE_FAILED:
			((AlertDialog) dialog).setMessage(getString(R.string.error_code_prefix) + mEnableExternalCaptureErrorCode);
			break;
			
		default:
			break;
		}
	}

	private void refreshCameraUI() {
		boolean isEnable = mQavsdkControl.getIsEnableCamera();
		boolean isFront = mQavsdkControl.getIsFrontCamera();
		boolean isInOnOffCamera = mQavsdkControl.getIsInOnOffCamera();
		boolean isInSwitchCamera = mQavsdkControl.getIsInSwitchCamera();
		Button buttonEnableCamera = (Button) findViewById(R.id.qav_bottombar_camera);
		Button buttonSwitchCamera = (Button) findViewById(R.id.qav_bottombar_switchcamera);

		
		if (isEnable) {
			buttonEnableCamera.setSelected(true);
			buttonEnableCamera.setText(R.string.audio_close_camera_acc_txt);
			buttonSwitchCamera.setVisibility(View.VISIBLE);
		} else {
			buttonEnableCamera.setSelected(false);
			buttonEnableCamera.setText(R.string.audio_open_camera_acc_txt);
			buttonSwitchCamera.setVisibility(View.GONE);
		}

		if (isFront) {
			buttonSwitchCamera.setText(R.string.gaudio_switch_camera_front_acc_txt);
		} else {
			buttonSwitchCamera.setText(R.string.gaudio_switch_camera_back_acc_txt);
		}
		if (isInOnOffCamera) {
			if (isEnable) {
				Util.switchWaitingDialog(this, mDialogAtOffCamera, DIALOG_AT_OFF_CAMERA, true);
				Util.switchWaitingDialog(this, mDialogAtOnCamera, DIALOG_AT_ON_CAMERA, false);
			} else {
				Util.switchWaitingDialog(this, mDialogAtOffCamera, DIALOG_AT_OFF_CAMERA, false);
				Util.switchWaitingDialog(this, mDialogAtOnCamera, DIALOG_AT_ON_CAMERA, true);
			}
		} else {
			Util.switchWaitingDialog(this, mDialogAtOffCamera, DIALOG_AT_OFF_CAMERA, false);
			Util.switchWaitingDialog(this, mDialogAtOnCamera, DIALOG_AT_ON_CAMERA, false);
		}

		if (isInSwitchCamera) {
			if (isFront) {
				Util.switchWaitingDialog(this, mDialogAtSwitchBackCamera, DIALOG_AT_SWITCH_BACK_CAMERA, true);
				Util.switchWaitingDialog(this, mDialogAtSwitchFrontCamera, DIALOG_AT_SWITCH_FRONT_CAMERA, false);
			} else {
				Util.switchWaitingDialog(this, mDialogAtSwitchBackCamera, DIALOG_AT_SWITCH_BACK_CAMERA, false);
				Util.switchWaitingDialog(this, mDialogAtSwitchFrontCamera, DIALOG_AT_SWITCH_FRONT_CAMERA, true);
			}
		} else {
			Util.switchWaitingDialog(this, mDialogAtSwitchBackCamera, DIALOG_AT_SWITCH_BACK_CAMERA, false);
			Util.switchWaitingDialog(this, mDialogAtSwitchFrontCamera, DIALOG_AT_SWITCH_FRONT_CAMERA, false);
		}
	}

	private void updateHandfreeButton() {
		Button button = (Button) findViewById(R.id.qav_bottombar_handfree);

		if (mQavsdkControl.getHandfreeChecked()) {
			button.setSelected(true);
			button.setText(R.string.audio_switch_to_speaker_mode_acc_txt);
		} else {
			button.setSelected(false);
			button.setText(R.string.audio_switch_to_headset_mode_acc_txt);
		}
	}
	class VideoOrientationEventListener extends OrientationEventListener {
		
		boolean mbIsTablet = false;	
	
		public VideoOrientationEventListener(Context context, int rate) {
			super(context, rate);
			mbIsTablet = PhoneStatusTools.isTablet(context);
		}

		int mLastOrientation = -25;
		@Override
		public void onOrientationChanged(int orientation) {
			if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
				if (mLastOrientation != orientation) {
					/*
					 * if (mControlUI != null) { mControlUI.setRotation(270); }
					 * if (mVideoLayerUI != null) {
					 * mVideoLayerUI.setRotation(270); }
					 */
				}
				mLastOrientation = orientation;
				return;
			}

			if (mLastOrientation < 0) {
				mLastOrientation = 0;
			}

			if (((orientation - mLastOrientation) < 20) 
					&& ((orientation - mLastOrientation) > -20)) {
				return;
			}
			
			
			if(mbIsTablet){
				orientation -= 90;
				if (orientation < 0) {
					orientation += 360;
				} 
			}
				
			mLastOrientation = orientation;
            if (orientation > 314 || orientation < 45) {
                if (mQavsdkControl != null) {
                	mQavsdkControl.setRotation(0);
 
                }

                mRotationAngle = 0;
            } else if (orientation > 44 && orientation < 135) {
                if (mQavsdkControl != null) {
                	mQavsdkControl.setRotation(90);               	
                }
                mRotationAngle = 90;
            } else if (orientation > 134 && orientation < 225) {
                if (mQavsdkControl != null) {
                	mQavsdkControl.setRotation(180);             	
                }
                mRotationAngle = 180;
            } else {
                if (mQavsdkControl != null) {
                	mQavsdkControl.setRotation(270);              	
                }
                mRotationAngle = 270;
            }
		}		
	}
	
	void registerOrientationListener() {
		if (mOrientationEventListener == null) {
			mOrientationEventListener = new VideoOrientationEventListener(super.getApplicationContext(), SensorManager.SENSOR_DELAY_UI);
		}
	}
	
	void startOrientationListener() {
		if (mOrientationEventListener != null) {
			mOrientationEventListener.enable();
		}
	}

	void stopOrientationListener() {
		if (mOrientationEventListener != null) {
			mOrientationEventListener.disable();
		}
	} 
}