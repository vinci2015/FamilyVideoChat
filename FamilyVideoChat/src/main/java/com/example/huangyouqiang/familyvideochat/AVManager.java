package com.example.huangyouqiang.familyvideochat;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.huangyouqiang.familyvideochat.avsdk.control.QavsdkControl;
import com.example.huangyouqiang.familyvideochat.ui.AndroidApplication;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVView;

import java.util.Date;
import java.util.List;

import tencent.tls.platform.TLSLoginHelper;

public class AVManager implements AVChatStartContextCallBack{
	private static String ImageDownloadLocalPath = "";
	//是否在视频房间中
	private int roomId;
	private String robotId = "root";
	private boolean isInRoom = false;
	private static final String TAG = "AVManager";
	private Context context ;
	private QavsdkControl mQavsdkControl = null;
	private TLSLoginHelper tlsloginHelper;
	private TIMConversation conversation = null;
	private IMManager mIMManager;

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

//	private AVChatCallBack AVChatCallback;
	/*
	 * 扬声器模式
	 */
	public static final int OUTPUT_MODE_SPEAKER = 1;
	/*
	 * 听筒模式
	 */
	public static final int OUTPUT_MODE_HEADSET = 0;
	private AVManager(Context context){
		this.context = context;
		mQavsdkControl = new QavsdkControl(context);
		mIMManager = IMManager.getInstance(context);
		tlsloginHelper = TLSLoginHelper.getInstance();
	};
	
	private static volatile AVManager instance = null;
	
	public static AVManager getInstance(Context context){
		if(instance == null){
			synchronized (AVManager.class) {
				if(instance == null){
					instance = new AVManager(context);
				}
			}
		}
		return instance;
	}
	/**
	 * 初始化
	 * 
	 */
	public void initSdk(){
//		mIMManager = IMManager.getInstance(context);
//		mIMManager.initSdk();
		addAVMessageListener();
	}
	/**
	 * 登录 包含账号的登录以及音视频sdk的开启
	 * @param mStartContxtCallBack
	 */
	public void login(final AVChatStartContextCallBack mStartContxtCallBack){

	}
	/*
	 * 注册
	 */
	public void register(){
	}
	/*
	 * 视频邀请
	 * 返回0表示正常，返回以表示失败
	 */
	public int invite(String mReceiveIdentifier,final int relationId){
	//	mQavsdkControl.invite(mReceiveIdentifier, isVideo);
		Log.i(TAG, "invite()"+ mReceiveIdentifier);
		if(mReceiveIdentifier.equals(LocalSaveManager.getInstance(AndroidApplication.getContext()).getAccountUserId())){
			Log.d(TAG, "不能邀请自己");
			return 1;
		}
		conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, mReceiveIdentifier);
		TIMMessage message = new TIMMessage();
		TIMTextElem  elemCommand = new TIMTextElem();
		
		elemCommand.setText("0");
		TIMTextElem elemContent = new TIMTextElem();
		elemContent.setText(String.valueOf(relationId));
		int m1 = message.addElement(elemCommand);
		int m2 = message.addElement(elemContent);
		conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
			
			@Override
			public void onSuccess(TIMMessage arg0) {
				Log.i(TAG, "send invite message successful");
				if(!isInRoom){
					//邀请信息送达至对方后，先行进入视频房间中
					enterRoom(relationId,LocalSaveManager.getInstance(AndroidApplication.getContext()).getAccountUserId());
				}
			}
			
