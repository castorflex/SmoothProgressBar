package fr.castorflex.android.smoothprogressbar.sample;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by castorflex on 12/1/13.
 */
public class MakeCustomActivity extends Activity {

  private SmoothProgressBar mProgressBar;
  private CheckBox mCheckBoxMirror;
  private CheckBox mCheckBoxReversed;
  private CheckBox mCheckBoxGradients;
  private Spinner mSpinnerInterpolators;
  private SeekBar mSeekBarSectionsCount;
  private SeekBar mSeekBarStrokeWidth;
  private SeekBar mSeekBarSeparatorLength;
  private SeekBar mSeekBarSpeed;
  private Button mButton;
  private TextView mTextViewSpeed;
  private TextView mTextViewStrokeWidth;
  private TextView mTextViewSeparatorLength;
  private TextView mTextViewSectionsCount;

  private float mSpeed;
  private int mStrokeWidth;
  private int mSeparatorLength;
  private int mSectionsCount;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_custom);

    mProgressBar = (SmoothProgressBar) findViewById(R.id.progressbar);
    mCheckBoxMirror = (CheckBox) findViewById(R.id.checkbox_mirror);
    mCheckBoxReversed = (CheckBox) findViewById(R.id.checkbox_reversed);
    mCheckBoxGradients = (CheckBox) findViewById(R.id.checkbox_gradients);
    mSpinnerInterpolators = (Spinner) findViewById(R.id.spinner_interpolator);
    mSeekBarSectionsCount = (SeekBar) findViewById(R.id.seekbar_sections_count);
    mSeekBarStrokeWidth = (SeekBar) findViewById(R.id.seekbar_stroke_width);
    mSeekBarSeparatorLength = (SeekBar) findViewById(R.id.seekbar_separator_length);
    mSeekBarSpeed = (SeekBar) findViewById(R.id.seekbar_speed);
    mButton = (Button) findViewById(R.id.button);
    mTextViewSpeed = (TextView) findViewById(R.id.textview_speed);
    mTextViewSectionsCount = (TextView) findViewById(R.id.textview_sections_count);
    mTextViewSeparatorLength = (TextView) findViewById(R.id.textview_separator_length);
    mTextViewStrokeWidth = (TextView) findViewById(R.id.textview_stroke_width);


    mSeekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mSpeed = ((float) progress + 1) / 10;
        mTextViewSpeed.setText("Speed: " + mSpeed);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    mSeekBarSectionsCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mSectionsCount = progress + 1;
        mTextViewSectionsCount.setText("Sections count: " + mSectionsCount);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    mSeekBarSeparatorLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mSeparatorLength = progress;
        mTextViewSeparatorLength.setText(String.format("Separator length: %ddp", mSeparatorLength));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    mSeekBarStrokeWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mStrokeWidth = progress;
        mTextViewStrokeWidth.setText(String.format("Stroke width: %ddp", mStrokeWidth));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    mSeekBarSeparatorLength.setProgress(4);
    mSeekBarSectionsCount.setProgress(4);
    mSeekBarStrokeWidth.setProgress(4);
    mSeekBarSpeed.setProgress(9);

    mSpinnerInterpolators.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.interpolators)));

    mButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setValues();
      }
    });
  }

  private void setValues() {

    mProgressBar.setSmoothProgressDrawableSpeed(mSpeed);
    mProgressBar.setSmoothProgressDrawableSectionsCount(mSectionsCount);
    mProgressBar.setSmoothProgressDrawableSeparatorLength(dpToPx(mSeparatorLength));
    mProgressBar.setSmoothProgressDrawableStrokeWidth(dpToPx(mStrokeWidth));
    mProgressBar.setSmoothProgressDrawableReversed(mCheckBoxReversed.isChecked());
    mProgressBar.setSmoothProgressDrawableMirrorMode(mCheckBoxMirror.isChecked());
    mProgressBar.setSmoothProgressDrawableUseGradients(mCheckBoxGradients.isChecked());

    Interpolator interpolator;
    switch (mSpinnerInterpolators.getSelectedItemPosition()) {
      case 1:
        interpolator = new LinearInterpolator();
        break;
      case 2:
        interpolator = new AccelerateDecelerateInterpolator();
        break;
      case 3:
        interpolator = new DecelerateInterpolator();
        break;
      case 0:
      default:
        interpolator = new AccelerateInterpolator();
        break;
    }

    mProgressBar.setSmoothProgressDrawableInterpolator(interpolator);
    mProgressBar.setSmoothProgressDrawableColors(getResources().getIntArray(R.array.colors));
  }

  public int dpToPx(int dp) {
    Resources r = getResources();
    int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        dp, r.getDisplayMetrics());
    return px;
  }
}
