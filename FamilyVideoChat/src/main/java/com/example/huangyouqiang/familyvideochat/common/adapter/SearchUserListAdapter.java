package com.example.huangyouqiang.familyvideochat.common.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.UserModel;
import com.example.huangyouqiang.familyvideochat.ui.activity.AddFriendActivity;
import com.tencent.TIMAddFriendRequest;
import com.tencent.TIMFriendResult;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangyouqiang on 2016/4/7.
 */
public class SearchUserListAdapter extends RecyclerView.Adapter<SearchUserListAdapter.ViewHolder>
				implements View.OnClickListener{

	private Context mContext;
	private List<UserModel> datas = null;
	public SearchUserListAdapter(Context context,List<UserModel> datas){
		this.mContext = context;
		if(datas == null){
			this.datas = new ArrayList<>();
		}else {
			this.datas = datas;
		}
	}
	private RecycleItemClickListener recycleItemClickListener = null;
	public  interface RecycleItemClickListener {
		void onItemClick(View view,String data);
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
		ViewHolder viewHolder = new ViewHolder(view);
		//view.setOnClickListener(this);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		holder.user.setText(datas.get(position).getUserId());
		holder.add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//创建请求列表
				List<TIMAddFriendRequest> reqList = new ArrayList<TIMAddFriendRequest>();

//添加好友请求
				TIMAddFriendRequest req = new TIMAddFriendRequest();
				req.setAddrSource("AddSource_Type_family");
				//加好友来源字符串中必须符合如下格式：“AddSource_Type_XXXXXXXX”，即必须包含前缀“AddSource_Type_”，
				// "XXXXXXXX"为App自定义填写，最长不超过8字节，一个符合规范的加好友来源参数例如"AddSource_Type_Android"。
				req.setAddWording("add me");
				req.setIdentifier(datas.get(position).getUserId());
				req.setRemark("Cat");
				reqList.add(req);

//申请添加好友
				TIMFriendshipManager.getInstance().addFriend(reqList, new TIMValueCallBack<List<TIMFriendResult>>() {
					@Override
					public void onError(int code, String desc){
						//错误码code和错误描述desc，可用于定位请求失败原因
						//错误码code列表请参见错误码表
						Log.e("add friend error",desc);
					}

					@Override
					public void onSuccess(List<TIMFriendResult> result){
						List<String> userlist = new ArrayList<String>();
						for(TIMFriendResult res : result) {
							Log.e("add friend success", "identifier: " + res.getIdentifer() + " status: " + res.getStatus());
							userlist.add(res.getIdentifer());
						}
						TIMFriendshipManager.getInstance().getFriendsProfile(userlist, new TIMValueCallBack<List<TIMUserProfile>>() {
							@Override
							public void onError(int i, String s) {
								Log.e("getfriendprofile","error,"+s);
							}

							@Override
							public void onSuccess(List<TIMUserProfile> timUserProfileList) {
								Log.i("getFriendProfile","success,");

								for(TIMUserProfile userProfile:timUserProfileList){
									UserModel userModel = UserModel.transformFrom(userProfile);
									((AddFriendActivity)mContext).addFriend(userModel);
									((AddFriendActivity)mContext).showMessage("add friend success : "+userModel.getDisplayName(),null);
								}
							}
						});
					}
				});
			}
		});
		holder.itemView.setTag(datas.get(position));
		holder.itemView.setBackgroundResource(R.drawable.friends_item_bg);
	}

	@Override
	public int getItemCount() {
		if(datas == null){
			return 0;
		}
		return datas.size();
	}

	@Override
	public void onClick(View v) {
		if(recycleItemClickListener != null){
			recycleItemClickListener.onItemClick(v,((UserModel)v.getTag()).getUserId());
		}
	}

	public void setItemClickListener(RecycleItemClickListener listener){
		this.recycleItemClickListener = listener;
	}
	public static class ViewHolder extends RecyclerView.ViewHolder{
		public TextView user;
		public Button add;
		public ViewHolder(View itemView) {
			super(itemView);
			user = (TextView) itemView.findViewById(R.id.tv_user);
			add = (Button) itemView.findViewById(R.id.btn_add);
		}
	}
	public void updateDatas(List<UserModel> userModelList){
		this.datas = userModelList;
		notifyItemInserted(0);
		//notifyDataSetChanged();
	}
}
