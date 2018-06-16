
package com.infi.lyrical.views.Gallery;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.infi.lyrical.util.AndroidUtils;

/**
 * LinearLayout which applies blur effect into its background
 */
public class BlurRelativeLayout extends RelativeLayout {

    // Blur renderer instance
    private BlurRenderer mBlurRenderer;
    private boolean shouldRender;

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
        invalidate();
    }

    /**
     * Default constructor
     */
    public BlurRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    /**
     * Default constructor
     */
    public BlurRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * Initialize layout to handle background blur effect
     */
    private void init(AttributeSet attrs) {
        mBlurRenderer = new BlurRenderer(this);
        int maxBlur= AndroidUtils.displaySize.x/8;
        mBlurRenderer.setBlurRadius(maxBlur/8);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mBlurRenderer.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBlurRenderer.onDetachedFromWindow();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(!shouldRender){
            super.dispatchDraw(canvas);
            return;
        }
        // If this is off-screen pass apply blur only
        if (mBlurRenderer.isOffscreenCanvas(canvas)) {
            mBlurRenderer.applyBlur();
        }
        // Otherwise draw blurred background image and continue to child views
        else {
            mBlurRenderer.drawToCanvas(canvas);
            super.dispatchDraw(canvas);
        }
    }

    /**
     * Set blur radius in pixels
     */
    public void setBlurRadius(int radius) {
        mBlurRenderer.setBlurRadius(radius);
        invalidate();
    }

}
