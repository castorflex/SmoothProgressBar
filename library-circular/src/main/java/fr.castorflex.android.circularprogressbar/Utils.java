package fr.castorflex.android.circularprogressbar;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;

import static java.lang.Math.min;

class Utils {

  private Utils() {
  }

  static void checkSpeed(float speed) {
    if (speed <= 0f)
      throw new IllegalArgumentException("Speed must be >= 0");
  }

  static void checkColors(int[] colors) {
    if (colors == null || colors.length == 0)
      throw new IllegalArgumentException("You must provide at least 1 color");
  }

  static void checkAngle(int angle) {
    if (angle < 0 || angle > 360)
      throw new IllegalArgumentException(String.format("Illegal angle %d: must be >=0 and <=360", angle));
  }

  static void checkPositiveOrZero(float number, String name) {
    if (number < 0)
      throw new IllegalArgumentException(String.format("%s %f must be positive", name, number));
  }

  static void checkPositive(int number, String name) {
    if (number <= 0)
      throw new IllegalArgumentException(String.format("%s must not be null", name));
  }

  static void checkNotNull(Object o, String name) {
    if (o == null)
      throw new IllegalArgumentException(String.format("%s must be not null", name));
  }

  static float getAnimatedFraction(ValueAnimator animator) {
    float fraction = animator.getDuration() > 0 ? ((float) animator.getCurrentPlayTime()) / animator.getDuration() : 0f;

    fraction = min(fraction, 1f);
    fraction = animator.getInterpolator().getInterpolation(fraction);
    return fraction;
  }

  @TargetApi(21)
  public static boolean isPowerSaveModeEnabled(@NonNull PowerManager powerManager) {
    if (Build.VERSION.SDK_INT < 21) return false;

    try {
      return powerManager.isPowerSaveMode();
    } catch (Exception e) {
      return false;
    }
  }

  public static PowerManager powerManager(Context context) {
    return (PowerManager) context.getSystemService(Context.POWER_SERVICE);
  }
}
