package com.example.airpad_pir;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class JoystickCustomView extends View implements Runnable {

	public final static long DEFAULT_LOOP_INTERVAL = 100; // 100 ms
	private long loopInterval= DEFAULT_LOOP_INTERVAL;
	private OnJoystickMoveListener onJoystickMoveListener; // Listener
	private Thread thread = new Thread(this);
	
	private int currentEventId =-1;
	private final double RAD = 57.2957795;
	private float stick_scale;
	private int dim;//moving joystick
	private int stroke_start_color;
	private int stroke_middle_color;
	private int stroke_end_color;
	int envelopeRadius;
	
	private Paint envelopeView;
	private RadialGradient radGrad;
	private Drawable joystick = null;

	private int xPosition, yPosition;
	private double centerX , centerY;
	//private double lastAngle = 0;
	//private double lastPower = 0;

	public JoystickCustomView(Context context) {
		super(context);
	}

	public JoystickCustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(attrs);
	}

	public JoystickCustomView(Context context, AttributeSet attrs,
			int defaultStyle) {
		super(context, attrs, defaultStyle);
		initAttrs(attrs);
	}

	public void setJoystick(Drawable img) {
		joystick = img;
	}

	public void setCurrentEventId(int id){
		currentEventId= id;
	}
	
	public int getCurrentEventId(){
		return currentEventId;
	}
	
	@SuppressWarnings("deprecation")
	private void initAttrs(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.Joystick, 0, 0);
		stick_scale = a.getFloat(R.styleable.Joystick_stick_scale, 0.2f);
		stroke_start_color = a.getColor(R.styleable.Joystick_stroke_start_color,
				getResources().getColor(R.color.white));
		stroke_end_color = a.getColor(R.styleable.Joystick_stroke_end_color,
				getResources().getColor(R.color.white));
		stroke_middle_color = a.getColor(R.styleable.Joystick_stroke_middle_color,
				getResources().getColor(R.color.white));
		Drawable image = a.getDrawable(R.styleable.Joystick_joy_image);
		a.recycle();
		
		setJoystick(image);
		envelopeView = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
		super.onSizeChanged(xNew, yNew, xOld, yOld);
		xPosition = super.getWidth() / 2;
		yPosition = super.getHeight() / 2;
		int d = Math.min(xNew, yNew);
		dim = (int) (d * stick_scale/2.0);
		envelopeRadius = d/2;
		radGrad= new RadialGradient(xNew/2,yNew/2,envelopeRadius,new int[]{stroke_start_color,stroke_middle_color,
				stroke_end_color}, new float[]{0.8f,0.9f,1.0f},Shader.TileMode.CLAMP);

	}

	private int singleMeasure(int spec, int screenDim) {
		int mode = MeasureSpec.getMode(spec);
		int size = MeasureSpec.getSize(spec);
		if (mode == MeasureSpec.UNSPECIFIED)
			return 100;
		else
			return size;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		DisplayMetrics metrics = getContext().getResources()
				.getDisplayMetrics();
		int screenWidth = metrics.widthPixels;
		int screenHeight = metrics.heightPixels;

		int retourWidth = singleMeasure(widthMeasureSpec, screenWidth);
		int retourHeight = singleMeasure(heightMeasureSpec, screenHeight);
		int retour = Math.min(retourWidth, retourHeight);
		dim = (int) (retour * stick_scale/2.0);
		envelopeRadius = retour/2;
		setMeasuredDimension(retour, retour);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		centerX = getWidth() / 2;
		centerY = getHeight() / 2;
		envelopeView.setShader(radGrad);
		canvas.drawCircle((int) centerX, (int) centerY, envelopeRadius,envelopeView);
		if (joystick!=null){
			joystick.setBounds(xPosition- dim, yPosition-dim, xPosition+dim, yPosition+dim);
			joystick.draw(canvas);
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int pointerId = event.getPointerId(event.getActionIndex());
		if (event.getAction() == MotionEvent.ACTION_UP) {
			System.out.println(" "+ currentEventId + " is ACTION UP");
			xPosition = (int) centerX;
			yPosition = (int) centerY;
			thread.interrupt();
			if (onJoystickMoveListener != null)
				onJoystickMoveListener.onValueChanged(getAngle(), getPower());
			return true;
		}
		if(pointerId != currentEventId){
			return false;
		}
		int pointerIndex = event.findPointerIndex(currentEventId);
		xPosition = (int) event.getX(pointerIndex);
		yPosition = (int) event.getY(pointerIndex);
		double abs = Math.sqrt((xPosition - centerX) * (xPosition - centerX)
				+ (yPosition - centerY) * (yPosition - centerY));
		if (abs > (envelopeRadius-dim)) {
			xPosition = (int) ((xPosition - centerX) * (envelopeRadius-dim) / abs + centerX);
			yPosition = (int) ((yPosition - centerY) * (envelopeRadius-dim) / abs + centerY);
		}
		invalidate();
		if (onJoystickMoveListener != null
				&& (event.getAction() == MotionEvent.ACTION_DOWN)) {
			System.out.println(" "+ currentEventId + " is ACTION DOWN");
			if (thread != null && thread.isAlive()) {
				thread.interrupt();
			}
			thread = new Thread(this);
			thread.start();
			if (onJoystickMoveListener != null)
				onJoystickMoveListener.onValueChanged(getAngle(), getPower());
		}
		return true;
	}

	private double getAngle() {
		if (xPosition > centerX) {
			return (Math.atan((centerY-yPosition)/(xPosition - centerX))* RAD );
		} else if (xPosition < centerX) {
			if(yPosition>centerY)
				return -180+Math.atan((centerY-yPosition)/(xPosition-centerX))* RAD;
			return Math.atan((centerY-yPosition)/(xPosition-centerX))* RAD +180.0;
		} else {
			if (yPosition < centerY) {
				return 90;
			} else  if (yPosition >centerY){
				return -90;
			} else{
				return 0;
			}
		}
	}

	private double getPower() {
		return 100.0 * (Math.sqrt((xPosition - centerX)
				* (xPosition - centerX) + (yPosition - centerY)
				* (yPosition - centerY)) /(envelopeRadius-dim));
	}

	public void setOnJoystickMoveListener(OnJoystickMoveListener listener,
			long repeatInterval) {
		this.onJoystickMoveListener = listener;
		this.loopInterval = repeatInterval;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			post(new Runnable() {
				public void run() {
					if (onJoystickMoveListener != null)
						onJoystickMoveListener.onValueChanged(getAngle(),getPower());
				}
			});
			try {
				Thread.sleep(loopInterval);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public interface OnJoystickMoveListener {
		public void onValueChanged(double angle, double power);
	}

}
