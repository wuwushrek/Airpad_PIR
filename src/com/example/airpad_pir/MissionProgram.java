package com.example.airpad_pir;

public class MissionProgram {
	private String typeMission;
	private String missionName;
	//A rajouter tout les blocs codes dans cette mission
	
	public MissionProgram(String nomMission , String typeMission){
		this.typeMission= typeMission;
		this.missionName= nomMission;
	}
	
	public String getTypeMission(){
		return typeMission;
	}
	
	public String getNomMission(){
		return missionName;
	}
	@Override
	public boolean equals(Object obj){
		if(obj instanceof MissionProgram ){
			return (((MissionProgram) obj).missionName== missionName)&& ((MissionProgram) obj).typeMission== typeMission;
		}else {
			return false;
		}
	}
}
