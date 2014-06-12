package fr.castorflex.android.smoothprogressbar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by castorflex on 11/10/13.
 */
public class SmoothProgressDrawable extends Drawable implements Animatable {

  public interface Callbacks {
    public void onStop();

    public void onStart();
  }

  private static final long FRAME_DURATION = 1000 / 60;
  private final static float OFFSET_PER_FRAME = 0.01f;

  private final Rect fBackgroundRect = new Rect();
  private Callbacks mCallbacks;
  private Interpolator mInterpolator;
  private Rect mBounds;
  private Paint mPaint;
  private int[] mColors;
  private int mColorsIndex;
  private boolean mRunning;
  private float mCurrentOffset;
  private float mFinishingOffset;
  private int mSeparatorLength;
  private int mSectionsCount;
  private float mSpeed;
  private float mProgressiveStartSpeed;
  private float mProgressiveStopSpeed;
  private boolean mReversed;
  private boolean mNewTurn;
  private boolean mMirrorMode;
  private float mMaxOffset;
  private boolean mFinishing;
  private boolean mProgressiveStartActivated;
  private int mStartSection;
  private int mCurrentSections;
  private float mStrokeWidth;
  private Drawable mBackgroundDrawable;
  private boolean mUseGradients;
  private int[] mLinearGradientColors;
  private float[] mLinearGradientPositions;


  private SmoothProgressDrawable(Interpolator interpolator,
                                 int sectionsCount,
                                 int separatorLength,
                                 int[] colors,
                                 float strokeWidth,
                                 float speed,
                                 float progressiveStartSpeed,
                                 float progressiveStopSpeed,
                                 boolean reversed,
                                 boolean mirrorMode,
                                 Callbacks callbacks,
                                 boolean progressiveStartActivated,
                                 Drawable backgroundDrawable,
                                 boolean useGradients) {
    mRunning = false;
    mInterpolator = interpolator;
    mSectionsCount = sectionsCount;
    mStartSection = 0;
    mCurrentSections = mSectionsCount;
    mSeparatorLength = separatorLength;
    mSpeed = speed;
    mProgressiveStartSpeed = progressiveStartSpeed;
    mProgressiveStopSpeed = progressiveStopSpeed;
    mReversed = reversed;
    mColors = colors;
    mColorsIndex = 0;
    mMirrorMode = mirrorMode;
    mFinishing = false;
    mBackgroundDrawable = backgroundDrawable;
    mStrokeWidth = strokeWidth;

    mMaxOffset = 1f / mSectionsCount;

    mPaint = new Paint();
    mPaint.setStrokeWidth(strokeWidth);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setDither(false);
    mPaint.setAntiAlias(false);

    mProgressiveStartActivated = progressiveStartActivated;
    mCallbacks = callbacks;

    mUseGradients = useGradients;
    refreshLinearGradientOptions();
  }

  ////////////////////////////////////////////////////////////////////////////
  ///////////////////         SETTERS
  public void setInterpolator(Interpolator interpolator) {
    if (interpolator == null) throw new IllegalArgumentException("Interpolator cannot be null");
    mInterpolator = interpolator;
    invalidateSelf();
  }

  public void setColors(int[] colors) {
    if (colors == null || colors.length == 0)
      throw new IllegalArgumentException("Colors cannot be null or empty");
    mColorsIndex = 0;
    mColors = colors;
    refreshLinearGradientOptions();
    invalidateSelf();
  }

  public void setColor(int color) {
    setColors(new int[]{color});
  }

  public void setSpeed(float speed) {
    if (speed < 0) throw new IllegalArgumentException("Speed must be >= 0");
    mSpeed = speed;
    invalidateSelf();
  }

  public void setProgressiveStartSpeed(float speed) {
    if (speed < 0) throw new IllegalArgumentException("SpeedProgressiveStart must be >= 0");
    mProgressiveStartSpeed = speed;
    invalidateSelf();
  }

