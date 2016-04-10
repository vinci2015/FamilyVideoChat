package com.example.huangyouqiang.familyvideochat;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.huangyouqiang.familyvideochat.exception.NullPreferenceException;

/**
 * Created by huangyouqiang on 2016/4/2.
 */
public class LocalSaveManager {
	private static final String ACCOUNT_NAME = "account_info";
	private static final String ACCOUNT_USER_ID = "ACCOUNT_USER_ID";
	private static final String ACCOUNT_USER_SIG = "ACCOUNT_USER_SIG";
	private SharedPreferences accountFile;
	private static volatile LocalSaveManager instance = null;
	private LocalSaveManager(Context context){
		accountFile = context.getSharedPreferences(ACCOUNT_NAME,context.MODE_PRIVATE);
	}
	public static LocalSaveManager getInstance(Context context){
		if(instance == null){
			synchronized (LocalSaveManager.class){
				if(instance == null){
					instance   = new LocalSaveManager(context);
				}
			}
		}
		return instance;
	}

	public boolean setAccountInfo(String userId,String userSig){
		if(accountFile != null){
			SharedPreferences.Editor editor = accountFile.edit();
			editor.putString(ACCOUNT_USER_ID,userId);
			editor.putString(ACCOUNT_USER_SIG,userSig);
			editor.commit();
			return true;
		}else{
			return false;
		}
	}

	public String getAccountUserId(){
		String userId = accountFile.getString(ACCOUNT_USER_ID,"");
		if(userId ==""){
			try {
				throw new NullPreferenceException("userId is null");
			} catch (NullPreferenceException e) {
				e.printStackTrace();
			}
		}
		return userId;
	}
	public String getAccountUserSig(String userId){
		String sig = "";
		if(accountFile != null){
			String accountUserId = getAccountUserId();
			if(userId.equals(accountUserId)){
				sig = accountFile.getString(ACCOUNT_USER_SIG,"");
				if(sig ==""){
					try {
						throw new NullPreferenceException("user sig is null");
					} catch (NullPreferenceException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return sig;
	}
	public boolean clearAccountInfo(){
		if(accountFile != null){
			SharedPreferences.Editor editor = accountFile.edit();
			editor.putString(ACCOUNT_USER_ID,"");
			editor.putString(ACCOUNT_USER_SIG,"");
			editor.commit();
			return true;
		}else{
			return false;
		}
	}
}
