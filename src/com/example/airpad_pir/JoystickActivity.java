package com.example.airpad_pir;

import com.example.airpad_pir.JoystickCustomView.OnJoystickMoveListener;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class JoystickActivity extends Activity{
	private TextView angleTextView;
	private TextView powerTextView;
	private TextView directionTextView;
	private RelativeLayout mainLayout;
	
	private JoystickCustomView joystick;
	private SpeedometerGauge speedometer;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_drone_layout);

		angleTextView = (TextView) findViewById(R.id.angleTextView);
		powerTextView = (TextView) findViewById(R.id.powerTextView);
		directionTextView = (TextView) findViewById(R.id.directionTextView);
		mainLayout =(RelativeLayout) findViewById(R.id.joystick_parent_view);
		// Referencing also other views
		joystick = (JoystickCustomView) findViewById(R.id.joystickView);
		speedometer= (SpeedometerGauge) findViewById(R.id.speedometer);

		// Event listener that always returns the variation of the angle in
		// degrees, motion power in percentage and direction of movement
		joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

			@Override
			public void onValueChanged(double angle, double power) {
				// TODO Auto-generated method stub
				angleTextView.setText("Angle: " + String.valueOf(angle) + "°");
				powerTextView.setText("Power: " + String.valueOf(power) + "%");
			}
		}, JoystickCustomView.DEFAULT_LOOP_INTERVAL);
		speedometer.setLabelConverter(new SpeedometerGauge.LabelConverter() {
			@Override
			public String getLabelFor(double progress, double maxProgress) {
				return String.valueOf((int) Math.round(progress));
			}
		});
		speedometer.setMaxSpeed(100);
		speedometer.setMajorTickStep(20);
		speedometer.setMinorTicks(2);
		speedometer.addColoredRange(20, 40, Color.GREEN);
		speedometer.addColoredRange(40, 70, Color.YELLOW);
		speedometer.addColoredRange(70, 100, Color.RED);
		speedometer.setSpeed(45);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int[] position= new int[2];
		mainLayout.getLocationOnScreen(position);
		float relativePosX = ev.getX()-position[0];
		float relativePosY =ev.getY()-position[1];
		int extraYpixel =getResources().getDimensionPixelSize(R.dimen.information_bar_height);
		if(relativePosX<mainLayout.getWidth()/2 && relativePosY<mainLayout.getHeight()-extraYpixel){
			if (ev.getAction() == MotionEvent.ACTION_UP) {
				joystick.setVisibility(View.GONE);
			}
			if (ev.getAction() == MotionEvent.ACTION_DOWN) {
				joystick.setTranslationX(adaptedCoordinate(relativePosX,mainLayout.getWidth()/2,joystick.getWidth()/2));
				joystick.setTranslationY(adaptedCoordinate(relativePosY,mainLayout.getHeight()
						-extraYpixel,joystick.getHeight()/2));
				joystick.setVisibility(View.VISIBLE);
			}
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private float adaptedCoordinate(float evX , int width , int viewMidWidth){
		if(evX-viewMidWidth>0 && evX+viewMidWidth<width){
			return evX-viewMidWidth;
		}
		if(evX-viewMidWidth<=0){
			return 0;
		}
		return width- 2*viewMidWidth;
	}
}