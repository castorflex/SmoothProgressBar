package fr.castorflex.android.circularprogressbar;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.support.annotation.NonNull;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import static fr.castorflex.android.circularprogressbar.Utils.getAnimatedFraction;

class DefaultDelegate implements PBDelegate {

  private static final ArgbEvaluator COLOR_EVALUATOR = new ArgbEvaluator();
  private static final Interpolator END_INTERPOLATOR = new LinearInterpolator();
  private static final long ROTATION_ANIMATOR_DURATION = 2000;
  private static final long SWEEP_ANIMATOR_DURATION = 600;
  private static final long END_ANIMATOR_DURATION = 200;

  private ValueAnimator mSweepAppearingAnimator;
  private ValueAnimator mSweepDisappearingAnimator;
  private ValueAnimator mRotationAnimator;
  private ValueAnimator mEndAnimator;
  private boolean mModeAppearing;

  private int mCurrentColor;
  private int mCurrentIndexColor;
  private float mCurrentSweepAngle;
  private float mCurrentRotationAngleOffset = 0;
  private float mCurrentRotationAngle = 0;
  private float mCurrentEndRatio = 1f;
  private boolean mFirstSweepAnimation;

  //params
  private Interpolator mAngleInterpolator;
  private Interpolator mSweepInterpolator;
  private int[] mColors;
  private float mSweepSpeed;
  private float mRotationSpeed;
  private int mMinSweepAngle;
  private int mMaxSweepAngle;
  private boolean mUseGradient;

  private CircularProgressDrawable mParent;
  private CircularProgressDrawable.OnEndListener mOnEndListener;

  public DefaultDelegate(@NonNull CircularProgressDrawable parent,
                         @NonNull Options options) {
    mParent = parent;
    mSweepInterpolator = options.sweepInterpolator;
    mAngleInterpolator = options.angleInterpolator;
    mCurrentIndexColor = 0;
    mColors = options.colors;
    mCurrentColor = mColors[0];
    mSweepSpeed = options.sweepSpeed;
    mRotationSpeed = options.rotationSpeed;
    mMinSweepAngle = options.minSweepAngle;
    mMaxSweepAngle = options.maxSweepAngle;
    mUseGradient = options.useGradient;

    setupAnimations();
  }

  private void reinitValues() {
    mFirstSweepAnimation = true;
    mCurrentEndRatio = 1f;
    mParent.getCurrentPaint().setColor(mCurrentColor);
  }

  @Override
  public void draw(Canvas canvas, Paint paint) {
    float startAngle = mCurrentRotationAngle - mCurrentRotationAngleOffset;
    float sweepAngle = mCurrentSweepAngle;
    if (!mModeAppearing) {
      startAngle = startAngle + (360 - sweepAngle);
    }
    startAngle %= 360;
    if (mCurrentEndRatio < 1f) {
      float newSweepAngle = sweepAngle * mCurrentEndRatio;
      startAngle = (startAngle + (sweepAngle - newSweepAngle)) % 360;
      sweepAngle = newSweepAngle;
    }
    if(mUseGradient)
    {
      canvas.rotate(startAngle, mParent.getDrawableBounds().centerX(),
              mParent.getDrawableBounds().centerY());
      int leadingColor;
      if(mColors.length > 1)
        leadingColor = mColors[1];
      else
        leadingColor = Color.BLACK;
      int[] gradientColors = {mColors[0], leadingColor};
      float[] positions = {0, sweepAngle / 360f};
      SweepGradient gradient = new SweepGradient(mParent.getDrawableBounds().centerX(),
              mParent.getDrawableBounds().centerY(), gradientColors, positions);
      paint.setShader(gradient);
      paint.setStrokeCap(Paint.Cap.ROUND);
      canvas.drawArc(mParent.getDrawableBounds(), 6, sweepAngle, false, paint);
    }
    else {
      canvas.drawArc(mParent.getDrawableBounds(), startAngle, sweepAngle, false, paint);
    }
  }

  @Override
  public void start() {
    mEndAnimator.cancel();
    reinitValues();
    mRotationAnimator.start();
    mSweepAppearingAnimator.start();
  }

  @Override
  public void stop() {
    stopAnimators();
  }

  private void stopAnimators() {
    mRotationAnimator.cancel();
    mSweepAppearingAnimator.cancel();
    mSweepDisappearingAnimator.cancel();
    mEndAnimator.cancel();
  }

