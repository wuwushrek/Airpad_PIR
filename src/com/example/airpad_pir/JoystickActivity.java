package com.example.airpad_pir;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.airpad_pir.JoystickCustomView.OnJoystickMoveListener;

//https://github.com/bilthon/Android-Speedometer
public class JoystickActivity extends Activity implements SensorEventListener{
	
	private SensorManager sensorManager;
	private Sensor senAccelerometer;
	private long lastUpdate =0;
	private float last_x,last_y,last_z;
	//private static final int SHAKE_THRESHOLD = 200;
	private boolean pausingGyro = false;
	
	private static final int ID_BLUETOOTH_LAUNCHING =0;
	private static final int ID_CAMERA_LAUNCH =1;
	
	private final int[] position = new int[2];
	private static final long TIME_TO_TAP=500;
	private static final long ANIMATION_TIME = 500;
	private static final long SHOWING_ANIMATION =1000;
	private static final int NUMBER_TAP=2;
	private int countTap =0;
	private long lastTapTime=0;
	private int extraYBottom ;
	private int extraYTop;
	private long timeWhenStopped =0;
	private double leftJoystickSize = 0.0;
	private double rightJoystickSize = 0.0;
	
	public static ProgressDialog progress;
	private ButtonsListener mButtonListener;
	
	private RelativeLayout mainLayout;
	private RelativeLayout informationL;
	private RelativeLayout menuContent;
	
	private JoystickCustomView throttle;
	private JoystickCustomView joystick;
	private SpeedometerGauge speedometer;
	
	private Button homeButton;
	private Button backButton;
	private Button altitudeButton;
	private Button rayonButton;
	
	private ImageView showSpeed;
	private ImageView showPerspectiveView;	
	private ImageView helpControl;
	private ImageView programView;
	private ImageView playChrono , pauseChrono , stopChrono;
	
	private Chronometer chrono;
	private Drone2DRendering droneRendering;
	private Drone mDrone;
	
	private ImageButton settingButton;
	private ImageButton startControl;
	private Button emergencyButton;
	private Button battery;
	private ImageButton cameraButton;
	private Button missionControl;
	private Switch gyro_select;
	private Button playGyro;
	private Button plusJoystick;
	private Button moinsJoystick;
	private Button plusThrottle;
	private Button moinsThrottle;
	
	private BluetoothHandle mBluetooth;

