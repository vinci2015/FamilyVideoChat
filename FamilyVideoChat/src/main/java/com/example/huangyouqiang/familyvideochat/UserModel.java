package com.example.huangyouqiang.familyvideochat;

/**
 * Created by huangyouqiang on 2016/4/7.
 */
public class UserModel {
	private String userId;
	private String userSig;
	private int sex ;//0--male,1--female

	public UserModel(String userId){
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}


	public String getUserSig() {
		return userSig;
	}

	public void setUserSig(String userSig) {
		this.userSig = userSig;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	@Override
	public String toString() {
		return "UserModel{" +
						"userId='" + userId + '\'' +
						", sex=" + (sex==0?"男":"女") +
						'}';
	}
}
