package com.example.airpad_pir;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class Drone2DRendering extends View implements WhenDroneMove {

	private static int MAX_RAYON_PIXEL = 70;
	private Drone mDrone;
	private Paint radiusPaint;
	private Paint circlePaint;
	private Paint helicePaint;
	private Paint hauteurPaint;
	
	private RectF background;
	private Paint backgroundPaint;
	private LinearGradient backgroundColor;
	
	public Drone2DRendering(Context context) {
		super(context);
		mDrone = new Drone();
		initView();
	}
	
	public Drone2DRendering (Context context , AttributeSet attrs , int defStyle){
		super(context , attrs, defStyle);
		mDrone = new Drone();
		initView();
	}
	
	public Drone2DRendering	(Context context, AttributeSet attrs){
		super( context , attrs);
		mDrone = new Drone();
		initView();
	}
	
	public void setDrone(Drone drone){
		mDrone = drone;
		this.invalidate();
	}
	
	private void initView(){
		radiusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		radiusPaint.setStyle(Paint.Style.STROKE);
		radiusPaint.setColor(Color.BLUE);
		radiusPaint.setStrokeWidth(2.0f);
		
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setStyle(Paint.Style.FILL);
		circlePaint.setColor(Color.LTGRAY);
		
		helicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		helicePaint.setStyle(Paint.Style.FILL);
		helicePaint.setColor(Color.BLACK);
		
		hauteurPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		hauteurPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		hauteurPaint.setColor(Color.BLACK);
		
		backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		background = new RectF();
		
	}
	
	private int singleMeasure(int spec, int screenDim,boolean isWidth){
		int mode = MeasureSpec.getMode(spec);
		int size = MeasureSpec.getSize(spec);
		
		if(mode == MeasureSpec.UNSPECIFIED)
			return isWidth?(MAX_RAYON_PIXEL*2+ MAX_RAYON_PIXEL/2):(MAX_RAYON_PIXEL*2);
		else{
			MAX_RAYON_PIXEL= isWidth?(size*2/5):(size/2);
			return size;
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec , int heightMeasureSpec){
		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		int screenWidth = metrics.widthPixels;
		int screenHeight = metrics.heightPixels;

		int retourWidth = singleMeasure(widthMeasureSpec,screenWidth,true);
		int retourHeight = singleMeasure(heightMeasureSpec,screenHeight,false);
		
		int retour = Math.min(retourWidth, retourHeight);
		setMeasuredDimension(retour, retour);
	}
	
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		backgroundColor = new LinearGradient(-40,-40,xNew , yNew,Color.BLUE, Color.MAGENTA,Shader.TileMode.CLAMP);
		backgroundPaint.setShader(backgroundColor);
		background.top=-40;
		background.left=-40;
		background.right=xNew+40;
		background.bottom=yNew+40;
	}
	
	private int getPlanScreenPosition( double realPosition){
		return (int) (realPosition*MAX_RAYON_PIXEL/Drone.MAX_RAYON);
	}
	private int getAltScreenPosition( double realPosition){
		return (int) (realPosition*2*MAX_RAYON_PIXEL/Drone.MAX_ALTITUDE);
	}
	
	@Override 
	protected void onDraw(Canvas canvas){
		int width = getWidth();
		int height = getHeight();
		canvas.drawRoundRect(background, 10, 10, backgroundPaint);
		
		int currentRayon = getPlanScreenPosition(mDrone.getCurrentRayon());
		int currentX = getPlanScreenPosition (mDrone.getXPosition())+MAX_RAYON_PIXEL;
		int currentY = getPlanScreenPosition(-mDrone.getYPosition()) + MAX_RAYON_PIXEL;
		int currentZ = getAltScreenPosition(-mDrone.getZPosition())+ MAX_RAYON_PIXEL;
		
		//Dessin du rayonMax
		canvas.drawCircle(MAX_RAYON_PIXEL,height/2,MAX_RAYON_PIXEL, radiusPaint);
		
		//dessin du rayon actuel
		canvas.drawCircle(MAX_RAYON_PIXEL, height/2,currentRayon, circlePaint);
		//canvas.drawCircle(currentX, currentY, 10, hauteurPaint);
		
		//dessin du potard
		canvas.drawLine(MAX_RAYON_PIXEL,height/2, currentX, currentY, radiusPaint);
		
		//dessin de la donnee de l'altitude
		int distCircle = 40;
		int longueurHorizontale = width-2*MAX_RAYON_PIXEL-distCircle;
		canvas.drawRect(width-longueurHorizontale,currentZ, width-longueurHorizontale+15, 
				height-5, hauteurPaint);//hauteur actuelle
		canvas.drawRect(width-longueurHorizontale,0, width-longueurHorizontale+15,
				currentZ,radiusPaint);
		canvas.drawRect(width-longueurHorizontale-10, height-5, width-longueurHorizontale+25,
				height, hauteurPaint);//20 de longueur
		canvas.drawLine(width-longueurHorizontale,currentZ, width-25,currentZ, hauteurPaint);
		createDroneProjection(canvas,width-25,currentZ);
		
		createDronePlan(canvas,currentX,currentY);
		
	}
	
	public void createDroneProjection(Canvas canvas, int xPos ,int currentZ){

		canvas.drawLine(xPos, currentZ, xPos, currentZ-10, hauteurPaint);
		
		canvas.drawLine(xPos, currentZ-10, xPos-15, currentZ-10, hauteurPaint);
		canvas.drawLine(xPos-15, currentZ-10, xPos-15,currentZ-20,hauteurPaint);
		canvas.drawLine(xPos-25,currentZ-20 , xPos-5,currentZ-20,radiusPaint);
		
		canvas.drawLine(xPos, currentZ-10, xPos+15, currentZ-10, hauteurPaint);
		canvas.drawLine(xPos+15, currentZ-10, xPos+15,currentZ-20,hauteurPaint);
		canvas.drawLine(xPos+5,currentZ-20 , xPos+25,currentZ-20,radiusPaint);
		
		
	}
	
	public void createDronePlan(Canvas canvas, int xPos , int yPos){
		canvas.drawLine(xPos, yPos-25, xPos, yPos+25, hauteurPaint);
		canvas.drawLine(xPos-25,yPos, xPos+25,yPos,hauteurPaint);
		
		canvas.drawOval(myOval(16,5,xPos+8,yPos-25), hauteurPaint);
		canvas.drawOval(myOval(16,5,xPos-8,yPos-25), hauteurPaint);
		canvas.drawOval(myOval(16,5,xPos+8,yPos+25), hauteurPaint);
		canvas.drawOval(myOval(16,5,xPos-8,yPos+25), hauteurPaint);
		

		canvas.drawOval(myOval(5,16,xPos-25,yPos-8), hauteurPaint);
		canvas.drawOval(myOval(5,16,xPos+25,yPos-8), hauteurPaint);
		canvas.drawOval(myOval(5,16,xPos-25,yPos+8), hauteurPaint);
		canvas.drawOval(myOval(5,16,xPos+25,yPos+8), hauteurPaint);
		//canvas.drawOval(myOval(10,25,xPos-25,yPos), hauteurPaint);
	}
	
	public RectF myOval(float width , float height ,float x , float y){
		return new RectF(x-width/2,y-height/2,x+width/2,y+height/2);
	}

	@Override
	public void repaintDrone() {
		this.postInvalidate(-40,-40, 2*MAX_RAYON_PIXEL+60, getHeight()+40);
		/*
		background.top=-40;
		background.left=-40;
		background.right=xNew+40;
		background.bottom=yNew+40;*/
	}

	@Override
	public boolean isVisible() {
		return this.getVisibility()==View.VISIBLE;
	}
}