	private boolean useAccelerometer = false;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_drone_layout);
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		senAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		//bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
		//upBottom = AnimationUtils.loadAnimation(this, R.anim.up_bottom);
		//mButtonAnimation = AnimationUtils.loadAnimation(this,R.anim.scaling_button);	
		mButtonListener = new ButtonsListener();
		

		mainLayout =(RelativeLayout) findViewById(R.id.joystick_parent_view);
		informationL =(RelativeLayout) findViewById(R.id.information_view);
		menuContent =(RelativeLayout) findViewById(R.id.content);

		homeButton = (Button) findViewById(R.id.home_control);
		backButton =(Button) findViewById(R.id.back_control);
		altitudeButton = (Button) findViewById(R.id.hauteur_but);
		rayonButton = (Button) findViewById(R.id.circle_distance);
		
		showSpeed = (ImageView) findViewById(R.id.activate_speed);
		showPerspectiveView = (ImageView) findViewById(R.id.drone_position);
		helpControl =(ImageView) findViewById(R.id.help_control_page);
		programView = (ImageView) findViewById(R.id.program_control);
		playChrono = (ImageView) findViewById(R.id.play_enable);
		pauseChrono= (ImageView) findViewById(R.id.pause_disable);
		stopChrono =(ImageView) findViewById(R.id.stop_button);
		chrono =(Chronometer) findViewById(R.id.chronometre);
		
		settingButton = (ImageButton) findViewById(R.id.settings_menu);
		startControl = (ImageButton) findViewById(R.id.start_control);
		emergencyButton = (Button)	findViewById(R.id.emergency);
		battery = 	(Button)	findViewById(R.id.battery_data);
		cameraButton = (ImageButton) findViewById(R.id.camera_button);
		missionControl = (Button) findViewById(R.id.mission_control);


		speedometer= (SpeedometerGauge) findViewById(R.id.speedometer_gauge);
		joystick = (JoystickCustomView) findViewById(R.id.joystickView);
		throttle = (JoystickCustomView) findViewById(R.id.throttleView);
		droneRendering = (Drone2DRendering) findViewById(R.id.droneVue);
		gyro_select = (Switch) findViewById(R.id.gyro_select_value);
		playGyro = (Button) findViewById(R.id.play_accelero_button);
		
		plusJoystick = (Button) findViewById(R.id.plus_joystick);
		plusThrottle =(Button) findViewById(R.id.plus_throttle);
		moinsJoystick = (Button) findViewById(R.id.moins_joystick);
		moinsThrottle = (Button) findViewById(R.id.moins_throttle);
		
		plusJoystick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(leftJoystickSize<1.0){
					int nextWidth = joystick.getWidth()+ (int) (leftJoystickSize*20);
					int nextHeight = joystick.getHeight()+(int) (leftJoystickSize*20);
					joystick.setLayoutParams(new RelativeLayout.LayoutParams(nextWidth,nextHeight));
					leftJoystickSize+=0.1;
				}
				joystick.setVisibility(View.VISIBLE);
				joystick.setTranslationX(200);
				joystick.setTranslationY(200);
			}
		});
		moinsJoystick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(leftJoystickSize>0){
					int nextWidth = joystick.getWidth()- (int) (leftJoystickSize*20);
					int nextHeight = joystick.getHeight()-(int) (leftJoystickSize*20);
					joystick.setLayoutParams(new RelativeLayout.LayoutParams(nextWidth,nextHeight));
					leftJoystickSize-=0.1;
				}
				joystick.setVisibility(View.VISIBLE);
				joystick.setTranslationX(200);
				joystick.setTranslationY(200);
			}
		});
		plusThrottle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(rightJoystickSize<1.0){
					int nextWidth = throttle.getWidth()+ (int) (rightJoystickSize*20);
					int nextHeight = throttle.getHeight()+(int) (rightJoystickSize*20);
					throttle.setLayoutParams(new RelativeLayout.LayoutParams(nextWidth,nextHeight));
					rightJoystickSize+=0.1;
				}
				throttle.setVisibility(View.VISIBLE);
				throttle.setTranslationX(700);
				throttle.setTranslationY(200);
			}
		});
		moinsThrottle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(rightJoystickSize>0){
					int nextWidth = throttle.getWidth()- (int) (rightJoystickSize*20);
					int nextHeight = throttle.getHeight()-(int) (rightJoystickSize*20);
					throttle.setLayoutParams(new RelativeLayout.LayoutParams(nextWidth,nextHeight));
					leftJoystickSize-=0.1;
				}
				throttle.setVisibility(View.VISIBLE);
				throttle.setTranslationX(700);
				throttle.setTranslationY(200);
			}
		});
		
		joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

			@Override
			public void onValueChanged(double angle, double power) {
				mDrone.moveTo(angle, power);
			}
		}, JoystickCustomView.DEFAULT_LOOP_INTERVAL);
		throttle.setOnJoystickMoveListener(new OnJoystickMoveListener() {

			@Override
			public void onValueChanged(double angle, double power) {
				mDrone.moveVitesse(angle, power);
			}
		}, JoystickCustomView.DEFAULT_LOOP_INTERVAL);
		
		settingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dragSettingMenu();
			}
		});
		/*cameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cameraAction();
			}
		});*
		/*startControl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startConnection();
			}
		});*/
		
		homeButton.setOnClickListener(mButtonListener);
		backButton.setOnClickListener(mButtonListener);
		altitudeButton.setOnClickListener(mButtonListener);
		rayonButton.setOnClickListener(mButtonListener);
		programView.setOnClickListener(mButtonListener);
		showPerspectiveView.setOnClickListener(mButtonListener);
		showSpeed.setOnClickListener(mButtonListener);
		stopChrono.setOnClickListener(mButtonListener);
		pauseChrono.setOnClickListener(mButtonListener);
		playChrono.setOnClickListener(mButtonListener);
		helpControl.setOnClickListener(mButtonListener);
		emergencyButton.setOnClickListener(mButtonListener);
		startControl.setOnClickListener(mButtonListener);
		cameraButton.setOnClickListener(mButtonListener);

		speedometer.setLabelConverter(new SpeedometerGauge.LabelConverter() {
			@Override
			public String getLabelFor(double progress, double maxProgress) {
				return String.valueOf((int) Math.round(progress));
			}
		});
		gyro_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					useAccelerometer=true;
					startAccelerometer();
				}else {
					stopAccelerometer();
					useAccelerometer = false;
				}
			}
		});
		
		playGyro.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mBluetooth.isConnect()){
					Toast.makeText(JoystickActivity.this, "Connect your Smartphone to AirPad !!", Toast.LENGTH_LONG).show();
					return;
				}
				if(pausingGyro){
					sensorManager.registerListener(JoystickActivity.this, senAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
					mBluetooth.launchAccelerometerTransfert();
					playGyro.setText("PAUSE");
					pausingGyro = false;
				}else{
					sensorManager.unregisterListener(JoystickActivity.this);
					playGyro.setText("PLAY");
					pausingGyro = true;
				}
			}
		});
		speedometer.setMaxSpeed(100);
		speedometer.setMajorTickStep(20);
		speedometer.setMinorTicks(2);
		speedometer.addColoredRange(20, 40, Color.GREEN);
		speedometer.addColoredRange(40, 70, Color.YELLOW);
		speedometer.addColoredRange(70, 100, Color.RED);
		speedometer.setSpeed(0);
		informationL.bringToFront();
		joystick.bringToFront();
		throttle.bringToFront();
		menuContent.bringToFront();

		extraYBottom= getResources().getDimensionPixelSize(R.dimen.information_bar_height);
		extraYTop =getResources().getDimensionPixelSize(R.dimen.top_menu_height);

		mDrone = new Drone();
		droneRendering.setDrone(mDrone);
		mDrone.setActionWhenDroneMove(droneRendering);
		mDrone.setActionWhenVitesseChange(speedometer);
		mBluetooth = new BluetoothHandle(this,mDrone);
		//sensorManager.registerListener(this, senAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void startAccelerometer(){
		mBluetooth.setUsingSensor(useAccelerometer);
		playGyro.setVisibility(View.VISIBLE);
		sensorManager.registerListener(JoystickActivity.this, senAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
		if(mBluetooth.isConnect()){
			mBluetooth.launchAccelerometerTransfert();
			playGyro.setText("PAUSE");
			pausingGyro=false;
		}else{
			playGyro.setText("PLAY");
			pausingGyro=true;
		}
	}
	
	public void stopAccelerometer(){
		mBluetooth.setUsingSensor(false);
		sensorManager.unregisterListener(JoystickActivity.this);
		playGyro.setVisibility(View.GONE);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		mBluetooth.disconnect();
		if(useAccelerometer){
			stopAccelerometer();
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if(useAccelerometer){
			stopAccelerometer();
		}
	}
	@Override
	protected void onResume(){
		super.onResume();
		if(useAccelerometer){
			startAccelerometer();
		}
	}
	
	
	@Override
	public Dialog onCreateDialog(int id){
		AlertDialog.Builder box = null;
		if(id == ID_BLUETOOTH_LAUNCHING){
			box = new AlertDialog.Builder(this);
			box.setTitle("Communication Bluetooth");
			box.setIcon(R.drawable.bluetooth_connex);
			box.setMessage("Appairage et commande manuelle en cours d'initialisation ....")
						.setCancelable(true);
		}else{
			box = new AlertDialog.Builder(this);
			box.setTitle("Camera");
			box.setIcon(R.drawable.camera_control);
			box.setMessage("Activation de la camera en cours ....")
						.setCancelable(true);
		}
		return box.create();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//System.out.println("NOMBRE POINTER "+ev.getPointerCount()+", "+ev.getPointerId(0)+ (ev.getPointerCount()==2?ev.getPointerId(1):-5));
		mainLayout.getLocationOnScreen(position);
		int pointerId = ev.getPointerId(ev.getActionIndex());
		//System.out.println("POINTER ID "+pointerId); 
		float relativePosX = ev.getX(ev.getActionIndex())-position[0];
		float relativePosY =ev.getY(ev.getActionIndex())-position[1];
		
		if(ev.getAction()==MotionEvent.ACTION_DOWN && menuContent.getVisibility()== View.VISIBLE 
				&& relativePosX>menuContent.getWidth()){
			slideToLeft(menuContent);
			return super.dispatchTouchEvent(ev);
		}else if(menuContent.getVisibility()==View.VISIBLE){
			return super.dispatchTouchEvent(ev);
		}
		
		if(ev.getAction()==MotionEvent.ACTION_UP){
			//System.out.println("POINTER ID "+pointerId+" , ACTION UP");
			if(joystick.getCurrentEventId() == pointerId){
				joystick.setVisibility(View.GONE);
				joystick.setCurrentEventId(-1);
				mBluetooth.stopSendingPosition();
			}else if (throttle.getCurrentEventId()== pointerId){
				throttle.setVisibility(View.GONE);
				throttle.setCurrentEventId(-1);
			}
			long time = System.currentTimeMillis();
			if((time-lastTapTime)>TIME_TO_TAP || countTap==0){
				lastTapTime=time;
				countTap=1;
			}else{
				countTap++;
			}
			if(countTap==NUMBER_TAP){
				slideUpDown(informationL);
			}
		}
		if(useAccelerometer)
			return super.dispatchTouchEvent(ev);
		if(ev.getActionMasked()==MotionEvent.ACTION_POINTER_UP){
			if(joystick.getCurrentEventId()== pointerId){
				joystick.setVisibility(View.GONE);
				joystick.setCurrentEventId(-1);
				mBluetooth.stopSendingPosition();
			}else if(throttle.getCurrentEventId() == pointerId){
				throttle.setVisibility(View.GONE);
				throttle.setCurrentEventId(-1);
			}
		}
		
		if((ev.getActionMasked()== MotionEvent.ACTION_DOWN || ev.getActionMasked()==MotionEvent.ACTION_POINTER_DOWN)
				&& (ev.getPointerCount()<=2)){
			if((relativePosX<mainLayout.getWidth()/2) && (relativePosY<mainLayout.getHeight()-extraYBottom) 
				&& (relativePosY>extraYTop)){
				joystick.setTranslationX(adaptedCoordinate(relativePosX,0,mainLayout.getWidth()/2,joystick.getWidth()/2));
				joystick.setTranslationY(adaptedCoordinateY(relativePosY,mainLayout.getHeight(),joystick.getHeight()/2));
				joystick.setVisibility(View.VISIBLE);
				joystick.setCurrentEventId(pointerId);
				mBluetooth.startSendingPosition();
			}
			if((relativePosX>mainLayout.getWidth()/2) && (relativePosY<mainLayout.getHeight()-extraYBottom)
				&& (relativePosY>extraYTop)){
				throttle.setTranslationX(adaptedCoordinate(relativePosX,mainLayout.getWidth()/2,mainLayout.getWidth(),joystick.getWidth()/2));
				throttle.setTranslationY(adaptedCoordinateY(relativePosY,mainLayout.getHeight(),joystick.getHeight()/2));
				throttle.setVisibility(View.VISIBLE);
				throttle.setCurrentEventId(pointerId);
			}
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private float adaptedCoordinate(float evX ,float leftLimit,int width , int viewMidWidth){
		if(evX-viewMidWidth>leftLimit && evX+viewMidWidth<width){
			return evX-viewMidWidth;
		}
		if(evX-viewMidWidth<=leftLimit){
			return leftLimit;
		}
		return width- 2*viewMidWidth;
	}
	private float adaptedCoordinateY(float evX , int width , int viewMidWidth){
		if(evX-viewMidWidth>extraYTop && evX+viewMidWidth<width-extraYBottom){
			return evX-viewMidWidth;
		}
		if(evX-viewMidWidth<=extraYTop)
			return extraYTop;
		return width-extraYBottom-2*viewMidWidth;
	}
	
	public void slideUpDown(View v){
		if(!isPanelShown()){
			slideToTop(v);
			System.out.println("SLIDING TOP");
		}else{
			slideToBottom(v);
			System.out.println("SLIDING BOTTOM");
		}
	}
	
	public void emergencyAction(){
		if(!mBluetooth.isConnect()){
			return;
		}
		try {
			if(useAccelerometer){
				stopAccelerometer();
			}
			mDrone.setDesireAngle(0.0);
			mBluetooth.sendConsigne();
			mBluetooth.disconnect();
			startControl.setBackgroundResource(R.drawable.top_button_selector);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void cameraAction(){
		showDialog(ID_CAMERA_LAUNCH);
	}
	
	public void startConnection(){
		if(mBluetooth.isConnect()){
			startControl.setBackgroundResource(R.drawable.round_button_selector);
			mBluetooth.disconnect();
			System.out.println("EST EN TRAIN DE DECONNECTER");
		}else{
			startControl.setBackgroundResource(R.drawable.red_button_pressed);
			mBluetooth.connect();
			System.out.println("EST EN TRAIN DE SE CONNECTER");
		}
	}
	
	public void openMissionsSetting(){
		
	}
	
	public void showSpeedometer(){
		AnimationSet anim = new AnimationSet(true);
		anim.setFillAfter(false);
		Animation scale= null;
		Animation fade = null;
		int repeatCount =0;
		if(speedometer.getVisibility()==View.GONE){
			showSpeed.setImageResource(R.drawable.on_speed);
			speedometer.setVisibility(View.VISIBLE);
			scale =scaleAnimation(0f,1f,0f,1f,0.5f,0.5f);
			fade= fadeAnimation(0f,1f,repeatCount);
		}else{
			showSpeed.setImageResource(R.drawable.off_speed);
			fade = fadeAnimation(1f,0f,repeatCount);
			scale = scaleAnimation(1f,0f,1f,0f,0.5f,0.5f);
			anim.setAnimationListener(new AnimationListener(){
				@Override
				public void onAnimationEnd(Animation arg0) {
					speedometer.setVisibility(View.GONE);
				}
				@Override
				public void onAnimationRepeat(Animation arg0) {}
				@Override
				public void onAnimationStart(Animation arg0) {}
			});
		}
		scale.setDuration(SHOWING_ANIMATION);
		fade.setDuration(SHOWING_ANIMATION/(repeatCount+1));
		anim.addAnimation(fade);
		anim.addAnimation(scale);
		speedometer.startAnimation(anim);
	}
	
	public void showPlanView(){
		AnimationSet anim = new AnimationSet(true);
		anim.setFillAfter(false);
		Animation scale= null;
		Animation fade = null;
		int repeatCount =0;
		if(droneRendering.getVisibility()== View.GONE){
			showPerspectiveView.setImageResource(R.drawable.on_plan_view);
			droneRendering.setVisibility(View.VISIBLE);
			scale =scaleAnimation(0f,1f,0f,1f,0.5f,0.5f);
			fade= fadeAnimation(0f,1f,repeatCount);
		}else{
			showPerspectiveView.setImageResource(R.drawable.off_plan_view);
			fade = fadeAnimation(1f,0f,repeatCount);
			scale = scaleAnimation(1f,0f,1f,0f,0.5f,0.5f);
			anim.setAnimationListener(new AnimationListener(){
				@Override
				public void onAnimationEnd(Animation arg0) {
					droneRendering.setVisibility(View.GONE);
				}
				@Override
				public void onAnimationRepeat(Animation arg0) {}
				@Override
				public void onAnimationStart(Animation arg0) {}
			});
		}
		scale.setDuration(SHOWING_ANIMATION);
		fade.setDuration(SHOWING_ANIMATION/(repeatCount+1));
		anim.addAnimation(fade);
		anim.addAnimation(scale);
		droneRendering.startAnimation(anim);
	}
	
	public void homePage(){
		Intent i = new Intent(this,MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}
	
	public void backPage(){
		onBackPressed();
	}
	
	public void programPage(){
		Intent i = new Intent(this,MainActivity.class);
		i.putExtra("programPage", true);
		startActivity(i);
	}
	
	public void playChrono(){
		playChrono.setEnabled(false);
		playChrono.setImageResource(R.drawable.play_disabled);
		chrono.setBase(SystemClock.elapsedRealtime()+timeWhenStopped);
		chrono.start();
		pauseChrono.setEnabled(true);
		pauseChrono.setImageResource(R.drawable.pause_enable);
		stopChrono.setEnabled(true);
		stopChrono.setImageResource(R.drawable.stop_enabled);
		System.out.println("Lancement du chrono");
	}
	
	public void pauseChrono(){
		timeWhenStopped = chrono.getBase()-SystemClock.elapsedRealtime();
		chrono.stop();
		playChrono.setEnabled(true);
		playChrono.setImageResource(R.drawable.play_enabled);
		stopChrono.setEnabled(true);
		stopChrono.setImageResource(R.drawable.stop_enabled);
		pauseChrono.setEnabled(false);
		pauseChrono.setImageResource(R.drawable.pause_disable);
	}
	
	public void stopChrono(){
		chrono.stop();
		timeWhenStopped=0;
		playChrono.setEnabled(true);
		playChrono.setImageResource(R.drawable.play_enabled);
		stopChrono.setEnabled(false);
		stopChrono.setImageResource(R.drawable.stop_disabled);
		pauseChrono.setEnabled(false);
		pauseChrono.setImageResource(R.drawable.pause_disable);
		chrono.setText("00:00");
	}
	
	public void backHome(){
		Intent secondeActivite = new Intent(this,
				MainActivity.class);
		startActivity(secondeActivite);
	}
	
	private boolean isPanelShown() {
        return informationL.getVisibility() == View.VISIBLE;
    }
	
	public void dragSettingMenu(){
		if(menuContent.getVisibility()==View.VISIBLE){
			slideToLeft(menuContent);
		}else{
			slideToRight(menuContent);
		}
	}
	//1,1.5
	public static Animation scaleAnimation(float startScaleX, float endScaleX,
			float startScaleY , float endScaleY, float centerX, float centerY){
		ScaleAnimation scale = new ScaleAnimation(startScaleX,endScaleX,
				startScaleY,endScaleY,
				Animation.RELATIVE_TO_SELF,centerX,
				Animation.RELATIVE_TO_SELF,centerY);
		scale.setFillAfter(false);
		return scale;
	}
	
	public static Animation fadeAnimation(float fromAlpha, float toAlpha , int repeatCount){
		Animation fade = new AlphaAnimation(fromAlpha, toAlpha);
		fade.setFillAfter(false);
		fade.setRepeatCount(repeatCount);
		fade.setRepeatMode(Animation.REVERSE);
		return fade;
	}
	
	public void slideToLeft(final View v){
		TranslateAnimation animate =  new TranslateAnimation(0,-v.getWidth(),0,0);
		animate.setDuration(ANIMATION_TIME);
		animate.setFillAfter(true);
		animate.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {
				v.setVisibility(View.GONE);
				if(v instanceof ViewGroup){
					ViewGroup view  =(ViewGroup) v; 
					for( int i=0; i<view.getChildCount();i++){
						view.getChildAt(i).setVisibility(View.GONE);
					}
				}
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationStart(Animation animation) {	}
		});
		v.startAnimation(animate);
		
	}
	public void slideToRight(final View v){
		TranslateAnimation animate =  new TranslateAnimation(-v.getWidth(),0,0,0);
		animate.setDuration(ANIMATION_TIME);
		animate.setFillAfter(true);
		animate.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationStart(Animation animation) {	
				v.setVisibility(View.VISIBLE);
				if(v instanceof ViewGroup){
					ViewGroup view  =(ViewGroup) v; 
					for( int i=0; i<view.getChildCount();i++){
						view.getChildAt(i).setVisibility(View.VISIBLE);
					}
				}
			}
		});
		v.startAnimation(animate);
	}
	
	public static void slideToBottom(final View v){
		TranslateAnimation animate =  new TranslateAnimation(0,0,0,v.getHeight());
		animate.setDuration(ANIMATION_TIME);
		animate.setFillAfter(true);
		animate.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {
				v.setVisibility(View.GONE);
				if(v instanceof ViewGroup){
					ViewGroup view  =(ViewGroup) v; 
					for( int i=0; i<view.getChildCount();i++){
						view.getChildAt(i).setVisibility(View.GONE);
					}
				}
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationStart(Animation animation) {	}
		});
		v.startAnimation(animate);
	}
	
	public static void slideToTop(final View v){
		TranslateAnimation animate =  new TranslateAnimation(0,0,v.getHeight(),0);
		animate.setDuration(ANIMATION_TIME);
		animate.setFillAfter(true);
		animate.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation arg0) {}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationStart(Animation animation) {
				v.setVisibility(View.VISIBLE);
				if(v instanceof ViewGroup){
					ViewGroup view  =(ViewGroup) v; 
					for( int i=0; i<view.getChildCount();i++){
						view.getChildAt(i).setVisibility(View.VISIBLE);
					}
				}
			}
		});
		v.startAnimation(animate);
	}
	
	class ButtonsListener implements View.OnClickListener{
		@Override
		public void onClick(final View v) {
			Animation anim;
			if(v ==startControl || v==emergencyButton || v==cameraButton){
				anim= scaleAnimation(1f,1.5f,1f,1.5f,0.5f,0.0f);
				anim.setRepeatCount(3);
				if(v==emergencyButton){
					emergencyAction();
				}else if(v==cameraButton){
					cameraAction();
				}else if(v==startControl){
					startConnection();
				}
				anim.setDuration(ANIMATION_TIME);
				v.startAnimation(anim);
				return;
			}else{
				anim = scaleAnimation(1f,1.5f,1f,1.5f,0.0f,1.0f);
			}
			anim.setDuration(ANIMATION_TIME);
			v.startAnimation(anim);
			anim.setAnimationListener(new AnimationListener(){
				@Override
				public void onAnimationEnd(Animation animation) {
					if(v == homeButton){
						homePage();
					}else if( v == backButton){
						backPage();
					} else if(v == programView){
						programPage();
					} else if(v == playChrono){
						playChrono();
					} else if(v== pauseChrono){
						pauseChrono();
					} else if(v== stopChrono){
						stopChrono();
					} else if(v== showSpeed){
						showSpeedometer();
					} else if(v== showPerspectiveView){
						showPlanView();
					} else if(v== helpControl){
						
					}
				}
				@Override
				public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationStart(Animation animation) {}
			});
		}	
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		Sensor mySensor = sensorEvent.sensor;
	    if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	    	//float x = sensorEvent.values[0];
	        //float y = sensorEvent.values[1];
	        //float z = sensorEvent.values[2];
	    	float x = -sensorEvent.values[1];
	    	float y = sensorEvent.values[0];
	    	float z = sensorEvent.values[2];
	        long curTime = System.currentTimeMillis();
	        
	        if ((curTime - lastUpdate) > 100) {
	            //long diffTime = (curTime - lastUpdate);
	            lastUpdate = curTime;
	            //float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
            	//System.out.println("X: "+x +" Y:  "+y+" Z: "+z);
            	if(Math.abs(y-last_y)<0.5){
            		return;
            	}
	            /*if (speed > SHAKE_THRESHOLD) {
	            	System.out.println("ON DEPASSE SEUIL: "+" X: "+x +" Y:  "+y+" Z: "+z);
	            }*/
            	if(pausingGyro)
            		return;
	            last_x = x;
	            last_y = y;
	            last_z = z;
	            mBluetooth.changeAccelConsigne(y);
	        }
	    }
	}
	
}