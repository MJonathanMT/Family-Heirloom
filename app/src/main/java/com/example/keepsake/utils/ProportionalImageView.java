package com.example.keepsake.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ProportionalImageView extends ImageView {

    /**
     * Allows to set a ratio on Image View component by automatically scales its height according
     * to its computed width keeping its original aspect ratio intact.
     * @param context handler in the system, it provides access to resources, databases, preferences, etc.
     */
    public ProportionalImageView(Context context) {
        super(context);
    }

    /**
     * Allows to set a ratio on Image View component by automatically scales its height according
     * to its computed width keeping its original aspect ratio intact.
     * @param context handler in the system, it provides access to resources, databases, preferences, etc.
     * @param attrs collection of attributes, as found associated with a tag in an XML document
     */
    public ProportionalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Allows to set a ratio on Image View component by automatically scales its height according
     * to its computed width keeping its original aspect ratio intact.
     * @param context handler in the system, it provides access to resources, databases, preferences, etc.
     * @param attrs collection of attributes, as found associated with a tag in an XML document
     * @param defStyle default style to apply to this view
     */
    public ProportionalImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Allows to set a ratio on Image View component by automatically scales its height according
     * to its computed width keeping its original aspect ratio intact.
     * @param widthMeasureSpec width of the image
     * @param heightMeasureSpec height of the image
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            int w = MeasureSpec.getSize(widthMeasureSpec);
            int h = w * ((Drawable) d).getIntrinsicHeight() / d.getIntrinsicWidth();
            setMeasuredDimension(w, h);
        }
        else super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}