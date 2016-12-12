package com.example.helloworld.fragments;

import java.io.IOException;

import com.example.helloworld.MD5;
import com.example.helloworld.R;
import com.example.helloworld.RegisterActivity;
import com.example.helloworld.api.Server;
import com.example.helloworld.fragments.inputcells.SimpleTextInputCellFragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PasswordRecoverStep2Fragment extends Fragment {
	View view;
	SimpleTextInputCellFragment f1;
	SimpleTextInputCellFragment f2;
	SimpleTextInputCellFragment f3;
	PasswordRecoverStep1Fragment p1;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if(view==null){
			view = inflater.inflate(R.layout.fragment_password_recover_step2, null);

			f1 = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_verify);
			f2 = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password);
			f3 = (SimpleTextInputCellFragment) getFragmentManager().findFragmentById(R.id.input_password_repeat);

			view.findViewById(R.id.btn_submit).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onfindpassword();
				}

			});

		}

		return view;
	}

	private void onfindpassword() {
		String pass1 = f2.getText();
		String pass2 = f3.getText();

		if(pass1.equals(pass2)){
			if(onPasswordRecoverListener!=null)
				onPasswordRecoverListener.onPasswordRecover();
		}
		else {
			new AlertDialog.Builder(getActivity())
			.setTitle("提示")
			.setMessage("密码不一致")
			.setPositiveButton("确定", null)
			.show();
		}
	}

	public String getText() {
		return f2.getText();
	}

	public static interface OnPasswordRecoverListener {
		void onPasswordRecover();
	}

	OnPasswordRecoverListener onPasswordRecoverListener;

	public void setOnPasswordRecoverListener(OnPasswordRecoverListener onPasswordRecoverListener) {
		this.onPasswordRecoverListener = onPasswordRecoverListener;
	}

	@Override
	public void onResume() {
		super.onResume();

		f1.setLabelText("旧密码");
		f1.setText("136147");

		f2.setLabelText("新密码");
		f2.setText("1361471");

		f3.setLabelText("确认密码");
		f3.setText("1361471");

	}
}
