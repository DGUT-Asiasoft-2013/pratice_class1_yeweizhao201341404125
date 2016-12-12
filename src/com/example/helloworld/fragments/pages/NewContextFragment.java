package com.example.helloworld.fragments.pages;

import com.example.helloworld.R;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class NewContextFragment extends Fragment {
	View view;
	TextView textview;
	EditText edittext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(view ==null){
			view = inflater.inflate(R.layout.fragment_new_context, null);
			
			textview = (TextView)view.findViewById(R.id.title);
			edittext = (EditText)view.findViewById(R.id.text);
		}
		return view;
	}
	
	public void setTitle(String edittext){
		textview.setText(edittext);
	}
	
	public void setText(String labelText){
		edittext.setText(labelText);
	}
	
	public String  getTitle() {
		return textview.getText().toString();
	}
	public String  getText() {
		return  edittext.getText().toString();
	}
}
