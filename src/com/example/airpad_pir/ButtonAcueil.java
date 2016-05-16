package com.example.airpad_pir;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ButtonAcueil extends LinearLayout {
	private ImageView mImageView;
	private TextView mTextView;

	public ButtonAcueil(Context context) {
		super(context);
	}

	public ButtonAcueil(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.Options, 0, 0);
		String text = a.getString(R.styleable.Options_text);
		Drawable image = a.getDrawable(R.styleable.Options_icon);
		a.recycle();

		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_VERTICAL);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.button_accueil_layout, this, true);
		mTextView = (TextView) getChildAt(1);
		mImageView = (ImageView) getChildAt(0);

		mTextView.setText(text);
		if (image != null) {
			mImageView.setBackground(image);
		}
	}

}
