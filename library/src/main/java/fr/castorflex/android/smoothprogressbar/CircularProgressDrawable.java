package fr.castorflex.android.smoothprogressbar;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
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

  private static final ArgbEvaluator COLOR_EVALUATOR               = new ArgbEvaluator();
  private static final Interpolator  DEFAULT_ROTATION_INTERPOLATOR = new LinearInterpolator();
  private static final Interpolator  DEFAULT_SWEEP_INTERPOLATOR    = new DecelerateInterpolator();
  private static final int           ROTATION_ANIMATOR_DURATION    = 2000;
  private static final int           SWEEP_ANIMATOR_DURATION       = 600;
  private final        RectF         fBounds                       = new RectF();

  private ValueAnimator mSweepAppearingAnimator;
  private ValueAnimator mSweepDisappearingAnimator;
  private ValueAnimator mRotationAnimator;
  private boolean       mModeAppearing;
  private Paint         mPaint;
  private boolean       mRunning;
  private int           mCurrentColor;
  private int           mCurrentIndexColor;
  private float         mCurrentSweepAngle;
  private float mCurrentRotationAngleOffset = 0;
  private float mCurrentRotationAngle       = 0;

  //params
  private Interpolator mAngleInterpolator;
  private Interpolator mSweepInterpolator;
  private float        mBorderWidth;
  private int[]        mColors;
  private float        mSweepSpeed;
  private float        mRotationSpeed;
  private int          mMinSweepAngle;
  private int          mMaxSweepAngle;

  private CircularProgressDrawable(int[] colors,
                                   float borderWidth,
                                   float sweepSpeed,
                                   float rotationSpeed,
                                   int minSweepAngle,
                                   int maxSweepAngle,
                                   Style style,
                                   Interpolator angleInterpolator,
                                   Interpolator sweepInterpolator) {
    mSweepInterpolator = sweepInterpolator;
    mAngleInterpolator = angleInterpolator;
    mBorderWidth = borderWidth;
    mCurrentIndexColor = 0;
    mColors = colors;
    mCurrentColor = mColors[0];
    mSweepSpeed = sweepSpeed;
    mRotationSpeed = rotationSpeed;
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
    float startAngle = mCurrentRotationAngle - mCurrentRotationAngleOffset;
    float sweepAngle = mCurrentSweepAngle;
    if (!mModeAppearing) {
      startAngle = startAngle + (360 - sweepAngle);
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
    return PixelFormat.TRANSLUCENT;
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
    mCurrentRotationAngleOffset += mMinSweepAngle;
  }

  private void setDisappearing() {
    mModeAppearing = false;
    mCurrentRotationAngleOffset = mCurrentRotationAngleOffset + (360 - mMaxSweepAngle);
  }

  //////////////////////////////////////////////////////////////////////////////
  ////////////////            Animation

  private void setupAnimations() {
    mRotationAnimator = ValueAnimator.ofFloat(0f, 360f);
    mRotationAnimator.setInterpolator(mAngleInterpolator);
    mRotationAnimator.setDuration((long) (ROTATION_ANIMATOR_DURATION / mRotationSpeed));
    mRotationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float angle = animation.getAnimatedFraction() * 360f;
        setCurrentRotationAngle(angle);
      }
    });
    mRotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
    mRotationAnimator.setRepeatMode(ValueAnimator.RESTART);

    mSweepAppearingAnimator = ValueAnimator.ofFloat(mMinSweepAngle, mMaxSweepAngle);
    mSweepAppearingAnimator.setInterpolator(mSweepInterpolator);
    mSweepAppearingAnimator.setDuration((long) (SWEEP_ANIMATOR_DURATION / mSweepSpeed));
    mSweepAppearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float animatedFraction = animation.getAnimatedFraction();
        setCurrentSweepAngle(mMinSweepAngle + animatedFraction * (mMaxSweepAngle - mMinSweepAngle));
      }
    });
    mSweepAppearingAnimator.addListener(new Animator.AnimatorListener() {
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
          mSweepDisappearingAnimator.start();
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

    mSweepDisappearingAnimator = ValueAnimator.ofFloat(mMaxSweepAngle, mMinSweepAngle);
    mSweepDisappearingAnimator.setInterpolator(mSweepInterpolator);
    mSweepDisappearingAnimator.setDuration((long) (SWEEP_ANIMATOR_DURATION / mSweepSpeed));
    mSweepDisappearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float animatedFraction = animation.getAnimatedFraction();
        setCurrentSweepAngle(mMaxSweepAngle - animatedFraction * (mMaxSweepAngle - mMinSweepAngle));

        long duration = animation.getDuration();
        long played = animation.getCurrentPlayTime();
        float fraction = (float) played / duration;
        if (mColors.length > 1 && fraction > .7f) { //because
          int prevColor = mCurrentColor;
          int nextColor = mColors[(mCurrentIndexColor + 1) % mColors.length];
          mCurrentColor = (Integer) COLOR_EVALUATOR.evaluate((fraction - .7f) / (1 - .7f), prevColor, nextColor);
          mPaint.setColor(mCurrentColor);
        }
      }
    });
    mSweepDisappearingAnimator.addListener(new Animator.AnimatorListener() {
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
          mSweepAppearingAnimator.start();
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
    mRotationAnimator.start();
    mSweepAppearingAnimator.start();
    invalidateSelf();
  }

  @Override
  public void stop() {
    if (!isRunning()) {
      return;
    }
    mRunning = false;
    mRotationAnimator.cancel();
    mSweepAppearingAnimator.cancel();
    mSweepDisappearingAnimator.cancel();
    invalidateSelf();
  }

  @Override
  public boolean isRunning() {
    return mRunning;
  }

  public void setCurrentRotationAngle(float currentRotationAngle) {
    mCurrentRotationAngle = currentRotationAngle;
    invalidateSelf();
  }

  public void setCurrentSweepAngle(float currentSweepAngle) {
    mCurrentSweepAngle = currentSweepAngle;
    invalidateSelf();
  }

  public static class Builder {
    private int[] mColors;
    private float mSweepSpeed;
    private float mRotationSpeed;
    private float mStrokeWidth;
    private int   mMinSweepAngle;
    private int   mMaxSweepAngle;
    private Style mStyle;
    private Interpolator mSweepInterpolator = DEFAULT_SWEEP_INTERPOLATOR;
    private Interpolator mAngleInterpolator = DEFAULT_ROTATION_INTERPOLATOR;

    public Builder(Context context) {
      initValues(context);
    }

    private void initValues(Context context) {
      mStrokeWidth = context.getResources().getDimension(R.dimen.cpb_default_stroke_width);
      mSweepSpeed = 1f;
      mRotationSpeed = 1f;
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
      mStrokeWidth = strokeWidth;
      return this;
    }

    public Builder style(Style style) {
      checkNotNull(style, "Style");
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
      return new CircularProgressDrawable(mColors,
          mStrokeWidth,
          mSweepSpeed,
          mRotationSpeed,
          mMinSweepAngle,
          mMaxSweepAngle,
          mStyle,
          mAngleInterpolator,
          mSweepInterpolator);
    }
  }
}