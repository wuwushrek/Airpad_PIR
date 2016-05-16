package com.example.airpad_pir;
/*
import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	private ButtonAcueil tutoAccueil,programmation,drone,reglage,apropos;
	private RelativeLayout rotate_area;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		rotate_area= (RelativeLayout) findViewById(R.id.rotating_area);
		tutoAccueil = (ButtonAcueil) findViewById(R.id.drone_tuto);
		programmation = (ButtonAcueil) findViewById(R.id.drone_programmation);
		drone = (ButtonAcueil) findViewById(R.id.drone_menu);
		reglage=(ButtonAcueil) findViewById(R.id.drone_reglage);
		apropos=(ButtonAcueil) findViewById(R.id.drone_propos);
		
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.test2);
		Animation anim2 =AnimationUtils.loadAnimation(this, R.anim.counter_clock);
		rotate_area.startAnimation(anim);
		tutoAccueil.startAnimation(anim2);
		programmation.startAnimation(anim2);
		drone.startAnimation(anim2);
		//Animation animation = AnimationUtils.loadAnimation(this, R.anim.test);
		//programmation.startAnimation(animation);
		//Animation anim2 =AnimationUtils.loadAnimation(this, R.anim.test2);
		//tutoAccueil.startAnimation(anim2);
	}
}*/
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appl.coverflow.CoverFlowCarousel;



public class MainActivity extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoverFlowCarousel carousel = (CoverFlowCarousel)findViewById(R.id.carousel);
        final MyAdapter adapter = new MyAdapter();
        carousel.setAdapter(adapter);
        carousel.setSelection(adapter.getCount()/2); //adapter.getCount()-1
        //carousel.setSlowDownCoefficient(1);
        carousel.setSpacing(0.5f);

    }


    private class MyAdapter extends BaseAdapter {
        private int[] mResourceImageIds = {R.drawable.drone_im, R.drawable.proffesseur_im, R.drawable.programming, 
        		R.drawable.settings_im,  R.drawable.information};

        private int[] mResourceTextIds = {R.string.drone, R.string.tuto, R.string.programmation, R.string.regl,
            R.string.apropos};
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            MyFrame v;
            if (convertView == null) {
                v = new MyFrame(MainActivity.this);
            } else {
                v = (MyFrame)convertView;
            }

            v.setImageResAndText(mResourceImageIds[position % mResourceImageIds.length],
            		mResourceTextIds[position % mResourceTextIds.length]);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "clicked position:"+position,Toast.LENGTH_SHORT).show();
                    if(position%5==0){
                    	Intent secondeActivite = new Intent(MainActivity.this,JoystickActivity.class);
                    	startActivity(secondeActivite);
                    }
                }
            });


            return v;
        }

        public void addView(){
            mCount++;
            notifyDataSetChanged();
        }
    }

    public static class MyFrame extends LinearLayout{
        private ImageView mImageView;
        private TextView mTextView;

        public void setImageResAndText(int resId,int resText){
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
            //setBackgroundColor(Color.WHITE);
            setSelected(false);
        }

        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            if(selected) {
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
