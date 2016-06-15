package com.example.airpad_pir;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ProgramViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<MissionProgram> programmes;
	private static LayoutInflater inflater = null;
	
	public ProgramViewAdapter(Context c){
		mContext = c;
		programmes = new ArrayList<MissionProgram>();
		inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public ProgramViewAdapter(Context c , List<MissionProgram> mMissions){
		mContext = c;
		inflater =(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		programmes = mMissions;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return programmes.size();
	}

	@Override
	public Object getItem(int position) {
		return programmes.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.program_item_layout, null);
			holder = new Holder();
			holder.playButton = (ImageButton) convertView.findViewById(R.id.play_program);
			holder.deleteButton = (ImageButton) convertView.findViewById(R.id.delete_program);
			holder.modifierButton= (ImageButton) convertView.findViewById(R.id.modify_program);
			holder.programName=(TextView) convertView.findViewById(R.id.programName);
			holder.programType = (ImageView) convertView.findViewById(R.id.typeImage);
			convertView.setTag(holder);
		}else{ 
			holder = (Holder) convertView.getTag();
		}
		MissionProgram mission = (MissionProgram) getItem(position);
		if(mission!=null){
			holder.programName.setText(mission.getNomMission());
			holder.programType.setImageResource(getImageAssociateWith(mission.getTypeMission()));
		}
		return convertView;
	}
	
	public int getImageAssociateWith(String type){
		if(type.equals("AIRPAD RESCUE")){
			return R.drawable.rescue_mission_icon;
		}else if(type.equals("AIRPAD ARTIST")){
			return R.drawable.mission_artist_icon;
		}else if(type.equals("AIRPAD INVADER")){
			return R.drawable.mission_invader_icon;
		}else if(type.equals("AIRPAD RACE")){
			return R.drawable.race_mission_icon;
		}else{
			return R.drawable.mission_other_icon;
		}
	}
	
	public void addMission(MissionProgram mission){
		programmes.add(mission);
		notifyDataSetChanged();
	}
	
	public void removeMission(MissionProgram mission){
		programmes.remove(mission);
		notifyDataSetChanged();
	}
	
	public class Holder{
		TextView programName;
		ImageView programType;
		ImageButton playButton;
		ImageButton modifierButton;
		ImageButton deleteButton;
	}
}
