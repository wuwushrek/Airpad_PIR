package com.example.airpad_pir;

public class Drone {
	public static  final int MAX_RAYON = 100;// en cm
	public static final int MAX_ALTITUDE = 1000; // en cm
	private volatile static double MAX_DEPLACEMENT = 5; // en angle
	private static final double MAX_VITESSE  = 2;
	public static final double MAX_ANGLE = 180; // en degre
	public static final double MAX_D_VITESSE =10;
	
	private double currentRayon;
	
	private volatile double desireAngle;
	private volatile double currentAngle;
	private volatile double currentVitesse;
	
	private double xPosition;
	private double yPosition;
	private double zPosition;
	private WhenDroneMove mDroneObserver;
	
	public Drone(){
		xPosition = MAX_RAYON;
		yPosition = 0;
		zPosition = MAX_ALTITUDE/2;
		
		desireAngle = 0;
		currentAngle = 0;
		
		currentRayon = MAX_RAYON;
		currentVitesse = 0;
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
	
	public synchronized double getXPosition(){
		return xPosition;
	}
	
	public synchronized double getYPosition(){
		return yPosition;
	}
	
	public synchronized void setCurrentAngle(double angle){
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
		double dist = (MAX_DEPLACEMENT+currentVitesse)*power;
		double deltaAngle = angleRad*dist;
		//double deltaY = Math.sin(angleRad)*dist; /:Pas besoin vu 1 seul degre de liberte
		incrementPosition(deltaAngle);
	}
	
	public double getDesireAngle(){
		return desireAngle;
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
	public void incrementVitesse(double deltaVitesse){
		/*if(Math.abs(desireAngle+deltaAngle)>MAX_ANGLE){
			desireAngle=MAX_ANGLE;
		}else{
			desireAngle+=deltaAngle;
		}*/
		double res = currentVitesse;
		if(res+deltaVitesse>MAX_D_VITESSE){
			res=MAX_D_VITESSE;
		}else if((res+deltaVitesse)<-MAX_D_VITESSE){
			res= -MAX_D_VITESSE;
		}else {
			res+=deltaVitesse;
		}
		res+=MAX_D_VITESSE;
		currentVitesse=res;
		
	}
	
	public synchronized void setPosition(){
		if(mDroneObserver!=null && mDroneObserver.isVisible()){
			double angleRadian = Math.toRadians(currentAngle);
			xPosition= Math.cos(angleRadian)*currentRayon;
			yPosition = Math.sin(angleRadian)*currentRayon;
			mDroneObserver.repaintDrone();
		}
	}
}
