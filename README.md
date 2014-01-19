##Description

Small library allowing you to make a smooth indeterminate progress bar. You can either user your progress bars and set this drawable or use directly the `SmoothProgressBarView`.

Sample app available on the [Play Store]

##How does it work

I made a [blog post] about that.

##Integration

The lib is now on Maven Central. All you have to do is add it on your gradle build:

```xml
dependencies {
    // of course, do not write x.x.x but the version number
    compile 'com.github.castorflex.smoothprogressbar:library:x.x.x'
}
```

You can find the last version on [Gradle Please]

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
    app:spb_mirror_mode="false"
    />
```

-   Or instantiate a `SmoothProgressDrawable` and set it to your ProgressBar (do not forget to set the Horizontal Style)

```java
mProgressBar.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(context)
    .color(0xff0000)
    .interpolator(new DecelerateInterpolator())
    .sectionsCount(4)
    .separatorLength(8)     //You should use Resources#getDimensionPixelSize
    .strokeWidth(8f)         //You should use Resources#getDimension
    .speed(2.0)             //2 times faster
    .reversed(false)
    .mirrorMode(false)
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

####Badges
Travis master: [![Build Status](https://travis-ci.org/castorflex/SmoothProgressBar.png?branch=master)](https://travis-ci.org/castorflex/SmoothProgressBar)
Travis dev: [![Build Status](https://travis-ci.org/castorflex/SmoothProgressBar.png?branch=dev)](https://travis-ci.org/castorflex/SmoothProgressBar)
Bitdeli: [![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/castorflex/smoothprogressbar/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

[![Analytics](https://ga-beacon.appspot.com/UA-32954204-2/SmoothProgressBar/readme)](https://github.com/igrigorik/ga-beacon)

[blog post]: http://antoine-merle.com/blog/2013/11/12/make-your-progressbar-more-smooth/

[Play Store]: https://play.google.com/store/apps/details?id=fr.castorflex.android.smoothprogressbar.sample

[Gradle Please]: http://gradleplease.appspot.com/
