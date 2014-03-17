package fr.castorflex.android.smoothprogressbar;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;

/**
 * Created by castorflex on 3/5/14.
 */
public final class SmoothProgressBarUtils {
  private SmoothProgressBarUtils() {
  }

  public static Drawable generateDrawableWithColors(int[] colors, float strokeWidth) {
    if (colors == null || colors.length == 0) return null;

    return new ShapeDrawable(new ColorsShape(strokeWidth, colors));
  }
}
