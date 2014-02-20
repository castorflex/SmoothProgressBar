package fr.castorflex.android.smoothprogressbar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class MainActivity extends Activity {

  private ProgressBar mProgressBar1;
  private ProgressBar mProgressBar2;
  private SmoothProgressBar mGoogleNow;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar2);
    mProgressBar2 = (ProgressBar) findViewById(R.id.progressbar3);

    mProgressBar1.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(this).interpolator(new AccelerateInterpolator()).build());
    mProgressBar2.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(this).interpolator(new DecelerateInterpolator()).build());

    mGoogleNow = (SmoothProgressBar) findViewById(R.id.google_now);

    findViewById(R.id.button_make).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, MakeCustomActivity.class);
        startActivity(intent);
      }
    });

    findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mGoogleNow.progressiveStart();
      }
    });

    findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mGoogleNow.progressiveStop();
      }
    });
  }
}
