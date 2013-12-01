package fr.castorflex.android.smoothprogressbar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class MainActivity extends Activity {

    private ProgressBar mProgressBar1;
    private ProgressBar mProgressBar2;
    private ProgressBar mProgressBar3;
    private ProgressBar mProgressBar4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar1);
        mProgressBar2 = (ProgressBar) findViewById(R.id.progressbar2);
        mProgressBar3 = (ProgressBar) findViewById(R.id.progressbar3);
        mProgressBar4 = (ProgressBar) findViewById(R.id.progressbar4);

        mProgressBar1.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(this).interpolator(new LinearInterpolator()).build());
        mProgressBar2.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(this).interpolator(new AccelerateInterpolator()).build());
        mProgressBar3.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(this).interpolator(new DecelerateInterpolator()).build());
        mProgressBar4.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(this).interpolator(new AccelerateDecelerateInterpolator()).build());

        findViewById(R.id.button_make).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MakeCustomActivity.class);
                startActivity(intent);
            }
        });
    }
}
