package fr.castorflex.android.circularprogressbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.UiThread;

interface PBDelegate {

  @UiThread
  void draw(Canvas canvas, Paint paint);

  @UiThread
  void start();

  @UiThread
  void stop();

  @UiThread
  void progressiveStop(CircularProgressDrawable.OnEndListener listener);
}