  public void setProgressiveStopSpeed(float speed) {
    if (speed < 0) throw new IllegalArgumentException("SpeedProgressiveStop must be >= 0");
    mProgressiveStopSpeed = speed;
    invalidateSelf();
  }

  public void setSectionsCount(int sectionsCount) {
    if (sectionsCount <= 0) throw new IllegalArgumentException("SectionsCount must be > 0");
    mSectionsCount = sectionsCount;
    mMaxOffset = 1f / mSectionsCount;
    mCurrentOffset %= mMaxOffset;
    refreshLinearGradientOptions();
    invalidateSelf();
  }

  public void setSeparatorLength(int separatorLength) {
    if (separatorLength < 0)
      throw new IllegalArgumentException("SeparatorLength must be >= 0");
    mSeparatorLength = separatorLength;
    invalidateSelf();
  }

  public void setStrokeWidth(float strokeWidth) {
    if (strokeWidth < 0) throw new IllegalArgumentException("The strokeWidth must be >= 0");
    mPaint.setStrokeWidth(strokeWidth);
    invalidateSelf();
  }

  public void setReversed(boolean reversed) {
    if (mReversed == reversed) return;
    mReversed = reversed;
    invalidateSelf();
  }

  public void setMirrorMode(boolean mirrorMode) {
    if (mMirrorMode == mirrorMode) return;
    mMirrorMode = mirrorMode;
    invalidateSelf();
  }

  public void setBackgroundDrawable(Drawable backgroundDrawable) {
    if (mBackgroundDrawable == backgroundDrawable) return;
    mBackgroundDrawable = backgroundDrawable;
    invalidateSelf();
  }

  public Drawable getBackgroundDrawable() {
    return mBackgroundDrawable;
  }

  public int[] getColors() {
    return mColors;
  }

  public float getStrokeWidth() {
    return mStrokeWidth;
  }

  public void setProgressiveStartActivated(boolean progressiveStartActivated) {
    mProgressiveStartActivated = progressiveStartActivated;
  }

  public void setUseGradients(boolean useGradients) {
    if (mUseGradients == useGradients) return;

    mUseGradients = useGradients;
    refreshLinearGradientOptions();
    invalidateSelf();
  }

