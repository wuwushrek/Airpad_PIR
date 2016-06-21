package com.example.airpad_pir;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import com.appl.coverflow.CoverFlowCarousel;

public class MainActivity extends Activity{
	
	int position =0;
	private VideoView mSurfaceView = null;
	private final PlayVideo playing = new PlayVideo();
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM--yyyy 'A' HH:mm:ss");

	private final static int DIALOG_ADD = 1;

	private ViewFlipper viewFlipper;
	private CoverFlowCarousel carousel;
	private final MyAdapter adapter = new MyAdapter();

	private GridView mProgramList;
	private ProgramViewAdapter programAdapter;
	private ImageButton backButton;
	private ImageButton homeButton;
	private ImageButton addProgramButton;
	
	private ProgressBar mProgress;
	private ImageButton playButton;
	private ImageButton pauseButton;
	private ImageButton stopButton;
	private TextView  nomProgram , typeProgram , dateProgram;
	private ImageButton backProgramButton , controlManuelButton , cameraButton;
	private ImageView imageProgramType;
	private RelativeLayout playView;
	private RelativeLayout mainProgramPage;
	

	private final String[] typesProgram = { "AIRPAD RESCUE", "AIRPAD ARTIST",
			"AIRPAD INVADER", "AIRPAD RACE", "PERSONAL PROGRAM" };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mainProgramPage = (RelativeLayout) findViewById(R.id.main_program_page);
		playView = (RelativeLayout) findViewById(R.id.play_program_view);
		mSurfaceView = (VideoView) findViewById(R.id.surface);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		mProgramList = (GridView) findViewById(R.id.program_list);
		//helpButton = (ImageButton) findViewById(R.id.help_program_button);
		backButton = (ImageButton) findViewById(R.id.back);
		homeButton = (ImageButton) findViewById(R.id.home_button);
		addProgramButton = (ImageButton) findViewById(R.id.add_program_button);
		
		mProgress = (ProgressBar) findViewById(R.id.progress_bar_play);
		playButton = (ImageButton) findViewById(R.id.play_program_btn);
		pauseButton = (ImageButton) findViewById(R.id.pause_program_btn);
		stopButton = (ImageButton)  findViewById(R.id.stop_program_btn);
		nomProgram = (TextView) findViewById(R.id.nom_program_button);
		typeProgram = (TextView) findViewById(R.id.type_program_button);
		dateProgram = (TextView) findViewById(R.id.date_program_button);
		backProgramButton = (ImageButton) findViewById(R.id.back_program_btn);
		controlManuelButton = (ImageButton) findViewById(R.id.control_manuel);
		cameraButton = (ImageButton) findViewById(R.id.camera_play_btn);
		imageProgramType = (ImageView) findViewById(R.id.type_program);

		carousel = (CoverFlowCarousel) findViewById(R.id.carousel);
		carousel.setAdapter(adapter);
		carousel.setSelection(adapter.getCount() / 2); // adapter.getCount()-1
		carousel.setSpacing(0.5f);

