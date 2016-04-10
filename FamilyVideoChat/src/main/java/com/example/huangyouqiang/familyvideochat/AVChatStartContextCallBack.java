package com.example.huangyouqiang.familyvideochat;

public interface AVChatStartContextCallBack {
	void onReceiveInvite(String peerId, int roomId);
	void onReceiveRefuse(String peerId);
	void onReceiveAccept(String peerId);
	void onReceiveHangUp(String peerId);
	void onReceiveConflict(String peerId);
	void onReceiveInviteCancel(String peerId);
	void onReceiveMonitor(String peerId, int roomId);
	void onStartContextSuccessful();
	void onStartContextFailed();
}
