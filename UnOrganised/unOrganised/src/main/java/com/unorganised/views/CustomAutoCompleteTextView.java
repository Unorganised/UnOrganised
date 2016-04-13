package com.unorganised.views;

import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.unorganised.R;


/** Customizing AutoCompleteTextView to return Place Description   
 *  corresponding to the selected item
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {
	Drawable searchBtn;
	Drawable clear ;
	public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// The image we defined for the search button
		 searchBtn = getResources().getDrawable(
				R.drawable.icon_search);
		// The image we defined for the clear button
		clear = getResources().getDrawable(
				android.R.drawable.ic_menu_close_clear_cancel);
		this.setCompoundDrawablesWithIntrinsicBounds(searchBtn, null,
				clear, null);
		// if the clear button is pressed, fire up the handler. Otherwise do nothing
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				CustomAutoCompleteTextView et = CustomAutoCompleteTextView.this;

				if (et.getCompoundDrawables()[2] == null)
					return false;

				if (event.getAction() != MotionEvent.ACTION_UP)
					return false;

				if (event.getX() > et.getWidth() - et.getPaddingRight() - clear.getIntrinsicWidth()) {
					et.setText("");
				}
				return false;
			}
		});


	}

	/** Returns the Place Description corresponding to the selected item */
	@Override
	protected CharSequence convertSelectionToString(Object selectedItem) {
		/** Each item in the autocompetetextview suggestion list is a hashmap object */
		HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
		return hm.get("description");
	}
	@Override
	public void setCompoundDrawables(Drawable left, Drawable top,
			Drawable right, Drawable bottom) {
		if (left != null) {
			searchBtn = left;
        }
        if (right != null) {
            clear = right;
        }

		super.setCompoundDrawables(left, top, right, bottom);
	}
	
}
