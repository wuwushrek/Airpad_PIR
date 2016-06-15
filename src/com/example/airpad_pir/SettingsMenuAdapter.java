package com.example.airpad_pir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsMenuAdapter extends ArrayAdapter<String>{

	private LayoutInflater inflater;
	public SettingsMenuAdapter(Context context, int resource,
			int textViewResourceId, String[] objects) {
		super(context, resource, textViewResourceId, objects);
		inflater =	LayoutInflater.from(context);
	}
	
	
	public  View getCustomView(int position , View convertView,ViewGroup parent){
		View layout;
		if(convertView!= null){
			layout = convertView;
		}else{
			layout = inflater.inflate(R.layout.settings_item, parent , false);
		}
		TextView text = (TextView) (layout.findViewById(R.id.parameter_id));
		text.setText(this.getItem(position));
		SeekBar slider = (SeekBar) (layout.findViewById(R.id.sensibility));
		
		if(position==0){
			slider.setVisibility(View.GONE);
			text.setTextSize(20f);
		}
		return layout;
	}
	
	@Override
	public View getDropDownView(int position , View convertView , ViewGroup parent){
		return getCustomView(position,convertView,parent);
	}
	
	@Override
	public View getView(int position , View convertView , ViewGroup parent){
		return getCustomView(position, convertView , parent);
	}
}
