package com.example.huangyouqiang.familyvideochat.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huangyouqiang.familyvideochat.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainFragment extends BaseFragment {
	@InjectView(R.id.toPerson)
	TextView toPerson;
	private OnDeliverMessageListener messageListener = null;


	public static MainFragment newInstance() {
		return new MainFragment();
	}

	public MainFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		this.messageListener = (OnDeliverMessageListener) container.getContext();
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.inject(this, view);
		return view;
	}

	public void setToPerson(String person){
		toPerson.setText("Chat with "+person);
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}


	public interface OnDeliverMessageListener {
		void onDeliverMessage(String msg);
	}
}
