package com.example.helloworld.fragments.pages;

import java.io.IOException;
import java.util.List;

import com.example.helloworld.R;
import com.example.helloworld.api.Server;
import com.example.helloworld.api.entity.Article;
import com.example.helloworld.api.entity.Comment;
import com.example.helloworld.api.entity.Page;
import com.example.helloworld.fragments.widgets.AvatarView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class NoteListFragment extends Fragment {
	View view;

	List<Comment> data;
	int page = 0;
	ListView note_list;
	
	TextView textLoadMore;
	View btnLoadMore;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view==null){
			view = inflater.inflate(R.layout.fragment_page_note_list, null);
			note_list = (ListView)view.findViewById(R.id.note_list);
			btnLoadMore = LayoutInflater.from(getActivity()).inflate(R.layout.widget_load_more_button, null);
			textLoadMore = (TextView) btnLoadMore.findViewById(R.id.text);
			
			note_list.addFooterView(btnLoadMore);
			note_list.setAdapter(listAdapter);

			btnLoadMore.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					loadmore();
				}
			});
		}

		return view;
	}
	
	void loadmore(){
		page++;
		btnLoadMore.setEnabled(false);
		textLoadMore.setText("载入中…");

		Request request = Server.requestBuilderWithApi("/article/author_id/comment?page="+(page+1)).get().build();
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
					Page<Comment> feeds = new ObjectMapper().readValue(arg1.body().string(), new TypeReference<Page<Comment>>() {});
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
	
	//适配器的配置
	BaseAdapter listAdapter = new BaseAdapter() {

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;

			if(convertView==null){
				//构建LayoutInflater显示listview
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				view = inflater.inflate(R.layout.fragments_listview, null);	
			}else{
				view = convertView;
			}

			//将数据导入listview
			TextView context = (TextView) view.findViewById(R.id.list_text);
			TextView title = (TextView) view.findViewById(R.id.list_title);
			TextView name = (TextView) view.findViewById(R.id.list_name);
			AvatarView avatar = (AvatarView) view.findViewById(R.id.list_contentavatar);
			TextView list_creatDate = (TextView) view.findViewById(R.id.list_creatDate);
			Comment comment = data.get(position);
			context.setText(comment.getText());
			title.setText(comment.getText());
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
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
				
		Request request = Server.requestBuilderWithApi("/article/author_id/comment")
				.get()
				.build();
		
		Server.getSharedClient().newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				try {

					//取出Page数据
					final Page<Comment> data = new ObjectMapper()
							.readValue(arg1.body().string(), new TypeReference<Page<Comment>>() {
							});

					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							NoteListFragment.this.page = data.getNumber();
							NoteListFragment.this.data = data.getContent();
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
			public void onFailure(Call arg0, final IOException arg1) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						new AlertDialog.Builder(getActivity())
						.setMessage(arg1.getMessage())
						.show();
					}
				});
			}
		});
	}
}
