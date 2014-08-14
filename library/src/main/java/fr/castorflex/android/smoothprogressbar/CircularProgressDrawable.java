package fr.castorflex.android.smoothprogressbar;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import static fr.castorflex.android.smoothprogressbar.Utils.checkAngle;
import static fr.castorflex.android.smoothprogressbar.Utils.checkColors;
import static fr.castorflex.android.smoothprogressbar.Utils.checkNotNull;
import static fr.castorflex.android.smoothprogressbar.Utils.checkPositiveOrZero;
import static fr.castorflex.android.smoothprogressbar.Utils.checkSpeed;

public class CircularProgressDrawable extends Drawable
    implements Animatable {

  public enum Style {NORMAL, ROUNDED}

  private static final ArgbEvaluator COLOR_EVALUATOR         = new ArgbEvaluator();
  private static final Interpolator  ANGLE_INTERPOLATOR      = new LinearInterpolator();
  private static final Interpolator  SWEEP_INTERPOLATOR      = new DecelerateInterpolator();
  private static final int           ANGLE_ANIMATOR_DURATION = 2000;
  private static final int           SWEEP_ANIMATOR_DURATION = 600;
  private final        RectF         fBounds                 = new RectF();

  private ObjectAnimator mObjectAnimatorSweepAppearing;
  private ObjectAnimator mObjectAnimatorSweepDisappearing;
  private ObjectAnimator mObjectAnimatorAngle;
  private boolean        mModeAppearing;
  private Paint          mPaint;
  private float mCurrentGlobalAngleOffset = 0;
  private float mCurrentGlobalAngle       = 0;
  private float   mCurrentSweepAngle;
  private boolean mRunning;
  private int     mCurrentIndexColor;
  private int     mCurrentColor;

  //params
  private float mBorderWidth;
  private int[] mColors;
  private float mSpeed;
  private int   mMinSweepAngle;
  private int   mMaxSweepAngle;

  private CircularProgressDrawable(int[] colors,
                                   float borderWidth,
                                   float speed,
                                   int minSweepAngle,
                                   int maxSweepAngle,
                                   Style style) {
    mBorderWidth = borderWidth;
    mCurrentIndexColor = 0;
    mColors = colors;
    mCurrentColor = mColors[0];
    mSpeed = speed;
    mMinSweepAngle = minSweepAngle;
    mMaxSweepAngle = maxSweepAngle;

    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeWidth(borderWidth);
    mPaint.setStrokeCap(style == Style.ROUNDED ? Paint.Cap.ROUND : Paint.Cap.BUTT);
    mPaint.setColor(mColors[0]);

    setupAnimations();
  }

  @Override
  public void draw(Canvas canvas) {
    float startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset;
    float sweepAngle = mCurrentSweepAngle;
    if (!mModeAppearing) {
      startAngle = startAngle + (360 - sweepAngle);
//      sweepAngle = 360 - sweepAngle - mMinSweepAngle;
    } else {
//      sweepAngle += mMinSweepAngle;
    }
    startAngle %= 360;
    canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint);
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
    return PixelFormat.TRANSPARENT;
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    fBounds.left = bounds.left + mBorderWidth / 2f + .5f;
    fBounds.right = bounds.right - mBorderWidth / 2f - .5f;
    fBounds.top = bounds.top + mBorderWidth / 2f + .5f;
    fBounds.bottom = bounds.bottom - mBorderWidth / 2f - .5f;
  }

  private void setAppearing() {
    mModeAppearing = true;
    mCurrentGlobalAngleOffset += mMinSweepAngle;
  }

  private void setDisappearing() {
    mModeAppearing = false;
    mCurrentGlobalAngleOffset = mCurrentGlobalAngleOffset + (360 - mMaxSweepAngle);
  }

  //////////////////////////////////////////////////////////////////////////////
  ////////////////            Animation

  public static final Property<CircularProgressDrawable, Float> ROTATION_PROPERTY
      = new Property<CircularProgressDrawable, Float>(Float.class, "rotation") {
    @Override
    public Float get(CircularProgressDrawable object) {
      return object.getCurrentGlobalAngle();
    }

    @Override
    public void set(CircularProgressDrawable object, Float value) {
      object.setCurrentGlobalAngle(value);
    }
  };

  private Property<CircularProgressDrawable, Float> SWEEP_PROPERTY
      = new Property<CircularProgressDrawable, Float>(Float.class, "sweep") {
    @Override
    public Float get(CircularProgressDrawable object) {
      return object.getCurrentSweepAngle();
    }

    @Override
    public void set(CircularProgressDrawable object, Float value) {
      object.setCurrentSweepAngle(value);
    }
  };

  private void setupAnimations() {
    mObjectAnimatorAngle = ObjectAnimator.ofFloat(this, ROTATION_PROPERTY, 360f);
    mObjectAnimatorAngle.setInterpolator(ANGLE_INTERPOLATOR);
    mObjectAnimatorAngle.setDuration(ANGLE_ANIMATOR_DURATION);
    mObjectAnimatorAngle.setRepeatMode(ValueAnimator.RESTART);
    mObjectAnimatorAngle.setRepeatCount(ValueAnimator.INFINITE);

    mObjectAnimatorSweepAppearing = ObjectAnimator.ofFloat(this, SWEEP_PROPERTY, mMinSweepAngle, mMaxSweepAngle);
    mObjectAnimatorSweepAppearing.setInterpolator(SWEEP_INTERPOLATOR);
    mObjectAnimatorSweepAppearing.setDuration((long) (SWEEP_ANIMATOR_DURATION / mSpeed));
    mObjectAnimatorSweepAppearing.addListener(new Animator.AnimatorListener() {
      boolean cancelled = false;

      @Override
      public void onAnimationStart(Animator animation) {
        cancelled = false;
        mModeAppearing = true;
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        if (!cancelled) {
          setDisappearing();
          mObjectAnimatorSweepDisappearing.start();
        }
      }

      @Override
      public void onAnimationCancel(Animator animation) {
        cancelled = true;
      }

      @Override
      public void onAnimationRepeat(Animator animation) {
      }
    });

    mObjectAnimatorSweepDisappearing = ObjectAnimator.ofFloat(this, SWEEP_PROPERTY, mMaxSweepAngle, mMinSweepAngle);
    mObjectAnimatorSweepDisappearing.setInterpolator(SWEEP_INTERPOLATOR);
    mObjectAnimatorSweepDisappearing.setDuration((long) (SWEEP_ANIMATOR_DURATION / mSpeed));
    mObjectAnimatorSweepDisappearing.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        long duration = animation.getDuration();
        long played = animation.getCurrentPlayTime();
        float fraction = (float) played / duration;
        if (mColors.length > 1 && fraction > .7f) {
          int prevColor = mCurrentColor;
          int nextColor = mColors[(mCurrentIndexColor + 1) % mColors.length];
          mCurrentColor = (Integer) COLOR_EVALUATOR.evaluate((fraction - .7f) / (1 - .7f), prevColor, nextColor);
          mPaint.setColor(mCurrentColor);
        }
      }
    });
    mObjectAnimatorSweepDisappearing.addListener(new Animator.AnimatorListener() {
      boolean cancelled;

      @Override
      public void onAnimationStart(Animator animation) {
        cancelled = false;
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        if (!cancelled) {
          setAppearing();
          mCurrentIndexColor = (mCurrentIndexColor + 1) % mColors.length;
          mCurrentColor = mColors[mCurrentIndexColor];
          mPaint.setColor(mCurrentColor);
//          mCurrentGlobalAngle -= mMinSweepAngle;
          mObjectAnimatorSweepAppearing.start();
        }
      }

      @Override
      public void onAnimationCancel(Animator animation) {
        cancelled = true;
      }

      @Override
      public void onAnimationRepeat(Animator animation) {
      }
    });
  }

  @Override
  public void start() {
    if (isRunning()) {
      return;
    }
    mRunning = true;
    mObjectAnimatorAngle.start();
    mObjectAnimatorSweepAppearing.start();
    invalidateSelf();
  }

  @Override
  public void stop() {
    if (!isRunning()) {
      return;
    }
    mRunning = false;
    mObjectAnimatorAngle.cancel();
    mObjectAnimatorSweepAppearing.cancel();
    mObjectAnimatorSweepDisappearing.cancel();
    invalidateSelf();
  }

  @Override
  public boolean isRunning() {
    return mRunning;
  }

  public void setCurrentGlobalAngle(float currentGlobalAngle) {
    mCurrentGlobalAngle = currentGlobalAngle;
    invalidateSelf();
  }

  public float getCurrentGlobalAngle() {
    return mCurrentGlobalAngle;
  }

  public void setCurrentSweepAngle(float currentSweepAngle) {
    mCurrentSweepAngle = currentSweepAngle;
    invalidateSelf();
  }

  public float getCurrentSweepAngle() {
    return mCurrentSweepAngle;
  }

  public static class Builder {
    private int[] mColors;
    private float mSpeed;
    private float mStrokeWidth;
    private int   mMinSweepAngle;
    private int   mMaxSweepAngle;
    private Style mStyle;

    public Builder(Context context) {
      initValues(context);
    }

    private void initValues(Context context) {
      mStrokeWidth = context.getResources().getDimension(R.dimen.cpb_default_stroke_width);
      mSpeed = 1f;
      mColors = new int[]{context.getResources().getColor(R.color.cpb_default_color)};
      mMinSweepAngle = context.getResources().getInteger(R.integer.cpb_default_min_sweep_angle);
      mMaxSweepAngle = context.getResources().getInteger(R.integer.cpb_default_max_sweep_angle);
      mStyle = Style.ROUNDED;
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

    public Builder speed(float speed) {
      checkSpeed(speed);
      mSpeed = speed;
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
      mStrokeWidth = strokeWidth;
      return this;
    }

    public Builder style(Style style) {
      checkNotNull(style, "Style");
      mStyle = style;
      return this;
    }

    public CircularProgressDrawable build() {
      return new CircularProgressDrawable(mColors, mStrokeWidth, mSpeed, mMinSweepAngle, mMaxSweepAngle, mStyle);
    }
  }
}