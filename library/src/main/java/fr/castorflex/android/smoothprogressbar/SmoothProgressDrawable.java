package fr.castorflex.android.smoothprogressbar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by castorflex on 11/10/13.
 */
public class SmoothProgressDrawable extends Drawable implements Animatable {

    private static final long FRAME_DURATION = 1000 / 60;
    private final static float OFFSET_PER_FRAME = 0.01f;

	private static final int DEFAULT_START_COLOR = 0x00000000; // transparent

    /**
     * Transitioning to the indeterminate state.
     */
    private static final int STATE_BEGIN = -1;
    /**
     * The usual indeterminate state.
     */
    private static final int STATE_SMOOTH = 0;
    /**
     * Handles the transition from begin to finish, without going through {@link #STATE_SMOOTH}
     */
    private static final int STATE_HALFFINISH = 2;
    /**
     * Transition out of the indeterminate state.
     */
    private static final int STATE_FINISH = 1;

    private Interpolator mInterpolator;
    private Rect mBounds;
    private Paint mPaint;
    private int[] mColors;
    private int mColorsIndex;
    private boolean mRunning;
    private float mCurrentOffset;
    private int mSeparatorLength;
    private int mSectionsCount, mStartSection, mCurrentSections;
    private float mSpeed;
    private boolean mReversed;
    private boolean mNewTurn;
    private boolean mMirrorMode;
    private float mMaxOffset;
    private int mState;
    private int mStartColor;

    private SmoothProgressDrawable(Interpolator interpolator, int sectionsCount, int separatorLength, int[] colors, float strokeWidth, float speed, boolean reversed, boolean mirrorMode) {
        mRunning = false;
        mInterpolator = interpolator;
        mSectionsCount = sectionsCount;
        mStartSection = 0;
        mCurrentSections = 0;
        mSeparatorLength = separatorLength;
        mSpeed = speed;
        mReversed = reversed;
        mColors = colors;
        mColorsIndex = 0;
        mMirrorMode = mirrorMode;
        mState = STATE_BEGIN;

        mMaxOffset = 1f / mSectionsCount;

        mPaint = new Paint();
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(false);
        mPaint.setAntiAlias(false);
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
        invalidateSelf();
    }

    public void setColor(int color) {
        setColors(new int[]{color});
    }

    public void setSpeed(float speed){
        if (speed < 0) throw new IllegalArgumentException("Speed must be >= 0");
        mSpeed = speed;
        invalidateSelf();
    }

    public void setSectionsCount(int sectionsCount){
        if (sectionsCount <= 0) throw new IllegalArgumentException("SectionsCount must be > 0");
        mSectionsCount = sectionsCount;
        mMaxOffset = 1f / mSectionsCount;
        mCurrentOffset %= mMaxOffset;
        invalidateSelf();
    }

    public void setSeparatorLength(int separatorLength){
        if (separatorLength < 0)
            throw new IllegalArgumentException("SeparatorLength must be >= 0");
        mSeparatorLength = separatorLength;
        invalidateSelf();
    }

    public void setStrokeWidth(float strokeWidth){
        if (strokeWidth < 0) throw new IllegalArgumentException("The strokeWidth must be >= 0");
        mPaint.setStrokeWidth(strokeWidth);
        invalidateSelf();
    }

    public void setReversed(boolean reversed){
        if(mReversed == reversed) return;
        mReversed = reversed;
        invalidateSelf();
    }

    public void setMirrorMode(boolean mirrorMode){
        if(mMirrorMode == mirrorMode) return;
        mMirrorMode = mirrorMode;
        invalidateSelf();
    }

    ////////////////////////////////////////////////////////////////////////////
    ///////////////////         DRAW

    @Override
    public void draw(Canvas canvas) {
        mBounds = getBounds();
        canvas.clipRect(mBounds);

        int boundsWidth = mBounds.width();

        if (mReversed) {
            canvas.translate(boundsWidth, 0);
            canvas.scale(-1, 1);
        }

        drawStrokes(canvas);
    }

