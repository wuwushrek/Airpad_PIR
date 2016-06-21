package com.example.airpad_pir;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final Holder h = (Holder) v.getTag();
					if(h.playButton.getVisibility()!=View.VISIBLE){
						v.setBackgroundResource(R.drawable.red_button_pressed);
						h.deleteButton.setVisibility(View.VISIBLE);
						h.modifierButton.setVisibility(View.VISIBLE);
						h.playButton.setVisibility(View.VISIBLE);
					}else {
						v.setBackgroundResource(R.drawable.top_button_enabled);
						h.deleteButton.setVisibility(View.GONE);
						h.modifierButton.setVisibility(View.GONE);
						h.playButton.setVisibility(View.GONE);
					}
				}
			});
		}else{ 
			holder = (Holder) convertView.getTag();
		}
		final MissionProgram mission = (MissionProgram) getItem(position);
		if(mission!=null){
			holder.programName.setText(mission.getNomMission());
			holder.programType.setImageResource(getImageAssociateWith(mission.getTypeMission()));
			
			holder.deleteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
					alert.setTitle("SUPPRESION EN COURS");
					alert.setIcon(R.drawable.alert_icon);
					alert.setMessage("Etes vous de vouloir supprimer "+mission.getNomMission()+" ?");
					alert.setPositiveButton("OUI", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							// TODO Auto-generated method stub
							removeMission(mission);
							Toast.makeText(mContext,mission.getNomMission()+" a été supprimé...",Toast.LENGTH_LONG).show();
							dialog.dismiss();
						}
					});
					alert.setNegativeButton("NON", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(mContext,mission.getNomMission()+" n' a pas été supprimé...",Toast.LENGTH_SHORT).show();
							dialog.dismiss();
						}
					});
					alert.show();
				}
			});
			holder.modifierButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}
			});
			holder.playButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MainActivity) mContext).startPlayingMission(mission);
				}
			});
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
	
	public void modifierMission(){
		
	}
	
	public boolean contains (MissionProgram mission){
		return programmes.contains(mission);
	}
	
	public class Holder{
		TextView programName;
		ImageView programType;
		ImageButton playButton;
		ImageButton modifierButton;
		ImageButton deleteButton;
	}
}