  private void setAppearing() {
    mModeAppearing = true;
    mCurrentRotationAngleOffset += mMinSweepAngle;
  }

  private void setDisappearing() {
    mModeAppearing = false;
    mCurrentRotationAngleOffset = mCurrentRotationAngleOffset + (360 - mMaxSweepAngle);
  }

  public void setCurrentRotationAngle(float currentRotationAngle) {
    mCurrentRotationAngle = currentRotationAngle;
    mParent.invalidate();
  }

  public void setCurrentSweepAngle(float currentSweepAngle) {
    mCurrentSweepAngle = currentSweepAngle;
    mParent.invalidate();
  }

  private void setEndRatio(float ratio) {
    mCurrentEndRatio = ratio;
    mParent.invalidate();
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
        float angle = getAnimatedFraction(animation) * 360f;
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
        float animatedFraction = getAnimatedFraction(animation);
        float angle;
        if (mFirstSweepAnimation) {
          angle = animatedFraction * mMaxSweepAngle;
        } else {
          angle = mMinSweepAngle + animatedFraction * (mMaxSweepAngle - mMinSweepAngle);
        }
        setCurrentSweepAngle(angle);
      }
    });
    mSweepAppearingAnimator.addListener(new SimpleAnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        mModeAppearing = true;
      }

      @Override
      protected void onPreAnimationEnd(Animator animation) {
        if (isStartedAndNotCancelled()) {
          mFirstSweepAnimation = false;
          setDisappearing();
          mSweepDisappearingAnimator.start();
        }
      }
    });

    mSweepDisappearingAnimator = ValueAnimator.ofFloat(mMaxSweepAngle, mMinSweepAngle);
    mSweepDisappearingAnimator.setInterpolator(mSweepInterpolator);
    mSweepDisappearingAnimator.setDuration((long) (SWEEP_ANIMATOR_DURATION / mSweepSpeed));
    mSweepDisappearingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float animatedFraction = getAnimatedFraction(animation);
        setCurrentSweepAngle(mMaxSweepAngle - animatedFraction * (mMaxSweepAngle - mMinSweepAngle));

        long duration = animation.getDuration();
        long played = animation.getCurrentPlayTime();
        float fraction = (float) played / duration;
        if (mColors.length > 1 && fraction > .7f) { //because
          int prevColor = mCurrentColor;
          int nextColor = mColors[(mCurrentIndexColor + 1) % mColors.length];
          int newColor = (Integer) COLOR_EVALUATOR.evaluate((fraction - .7f) / (1 - .7f), prevColor, nextColor);
          mParent.getCurrentPaint().setColor(newColor);
        }
      }
    });
    mSweepDisappearingAnimator.addListener(new SimpleAnimatorListener() {
      @Override
      protected void onPreAnimationEnd(Animator animation) {
        if (isStartedAndNotCancelled()) {
          setAppearing();
          mCurrentIndexColor = (mCurrentIndexColor + 1) % mColors.length;
          mCurrentColor = mColors[mCurrentIndexColor];
          mParent.getCurrentPaint().setColor(mCurrentColor);
          mSweepAppearingAnimator.start();
        }
      }
    });
    mEndAnimator = ValueAnimator.ofFloat(1f, 0f);
    mEndAnimator.setInterpolator(END_INTERPOLATOR);
    mEndAnimator.setDuration(END_ANIMATOR_DURATION);
    mEndAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        setEndRatio(1f - getAnimatedFraction(animation));

      }
    });
  }

  /////////////////////////////////////////////////////////
  /// Stop
  /////////////////////////////////////////////////////////

  @Override
  public void progressiveStop(CircularProgressDrawable.OnEndListener listener) {
    if (!mParent.isRunning() || mEndAnimator.isRunning()) {
      return;
    }
    mOnEndListener = listener;
    mEndAnimator.addListener(new SimpleAnimatorListener() {

      @Override
      public void onPreAnimationEnd(Animator animation) {
        mEndAnimator.removeListener(this);
        CircularProgressDrawable.OnEndListener endListener = mOnEndListener;
        mOnEndListener = null;

        if(isStartedAndNotCancelled()) {
          setEndRatio(0f);
          mParent.stop();
          if (endListener != null) {
            endListener.onEnd(mParent);
          }
        }
      }
    });
    mEndAnimator.start();
  }
}
