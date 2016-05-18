package com.example.huangyouqiang.familyvideochat;

import android.os.Parcel;
import android.os.Parcelable;

import com.tencent.TIMUserProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangyouqiang on 2016/4/7.
 */
public class UserModel implements Parcelable{
	private String userId;
	private String userSig;
	private int sex ;//0--male,1--female
	private String nickName ="";

	public UserModel(String userId){
		this.userId = userId;
	}

	protected UserModel(Parcel in) {
		userId = in.readString();
		userSig = in.readString();
		sex = in.readInt();
		nickName = in.readString();
	}

	public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
		@Override
		public UserModel createFromParcel(Parcel in) {
			return new UserModel(in);
		}

		@Override
		public UserModel[] newArray(int size) {
			return new UserModel[size];
		}
	};

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

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public static UserModel transformFrom(TIMUserProfile userProfile){
		UserModel userModel  = new UserModel(userProfile.getIdentifier());
		userModel.setNickName(userProfile.getNickName());
		return userModel;
	}
	public static List<UserModel> transformFromList(List<TIMUserProfile> userProfileList){
		List<UserModel> userModelList = new ArrayList<>();
		for(TIMUserProfile userProfile : userProfileList){
			UserModel userModel = transformFrom(userProfile);
			userModelList.add(userModel);
		}
		return userModelList;
	}
	public String getDisplayName(){
		return getNickName().equals("")?getUserId():getNickName();
	}
	@Override
	public String toString() {
		return "UserModel{" +
						"userId='" + userId + '\'' +
						", sex=" + (sex==0?"男":"女") +
						'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userId);
		dest.writeString(userSig);
		dest.writeInt(sex);
		dest.writeString(nickName);
	}
}