		List<MissionProgram> firstItem = new ArrayList<MissionProgram>();
		firstItem.add(new MissionProgram("Mission 1", "AIRPAD RESCUE"));
		firstItem.add(new MissionProgram("Mission 2", "AIRPAD RESCUE"));
		firstItem.add(new MissionProgram("Mission 3", "AIRPAD RACE"));
		firstItem.add(new MissionProgram("Mission 4", "AIRPAD INVADER"));
		firstItem.add(new MissionProgram("Mission 5", "AIRPAD ARTIST"));
		firstItem.add(new MissionProgram("Mission 6", "AIRPAD INVADER"));
		firstItem.add(new MissionProgram("Mission 7", "AIRPAD RACE"));
		firstItem.add(new MissionProgram("Mission 8", "AIRPAD ARTIST"));
		firstItem.add(new MissionProgram("Personal Program", "AIRPAD MINE"));
		programAdapter = new ProgramViewAdapter(this, firstItem);
		mProgramList.setAdapter(programAdapter);

		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		homeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goingBack();
			}
		});
		addProgramButton.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_ADD);
			}
		});
		
		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startLoadingVideo();
			}
		});
		pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseVideo();
			}
		});
		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopLoadingVideo();
			}
		});
		
		backProgramButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				int repeatCount =0;
				stopLoadingVideo();
				Animation scale = JoystickActivity.scaleAnimation(1.0f,0.0f,1.0f,0.0f,0.5f,0.5f);
				Animation fade = JoystickActivity.fadeAnimation(1.0f,0.0f,repeatCount);
				scale.setDuration(1000);
				fade.setDuration(1000/(repeatCount+1));
				AnimationSet anim = new AnimationSet(true);
				anim.setFillAfter(false);
				anim.addAnimation(fade);
				anim.addAnimation(scale);
				anim.setAnimationListener(new AnimationListener(){
					@Override
					public void onAnimationEnd(Animation animation) {
						mSurfaceView.setVisibility(View.GONE);
						playView.setVisibility(View.GONE);
					}
					@Override
					public void onAnimationRepeat(Animation animation) {}
					@Override
					public void onAnimationStart(Animation animation) {}
					
				});
				mainProgramPage.setVisibility(View.VISIBLE);
				playView.setAnimation(anim);
				System.out.println("APPUIE SUR LE BUTTON BACK");
			}
		});
		
		controlManuelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopLoadingVideo();
				Intent secondeActivite = new Intent(MainActivity.this,
						JoystickActivity.class);
				startActivity(secondeActivite);
				//playView.setVisibility(View.GONE);
				//mainProgramPage.setVisibility(View.VISIBLE);
				//A rajouter des trucs
			}
		});
		cameraButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mSurfaceView.getVisibility()==View.GONE){
					mSurfaceView.setVisibility(View.VISIBLE);
					startLoadingVideo();
					cameraButton.setBackgroundResource(R.drawable.red_button_pressed);
				}else{
					mSurfaceView.setVisibility(View.GONE);
					stopLoadingVideo();
					cameraButton.setBackgroundResource(R.drawable.round_button_enabled);
				}
				System.out.println("DANS CAMERA BUTTON PRINT");
			}
		});
		
		boolean answer = getIntent().getBooleanExtra("programPage", false);
		if (answer) {
			viewFlipper.showNext();
		}
		try {
			mSurfaceView.setMediaController(null);
			mSurfaceView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.drone_video));
		} catch(Exception e){
			Log.e("ERRORRR", e.getMessage());
		}
		mSurfaceView.requestFocus();
		mSurfaceView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mSurfaceView.start();
			}
		});
		
		mProgress.setProgress(0);
		mProgress.setMax(mSurfaceView.getDuration());
	}

	public void goingBack() {
		viewFlipper.showPrevious();
	}

	public void stopLoadingVideo(){
		playing.stopRunning();
		if(mSurfaceView.getVisibility()==View.VISIBLE){
			mProgress.setProgress(0);
			mSurfaceView.seekTo(0);
			mSurfaceView.pause();
		}
		playButton.setEnabled(true);
		playButton.setScaleX(1.0f);
		playButton.setScaleY(1.0f);
		playButton.setImageResource(R.drawable.play_enabled);
		
		stopButton.setEnabled(false);
		stopButton.setScaleX(0.6f);
		stopButton.setScaleY(0.6f);
		stopButton.setImageResource(R.drawable.stop_disabled);
		
		pauseButton.setEnabled(false);
		pauseButton.setScaleX(0.6f);
		pauseButton.setScaleY(0.6f);
		pauseButton.setImageResource(R.drawable.pause_disable);
	}
	
	public void startLoadingVideo(){
		mSurfaceView.start();
		playing.launch();
		playButton.setImageResource(R.drawable.play_disabled);
		playButton.setScaleX(0.6f);
		playButton.setScaleY(0.6f);
		
		pauseButton.setScaleX(1.0f);
		pauseButton.setScaleY(1.0f);
		stopButton.setScaleX(1.0f);
		stopButton.setScaleY(1.0f);
		
		playButton.setEnabled(false);
		pauseButton.setEnabled(true);
		stopButton.setEnabled(true);
		pauseButton.setImageResource(R.drawable.pause_enable);
		stopButton.setImageResource(R.drawable.stop_enabled);
	}
	
	public void pauseVideo(){
		mSurfaceView.pause();
		playing.stopRunning();
		pauseButton.setImageResource(R.drawable.pause_disable);
		pauseButton.setEnabled(false);
		pauseButton.setScaleX(0.6f);
		pauseButton.setScaleY(0.6f);
		
		playButton.setEnabled(true);
		playButton.setScaleX(1.0f);
		playButton.setScaleY(1.0f);
		playButton.setImageResource(R.drawable.play_enabled);
	}
	
	public void startPlayingMission(MissionProgram m){
		int repeatCount = 0;
		nomProgram.setText(m.getNomMission());
		typeProgram.setText(m.getTypeMission());
		dateProgram.setText(sdf.format(Calendar.getInstance().getTime()));
		System.out.println("is NULL "+(imageProgramType== null));
		imageProgramType.setImageResource(programAdapter.getImageAssociateWith(m.getTypeMission()));
		Animation scale = JoystickActivity.scaleAnimation(0.0f,1.0f,0.0f,1.0f,0.5f,0.5f);
		Animation fade = JoystickActivity.fadeAnimation(0.0f,1.0f,repeatCount);
		scale.setDuration(1000);
		fade.setDuration(1000/(repeatCount+1));
		AnimationSet anim = new AnimationSet(true);
		anim.setFillAfter(false);
		anim.addAnimation(fade);
		anim.addAnimation(scale);
		anim.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationEnd(Animation animation) {
				mainProgramPage.setVisibility(View.GONE);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}
		});
		playView.setVisibility(View.VISIBLE);
		playView.startAnimation(anim);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialogDetails = null;
		switch (id) {
		case DIALOG_ADD: {
			LayoutInflater inflater = LayoutInflater.from(this);
			View dialogView = inflater
					.inflate(R.layout.dialog_add_layout, null);
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle("Creation programme");
			dialogBuilder.setIcon(R.drawable.add_program_icon);
			dialogBuilder.setView(dialogView);
			dialogDetails = dialogBuilder.create();
			break;
		}
		default: {}
		}
		return dialogDetails;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DIALOG_ADD: {
			final AlertDialog alertDialog = (AlertDialog) dialog;
			Button okButton = (Button) alertDialog
					.findViewById(R.id.btn_confirm);
			Button cancelButton = (Button) alertDialog
					.findViewById(R.id.btn_cancel);
			final EditText nomProgramme = (EditText) alertDialog
					.findViewById(R.id.nom_programme);
			nomProgramme.setFocusable(false);
			nomProgramme.setFocusableInTouchMode(true);
			final Spinner spinner = (Spinner) alertDialog
					.findViewById(R.id.type_liste);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item, typesProgram);
			spinner.setAdapter(adapter);
			spinner.setSelection(0);
			spinner.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					v.performClick();
					InputMethodManager imm = (InputMethodManager) getApplicationContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(nomProgramme.getWindowToken(),
							0);
					return false;
				}
			});
			okButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String nom = nomProgramme.getText().toString();
					int id = spinner.getSelectedItemPosition();
					MissionProgram mission = new MissionProgram(nom,
							typesProgram[id]);
					if (programAdapter.contains(mission)) {
						Toast.makeText(
								MainActivity.this,
								"Mission: "
										+ nom.toUpperCase()
										+ " est déjà existante, Utilisez un nom inexistant",
								Toast.LENGTH_LONG).show();
					} else {
						programAdapter.addMission(mission);
						Toast.makeText(MainActivity.this,
								"Création de la mission terminée",
								Toast.LENGTH_SHORT).show();
						alertDialog.dismiss();
					}
				}
			});
			cancelButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
				}
			});
			break;
		}
		default: {}
		}
	}

	private class MyAdapter extends BaseAdapter {
		private int[] mResourceImageIds = { R.drawable.drone_im,
				R.drawable.proffesseur_im, R.drawable.programming,
				R.drawable.settings_im, R.drawable.information };

		private int[] mResourceTextIds = { R.string.drone, R.string.tuto,
				R.string.programmation, R.string.regl, R.string.apropos };
		private int mCount = mResourceImageIds.length * 5;

		@Override
		public int getCount() {
			return mCount;
		}

		@Override
		public Object getItem(int position) {
			return mResourceImageIds[position % mResourceImageIds.length];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			MyFrame v;
			if (convertView == null) {
				v = new MyFrame(MainActivity.this);
			} else {
				v = (MyFrame) convertView;
			}

			v.setImageResAndText(mResourceImageIds[position
					% mResourceImageIds.length], mResourceTextIds[position
					% mResourceTextIds.length]);
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					/*Toast.makeText(MainActivity.this,
							"clicked position:" + position, Toast.LENGTH_SHORT)
							.show();*/
					if (position % 5 == 0) {
						Intent secondeActivite = new Intent(MainActivity.this,
								JoystickActivity.class);
						startActivity(secondeActivite);
					} else if (position % 5 == 2) {
						viewFlipper.showNext();
					}
				}
			});
			return v;
		}
	}

	public static class MyFrame extends LinearLayout {
		private ImageView mImageView;
		private TextView mTextView;

		public void setImageResAndText(int resId, int resText) {
			mImageView.setImageResource(resId);
			mTextView.setText(resText);
		}

		public MyFrame(Context context) {
			super(context);
			setOrientation(LinearLayout.VERTICAL);
			setGravity(Gravity.CENTER_VERTICAL);
			mImageView = new ImageView(context);
			mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
			mTextView = new TextView(context);
			mTextView.setGravity(Gravity.CENTER);
			mTextView.setTextSize(18);
			addView(mImageView);
			addView(mTextView);
			setBackgroundResource(R.drawable.button_accueil_selector);
			setClickable(true);
			setSelected(false);
		}

		@Override
		public void setSelected(boolean selected) {
			super.setSelected(selected);
			if (selected) {
				mImageView.setAlpha(1.0f);
				mTextView.setAlpha(1.0f);
				this.setAlpha(1.0f);
			} else {
				mImageView.setAlpha(0.5f);
				mTextView.setAlpha(0.5f);
				this.setAlpha(0.5f);
			}
		}
	}
	
	class PlayVideo implements Runnable {
		private volatile boolean isRunning = false;
		public void launch(){
			if(isRunning)
				return;
			isRunning = true;
			new Thread(this).start();
		}
		public void stopRunning(){
			isRunning= false;
		}
		@Override
		public void run(){
			mProgress.setMax(mSurfaceView.getDuration());
			while(isRunning){
				if(mSurfaceView.getDuration() != mProgress.getMax()){
					mProgress.setMax(mSurfaceView.getDuration());
				}
				System.out.println("Current position "+mSurfaceView.getCurrentPosition());
				mProgress.setProgress(mSurfaceView.getCurrentPosition());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
