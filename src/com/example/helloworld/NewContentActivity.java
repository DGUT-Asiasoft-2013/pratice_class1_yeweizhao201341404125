package com.example.helloworld;

import java.io.IOException;

import com.example.helloworld.api.Server;
import com.example.helloworld.fragments.pages.NewContextFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewContentActivity extends Activity {
	NewContextFragment ncf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_context);

		ncf = (NewContextFragment) getFragmentManager().findFragmentById(R.id.context1);
		
		findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onsend();
			}


		});

	}
	private void onsend() {
		
		final String title = ncf.getTitle();
		String text = ncf.getText();
		
		OkHttpClient client = Server.getSharedClient();

		MultipartBody requestBody = new MultipartBody.Builder()				
				.addFormDataPart("title",title )
				.addFormDataPart("text", text)
				.build();

		Request request = Server.requestBuilderWithApi("article")
				.method("post", null)
				.post(requestBody)
				.build();
		
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						new AlertDialog.Builder(NewContentActivity.this)
						.setTitle("发送成功")
						.setMessage(title)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
								overridePendingTransition(R.anim.none, R.anim.slide_out_bottom);
							}
						})
						.show();
					}
				});
			}
			
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						
						new AlertDialog.Builder(NewContentActivity.this)
						.setTitle("提示")
						.setMessage("发送失败!")
						.setPositiveButton("确定",null)
						.show();
					}
				});
				
			}
		});
		
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		ncf.setTitle("1111111");
		ncf.setText("136147hhhhhhhhhh136147");
	}
}
