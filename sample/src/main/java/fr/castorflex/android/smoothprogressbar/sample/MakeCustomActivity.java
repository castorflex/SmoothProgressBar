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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
  private SeekBar mSeekBarFactor;
  private TextView mTextViewFactor;
  private TextView mTextViewSpeed;
  private TextView mTextViewStrokeWidth;
  private TextView mTextViewSeparatorLength;
  private TextView mTextViewSectionsCount;

  private float mSpeed;
  private int mStrokeWidth;
  private int mSeparatorLength;
  private int mSectionsCount;
  private float mFactor = 1f;

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
    mSeekBarFactor = (SeekBar) findViewById(R.id.seekbar_factor);
    mTextViewSpeed = (TextView) findViewById(R.id.textview_speed);
    mTextViewSectionsCount = (TextView) findViewById(R.id.textview_sections_count);
    mTextViewSeparatorLength = (TextView) findViewById(R.id.textview_separator_length);
    mTextViewStrokeWidth = (TextView) findViewById(R.id.textview_stroke_width);
    mTextViewFactor = (TextView) findViewById(R.id.textview_factor);

    findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mProgressBar.progressiveStart();
      }
    });

    findViewById(R.id.button_stop).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mProgressBar.progressiveStop();
      }
    });

    mSeekBarFactor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mFactor = (progress + 1) / 10f;
        mTextViewFactor.setText("Factor: " + mFactor);
        setInterpolator(mSpinnerInterpolators.getSelectedItemPosition());
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    mSeekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mSpeed = ((float) progress + 1) / 10;
        mTextViewSpeed.setText("Speed: " + mSpeed);
        mProgressBar.setSmoothProgressDrawableSpeed(mSpeed);
        mProgressBar.setSmoothProgressDrawableProgressiveStartSpeed(mSpeed);
        mProgressBar.setSmoothProgressDrawableProgressiveStopSpeed(mSpeed);
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
        mProgressBar.setSmoothProgressDrawableSectionsCount(mSectionsCount);
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
        mProgressBar.setSmoothProgressDrawableSeparatorLength(dpToPx(mSeparatorLength));
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
        mProgressBar.setSmoothProgressDrawableStrokeWidth(dpToPx(mStrokeWidth));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });

    mCheckBoxGradients.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mProgressBar.setSmoothProgressDrawableUseGradients(isChecked);
      }
    });

    mCheckBoxMirror.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mProgressBar.setSmoothProgressDrawableMirrorMode(isChecked);
      }
    });

    mCheckBoxReversed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mProgressBar.setSmoothProgressDrawableReversed(isChecked);
      }
    });

    mSeekBarSeparatorLength.setProgress(4);
    mSeekBarSectionsCount.setProgress(4);
    mSeekBarStrokeWidth.setProgress(4);
    mSeekBarSpeed.setProgress(9);
    mSeekBarFactor.setProgress(9);

    mSpinnerInterpolators.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.interpolators)));
    mSpinnerInterpolators.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setInterpolator(position);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
    mSpinnerInterpolators.setSelection(0);
  }

  private void setInterpolator(int position) {
    Interpolator interpolator;
    switch (position) {
      case 1:
        interpolator = new LinearInterpolator();
        mSeekBarFactor.setEnabled(false);
        break;
      case 2:
        interpolator = new AccelerateDecelerateInterpolator();
        mSeekBarFactor.setEnabled(false);
        break;
      case 3:
        interpolator = new DecelerateInterpolator(mFactor);
        mSeekBarFactor.setEnabled(true);
        break;
      case 0:
      default:
        interpolator = new AccelerateInterpolator(mFactor);
        mSeekBarFactor.setEnabled(true);
        break;
    }

    mProgressBar.setSmoothProgressDrawableInterpolator(interpolator);
    mProgressBar.setSmoothProgressDrawableColors(getResources().getIntArray(R.array.gplus_colors));
  }

  public int dpToPx(int dp) {
    Resources r = getResources();
    int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        dp, r.getDisplayMetrics());
    return px;
  }
}
