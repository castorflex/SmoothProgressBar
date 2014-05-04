##0.5.1

-   Fixed bug with gradients when reversed mode enabled and mirror mode disabled

##0.5.0

-   Added a gradient option via XML and JAVA

##0.4.0

-   Added a `progressStart()` and `progressStop()` methods
-   Added parameter `progressStart_activated` which makes the drawable to animate progressively at each start
-   Added speed parameters for progressStart and progressStop
-   Added possibility to set a background for the progressiveStart/Stop (See Pocket example)
-   Added possibility to set a generated background according to the bar's colors
-   Added a listener `progressiveStopEndedListener`: called when the progressiveStop animation is over

##0.3.3

-   Added a ContentLoadingSmoothProgressBar (see also [ContentLoadingProgressBar](https://android.googlesource.com/platform/frameworks/support/+/refs/heads/master/v4/java/android/support/v4/widget/ContentLoadingProgressBar.java))

##0.3.2

-   targetSdkVersion is now 14. We just need holo style.

##0.3.1

-   Added a `applyStyle(int styleResId)` method

##0.3.0

-   `SmoothProgressDrawable.Builder#width` is now `setStrokeWidth`
-   The `strokeWidth` parameter is now a `float` (was an `int`)
-   Added possibility to modify dynamically the `SmoothProgressBar` and `SmoothProgressDrawable` properties.
    e.g. You can call `mProgressBar.setColor(mColor)`
