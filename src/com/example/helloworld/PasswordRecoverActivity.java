package com.example.helloworld;

import java.io.IOException;

import com.example.helloworld.api.Server;
import com.example.helloworld.fragments.PasswordRecoverStep1Fragment;
import com.example.helloworld.fragments.PasswordRecoverStep1Fragment.OnGoNextListener;
import com.example.helloworld.fragments.PasswordRecoverStep2Fragment;
import com.example.helloworld.fragments.PasswordRecoverStep2Fragment.OnPasswordRecoverListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PasswordRecoverActivity extends Activity {

	PasswordRecoverStep1Fragment step1 = new PasswordRecoverStep1Fragment();
	PasswordRecoverStep2Fragment step2 = new PasswordRecoverStep2Fragment();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_password_recover);

		step1.setOnGoNextListener(new OnGoNextListener() {

			@Override
			public void onGoNext() {
				goStep2();
			}
		});

		step2.setOnPasswordRecoverListener(new OnPasswordRecoverListener() {

			public void onPasswordRecover() {
				// TODO Auto-generated method stub
				onrpassword();
			}
		});

		getFragmentManager().beginTransaction().replace(R.id.container, step1).commit();
	}

	void goStep2(){

		getFragmentManager()
		.beginTransaction()	
		.setCustomAnimations(
				R.animator.slide_in_right,
				R.animator.slide_out_left,
				R.animator.slide_in_left,
				R.animator.slide_out_right)
		.replace(R.id.container, step2)
		.addToBackStack(null)
		.commit();
	}

	private void onrpassword() {
		// TODO Auto-generated method stub
		String s1 = step2.getText();
		s1 = MD5.getMD5(s1);
		 MultipartBody.Builder requestBodyBulider=new MultipartBody.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("email",step1.getText())
					.addFormDataPart("passwordHash",s1);
		 
		 OkHttpClient client =Server.getSharedClient();
		 Request request = Server.requestBuilderWithApi("passwordrecover")
					.method("post", null)
					.post(requestBodyBulider.build())
					.build();
		 
		 client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0,final Response arg1) throws IOException {	
				
					try {
						final String responseString = arg1.body().string();
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								PasswordRecoverActivity.this.onResponse(arg0, responseString);
								
							}
						});

					} catch (final Exception e) {
						runOnUiThread(new Runnable() {
							
							public void run() {
								PasswordRecoverActivity.this.onFailure(arg0, e);
							}
							
						});
					}	
			}
			
			public void onFailure(final Call arg0,final IOException arg1) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PasswordRecoverActivity.this.onFailure(arg0, arg1);					
					}
				});
				
			}
		});
		
	}



	void onFailure(Call arg0, Exception e) {
		new AlertDialog.Builder(PasswordRecoverActivity.this)
		.setTitle("修改失败"+e.getMessage())
		.setMessage(e.getLocalizedMessage())
		.setPositiveButton("确定",null)
		.show();
		
	}



	void onResponse(Call arg0,String string) {
		
		 new AlertDialog.Builder(PasswordRecoverActivity.this)
			.setMessage("修改成功")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
}
