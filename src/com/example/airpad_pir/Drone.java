package com.example.airpad_pir;

import android.view.View;

public class Drone {
	public static  final int MAX_RAYON = 100;// en cm
	public static final int MAX_ALTITUDE = 1000; // en cm
	private volatile static double MAX_DEPLACEMENT = 30;
	private static final double MAX_VITESSE  = 0.01;
	public static final double MAX_ANGLE = 180; // en degre
	public static final double MAX_D_VITESSE =1.0;
	
	private double currentRayon;
	
	private volatile double desireAngle;
	private volatile double currentAngle;
	private volatile double currentVitesse;
	
	private double xPosition;
	private double yPosition;
	private double zPosition;
	private WhenDroneMove mDroneObserver;
	private SpeedometerGauge mSpeed;
	
	public Drone(){
		xPosition = MAX_RAYON;
		yPosition = 0;
		zPosition = MAX_ALTITUDE/2;
		
		desireAngle = 0;
		currentAngle = 0;
		
		currentRayon = MAX_RAYON;
		currentVitesse = 0.5;
	}
	
	public Drone (double xPosition , double yPosition , double zPosition){
		this.xPosition= xPosition;
		this.yPosition = yPosition;
		this.zPosition = zPosition;
		currentRayon = Math.sqrt(xPosition*xPosition + yPosition* yPosition);
	}
	
	public Drone (double xPosition , double yPosition){
		this.xPosition= xPosition;
		this.yPosition= yPosition;
		this.zPosition = MAX_ALTITUDE;
		currentRayon = Math.sqrt(xPosition*xPosition + yPosition*yPosition);
	}
	
	public void setActionWhenDroneMove(WhenDroneMove droneObserver){
		mDroneObserver = droneObserver;
	}
	
	public void setActionWhenVitesseChange(SpeedometerGauge speed){
		mSpeed = speed;
		mSpeed.setSpeed(currentVitesse*100);
	}
	
	public double getXPosition(){
		return xPosition;
	}
	
	public double getYPosition(){
		return yPosition;
	}
	
	public void setCurrentAngle(double angle){
		currentAngle = angle;
	}
	
	public double getZPosition(){
		return zPosition;
	}
	
	public double getCurrentRayon(){
		return currentRayon;
	}
	
	public void moveTo(double angle ,double power){
		double angleRad = Math.sin(Math.toRadians(angle));
		double dist =0;
		synchronized(this){
			dist = (MAX_DEPLACEMENT*currentVitesse)*power;
		}
		double deltaAngle = angleRad*dist;
		incrementPosition(deltaAngle);
	}
	
	public double getDesireAngle(){
		return desireAngle;
	}
	
	public void setDesireAngle(double angle){
		desireAngle = angle;
	}
	
	public void moveVitesse(double angle , double power){
		double angleRad = Math.toRadians(angle);
		double dist = MAX_VITESSE*power;
		double deltaDep = Math.sin(angleRad)*dist;
		incrementVitesse(deltaDep);
	}
	
	public void incrementPosition(double deltaAngle){
		/*if(Math.abs(desireAngle+deltaAngle)>MAX_ANGLE){
			desireAngle=MAX_ANGLE;
		}else{
			desireAngle+=deltaAngle;
		}*/
		double res =desireAngle;
		if(res+deltaAngle>MAX_ANGLE){
			res=MAX_ANGLE;
		}else if((res+deltaAngle)<-MAX_ANGLE){
			res= -MAX_ANGLE;
		}else {
			res+=deltaAngle;
		}
		desireAngle=res;
	}
	public synchronized void incrementVitesse(double deltaVitesse){
		/*if(Math.abs(desireAngle+deltaAngle)>MAX_ANGLE){
			desireAngle=MAX_ANGLE;
		}else{
			desireAngle+=deltaAngle;
		}*/
		double res = currentVitesse;
		if(res+deltaVitesse>MAX_D_VITESSE){
			res=MAX_D_VITESSE;
		}else if((res+deltaVitesse)<0){
			res= 0;
		}else {
			res+=deltaVitesse;
		}
		currentVitesse=res;
		if(mSpeed.getVisibility()==View.VISIBLE){
			mSpeed.post(new Runnable(){
				@Override
				public void run() {
					mSpeed.setSpeed(currentVitesse*100);
				}
			});
		}
	}
	
	public void setPosition(){
		if(mDroneObserver!=null && mDroneObserver.isVisible()){
			double angleRadian = Math.toRadians(currentAngle);
			xPosition= Math.cos(angleRadian)*currentRayon;
			yPosition = Math.sin(angleRadian)*currentRayon;
			mDroneObserver.repaintDrone();
		}
	}
}
