package com.example.helloworld;

import com.example.helloworld.api.Server;
import com.example.helloworld.fragments.widgets.AvatarView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FeedContentActivity extends Activity {
	@SuppressWarnings("null")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_feed_content);

		String list_contenttext = getIntent().getStringExtra("list_contenttext");
		String list_contenttitle = getIntent().getStringExtra("list_contenttitle");
		String list_contentname = getIntent().getStringExtra("list_contentname");
		String list_creatDate = getIntent().getStringExtra("list_creatDate");
		//String list_contentavatar = getIntent().getStringExtra("list_contentavatar");
		AvatarView avatar = (AvatarView)findViewById(R.id.list_contentavatar);
     	avatar.load(Server.serverAddress+getIntent().getStringExtra("list_contentavatar"));
		
		TextView contenttext = (TextView) findViewById(R.id.list_contenttext);
		contenttext.setText(list_contenttext);

		TextView contenttitle = (TextView) findViewById(R.id.list_contenttitle);
		contenttitle.setText(list_contenttitle);

		TextView contentname = (TextView) findViewById(R.id.list_contentname);
		contentname.setText("作者："+list_contentname);

		TextView creatDate = (TextView) findViewById(R.id.list_contentcreattime);
		creatDate.setText("创建时间："+list_creatDate);
		
		
	}
}
