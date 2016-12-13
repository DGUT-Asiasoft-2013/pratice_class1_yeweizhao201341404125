package com.example.helloworld.fragments.pages;

import java.io.IOException;
import java.util.List;
import com.example.helloworld.FeedContentActivity;
import com.example.helloworld.R;
import com.example.helloworld.api.Server;
import com.example.helloworld.api.entity.Article;
import com.example.helloworld.api.entity.Page;
import com.example.helloworld.fragments.widgets.AvatarView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeedListFragment extends Fragment {

	View view;
	ListView listView;

	View btnLoadMore;
	TextView textLoadMore;

	List<Article> data;
	int page = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null){
			view = inflater.inflate(R.layout.fragment_page_feed_list, null);

			btnLoadMore = inflater.inflate(R.layout.widget_load_more_button, null);
			textLoadMore = (TextView) btnLoadMore.findViewById(R.id.text);

		}

		listView = (ListView) view.findViewById(R.id.list);
		listView.addFooterView(btnLoadMore);
		listView.setAdapter(listAdapter);


		btnLoadMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				loadmore();
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onItemClicked(position);
			}
		});
		return view;
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
			TextView title = (TextView) view.findViewById(R.id.list_title);
			AvatarView avatar = (AvatarView) view.findViewById(R.id.list_contentavatar);
			TextView list_creatDate = (TextView) view.findViewById(R.id.list_creatDate);
			Article article = data.get(position);
			context.setText(article.getText());
			title.setText(article.getTitle());
			list_creatDate.setText(DateFormat.format("yyyy-MM-dd hh:mm", article.getCreateDate()).toString());
			avatar.load(Server.serverAddress + article.getAuthorAvatar());
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

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		OkHttpClient client = Server.getSharedClient();
		Request request = Server.requestBuilderWithApi("feeds")
				.get()
				.build();

		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(final Call arg0, Response arg1) throws IOException {
				try {

					//取出Page数据s
					Page<Article> data = new ObjectMapper()
							.readValue(arg1.body().string(), new TypeReference<Page<Article>>() {
							});
					FeedListFragment.this.page = data.getNumber();
					FeedListFragment.this.data = data.getContent();
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							listAdapter.notifyDataSetInvalidated();
						}
					});					
				} catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(getActivity())
							.setMessage(e.getMessage())
							.show();
						}
					});
				}
			}

			@Override
			public void onFailure(final Call arg0, final IOException arg1) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						new AlertDialog.Builder(getActivity())
						.setMessage(arg1.getMessage())
						.show();
					}
				});
			}
		});

		//		PageArticle page = new PageArticle();
		//		page.getContent();
		//		page.getNumber();
		//		Article article = (Article) page.getContent();
		//		article.getId();
		//		article.getAuthorName();
		//		article.getTitle();
		//		article.getText();
		//		article.getCreateDate();
		//		article.getAuthorAvatar();





	}
	
	void loadmore(){
		btnLoadMore.setEnabled(false);
		textLoadMore.setText("载入中…");
		
		Request request = Server.requestBuilderWithApi("feeds/"+(page+1)).get().build();
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
					}
				});
				
				try{
					Page<Article> feeds = new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Article>>() {});
					if(feeds.getNumber()>page){
						if(data==null){
							data = feeds.getContent();
						}else{
							data.addAll(feeds.getContent());
						}
						page = feeds.getNumber();
						
						getActivity().runOnUiThread(new Runnable() {
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
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						btnLoadMore.setEnabled(true);
						textLoadMore.setText("加载更多");
					}
				});
			}
		});
}

	void onItemClicked(int position){
		String list_contenttext = data.get(position).getText();
		String list_contenttitle = data.get(position).getTitle();
		String list_contentname = data.get(position).getAuthorName();
		String list_creatDate = DateFormat.format("yyyy-MM-dd hh:mm", data.get(position).getCreateDate()).toString();
		String list_contentavatar  = data.get(position).getAuthorAvatar();

		Intent itnt = new Intent(getActivity(), FeedContentActivity.class);
		itnt.putExtra("list_contenttext", list_contenttext);
		itnt.putExtra("list_contenttitle", list_contenttitle);
		itnt.putExtra("list_contentname", list_contentname);
		itnt.putExtra("list_creatDate", list_creatDate);
		itnt.putExtra("list_contentavatar", list_contentavatar);
		//new ObjectMapper().readValue(src, Map.class);

		startActivity(itnt);
	}
}
