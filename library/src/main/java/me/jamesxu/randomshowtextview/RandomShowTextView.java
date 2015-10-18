package me.jamesxu.randomshowtextview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by james on 14/10/15.
 */
public class RandomShowTextView extends TextView {
    private final int default_text_color = Color.BLACK;
    private final int default_duration_time = 2000;

    private SpannableString spannableString;
    private AccelerateDecelerateInterpolator mSmoothInterpolator;
    private String text;
    private int duration;
    private int color;

    public RandomShowTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }


    public RandomShowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RandomShowTextView,
                defStyleAttr, 0);

        text = attributes.getString(R.styleable.RandomShowTextView_text);
        duration = attributes.getInt(R.styleable.RandomShowTextView_duration, default_duration_time);
        color = attributes.getColor(R.styleable.RandomShowTextView_textColor, default_text_color);
        init();
        fireWork();
    }


    private void init() {
        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
    }

    private void fireWork() {
        spannableString = new SpannableString(text);
        FireworksSpanGroup spanGroup = buildFireworksSpanGroup(spannableString, 0, spannableString.length() - 1);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(spanGroup, FIREWORKS_GROUP_PROGRESS_PROPERTY, 0.0f, 1.0f);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //refresh
                setText(spannableString);
            }
        });
        objectAnimator.setInterpolator(mSmoothInterpolator);
        objectAnimator.setDuration(duration);
        objectAnimator.start();
    }

    private void setText(String text) {
        this.text = text;
        fireWork();
    }
    
    private void startAnimaion() {
        fireWork();
    }

    private static final Property<FireworksSpanGroup, Float> FIREWORKS_GROUP_PROGRESS_PROPERTY =
            new Property<FireworksSpanGroup, Float>(Float.class, "FIREWORKS_GROUP_PROGRESS_PROPERTY") {

                @Override
                public void set(FireworksSpanGroup spanGroup, Float value) {
                    Log.i("mainActivity", "value=" + value);
                    spanGroup.setProgress(value);
                }

                @Override
                public Float get(FireworksSpanGroup spanGroup) {
                    return spanGroup.getProgress();
                }
            };


    private FireworksSpanGroup buildFireworksSpanGroup(SpannableString spannableString, int start, int end) {
        final FireworksSpanGroup group = new FireworksSpanGroup();
        for (int index = start; index <= end; index++) {
            MutableForegroundColorSpan span = new MutableForegroundColorSpan(0, color);
            group.addSpan(span);
            spannableString.setSpan(span, index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        group.init();
        return group;
    }


    public final class FireworksSpanGroup {

        private static final boolean DEBUG = false;
        private static final String TAG = "FireworksSpanGroup";

        private final float mProgress;
        private final ArrayList<MutableForegroundColorSpan> mSpans;
        private final ArrayList<Integer> mSpanIndexes;

        public FireworksSpanGroup() {
            mProgress = 0;
            mSpans = new ArrayList<MutableForegroundColorSpan>();
            mSpanIndexes = new ArrayList<Integer>();
        }

        public void addSpan(MutableForegroundColorSpan span) {
            span.setAlpha(0);
            mSpanIndexes.add(mSpans.size());
            mSpans.add(span);
        }

        public void init() {
            Collections.shuffle(mSpans);
        }

        public void setProgress(float progress) {
            int size = mSpans.size();
            float total = 1.0f * size * progress;


            for (int index = 0; index < size; index++) {
                MutableForegroundColorSpan span = mSpans.get(index);

                if (total >= 1.0f) {
                    Log.d(TAG, "显示了" + total);
                    span.setAlpha(255);
                    total -= 1.0f;
                } else {
                    span.setAlpha((int) (total * 255));
                    total = 0.0f;
                }
            }
        }

        public float getProgress() {
            return mProgress;
        }
    }

    public class MutableForegroundColorSpan extends ForegroundColorSpan {

        private int mAlpha = 255;
        private int mForegroundColor;

        public MutableForegroundColorSpan(int alpha, int color) {
            super(color);
            mAlpha = alpha;
            mForegroundColor = color;
        }

        public MutableForegroundColorSpan(Parcel src) {
            super(src);
            mForegroundColor = src.readInt();
            mAlpha = src.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mForegroundColor);
            dest.writeFloat(mAlpha);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getForegroundColor());
        }

        /**
         * @param alpha from 0 to 255
         */
        public void setAlpha(int alpha) {
            mAlpha = alpha;
        }

        public void setForegroundColor(int foregroundColor) {
            mForegroundColor = foregroundColor;
        }

        public float getAlpha() {
            return mAlpha;
        }

        @Override
        public int getForegroundColor() {
            return Color.argb(mAlpha, Color.red(mForegroundColor), Color.green(mForegroundColor), Color.blue(mForegroundColor));
        }
    }
}
