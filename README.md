##Description

Small library allowing you to make a smooth indeterminate progress bar. You can either user your progress bars and set this drawable or use directly the `SmoothProgressBarView`.

##Demo:
Sample app available on the [Play Store]

![SmoothProgressBar](screenshots/SPB_sample.gif)    

##How does it work

I wrote a [blog post] about that.

##Integration     

**SmoothProgressBar** (min API 7): [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.castorflex.smoothprogressbar/library/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.castorflex.smoothprogressbar/library)

**CircularProgressBar** (min API 14): [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.castorflex.smoothprogressbar/library-circular/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.castorflex.smoothprogressbar/library-circular)

The lib is now on Maven Central. All you have to do is add it on your gradle build:

```xml
dependencies {
    // of course, do not write x.x.x but the version number
    compile 'com.github.castorflex.smoothprogressbar:library:x.x.x'
    // or
    compile 'com.github.castorflex.smoothprogressbar:library-circular:x.x.x'
}
```
You can find the last stable version on [Gradle Please]


Or you can try the latest snapshots:

```xml
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
}

dependencies {
    compile 'com.github.castorflex.smoothprogressbar:library:1.0.1-SNAPSHOT@aar'
    compile 'com.github.castorflex.smoothprogressbar:library-circular:1.0.1-SNAPSHOT@aar'
}
```


If you really want (or have) to use Eclipse, please look at the forks.

##Usage

-	Use directly SmoothProgressBar:

```xml
<fr.castorflex.android.smoothprogressbar.SmoothProgressBar
	xmlns:android="http://schemas.android.com/apk/res/android"
	xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:indeterminate="true"
    app:spb_sections_count="4"
    app:spb_color="#FF0000"
    app:spb_speed="2.0"
    app:spb_stroke_width="4dp"
    app:spb_stroke_separator_length="4dp"
    app:spb_reversed="false"
    app:spb_mirror_mode="false"
    app:spb_progressiveStart_activated="true"
    app:spb_progressiveStart_speed="1.5"
    app:spb_progressiveStop_speed="3.4"
    />

<fr.castorflex.android.smoothprogressbar.CircularProgressBar
	xmlns:android="http://schemas.android.com/apk/res/android"
	xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:indeterminate="true"
    app:cpb_color="4"
    app:cpb_colors="@array/mycolors"
    app:spb_speed="1.0"
    app:cpb_stroke_width="4dp"
    app:cpb_min_sweep_angle="10"
    app:cpb_max_sweep_angle="300"
    />
```

Or use styles:

```xml
<style name="AppTheme">
    <item name="spbStyle">@style/GNowProgressBar</item>
    <item name="cpbStyle">@style/CircularThemeProgressBar</item>
</style>

<style name="GNowProgressBar" parent="SmoothProgressBar">
    <item name="spb_stroke_separator_length">0dp</item>
    <item name="spb_sections_count">2</item>
    <item name="spb_speed">1.7</item>
    <item name="spb_progressiveStart_speed">2</item>
    <item name="spb_progressiveStop_speed">3.4</item>
    <item name="spb_interpolator">spb_interpolator_acceleratedecelerate</item>
    <item name="spb_mirror_mode">true</item>
    <item name="spb_reversed">true</item>
    <item name="spb_colors">@array/gplus_colors</item>
    <item name="spb_progressiveStart_activated">true</item>
</style>

<style name="CircularThemeProgressBar" parent="android:Widget.Holo.ProgressBar">
        <item name="cpb_color">@color/cpb_default_color</item>
        <item name="cpb_stroke_width">@dimen/cpb_default_stroke_width</item>
        <item name="cpb_min_sweep_angle">@integer/cpb_default_min_sweep_angle</item>
        <item name="cpb_max_sweep_angle">@integer/cpb_default_max_sweep_angle</item>
        <item name="cpb_speed">@string/cpb_default_speed</item>
</style>
```

*You can find more styles [in the sample app][Sample Themes]*

-   Or instantiate a `SmoothProgressDrawable`/`CircularProgressDrawable` and set it to your ProgressBar

```java
mProgressBar.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(context)
    .color(0xff0000)
    .interpolator(new DecelerateInterpolator())
    .sectionsCount(4)
    .separatorLength(8)         //You should use Resources#getDimensionPixelSize
    .strokeWidth(8f)            //You should use Resources#getDimension
    .speed(2f)                 //2 times faster
    .progressiveStartSpeed(2)
    .progressiveStopSpeed(3.4)
    .reversed(false)
    .mirrorMode(false)
    .progressiveStart(true)
    .progressiveStopEndedListener(mListener) //called when the stop animation is over
    .build());
    
mProgressBar.setIndeterminateDrawable(new CircularProgressDrawable
    .Builder(this)
    .colors(getResources().getIntArray(R.array.gplus_colors))
    .sweepSpeed(1f)
    .strokeWidth(mStrokeWidth)
    .style(CircularProgressDrawable.Style.ROUNDED)
    [ ... ]
    .build();
```

You can also set many colors for one bar (see G+ app)

-   via xml (use the `app:spb_colors` attribute with a `integer-array` reference for that)

-   programmatically (use `SmoothProgressDrawable.Builder#colors(int[])` method).


##License

```
Copyright 2014 Antoine Merle

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

####Badges

Travis master: [![Build Status](https://travis-ci.org/castorflex/SmoothProgressBar.svg?branch=master)](https://travis-ci.org/castorflex/SmoothProgressBar)

Travis dev: [![Build Status](https://travis-ci.org/castorflex/SmoothProgressBar.svg?branch=dev)](https://travis-ci.org/castorflex/SmoothProgressBar?branch=dev)


[blog post]: http://antoine-merle.com/blog/2013/11/12/make-your-progressbar-more-smooth/

[Play Store]: https://play.google.com/store/apps/details?id=fr.castorflex.android.smoothprogressbar.sample

[Gradle Please]: http://gradleplease.appspot.com/#smoothprogressbar

[Sample Themes]: https://github.com/castorflex/SmoothProgressBar/blob/master/sample/src/main/res/values/styles.xml
