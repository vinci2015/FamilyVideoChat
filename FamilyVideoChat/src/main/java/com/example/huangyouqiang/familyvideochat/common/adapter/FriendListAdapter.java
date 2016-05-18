package com.example.huangyouqiang.familyvideochat.common.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.huangyouqiang.familyvideochat.R;
import com.example.huangyouqiang.familyvideochat.UserModel;

import java.util.List;

/**
 * Created by huangyouqiang on 2016/4/7.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder>
				implements View.OnClickListener{

	private List<UserModel> datas = null;
	public FriendListAdapter(List<UserModel> datas){
		this.datas = datas;
	}
	private RecycleItemClickListener recycleItemClickListener = null;
	public  interface RecycleItemClickListener {
		void onItemClick(View view,String data);
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_item,parent,false);
		ViewHolder viewHolder = new ViewHolder(view);
		view.setOnClickListener(this);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
			holder.friend.setText(datas.get(position).getDisplayName());
			holder.change.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					holder.change.setText("YES");
				}
			});
			holder.itemView.setTag(datas.get(position));
		holder.itemView.setBackgroundResource(R.drawable.friends_item_bg);
	}

	@Override
	public int getItemCount() {
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
		public TextView friend;
		public Button change;
		public ViewHolder(View itemView) {
			super(itemView);
			friend = (TextView) itemView.findViewById(R.id.tv_friend);
			change = (Button) itemView.findViewById(R.id.btn_edit);
		}
	}
	public void addItem(UserModel userModel){
		datas.add(userModel);
		notifyDataSetChanged();
	}
}
