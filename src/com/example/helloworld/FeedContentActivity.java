package com.example.helloworld;

import java.io.IOException;
import java.util.List;

import com.example.helloworld.api.Server;
import com.example.helloworld.api.entity.Article;
import com.example.helloworld.api.entity.Comment;
import com.example.helloworld.api.entity.Page;

import com.example.helloworld.fragments.widgets.AvatarView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeedContentActivity extends Activity {
	View view;
	
	TextView textLoadMore;
	View btnLoadMore;
	
	private boolean isLiked;
	TextView zan;
	ListView listView;

	int page = 0;
	Article article ;
	List<Comment> data;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_feed_content);


		article = (Article) getIntent().getSerializableExtra("article");
		btnLoadMore = LayoutInflater.from(this).inflate(R.layout.widget_load_more_button, null);
		textLoadMore = (TextView) btnLoadMore.findViewById(R.id.text);
		zan = (TextView)findViewById(R.id.list_zan);

		listView = (ListView)findViewById(R.id.listview_comment);
		listView.addFooterView(btnLoadMore);
		listView.setAdapter(listAdapter);

		btnLoadMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadmore();
			}
		});

		findViewById(R.id.btn_comment).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onsendcomment();
			}			
		});
		findViewById(R.id.dianzan).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onlike();
			}		
		});

	}

	private void onlike() {
		MultipartBody body = new MultipartBody.Builder()
				.addFormDataPart("likes", String.valueOf(!isLiked))
				.build(); 

		Request request = Server.requestBuilderWithApi("article/"+article.getId()+"/likes")
				.post(body).build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					public void run() {
						reload();
					}
				});
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						reload();
					}
				});
			}
		});
	}	

	void checkLiked(){
		Request request = Server.requestBuilderWithApi("article/"+article.getId()+"/isliked").get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try{
					final String responseString = arg1.body().string();
					final Boolean result = new ObjectMapper().readValue(responseString, Boolean.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onCheckLikedResult(result);
						}
					});
				}catch(final Exception e){
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onCheckLikedResult(false);
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onCheckLikedResult(false);
					}
				});				
			}
		});
	}

	void onCheckLikedResult(boolean result){
		isLiked = result;
		zan.setTextColor(result ? Color.BLUE : Color.BLACK);
	}
	void onReloadLikesResult(int count){
		if(count>0){
			zan.setText("赞("+count+")");
		}else{
			zan.setText("无");
		}
	}

	void reloadLikes(){
		Request request = Server.requestBuilderWithApi("/article/"+article.getId()+"/likes")
				.get().build();

		Server.getSharedClient().newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				try{
					String responseString = arg1.body().string();
					final Integer count = new ObjectMapper().readValue(responseString, Integer.class);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onReloadLikesResult(count);
						}
					});
				}catch (Exception e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							onReloadLikesResult(0);
						}
					});
				}
			}

			@Override
			public void onFailure(Call arg0, IOException e) {
				e.printStackTrace();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						onReloadLikesResult(0);
					}
				});
			}
		});
	}

	void reload(){
		reloadLikes();
		checkLiked();
	}

	private void onsendcomment() {
		// TODO Auto-generated method stub
		Intent inten = new Intent(FeedContentActivity.this, NewCommentActivity.class);

		inten.putExtra("article", article);
		startActivity(inten);
		overridePendingTransition(R.anim.slide_in_bottom, R.anim.none);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		reload();
		onresume();
		oncommentlist();
	}



	BaseAdapter listAdapter = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;

			if(convertView==null){
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.fragments_listview, null);	
			}else{
				view = convertView;
			}

			TextView context = (TextView) view.findViewById(R.id.list_text);
			//TextView title = (TextView) view.findViewById(R.id.list_title);
			TextView name = (TextView) view.findViewById(R.id.list_name);
			AvatarView avatar = (AvatarView) view.findViewById(R.id.list_contentavatar);
			TextView list_creatDate = (TextView) view.findViewById(R.id.list_creatDate);
			Comment comment = data.get(position);
			context.setText(comment.getText());
			//title.setText(comment.getText());
			name.setText(comment.getAuthor().getName());
			list_creatDate.setText(DateFormat.format("yyyy-MM-dd hh:mm", comment.getCreateDate()).toString());
			avatar.load(Server.serverAddress + comment.getAuthor().getAvatar());
			return view;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public int getCount() {
			return data==null ? 0 : data.size();
		}
	};



	private void oncommentlist() {
		// TODO Auto-generated method stub
		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("/article/"+article.getId()+"/comments")
				.get()
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				try {

					//取出Page数据s
					final Page<Comment> data = new ObjectMapper()
							.readValue(arg1.body().string(), new TypeReference<Page<Comment>>() {
							});

					runOnUiThread(new Runnable() {
						public void run() {
							FeedContentActivity.this.page = data.getNumber();
							FeedContentActivity.this.data = data.getContent();
							listAdapter.notifyDataSetInvalidated();
						}
					});					
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(FeedContentActivity.this)
							.setMessage(e.getMessage())
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						new AlertDialog.Builder(FeedContentActivity.this)
						.setMessage(arg1.getMessage())
						.show();
					}
				});
			}
		});

	}


	public void onresume() {

		String list_contenttext = getIntent().getStringExtra("list_contenttext");
		String list_contenttitle = getIntent().getStringExtra("list_contenttitle");
		String list_contentname = getIntent().getStringExtra("list_contentname");
		String list_contentcreattime = getIntent().getStringExtra("list_creatDate");

		AvatarView avatar = (AvatarView)findViewById(R.id.list_contentavatar);
		avatar.load(Server.serverAddress+getIntent().getStringExtra("list_contentavatar"));

		TextView contenttext = (TextView) findViewById(R.id.list_text);
		contenttext.setText(list_contenttext);


		TextView creatDate = (TextView) findViewById(R.id.list_creatDate);
		creatDate.setText(list_contentcreattime);

		TextView contenttitle = (TextView) findViewById(R.id.list_title);
		contenttitle.setText(list_contenttitle);

		TextView contentname = (TextView) findViewById(R.id.list_name);
		contentname.setText("作者："+list_contentname);

	}

	void loadmore(){
		btnLoadMore.setEnabled(false);
		textLoadMore.setText("载入中…");

		Request request = Server.requestBuilderWithApi("/article/"+article.getId()+"/comments/"+(page+1)).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				runOnUiThread(new Runnable() {
					public void run() {
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
					}
				});

				try{
					Page<Comment> feeds = new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Comment>>() {});
					if(feeds.getNumber()>page){
						if(data==null){
							data = feeds.getContent();
						}else{
							data.addAll(feeds.getContent());
						}
						page = feeds.getNumber();

						runOnUiThread(new Runnable() {
							public void run() {
								listAdapter.notifyDataSetChanged();
							}
						});
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				runOnUiThread(new Runnable() {
					public void run() {
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
					}
				});
			}
		});
	}


}
