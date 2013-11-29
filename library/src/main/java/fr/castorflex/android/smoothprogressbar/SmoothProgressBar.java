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


        int color = a.getColor(R.styleable.SmoothProgressBar_spb_color, res.getColor(R.color.spb_default_color));
        int sectionsCount = a.getInteger(R.styleable.SmoothProgressBar_spb_sections_count, res.getInteger(R.integer.spb_default_sections_count));
        int separatorLength = a.getDimensionPixelSize(R.styleable.SmoothProgressBar_spb_stroke_separator_length, res.getDimensionPixelSize(R.dimen.spb_default_stroke_separator_length));
        int width = a.getDimensionPixelSize(R.styleable.SmoothProgressBar_spb_stroke_width, res.getDimensionPixelSize(R.dimen.spb_default_stroke_width));
        String strSpeed = a.getString(R.styleable.SmoothProgressBar_spb_speed);
        int iInterpolator = a.getInteger(R.styleable.SmoothProgressBar_spb_interpolator, res.getInteger(R.integer.spb_default_interpolator));
        a.recycle();

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


        SmoothProgressDrawable.Builder builder = new SmoothProgressDrawable.Builder(context)
                .color(color)
                .interpolator(interpolator)
                .sectionsCount(sectionsCount)
                .separatorLength(separatorLength)
                .width(width);

        if (strSpeed != null) builder.speed(Float.parseFloat(strSpeed));

        setIndeterminateDrawable(builder.build());
    }
}
