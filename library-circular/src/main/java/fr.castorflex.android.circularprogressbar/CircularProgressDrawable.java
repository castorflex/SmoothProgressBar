package fr.castorflex.android.circularprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.PowerManager;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static fr.castorflex.android.circularprogressbar.Utils.checkAngle;
import static fr.castorflex.android.circularprogressbar.Utils.checkColors;
import static fr.castorflex.android.circularprogressbar.Utils.checkNotNull;
import static fr.castorflex.android.circularprogressbar.Utils.checkPositiveOrZero;
import static fr.castorflex.android.circularprogressbar.Utils.checkSpeed;

public class CircularProgressDrawable
    extends Drawable
    implements Animatable {

  public interface OnEndListener {
    void onEnd(CircularProgressDrawable drawable);
  }

  public static final int STYLE_NORMAL = 0;
  public static final int STYLE_ROUNDED = 1;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({STYLE_NORMAL, STYLE_ROUNDED})
  public @interface Style {

  }

  private final RectF mBounds = new RectF();

  private PowerManager mPowerManager;
  private Options mOptions;
  private Paint mPaint;
  private boolean mRunning;
  private PBDelegate mPBDelegate;

  /**
   * Private method, use #Builder instead
   */
  private CircularProgressDrawable(PowerManager powerManager, Options options) {
    mOptions = options;

    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeWidth(options.borderWidth);
    mPaint.setStrokeCap(options.style == STYLE_ROUNDED ? Paint.Cap.ROUND : Paint.Cap.BUTT);
    mPaint.setColor(options.colors[0]);
    mPowerManager = powerManager;

    initDelegate();
  }

  @Override
  public void draw(Canvas canvas) {
    if (isRunning()) mPBDelegate.draw(canvas, mPaint);
  }

  @Override
  public void setAlpha(int alpha) {
    mPaint.setAlpha(alpha);
  }

  @Override
  public void setColorFilter(ColorFilter cf) {
    mPaint.setColorFilter(cf);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    float border = mOptions.borderWidth;
    mBounds.left = bounds.left + border / 2f + .5f;
    mBounds.right = bounds.right - border / 2f - .5f;
    mBounds.top = bounds.top + border / 2f + .5f;
    mBounds.bottom = bounds.bottom - border / 2f - .5f;
  }


  @Override
  public void start() {
    initDelegate();
    mPBDelegate.start();
    mRunning = true;
    invalidateSelf();
  }

  /**
   * Inits the delegate. Create one if the delegate is null or not the right mode
   */
  private void initDelegate() {
    boolean powerSaveMode = Utils.isPowerSaveModeEnabled(mPowerManager);
    if (powerSaveMode) {
      if (mPBDelegate == null || !(mPBDelegate instanceof PowerSaveModeDelegate)) {
        if (mPBDelegate != null) mPBDelegate.stop();
        mPBDelegate = new PowerSaveModeDelegate(this);
      }
    } else {
      if (mPBDelegate == null || (mPBDelegate instanceof PowerSaveModeDelegate)) {
        if (mPBDelegate != null) mPBDelegate.stop();
        mPBDelegate = new DefaultDelegate(this, mOptions);
      }
    }
  }

  @Override
  public void stop() {
    mRunning = false;
    mPBDelegate.stop();
    invalidateSelf();
  }

  public void invalidate() {
    if (getCallback() == null) {
      stop(); // we don't want these animator to keep running...
    }
    invalidateSelf();
  }

  @Override
  public boolean isRunning() {
    return mRunning;
  }

  public Paint getCurrentPaint() {
    return mPaint;
  }

  public RectF getDrawableBounds() {
    return mBounds;
  }

  ////////////////////////////////////////////////////////////////////
  //Progressive stop
  ////////////////////////////////////////////////////////////////////

  public void progressiveStop(CircularProgressDrawable.OnEndListener listener) {
    mPBDelegate.progressiveStop(listener);
  }

  public void progressiveStop() {
    progressiveStop(null);
  }

  public static class Builder {
    private static final Interpolator DEFAULT_ROTATION_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator DEFAULT_SWEEP_INTERPOLATOR = new FastOutSlowInInterpolator();

    private Interpolator mSweepInterpolator = DEFAULT_SWEEP_INTERPOLATOR;
    private Interpolator mAngleInterpolator = DEFAULT_ROTATION_INTERPOLATOR;
    private float mBorderWidth;
    private int[] mColors;
    private float mSweepSpeed;
    private float mRotationSpeed;
    private int mMinSweepAngle;
    private int mMaxSweepAngle;
    @CircularProgressDrawable.Style int mStyle;
    private PowerManager mPowerManager;

    public Builder(@NonNull Context context) {
      this(context, false);
    }

    public Builder(@NonNull Context context, boolean editMode) {
      initValues(context, editMode);
    }

    private void initValues(@NonNull Context context, boolean editMode) {
      mBorderWidth = context.getResources().getDimension(R.dimen.cpb_default_stroke_width);
      mSweepSpeed = 1f;
      mRotationSpeed = 1f;
      if (editMode) {
        mColors = new int[]{Color.BLUE};
        mMinSweepAngle = 20;
        mMaxSweepAngle = 300;
      } else {
        mColors = new int[]{context.getResources().getColor(R.color.cpb_default_color)};
        mMinSweepAngle = context.getResources().getInteger(R.integer.cpb_default_min_sweep_angle);
        mMaxSweepAngle = context.getResources().getInteger(R.integer.cpb_default_max_sweep_angle);
      }
      mStyle = CircularProgressDrawable.STYLE_ROUNDED;
      mPowerManager = Utils.powerManager(context);
    }

    public Builder color(int color) {
      mColors = new int[]{color};
      return this;
    }

    public Builder colors(int[] colors) {
      checkColors(colors);
      mColors = colors;
      return this;
    }

    public Builder sweepSpeed(float sweepSpeed) {
      checkSpeed(sweepSpeed);
      mSweepSpeed = sweepSpeed;
      return this;
    }

    public Builder rotationSpeed(float rotationSpeed) {
      checkSpeed(rotationSpeed);
      mRotationSpeed = rotationSpeed;
      return this;
    }

    public Builder minSweepAngle(int minSweepAngle) {
      checkAngle(minSweepAngle);
      mMinSweepAngle = minSweepAngle;
      return this;
    }

    public Builder maxSweepAngle(int maxSweepAngle) {
      checkAngle(maxSweepAngle);
      mMaxSweepAngle = maxSweepAngle;
      return this;
    }

    public Builder strokeWidth(float strokeWidth) {
      checkPositiveOrZero(strokeWidth, "StrokeWidth");
      mBorderWidth = strokeWidth;
      return this;
    }

    public Builder style(@CircularProgressDrawable.Style int style) {
      mStyle = style;
      return this;
    }

    public Builder sweepInterpolator(Interpolator interpolator) {
      checkNotNull(interpolator, "Sweep interpolator");
      mSweepInterpolator = interpolator;
      return this;
    }

    public Builder angleInterpolator(Interpolator interpolator) {
      checkNotNull(interpolator, "Angle interpolator");
      mAngleInterpolator = interpolator;
      return this;
    }

    public CircularProgressDrawable build() {
      return new CircularProgressDrawable(
          mPowerManager,
          new Options(mAngleInterpolator,
              mSweepInterpolator,
              mBorderWidth,
              mColors,
              mSweepSpeed,
              mRotationSpeed,
              mMinSweepAngle,
              mMaxSweepAngle,
              mStyle));
    }
  }
}