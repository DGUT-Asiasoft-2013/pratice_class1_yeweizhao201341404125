package com.example.helloworld;

import java.io.IOException;

import com.example.helloworld.api.Server;
import com.example.helloworld.api.entity.Article;
import com.example.helloworld.fragments.pages.NewCommentFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewCommentActivity extends Activity {
	
	NewCommentFragment ncf;
	Article article;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_comment);
		ncf = (NewCommentFragment)getFragmentManager().findFragmentById(R.id.comment_fragment);
		
		article = (Article) getIntent().getSerializableExtra("article");
		
		findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onsend();
			}


		});
	}
	
	private void onsend() {
		int id = article.getId();
		
		String text = ncf.getText();
		
		OkHttpClient client = Server.getSharedClient();

		MultipartBody requestBody = new MultipartBody.Builder()				
				.addFormDataPart("text", text)
				.build();

		Request request = Server.requestBuilderWithApi("/article/"+id+"/comments")
				.method("post", null)
				.post(requestBody)
				.build();
		
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						new AlertDialog.Builder(NewCommentActivity.this)
						.setTitle("发送成功")
						.setMessage("文章发送成功!")
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
						
						new AlertDialog.Builder(NewCommentActivity.this)
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
		ncf.setText("136147hhhhhhhhhh136147");
	}
}
