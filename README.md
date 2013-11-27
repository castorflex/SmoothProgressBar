##Description

Small library allowing you to make a smooth indeterminate progress bar. You can either user your progress bars and set this drawable or use directly the `SmoothProgressBarView`.

##Usage

-	Use directly SmoothProgressBar:

```xml
<fr.castorflex.android.smoothprogressbar.SmoothProgressBar
	xmlns:android="http://schemas.android.com/apk/res/android"
	xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:indeterminate="true"
    app:spb__sections_count="12"
    app:spb__color="#FF0000"
    app:spb__speed="2.0"
    app:spb__stroke_width="4dp"
    app:spb__stroke_separator_length="4dp
    />
```

-   Or instantiate a `SmoothProgressDrawable` and set it to your ProgressBar (do not forget to set the Horizontal Style)

```java
mProgressBar.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(context)
    .color(0xff0000)
    .interpolator(new DecelerateInterpolator())
    .sectionsCount(4)
    .separatorLength(8)     //You should use Resources#getDimensionPixelSize
    .width(8)               //You should use Resources#getDimensionPixelSize
    .speed(2.0)             //2 times faster
    .build());
```


##License

```
"THE BEER-WARE LICENSE" (Revision 42):
You can do whatever you want with this stuff.
If we meet some day, and you think this stuff is worth it, you can buy me a beer in return.
```