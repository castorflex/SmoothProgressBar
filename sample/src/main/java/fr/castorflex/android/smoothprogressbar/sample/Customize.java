package fr.castorflex.android.smoothprogressbar.sample;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/**
 * Created by castorflex on 12/1/13.
 */
public class Customize extends Activity {

    private ProgressBar mProgressBar;
    private CheckBox mCheckBoxMirror;
    private CheckBox mCheckBoxReversed;
    private Spinner mSpinnerInterpolators;
    private Spinner mSpinnerColors;
    private SeekBar mSeekBarSectionsCount;
    private SeekBar mSeekBarStrokeWidth;
    private SeekBar mSeekBarSeparatorLength;
    private SeekBar mSeekBarSpeed;
    private TextView mTextViewSpeed;
    private TextView mTextViewStrokeWidth;
    private TextView mTextViewSeparatorLength;
    private TextView mTextViewSectionsCount;

    private boolean isReversed;
    private boolean isMirrored;
    private float mSpeed;
    private int mStrokeWidth;
    private int mSeparatorLength;
    private int mSectionsCount;
    private int[] mColors;
    private Interpolator mInterpolator;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_custom, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mCheckBoxReversed = (CheckBox) findViewById(R.id.checkbox_reversed);
        mCheckBoxMirror = (CheckBox) findViewById(R.id.checkbox_mirror);
        mSpinnerInterpolators = (Spinner) findViewById(R.id.spinner_interpolator);
        mSpinnerColors = (Spinner) findViewById(R.id.spinner_colors);
        mSeekBarSectionsCount = (SeekBar) findViewById(R.id.seekbar_sections_count);
        mSeekBarStrokeWidth = (SeekBar) findViewById(R.id.seekbar_stroke_width);
        mSeekBarSeparatorLength = (SeekBar) findViewById(R.id.seekbar_separator_length);
        mSeekBarSpeed = (SeekBar) findViewById(R.id.seekbar_speed);
        mTextViewSpeed = (TextView) findViewById(R.id.textview_speed);
        mTextViewSectionsCount = (TextView) findViewById(R.id.textview_sections_count);
        mTextViewSeparatorLength = (TextView) findViewById(R.id.textview_separator_length);
        mTextViewStrokeWidth = (TextView) findViewById(R.id.textview_stroke_width);

        setDefaults();

        mCheckBoxReversed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isReversed = isChecked;
                setValues();
            }
        });

        mCheckBoxMirror.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMirrored = isChecked;
                setValues();
            }
        });

        mSeekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSpeed = ((float) progress / 10) + 1;
                mTextViewSpeed.setText("Speed: " + mSpeed);
                setValues();
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
                setValues();
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
                setValues();
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
                setValues();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mSpinnerColors.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.colors_array)));
        mSpinnerColors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (mSpinnerColors.getSelectedItemPosition()) {
                    case 0:
                        mColors = new int[]{getResources().getColor(R.color.spb_default_color)};
                        break;
                    case 1:
                        mColors = new int[]{getResources().getColor(R.color.holo_red_dark)};
                        break;
                    case 2:
                        mColors = getResources().getIntArray(R.array.gplus_colors);
                        break;
                    default:
                        mInterpolator = new AccelerateInterpolator();
                        break;
                }
                setValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerInterpolators.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.interpolators)));
        mSpinnerInterpolators.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (mSpinnerInterpolators.getSelectedItemPosition()) {
                    case 1:
                        mInterpolator = new LinearInterpolator();
                        break;
                    case 2:
                        mInterpolator = new AccelerateDecelerateInterpolator();
                        break;
                    case 3:
                        mInterpolator = new DecelerateInterpolator();
                        break;
                    case 0:
                    default:
                        mInterpolator = new AccelerateInterpolator();
                        break;
                }
                setValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setValues() {
        SmoothProgressDrawable.Builder builder = new SmoothProgressDrawable.Builder(this);

        builder.speed(mSpeed)
                .sectionsCount(mSectionsCount)
                .separatorLength(dpToPx(mSeparatorLength))
                .width(dpToPx(mStrokeWidth))
                .mirrorMode(isMirrored)
                .reversed(isReversed)
                .colors(mColors)
                .interpolator(mInterpolator);

        SmoothProgressDrawable d = builder.build();
        d.setBounds(mProgressBar.getIndeterminateDrawable().getBounds());
        mProgressBar.setIndeterminateDrawable(d);
        d.start();
    }

    public int dpToPx(int dp) {
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, r.getDisplayMetrics());
        return px;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.defaults:
                setDefaults();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDefaults() {
        mSeekBarSpeed.setProgress((int) SmoothProgressBar.SPEED_DEFAULT * 10);
        mSeekBarStrokeWidth.setProgress(SmoothProgressBar.STROKE_WIDTH_DEFAULT);
        mSeekBarSeparatorLength.setProgress(SmoothProgressBar.SEPARATOR_LENGHT_DEFAULT);
        mSeekBarSectionsCount.setProgress(SmoothProgressBar.SECTIONS_COUNT_DEFAULT);
        mCheckBoxReversed.setChecked(false);
        mCheckBoxMirror.setChecked(false);
        mSpinnerColors.setSelection(0);
        mSpinnerInterpolators.setSelection(0);

        mSpeed = SmoothProgressBar.SPEED_DEFAULT;
        mStrokeWidth = SmoothProgressBar.STROKE_WIDTH_DEFAULT;
        mSeparatorLength = SmoothProgressBar.SEPARATOR_LENGHT_DEFAULT;
        mSectionsCount = SmoothProgressBar.SECTIONS_COUNT_DEFAULT;
        mColors = new int[]{getResources().getColor(R.color.spb_default_color)};

        mTextViewSpeed.setText("Speed: " + mSpeed);
        mTextViewStrokeWidth.setText(String.format("Stroke width: %ddp", mStrokeWidth));
        mTextViewSeparatorLength.setText(String.format("Separator length: %ddp", mSeparatorLength));
        mTextViewSectionsCount.setText("Sections count: " + mSectionsCount);
    }
}