			@Override
			public void onError(int code, String desc) {
				Log.e(TAG, "fail to send invite, code : "+code+" desc :"+desc);
				Toast.makeText(context, "邀请失败,对方账号未登录或不存在", Toast.LENGTH_LONG).show();
			}
		});
		return 0;
	}
	/**
	 * 监控
	 * @param mReceiveIdentifier 对方id
	 * @param relationId 房间号
	 */
	public int monitor(String mReceiveIdentifier,final int relationId){
		Log.i(TAG, "monitor()"+ mReceiveIdentifier);
		if(mReceiveIdentifier.equals(LocalSaveManager.getInstance(AndroidApplication.getContext()).getAccountUserId())){
			Log.d(TAG, "不能邀请自己");
			return 1;
		}
		setPeerId(mReceiveIdentifier);
		conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, mReceiveIdentifier);
		TIMMessage message = new TIMMessage();
		TIMTextElem  elemCommand = new TIMTextElem();
		
		elemCommand.setText("6");
		TIMTextElem elemContent = new TIMTextElem();
		elemContent.setText(String.valueOf(relationId));
		int m1 = message.addElement(elemCommand);
		int m2 = message.addElement(elemContent);
		//Log.e(TAG, "m1: "+m1+"  m2:"+m2+"    "+elemContent.getText());
		conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
			
			@Override
			public void onSuccess(TIMMessage arg0) {
				Log.i(TAG, "send monitor message successful");
				enterRoom(relationId,LocalSaveManager.getInstance(AndroidApplication.getContext()).getAccountUserId());
			}
			
			@Override
			public void onError(int code, String desc) {
				Log.e(TAG, "fail to send monitor, code : "+code+" desc :"+desc);
				Toast.makeText(context, "邀请失败,对方账号未登录或不存在", Toast.LENGTH_LONG).show();
			}
		});
		return 0;
	}
	/*
	 * 发送信息
	 */
	public void sendText(String peerId,String content){
		TIMConversation mconversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, peerId);
		TIMMessage message = new TIMMessage();
		TIMTextElem elemcommand = new TIMTextElem();
		elemcommand.setText("-1");
		TIMTextElem elemContent = new TIMTextElem();
		elemContent.setText(content);
		message.addElement(elemcommand);
		message.addElement(elemContent);
		mconversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
			
			@Override
			public void onSuccess(TIMMessage arg0) {
				Log.i(TAG, "yes sendText");
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				Log.i(TAG, "no sendText");
			}
		});
	}
	/*
	 * 进入视频房间
	 */
	private void enterRoom(int relationId,String roomRole){
		Log.i(TAG, "enter room---getRoom :"+mQavsdkControl.getAVContext().getRoom());
		if(mQavsdkControl.getAVContext().getRoom()== null){//同一时间只能创建一个视频房间
			mQavsdkControl.enterRoom(relationId,roomRole);
		}
		setRoomId(relationId);
	}
	
	public QavsdkControl getqavsdkControl(){
		return mQavsdkControl;
	}
	

	public int startAVChatContext(AVChatStartContextCallBack mCallBack){
		String userid = LocalSaveManager.getInstance(AndroidApplication.getContext()).getAccountUserId();
		return mQavsdkControl.startContext(userid
						,LocalSaveManager.getInstance(AndroidApplication.getContext()).getAccountUserSig(userid),this);
	}

	public boolean isInStartContext(){
		if(mQavsdkControl == null){
			return false;
		}else{
			return mQavsdkControl.getIsInStartContext();
		}
	}
	public boolean isInStopContext(){
		if(mQavsdkControl == null){
			return false;
		}else{
			return mQavsdkControl.getIsInStopContext();
		}
	}

	/*
	 * 关闭系统SDK
	 */
	public void logout(){
		mQavsdkControl.stopContext();
	}
	
	public boolean isAVSDkAlive(){
		if(mQavsdkControl.getAVContext() == null){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean getIsInCloseRoom(){
		return mQavsdkControl.getIsInCloseRoom();
	}
	/*
	 * 退出房间，相当于结束会话
	 */
	public int  closeRoom(){
		return mQavsdkControl.exitRoom();
	}
	/**
	 * 接受视频邀请或者接受监控请求
	 * @param receiverId 对方id
	 * @param relationId 房间id
	 */
	public void accept(String receiverId,final int relationId){
		//mQavsdkControl.accept();
		conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, receiverId);
		TIMMessage message = new TIMMessage();
		TIMTextElem  elemCommand = new TIMTextElem();
		elemCommand.setText("2");
		TIMTextElem elemContent = new TIMTextElem();
		elemContent.setText(String.valueOf("accept"));
		message.addElement(elemCommand);
		message.addElement(elemContent);
		conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
			
			@Override
			public void onSuccess(TIMMessage arg0) {
				Log.i(TAG, "send accept message successful");
				enterRoom(relationId,LocalSaveManager.getInstance(AndroidApplication.getContext()).getAccountUserId());
			}
			
			@Override
			public void onError(int code, String desc) {
				Log.e(TAG, "fail to send accept, code : "+code+" desc :"+desc);
				//Toast.makeText(context, "邀请失败", Toast.LENGTH_LONG).show();
			}
		});
	}
	/**
	 * 拒绝视频邀请
	 * @param receiverId 对方id
	 */
	public void refuse(String receiverId){
		//mQavsdkControl.refuse();
		Log.i(TAG, "refuse");
		conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, receiverId);
		TIMMessage message = new TIMMessage();
		TIMTextElem  elemCommand = new TIMTextElem();
		elemCommand.setText("1");
		TIMTextElem elemContent = new TIMTextElem();
		elemContent.setText("refuse");
		message.addElement(elemCommand);
		message.addElement(elemContent);
		conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
			
			@Override
			public void onSuccess(TIMMessage arg0) {
				Log.i(TAG, "send refuse message successful");
			}
			
			@Override
			public void onError(int code, String desc) {
				Log.e(TAG, "fail to send refuse, code : "+code+" desc :"+desc);
				//Toast.makeText(context, "邀请失败", Toast.LENGTH_LONG).show();
			}
		});
	}
	/**
	 *  发出视频邀请后，取消掉邀请
	 * @param receiverId
	 */
	public void cancel(String receiverId){
		Log.i(TAG, "cancel");
		conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, receiverId);
		TIMMessage message = new TIMMessage();
		TIMTextElem  elemCommand = new TIMTextElem();
		elemCommand.setText("3");
		TIMTextElem elemContent = new TIMTextElem();
		elemContent.setText("cancel");
		message.addElement(elemCommand);
		message.addElement(elemContent);
		conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
			
			@Override
			public void onSuccess(TIMMessage arg0) {
				Log.i(TAG, "send cancle message successful");
			}
			
			@Override
			public void onError(int code, String desc) {
				Log.e(TAG, "fail to send cancle, code : "+code+" desc :"+desc);
				//Toast.makeText(context, "邀请失败", Toast.LENGTH_LONG).show();
			}
		});
		//MyActivityManager.getInstance().clearActivity("AVActivity");
	}
	/*
	 * 冲突，已有视频房间，再有邀请即挂断
	 */
	public void conflict(String peerId){
		Log.i(TAG, "conflict");
		conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, peerId);
		TIMMessage message = new TIMMessage();
		TIMTextElem  elemCommand = new TIMTextElem();
		elemCommand.setText("4");
		TIMTextElem elemContent = new TIMTextElem();
		elemContent.setText("conflict");
		message.addElement(elemCommand);
		message.addElement(elemContent);
		conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
			
			@Override
			public void onSuccess(TIMMessage arg0) {
				Log.i(TAG, "send conflict message successful");
			}
			
			@Override
			public void onError(int code, String desc) {
				Log.e(TAG, "fail to send conflict, code : "+code+" desc :"+desc);
				//Toast.makeText(context, "邀请失败", Toast.LENGTH_LONG).show();
			}
		});
	}
	/**
	 * 挂断，包含退出房间操作
	 * @param peerId
	 */
	public void hangUp(String peerId){
		Log.i(TAG, "hangUp");
		conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, peerId);
		TIMMessage message = new TIMMessage();
		TIMTextElem  elemCommand = new TIMTextElem();
		elemCommand.setText("5");
		TIMTextElem elemContent = new TIMTextElem();
		elemContent.setText("hangUp");
		message.addElement(elemCommand);
		message.addElement(elemContent);
		conversation.sendMessage(message, new TIMValueCallBack<TIMMessage>() {
			
			@Override
			public void onSuccess(TIMMessage arg0) {
				Log.i(TAG, "send hangUp message successful");
			}
			
			@Override
			public void onError(int code, String desc) {
				Log.e(TAG, "fail to send hangUp, code : "+code+" desc :"+desc);
				//Toast.makeText(context, "邀请失败", Toast.LENGTH_LONG).show();
			}
		});
	//	MyActivityManager.getInstance().clearActivity("AvActivity");
	}
	//请求某人德尔画面
	public void requestRemoteView(String identifier){
		mQavsdkControl.requestRemoteView(identifier);
	}
	//设置网络类型
	public void setNetType(int netType){
		mQavsdkControl.setNetType(netType);
	}

	//远端是否有视频
	public void setRemoteHasVideo(boolean hasVideo,String peerId){
		mQavsdkControl.setRemoteHasVideo(hasVideo, peerId, AVView.VIDEO_SRC_TYPE_CAMERA);
	}
	/*
	 * 开启或关闭麦克风
	 */
	public void enableMic(boolean isEnable){
		mQavsdkControl.getAVContext().getAudioCtrl().enableMic(isEnable);
	}
	/*
	 * 开启或关闭扬声器/耳机
	 */
	public void enableSpeaker(boolean isEnable){
		mQavsdkControl.getAVContext().getAudioCtrl().enableSpeaker(isEnable);
	}
	/*
	 * 获取语音输出模式
	 */
	public int getAudioOutputMode(){
		return mQavsdkControl.getAVContext().getAudioCtrl().getAudioOutputMode();
	}
	/*
	 * 设置语音输出模式
	 */
	public void setAudioOutputMode(int outputMode){
		mQavsdkControl.getAVContext().getAudioCtrl().setAudioOutputMode(outputMode);
	}
	/*
	 * 注册委托
	 */
	public void setAudioDelegate(AVAudioCtrl.Delegate delegate){
		mQavsdkControl.getAVContext().getAudioCtrl().setDelegate(delegate);
	}
	/*
	 * 设置SDK播放音量，取值为0~100
	 */
	public void setVolume(int volume){
		mQavsdkControl.getAVContext().getAudioCtrl().setVolume(volume);
	}
	/*
	 * 开启TRAESERVICE
	 */
	public void startTRAEService(){
		mQavsdkControl.getAVContext().getAudioCtrl().startTRAEService();
	}
	/*
	 * 关闭TRAESERVICE
	 */
	public void stopTRAEService(){
		mQavsdkControl.getAVContext().getAudioCtrl().stopTRAEService();
	}
	/**
	 * 注册消息监听器
	 */
	public void addAVMessageListener(){
		TIMManager.getInstance().addMessageListener(mMessageListener);
		Log.i(TAG, "message listener");
	}
	/**
	 * 消息监听器，所有命令消息都通过这里分发
	 */
	private TIMMessageListener mMessageListener = new TIMMessageListener() {
		
		@Override
		public boolean onNewMessages(List<TIMMessage> list) {
			Log.d(TAG,"new messge listnener:" +list.size()+ " isSelf:"+list.get(0).isSelf()+" isRead :"+list.get(0).isRead());
				for(TIMMessage msg: list){	
					Date date = new Date();
					long currentTime = date.getTime()/1000;
					Log.i(TAG, " message sent time :"+msg.timestamp()+"  now time :"+currentTime);
					long timeInterval = currentTime-msg.timestamp();
					if(msg.isSelf()||timeInterval >=30){
						Log.i(TAG, "this message is sent from  "+timeInterval+" seconds ago. content :"+((TIMTextElem)msg.getElement(0)).getText());
						
						break;
					}
					conversation = msg.getConversation();
					String peerId = conversation.getPeer();
					TIMElem  elem  = msg.getElement(0);
					TIMElemType type = elem.getType();
					
					//text
					if(type == TIMElemType.Text){
						TIMTextElem textElem1 = (TIMTextElem) msg.getElement(0);
						TIMTextElem textElem2 = (TIMTextElem) msg.getElement(1);
						int commandId = Integer.parseInt(textElem1.getText());
						String content = textElem2.getText();
						Log.i(TAG, "messagelistener --- content : "+content);
					//	Intent intent = null;
						
						switch (commandId) {
							case -1://commandId = -1表示非音视频命令
								Log.i(TAG, content);
								break;
							case 0://commandId = 0 表示视频邀请,则content表示视频房间号
									if(getIsInRoom()){
										conflict(peerId);
									}else{
										int relationId = Integer.parseInt(content);
										onReceiveInvite(peerId, relationId);
				//					intent = new Intent(Util.ACTION_RECV_INVITE).putExtra("peerId", peerId)
				//																		   .putExtra("relationId", relationId);
									}
								break;
							case 1://commandId = 1 表示接收方拒绝了视频邀请，content无意
									onReceiveRefuse(peerId);
									//intent = new Intent(Util.ACTION_REFUSE_COMPLETE);
								break;
							case 2://commandId = 2 表示接收方接受了视频邀请，content无意
									onReceiveAccept(peerId);
									//intent = new Intent(Util.ACTION_ACCEPT_COMPLETE);
								break;
							case 3://commandId = 3表示发起方取消了视频邀请，content无意
									onReceiveInviteCancel(peerId);
									//intent = new Intent(Util.ACTION_INVITE_CANCELED);
								break;
							case 4://commandId = 4表示通话冲突，不能同时接受1个以上邀请
									onReceiveConflict(peerId);
									//intent = new Intent(Util.ACTION_INVITE_CONFLICT);
								break;
							case 5://commandId = 5表示对方挂断了视频
									onReceiveHangUp(peerId);
									//intent = new Intent(Util.ACTION_CHAT_HANGUP);
								break;
							case 6://commandId = 6表示收到监 控请求
								if(getIsInRoom()){
									conflict(peerId);
								}else{
									onReceiveMonitor(peerId, Integer.parseInt(content));
								}
								break;
						default:
							Log.i(TAG, "receive message : "+commandId);
							break;
						}
					/*	if(intent != null){
							context.sendBroadcast(intent);
						}*/
						conversation.setReadMessage(msg);
					}
				}				
			return false;
		}
	};
	/**
	 * 设置自己的id
	 * @param selfId
	 */
	public void setSelfId(String selfId){
		mQavsdkControl.setSelfId(selfId);
	}
	public void setLocalHasVideo(boolean isEnable,String selfId){
		mQavsdkControl.setLocalHasVideo(isEnable, selfId);
	}
	public void onMemberChange(){
		mQavsdkControl.onMemberChange();
	}
	public void sdkOnCreate(Context context, View contentView){
		mQavsdkControl.onCreate(context, contentView);
	}
	public void sdkOnResume(){
		mQavsdkControl.onResume();
	}
	public void sdkOnPause(){
		mQavsdkControl.onPause();
	}
	public void sdkOnDestroy(){
		mQavsdkControl.onDestroy();
	}
	/**
	 * 摄像头是否可用
	 * @return
	 */
	public boolean getIsEnableCamera() {
		return mQavsdkControl.getIsEnableCamera();
	}
	/**
	 * 打开摄像头，默认打开前置摄像头
	 * @return
	 */
	public int toggleEnableCamera() {
		return mQavsdkControl.toggleEnableCamera();
	}
	/**
	 * 是否为免提
	 * @return
	 */
	public boolean getHandfreeChecked(){
		return mQavsdkControl.getHandfreeChecked();
	}
	public void setIsInOnOffCamera(boolean isInOnOffCamera) {
		mQavsdkControl.setIsInOnOffCamera(isInOnOffCamera);
	}
	public boolean getIsFrontCamera() {
		return mQavsdkControl.getIsFrontCamera();
	}
	/**
	 * 切换前后摄像头
	 * @return
	 */
	public int toggleSwitchCamera() {
		return mQavsdkControl.toggleSwitchCamera();
	}
	public void setIsInSwitchCamera(boolean isInSwitchCamera) {
		mQavsdkControl.setIsInSwitchCamera(isInSwitchCamera);
	}
	
	public boolean enableUserRender(boolean isEnable) {
		return mQavsdkControl.enableUserRender(isEnable);
	}
	public boolean getIsInOnOffCamera() {
		return mQavsdkControl.getIsInOnOffCamera();
	}
	public boolean getIsInSwitchCamera() {
		return mQavsdkControl.getIsInSwitchCamera();
	}
	public void setRotation(int rotation) {
		mQavsdkControl.setRotation(rotation);
	}
	public void setIsInRoom(boolean isInRoom) {
		this.isInRoom = isInRoom;
	}
	public boolean getIsInRoom(){
		return isInRoom;
	}
	public String getSelfId(){return mQavsdkControl.getSelfIdentifier();}
	public String getPeerId(){
		return mQavsdkControl.getPeerIdentifier();
	}
	/**
	 * 设置要通信的对方的id
	 * @param key
	 */
	public void setPeerId(String key){
		mQavsdkControl.setPeerId(key);
	}
	/**
	 * 关闭所有activty
	 */
	public void quit(){
	}
/*	public void openApp(){
		context.startActivity(new Intent(context, firstActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}
	public void setAVChatCallBack(AVChatCallBack mAVChatCallBack) {
		this.AVChatCallback = mAVChatCallBack;
	}
	public AVChatCallBack getAVChatCallBack() {
		return AVChatCallback;
	}
	public void RemoveAVChatCallBack(){
		this.AVChatCallback = null;
	}
	public void setRobotId(String mRobotId){
		this.robotId = mRobotId;
	}
	public String getRobotId(){
		return robotId;
	}
	*/
	@Override
	public void onReceiveInvite(String peerId, int roomId) {

	}

	@Override
	public void onReceiveRefuse(String peerId) {

	}

	@Override
	public void onReceiveAccept(String peerId) {

	}

	@Override
	public void onReceiveHangUp(String peerId) {

	}

	@Override
	public void onReceiveConflict(String peerId) {

	}

	@Override
	public void onReceiveInviteCancel(String peerId) {

	}

	@Override
	public void onReceiveMonitor(String peerId, int roomId) {

	}

	@Override
	public void onStartContextSuccessful() {

	}

	@Override
	public void onStartContextFailed() {

	}
}
