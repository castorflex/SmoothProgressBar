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