    private void drawStrokes(Canvas canvas) {
        float prevValue = 0f;
        int boundsWidth = mBounds.width();
        if (mMirrorMode) boundsWidth /= 2;
        int width = boundsWidth + mSeparatorLength + mSectionsCount;
        int centerY = mBounds.centerY();
        float xSectionWidth = 1f / mSectionsCount;

        //new turn
        if (mNewTurn) {
            mColorsIndex = decrementColor(mColorsIndex);
            mNewTurn = false;

            if (mState == STATE_FINISH || mState == STATE_HALFFINISH) {

                mStartSection++;

                if (mStartSection > mSectionsCount + 1) {
                    stop();
                    return;
                }
            } else if (mState == STATE_BEGIN) {
                if (mCurrentSections == mSectionsCount) {
                    mState = STATE_SMOOTH;
                } else if (mCurrentSections < mSectionsCount) {
                    mCurrentSections++;
                }
            }
        }

        float prev;
        float end;
        float spaceLength;
        float xOffset;
        float ratioSectionWidth;
        float sectionWidth;
        float drawLength;
        int currentIndexColor = mColorsIndex;

        for (int i = 0; i <= mSectionsCount; ++i) {
            xOffset = xSectionWidth * i + mCurrentOffset;
            prev = Math.max(0f, xOffset - xSectionWidth);
            ratioSectionWidth = Math.abs(
                    mInterpolator.getInterpolation(prev) -
                            mInterpolator.getInterpolation(Math.min(xOffset, 1f)));
            sectionWidth = (int) (width * ratioSectionWidth);

            if (sectionWidth + prev < width)
                spaceLength = Math.min(sectionWidth, mSeparatorLength);
            else
                spaceLength = 0f;

            drawLength = sectionWidth > spaceLength ? sectionWidth - spaceLength : 0;
            end = prevValue + drawLength;

            if (end > prevValue) {
                /**
                 * This is not pretty. In fact, it's absurdly ugly.
                 *
                 * Can it be simplified? Probably.
                 *
                 * But it works.
                 *
                 * It covers two main cases:
                 *  - the normal flow of begin -> smooth -> finish
                 *  - the flow of begin -> finish, which has many edge cases depending on
                 *    how far into the "begin" state we are. This is mostly determined by
                 *    mCurrentSections. This then needs to work with mStartSection to transition
                 *    from that state into the finish state.
                 */
                if ((i > mCurrentSections && (mState == STATE_BEGIN)) ||
                        (mState == STATE_FINISH &&
                                ((i >= mStartSection && i > mCurrentSections) ||
                                        (i > mStartSection && i <= mCurrentSections && mStartSection == mCurrentSections))) ||
                        (mState == STATE_HALFFINISH &&
                                (i > mStartSection && mStartSection < mSectionsCount &&
                                        (i > mCurrentSections ||
                                                (mStartSection > 0 && i < mCurrentSections) ||
                                                mCurrentSections == 1 ||
                                                (mStartSection == 0 && i >= mCurrentSections)))
                        )) {
                    drawLine(canvas, boundsWidth,
                            Math.min(boundsWidth, prevValue), centerY, Math.min(boundsWidth, end), centerY,
                            mStartColor);
                } else if (i >= mStartSection) {
                    drawLine(canvas, boundsWidth,
                            Math.min(boundsWidth, prevValue), centerY, Math.min(boundsWidth, end), centerY,
                            mColors[currentIndexColor]);
                }
            }
            prevValue = end + spaceLength;
            currentIndexColor = incrementColor(currentIndexColor);
        }
    }

    private void drawLine(Canvas canvas, int canvasWidth, float startX, float startY, float stopX, float stopY, int color) {
        mPaint.setColor(color);

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

        canvas.save();
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
	 * Start the animation from a given color.
	 *
	 * @param index
	 *      the index color to transition into
	 */
	public void begin(int index) {
		begin(index, DEFAULT_START_COLOR); // transparent
	}

    /**
     * Start the animation from a given color.
     *
     * @param index
     *      the index color to transition into
     * @param startColor
     *      the color to transition from
     */
    public void begin(int index, int startColor) {
        mState = STATE_BEGIN;
        mStartSection = 0;
        mCurrentSections = 0;
        mColorsIndex = index;
        mStartColor = startColor;

        start();
    }

    /**
     * Finish the animation by animating the remaining sections.
     */
    public void finish() {
        if (mState == STATE_BEGIN && mCurrentSections < mSectionsCount) {
            mCurrentSections++;
            mState = STATE_HALFFINISH;
        } else {
            mState = STATE_FINISH;
        }

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
        if (isRunning()) return;
        scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
        invalidateSelf();
    }

    @Override
    public void stop() {
        if (!isRunning()) return;
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

    private final Runnable mUpdater = new Runnable() {

        @Override
        public void run() {
            if (mState == STATE_FINISH || mState == STATE_HALFFINISH) {
                mCurrentOffset += (2 * OFFSET_PER_FRAME * mSpeed);
            } else {
                mCurrentOffset += (OFFSET_PER_FRAME * mSpeed);
            }

            if (mCurrentOffset >= mMaxOffset) {
                mNewTurn = true;
                mCurrentOffset -= mMaxOffset;
            }
            scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
            invalidateSelf();
        }
    };


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
        private boolean mReversed;
        private boolean mMirrorMode;

        private int mStrokeSeparatorLength;
        private float mStrokeWidth;

        public Builder(Context context) {
            initValues(context);
        }

        public SmoothProgressDrawable build() {
            SmoothProgressDrawable ret = new SmoothProgressDrawable(mInterpolator, mSectionsCount, mStrokeSeparatorLength, mColors, mStrokeWidth, mSpeed, mReversed, mMirrorMode);
            return ret;
        }

        private void initValues(Context context) {
            Resources res = context.getResources();
            mInterpolator = new AccelerateInterpolator();
            mSectionsCount = res.getInteger(R.integer.spb_default_sections_count);
            mColors = new int[]{res.getColor(R.color.spb_default_color)};
            mSpeed = Float.parseFloat(res.getString(R.string.spb_default_speed));
            mReversed = res.getBoolean(R.bool.spb_default_reversed);

            mStrokeSeparatorLength = res.getDimensionPixelSize(R.dimen.spb_default_stroke_separator_length);
            mStrokeWidth = res.getDimensionPixelOffset(R.dimen.spb_default_stroke_width);
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

        public Builder reversed(boolean reversed) {
            mReversed = reversed;
            return this;
        }

        public Builder mirrorMode(boolean mirrorMode) {
            mMirrorMode = mirrorMode;
            return this;
        }
    }
}
