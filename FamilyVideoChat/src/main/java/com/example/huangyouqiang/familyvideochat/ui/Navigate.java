package com.example.huangyouqiang.familyvideochat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by huangyouqiang on 2016/3/30.
 */
public class Navigate {
	private Intent intent;
	private Context context;
	private boolean isBack = false;
	private Navigate() {
		this.context = AndroidApplication.getInstance().getCurrentActivity();
		if(this.context == null){
			Log.e("lll","context is null");
		}
	}

	public static Navigate build(){
		return new Navigate();
	}
	public  Navigate  navigateTo(Class toClass){
		if(this.context == null){
			Log.e("atag","context is null");
		}
		intent = new Intent(this.context,toClass);
		return this ;
	}

	public Navigate add(String name,String value){
		intent.putExtra(name,value);
		return  this;
	}

	public Navigate setBack(boolean isBack){
		this.isBack = isBack;
		return this;
	}
	public void start(){
		if(isBack){
			((Activity)this.context).startActivityForResult(intent,0);
		}else {
			this.context.startActivity(intent);
			((Activity)this.context).finish();
		}
	}
}
