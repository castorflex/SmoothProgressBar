package fr.castorflex.android.circularprogressbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * Created by castorflex on 9/12/15.
 */
class PowerSaveModeDelegate implements PBDelegate {
  private static final long REFRESH_RATE = TimeUnit.SECONDS.toMillis(1L);

  private final CircularProgressDrawable mParent;
  private int mCurrentRotation;

  PowerSaveModeDelegate(@NonNull CircularProgressDrawable parent) {
    mParent = parent;
  }

  @Override
  public void draw(Canvas canvas, Paint paint) {
    canvas.drawArc(mParent.getDrawableBounds(), mCurrentRotation, 300, false, paint);
  }

  @Override
  public void start() {
    mParent.invalidate();

    mParent.scheduleSelf(mRunnable, SystemClock.uptimeMillis() + REFRESH_RATE);
  }

  @Override
  public void stop() {
    mParent.unscheduleSelf(mRunnable);
  }

  @Override
  public void progressiveStop(CircularProgressDrawable.OnEndListener listener) {
    mParent.stop();
  }

  private final Runnable mRunnable = new Runnable() {
    @Override
    public void run() {
      mCurrentRotation += 50;
      mCurrentRotation %= 360;

      if (mParent.isRunning())
        mParent.scheduleSelf(this, SystemClock.uptimeMillis() + REFRESH_RATE);

      mParent.invalidate();
    }
  };
}
