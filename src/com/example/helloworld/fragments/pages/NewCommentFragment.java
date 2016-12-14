package com.example.helloworld.fragments.pages;

import com.example.helloworld.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class NewCommentFragment extends Fragment {
	
	View view;

	EditText edittext;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view ==null){
			view = inflater.inflate(R.layout.fragment_new_comment, null);
			
			
			edittext = (EditText)view.findViewById(R.id.comment_text);
		}
		return view;
	}
	

	public void setText(String labelText){
		edittext.setText(labelText);
	}
	
	public String  getText() {
		return  edittext.getText().toString();
	}

}
