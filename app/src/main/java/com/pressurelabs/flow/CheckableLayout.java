package com.pressurelabs.flow;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Flow_app
 *
 * @author Robert Simoes, 2016-08-28
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 */
public class CheckableLayout extends FrameLayout implements Checkable {
    private boolean mChecked;
    private ImageView image;

    public CheckableLayout(Context context) {
        super(context);
    }


    public CheckableLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public CheckableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    @SuppressWarnings("deprecation")
    public void setChecked(boolean checked) {
        mChecked = checked;
        setBackgroundDrawable(checked ? getResources().getDrawable(android.R.color.holo_blue_bright) : null);
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void toggle() {
        setChecked(!mChecked);
    }


//    private void init() {
//        inflate(getContext(), R.layout.checkable_layout, this);
//        this.image = (ImageView)findViewById(R.id.sandbox_gview_item_img);
//    }


}