  protected void refreshLinearGradientOptions() {
    if (mUseGradients) {
      mLinearGradientColors = new int[mSectionsCount + 2];
      mLinearGradientPositions = new float[mSectionsCount + 2];
    } else {
      mPaint.setShader(null);
      mLinearGradientColors = null;
      mLinearGradientPositions = null;
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  ///////////////////         DRAW

  @Override
  public void draw(Canvas canvas) {
    mBounds = getBounds();
    canvas.clipRect(mBounds);

    //new turn
    if (mNewTurn) {
      mColorsIndex = decrementColor(mColorsIndex);
      mNewTurn = false;

      if (isFinishing()) {
        mStartSection++;

        if (mStartSection > mSectionsCount) {
          stop();
          return;
        }
      }
      if (mCurrentSections < mSectionsCount) {
        mCurrentSections++;
      }
    }

    if (mUseGradients)
      drawGradient(canvas);

    drawStrokes(canvas);
  }

  private void drawGradient(Canvas canvas) {
    float xSectionWidth = 1f / mSectionsCount;
    int currentIndexColor = mColorsIndex;

    mLinearGradientPositions[0] = 0f;
    mLinearGradientPositions[mLinearGradientPositions.length - 1] = 1f;
    int firstColorIndex = currentIndexColor - 1;
    if (firstColorIndex < 0) firstColorIndex += mColors.length;

    mLinearGradientColors[0] = mColors[firstColorIndex];

    for (int i = 0; i < mSectionsCount; ++i) {

      float position = mInterpolator.getInterpolation(i * xSectionWidth + mCurrentOffset);
      mLinearGradientPositions[i + 1] = position;
      mLinearGradientColors[i + 1] = mColors[currentIndexColor];

      currentIndexColor = (currentIndexColor + 1) % mColors.length;
    }
    mLinearGradientColors[mLinearGradientColors.length - 1] = mColors[currentIndexColor];

    float left = mReversed ? (mMirrorMode ? Math.abs(mBounds.left - mBounds.right) / 2 : mBounds.left) : mBounds.left;
    float right = mMirrorMode ? (mReversed ? mBounds.left : Math.abs(mBounds.left - mBounds.right) / 2) :
        mBounds.right;
    float top = mBounds.centerY() - mStrokeWidth / 2;
    float bottom = mBounds.centerY() + mStrokeWidth / 2;
    LinearGradient linearGradient = new LinearGradient(left, top, right, bottom,
        mLinearGradientColors, mLinearGradientPositions,
        mMirrorMode ? Shader.TileMode.MIRROR : Shader.TileMode.CLAMP);

    mPaint.setShader(linearGradient);
  }

  private void drawStrokes(Canvas canvas) {
    if (mReversed) {
      canvas.translate(mBounds.width(), 0);
      canvas.scale(-1, 1);
    }

    float prevValue = 0f;
    int boundsWidth = mBounds.width();
    if (mMirrorMode) boundsWidth /= 2;
    int width = boundsWidth + mSeparatorLength + mSectionsCount;
    int centerY = mBounds.centerY();
    float xSectionWidth = 1f / mSectionsCount;

    float startX;
    float endX;
    float firstX = 0;
    float lastX = 0;
    float prev;
    float end;
    float spaceLength;
    float xOffset;
    float ratioSectionWidth;
    float sectionWidth;
    float drawLength;
    int currentIndexColor = mColorsIndex;

    if (mStartSection == mCurrentSections && mCurrentSections == mSectionsCount) {
      firstX = canvas.getWidth();
    }

    for (int i = 0; i <= mCurrentSections; ++i) {
      xOffset = xSectionWidth * i + mCurrentOffset;
      prev = Math.max(0f, xOffset - xSectionWidth);
      ratioSectionWidth = Math.abs(mInterpolator.getInterpolation(prev) -
          mInterpolator.getInterpolation(Math.min(xOffset, 1f)));
      sectionWidth = (int) (width * ratioSectionWidth);

      if (sectionWidth + prev < width)
        spaceLength = Math.min(sectionWidth, mSeparatorLength);
      else
        spaceLength = 0f;

      drawLength = sectionWidth > spaceLength ? sectionWidth - spaceLength : 0;
      end = prevValue + drawLength;
      if (end > prevValue && i >= mStartSection) {
        float xFinishingOffset = mInterpolator.getInterpolation(Math.min(mFinishingOffset, 1f));
        startX = Math.max(xFinishingOffset * width, Math.min(boundsWidth, prevValue));
        endX = Math.min(boundsWidth, end);
        drawLine(canvas, boundsWidth, startX, centerY, endX, centerY, currentIndexColor);
        if (i == mStartSection) { // first loop
          firstX = startX - mSeparatorLength;
        }
      }
      if (i == mCurrentSections) {
        lastX = prevValue + sectionWidth; //because we want to keep the separator effect
      }

      prevValue = end + spaceLength;
      currentIndexColor = incrementColor(currentIndexColor);
    }

    drawBackgroundIfNeeded(canvas, firstX, lastX);
  }

  private void drawLine(Canvas canvas, int canvasWidth, float startX, float startY, float stopX, float stopY, int currentIndexColor) {
    mPaint.setColor(mColors[currentIndexColor]);

    if (!mMirrorMode) {
      canvas.drawLine(startX, startY, stopX, stopY, mPaint);
    } else {
      if (mReversed) {
        canvas.drawLine(canvasWidth + startX, startY, canvasWidth + stopX, stopY, mPaint);
        canvas.drawLine(canvasWidth - startX, startY, canvasWidth - stopX, stopY, mPaint);
      } else {
        canvas.drawLine(startX, startY, stopX, stopY, mPaint);
        canvas.drawLine(canvasWidth * 2 - startX, startY, canvasWidth * 2 - stopX, stopY, mPaint);
      }
    }
  }

  private void drawBackgroundIfNeeded(Canvas canvas, float firstX, float lastX) {
    if (mBackgroundDrawable == null) return;

    fBackgroundRect.top = (int) ((canvas.getHeight() - mStrokeWidth) / 2);
    fBackgroundRect.bottom = (int) ((canvas.getHeight() + mStrokeWidth) / 2);

    fBackgroundRect.left = 0;
    fBackgroundRect.right = mMirrorMode ? canvas.getWidth() / 2 : canvas.getWidth();
    mBackgroundDrawable.setBounds(fBackgroundRect);

    //draw the background if the animation is over
    if (!isRunning()) {
      if (mMirrorMode) {
        canvas.save();
        canvas.translate(canvas.getWidth() / 2, 0);
        drawBackground(canvas, 0, fBackgroundRect.width());
        canvas.scale(-1, 1);
        drawBackground(canvas, 0, fBackgroundRect.width());
        canvas.restore();
      } else {
        drawBackground(canvas, 0, fBackgroundRect.width());
      }
      return;
    }

    if (!isFinishing() && !isStarting()) return;

    if (firstX > lastX) {
      float temp = firstX;
      firstX = lastX;
      lastX = temp;
    }

    if (firstX > 0) {
      if (mMirrorMode) {
        canvas.save();
        canvas.translate(canvas.getWidth() / 2, 0);
        if (mReversed) {
          drawBackground(canvas, 0, firstX);
          canvas.scale(-1, 1);
          drawBackground(canvas, 0, firstX);
        } else {
          drawBackground(canvas, canvas.getWidth() / 2 - firstX, canvas.getWidth() / 2);
          canvas.scale(-1, 1);
          drawBackground(canvas, canvas.getWidth() / 2 - firstX, canvas.getWidth() / 2);
        }
        canvas.restore();
      } else {
        drawBackground(canvas, 0, firstX);
      }
    }
    if (lastX <= canvas.getWidth()) {
      if (mMirrorMode) {
        canvas.save();
        canvas.translate(canvas.getWidth() / 2, 0);
        if (mReversed) {
          drawBackground(canvas, lastX, canvas.getWidth() / 2);
          canvas.scale(-1, 1);
          drawBackground(canvas, lastX, canvas.getWidth() / 2);
        } else {
          drawBackground(canvas, 0, canvas.getWidth() / 2 - lastX);
          canvas.scale(-1, 1);
          drawBackground(canvas, 0, canvas.getWidth() / 2 - lastX);
        }
        canvas.restore();
      } else {
        drawBackground(canvas, lastX, canvas.getWidth());
      }
    }
  }

  private void drawBackground(Canvas canvas, float fromX, float toX) {
    int count = canvas.save();
    canvas.clipRect(fromX, (int) ((canvas.getHeight() - mStrokeWidth) / 2),
        toX, (int) ((canvas.getHeight() + mStrokeWidth) / 2));
    mBackgroundDrawable.draw(canvas);
    canvas.restoreToCount(count);
  }

  private int incrementColor(int colorIndex) {
    ++colorIndex;
    if (colorIndex >= mColors.length) colorIndex = 0;
    return colorIndex;
  }

  private int decrementColor(int colorIndex) {
    --colorIndex;
    if (colorIndex < 0) colorIndex = mColors.length - 1;
    return colorIndex;
  }

  /**
   * Start the animation with the first color.
   * Calls progressiveStart(0)
   */
  public void progressiveStart() {
    progressiveStart(0);
  }

  /**
   * Start the animation from a given color.
   *
   * @param index
   */
  public void progressiveStart(int index) {
    resetProgressiveStart(index);
    start();
  }

  private void resetProgressiveStart(int index) {
    checkColorIndex(index);

    mCurrentOffset = 0;
    mFinishing = false;
    mFinishingOffset = 0f;
    mStartSection = 0;
    mCurrentSections = 0;
    mColorsIndex = index;
  }

  /**
   * Finish the animation by animating the remaining sections.
   */
  public void progressiveStop() {
    mFinishing = true;
    mStartSection = 0;
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

  ///////////////////////////////////////////////////////////////////////////
  ///////////////////         Animation: based on http://cyrilmottier.com/2012/11/27/actionbar-on-the-move/
  @Override
  public void start() {
    if (mProgressiveStartActivated) {
      resetProgressiveStart(0);
    }
    if (isRunning()) return;
    if (mCallbacks != null) {
      mCallbacks.onStart();
    }
    scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
    invalidateSelf();
  }

  @Override
  public void stop() {
    if (!isRunning()) return;
    if (mCallbacks != null) {
      mCallbacks.onStop();
    }
    mRunning = false;
    unscheduleSelf(mUpdater);
  }

  @Override
  public void scheduleSelf(Runnable what, long when) {
    mRunning = true;
    super.scheduleSelf(what, when);
  }

  @Override
  public boolean isRunning() {
    return mRunning;
  }

  public boolean isStarting() {
    return mCurrentSections < mSectionsCount;
  }

  public boolean isFinishing() {
    return mFinishing;
  }

  private final Runnable mUpdater = new Runnable() {

    @Override
    public void run() {
      if (isFinishing()) {
        mFinishingOffset += (OFFSET_PER_FRAME * mProgressiveStopSpeed);
        mCurrentOffset += (OFFSET_PER_FRAME * mProgressiveStopSpeed);
        if (mFinishingOffset >= 1f) {
          stop();
        }
      } else if (isStarting()) {
        mCurrentOffset += (OFFSET_PER_FRAME * mProgressiveStartSpeed);
      } else {
        mCurrentOffset += (OFFSET_PER_FRAME * mSpeed);
      }

      if (mCurrentOffset >= mMaxOffset) {
        mNewTurn = true;
        mCurrentOffset -= mMaxOffset;
      }

      if (isRunning())
        scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);

      invalidateSelf();
    }
  };

  ////////////////////////////////////////////////////////////////////////////
  ///////////////////     Listener

  public void setCallbacks(Callbacks callbacks) {
    mCallbacks = callbacks;
  }

  ////////////////////////////////////////////////////////////////////////////
  ///////////////////     Checks

  private void checkColorIndex(int index) {
    if (index < 0 || index >= mColors.length) {
      throw new IllegalArgumentException(String.format("Index %d not valid", index));
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  ///////////////////         BUILDER

  /**
   * Builder for SmoothProgressDrawable! You must use it!
   */
  public static class Builder {
    private Interpolator mInterpolator;
    private int mSectionsCount;
    private int[] mColors;
    private float mSpeed;
    private float mProgressiveStartSpeed;
    private float mProgressiveStopSpeed;
    private boolean mReversed;
    private boolean mMirrorMode;
    private float mStrokeWidth;
    private int mStrokeSeparatorLength;
    private boolean mProgressiveStartActivated;
    private boolean mGenerateBackgroundUsingColors;
    private boolean mGradients;
    private Drawable mBackgroundDrawableWhenHidden;

    private Callbacks mOnProgressiveStopEndedListener;

    public Builder(Context context) {
      initValues(context);
    }

    public SmoothProgressDrawable build() {
      if (mGenerateBackgroundUsingColors) {
        mBackgroundDrawableWhenHidden = SmoothProgressBarUtils.generateDrawableWithColors(mColors, mStrokeWidth);
      }
      SmoothProgressDrawable ret = new SmoothProgressDrawable(
          mInterpolator,
          mSectionsCount,
          mStrokeSeparatorLength,
          mColors,
          mStrokeWidth,
          mSpeed,
          mProgressiveStartSpeed,
          mProgressiveStopSpeed,
          mReversed,
          mMirrorMode,
          mOnProgressiveStopEndedListener,
          mProgressiveStartActivated,
          mBackgroundDrawableWhenHidden,
          mGradients);
      return ret;
    }

    private void initValues(Context context) {
      Resources res = context.getResources();
      mInterpolator = new AccelerateInterpolator();
      mSectionsCount = res.getInteger(R.integer.spb_default_sections_count);
      mColors = new int[]{res.getColor(R.color.spb_default_color)};
      mSpeed = Float.parseFloat(res.getString(R.string.spb_default_speed));
      mProgressiveStartSpeed = mSpeed;
      mProgressiveStopSpeed = mSpeed;
      mReversed = res.getBoolean(R.bool.spb_default_reversed);
      mStrokeSeparatorLength = res.getDimensionPixelSize(R.dimen.spb_default_stroke_separator_length);
      mStrokeWidth = res.getDimensionPixelOffset(R.dimen.spb_default_stroke_width);
      mProgressiveStartActivated = res.getBoolean(R.bool.spb_default_progressiveStart_activated);
      mGradients = false;
    }

    public Builder interpolator(Interpolator interpolator) {
      if (interpolator == null)
        throw new IllegalArgumentException("Interpolator can't be null");
      mInterpolator = interpolator;
      return this;
    }

    public Builder sectionsCount(int sectionsCount) {
      if (sectionsCount <= 0) throw new IllegalArgumentException("SectionsCount must be > 0");
      mSectionsCount = sectionsCount;
      return this;
    }

    public Builder separatorLength(int separatorLength) {
      if (separatorLength < 0)
        throw new IllegalArgumentException("SeparatorLength must be >= 0");
      mStrokeSeparatorLength = separatorLength;
      return this;
    }

    public Builder color(int color) {
      mColors = new int[]{color};
      return this;
    }

    public Builder colors(int[] colors) {
      if (colors == null || colors.length == 0)
        throw new IllegalArgumentException("Your color array must not be empty");
      mColors = colors;
      return this;
    }

    public Builder strokeWidth(float width) {
      if (width < 0) throw new IllegalArgumentException("The width must be >= 0");
      mStrokeWidth = width;
      return this;
    }

    public Builder speed(float speed) {
      if (speed < 0) throw new IllegalArgumentException("Speed must be >= 0");
      mSpeed = speed;
      return this;
    }

    public Builder progressiveStartSpeed(float progressiveStartSpeed) {
      if (progressiveStartSpeed < 0)
        throw new IllegalArgumentException("progressiveStartSpeed must be >= 0");
      mProgressiveStartSpeed = progressiveStartSpeed;
      return this;
    }

    public Builder progressiveStopSpeed(float progressiveStopSpeed) {
      if (progressiveStopSpeed < 0)
        throw new IllegalArgumentException("progressiveStopSpeed must be >= 0");
      mProgressiveStopSpeed = progressiveStopSpeed;
      return this;
    }

    public Builder reversed(boolean reversed) {
      mReversed = reversed;
      return this;
    }

    public Builder mirrorMode(boolean mirrorMode) {
      mMirrorMode = mirrorMode;
      return this;
    }

    public Builder progressiveStart(boolean progressiveStartActivated) {
      mProgressiveStartActivated = progressiveStartActivated;
      return this;
    }

    public Builder callbacks(Callbacks onProgressiveStopEndedListener) {
      mOnProgressiveStopEndedListener = onProgressiveStopEndedListener;
      return this;
    }

    public Builder backgroundDrawable(Drawable backgroundDrawableWhenHidden) {
      mBackgroundDrawableWhenHidden = backgroundDrawableWhenHidden;
      return this;
    }

    public Builder generateBackgroundUsingColors() {
      mGenerateBackgroundUsingColors = true;
      return this;
    }

    public Builder gradients() {
      return gradients(true);
    }

    public Builder gradients(boolean useGradients) {
      mGradients = useGradients;
      return this;
    }
  }
}
