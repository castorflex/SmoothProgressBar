package fr.castorflex.android.smoothprogressbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

/**
 * Created by castorflex on 11/10/13.
 */
public class SmoothProgressBar extends ProgressBar {

    private static final int INTERPOLATOR_ACCELERATE = 0;
    private static final int INTERPOLATOR_LINEAR = 1;
    private static final int INTERPOLATOR_ACCELERATEDECELERATE = 2;
    private static final int INTERPOLATOR_DECELERATE = 3;

    public SmoothProgressBar(Context context) {
        this(context, null);
    }

    public SmoothProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.spbStyle);
    }

    public SmoothProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        Resources res = context.getResources();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SmoothProgressBar, defStyle, 0);


        final int color = a.getColor(R.styleable.SmoothProgressBar_spb_color, res.getColor(R.color.spb_default_color));
        final int sectionsCount = a.getInteger(R.styleable.SmoothProgressBar_spb_sections_count, res.getInteger(R.integer.spb_default_sections_count));
        final int separatorLength = a.getDimensionPixelSize(R.styleable.SmoothProgressBar_spb_stroke_separator_length, res.getDimensionPixelSize(R.dimen.spb_default_stroke_separator_length));
        final int width = a.getDimensionPixelSize(R.styleable.SmoothProgressBar_spb_stroke_width, res.getDimensionPixelSize(R.dimen.spb_default_stroke_width));
        final String strSpeed = a.getString(R.styleable.SmoothProgressBar_spb_speed);
        final int iInterpolator = a.getInteger(R.styleable.SmoothProgressBar_spb_interpolator, res.getInteger(R.integer.spb_default_interpolator));
        final boolean reversed = a.getBoolean(R.styleable.SmoothProgressBar_spb_reversed, res.getBoolean(R.bool.spb_default_reversed));
        final boolean mirrorMode = a.getBoolean(R.styleable.SmoothProgressBar_spb_mirror_mode, res.getBoolean(R.bool.spb_default_mirror_mode));
        final int colorsId = a.getResourceId(R.styleable.SmoothProgressBar_spb_colors, 0);
        a.recycle();

        //interpolator
        Interpolator interpolator;
        switch (iInterpolator) {
            case INTERPOLATOR_ACCELERATEDECELERATE:
                interpolator = new AccelerateDecelerateInterpolator();
                break;
            case INTERPOLATOR_DECELERATE:
                interpolator = new DecelerateInterpolator();
                break;
            case INTERPOLATOR_LINEAR:
                interpolator = new LinearInterpolator();
                break;
            case INTERPOLATOR_ACCELERATE:
            default:
                interpolator = new AccelerateInterpolator();
        }

        int[] colors = null;
        //colors
        if (colorsId != 0) {
            colors = res.getIntArray(colorsId);
        }

        SmoothProgressDrawable.Builder builder = new SmoothProgressDrawable.Builder(context)
                .interpolator(interpolator)
                .sectionsCount(sectionsCount)
                .separatorLength(separatorLength)
                .width(width)
                .reversed(reversed)
                .mirrorMode(mirrorMode);

        if (strSpeed != null) builder.speed(Float.parseFloat(strSpeed));
        if(colors != null && colors.length > 0)
            builder.colors(colors);
        else
            builder.color(color);

        setIndeterminateDrawable(builder.build());
    }
}
