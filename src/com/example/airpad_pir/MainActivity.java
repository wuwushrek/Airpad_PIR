package com.example.airpad_pir;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.appl.coverflow.CoverFlowCarousel;

public class MainActivity extends Activity {

	private ViewFlipper viewFlipper;
	private CoverFlowCarousel carousel;
	private final MyAdapter adapter = new MyAdapter();
	
	private GridView mProgramList;
	private ProgramViewAdapter programAdapter;
	private ImageButton helpButton;
	private ImageButton backButton; 
	private ImageButton homeButton;
	private ImageButton addProgramButton;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		mProgramList = (GridView ) findViewById(R.id.program_list);
		helpButton = (ImageButton) findViewById(R.id.help_program_button);
		backButton = (ImageButton) findViewById(R.id.back);
		homeButton = (ImageButton) findViewById(R.id.home_button);
		addProgramButton = (ImageButton) findViewById(R.id.add_program_button);
		
		carousel = (CoverFlowCarousel) findViewById(R.id.carousel);
		carousel.setAdapter(adapter);
		carousel.setSelection(adapter.getCount() / 2); // adapter.getCount()-1
		carousel.setSpacing(0.5f);
		
		List<MissionProgram> firstItem = new ArrayList<MissionProgram>();
		firstItem.add(new MissionProgram("Mission 1","AIRPAD RESCUE"));
		firstItem.add(new MissionProgram("Mission 2","AIRPAD RESCUE"));
		firstItem.add(new MissionProgram("Mission 3","AIRPAD RACE"));
		firstItem.add(new MissionProgram("Mission 4","AIRPAD INVADER"));
		firstItem.add(new MissionProgram("Mission 5","AIRPAD ARTIST"));
		firstItem.add(new MissionProgram("Mission 6","AIRPAD INVADER"));
		firstItem.add(new MissionProgram("Mission 7","AIRPAD RACE"));
		firstItem.add(new MissionProgram("Mission 8","AIRPAD ARTIST"));
		firstItem.add(new MissionProgram("Personal Program","AIRPAD MINE"));
		programAdapter = new ProgramViewAdapter(this,firstItem);
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
		boolean answer= getIntent().getBooleanExtra("programPage", false);
		if(answer){
			viewFlipper.showNext();
		}
	}
	
	public void goingBack(){
		viewFlipper.showPrevious();
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
					Toast.makeText(MainActivity.this,
							"clicked position:" + position, Toast.LENGTH_SHORT)
							.show();
					if (position % 5 == 0) {
						Intent secondeActivite = new Intent(MainActivity.this,
								JoystickActivity.class);
						startActivity(secondeActivite);
					}else if(position % 5 == 2){
						viewFlipper.showNext();
					}
				}
			});

			return v;
		}

		void addView() {
			mCount++;
			notifyDataSetChanged();
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
}
