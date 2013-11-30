##Description

Small library allowing you to make a smooth indeterminate progress bar. You can either user your progress bars and set this drawable or use directly the `SmoothProgressBarView`.

Sample app available on the [Play Store]

##How does it work

I made a [blog post] about that.

##Usage

-	Use directly SmoothProgressBar:

```xml
<fr.castorflex.android.smoothprogressbar.SmoothProgressBar
	xmlns:android="http://schemas.android.com/apk/res/android"
	xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:indeterminate="true"
    app:spb_sections_count="12"
    app:spb_color="#FF0000"
    app:spb_speed="2.0"
    app:spb_stroke_width="4dp"
    app:spb_stroke_separator_length="4dp"
    app:spb_reversed="false"
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
    .reversed(false)
    .build());
```

You can also set many colors for one bar (see G+ app)

-   via xml (use the `app:spb_colors` attribute with a `integer-array` reference for that)

-   programmatically (use `SmoothProgressDrawable.Builder#colors(int[])` method).


##License

```
"THE BEER-WARE LICENSE" (Revision 42):
You can do whatever you want with this stuff.
If we meet some day, and you think this stuff is worth it, you can buy me a beer in return.
```


[blog post]: http://antoine-merle.com/blog/2013/11/12/make-your-progressbar-more-smooth/

[Play Store]: https://play.google.com/store/apps/details?id=fr.castorflex.android.smoothprogressbar.sample
