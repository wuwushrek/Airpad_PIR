package com.example.airpad_pir;

public class Drone {
	public static  final int MAX_RAYON = 100;// en cm
	public static final int MAX_ALTITUDE = 1000; // en cm
	
	private double currentRayon;
	private double xPosition;
	private double yPosition;
	private double zPosition;
	
	public Drone(){
		xPosition = MAX_RAYON;
		yPosition = 0;
		zPosition = MAX_ALTITUDE/2;
		currentRayon = MAX_RAYON;
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
	
	public double getXPosition(){
		return xPosition;
	}
	
	public double getYPosition(){
		return yPosition;
	}
	
	public double getZPosition(){
		return zPosition;
	}
	
	public void setXPosition(double newXValue){
		xPosition = newXValue;
		setRayon();
	}
	
	public void setYPosition(double newYValue){
		yPosition = newYValue;
		setRayon();
	}
	
	public void seZPosition(double newZValue){
		zPosition = newZValue;
	}
	
	public void setPosition(double newXValue , double newYValue){
		xPosition = newXValue;
		yPosition = newYValue;
		setRayon();
	}
	
	public double getCurrentRayon(){
		return currentRayon;
	}
	
	private void setRayon(){
		currentRayon = Math.sqrt(xPosition*xPosition + yPosition*yPosition);
	}
}
