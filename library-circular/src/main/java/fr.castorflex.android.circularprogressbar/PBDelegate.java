package fr.castorflex.android.circularprogressbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

interface PBDelegate {
  void draw(Canvas canvas, Paint paint);

  void start();

  void stop();

  void progressiveStop(CircularProgressDrawable.OnEndListener listener);
}
