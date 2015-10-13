package me.jamesxu.randomshowtextview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

/**
 * Created by james on 14/10/15.
 */
public class RandomShowTextView extends TextView {
    private SpannableString spannableString;
    private AccelerateDecelerateInterpolator mSmoothInterpolator;

    public RandomShowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public RandomShowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mSmoothInterpolator = new AccelerateDecelerateInterpolator();
        fireWork();
    }

    private void fireWork() {
        spannableString = new SpannableString("许锦洋");
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
        objectAnimator.setDuration(2000);
        objectAnimator.start();
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
            MutableForegroundColorSpan span = new MutableForegroundColorSpan(0, Color.GREEN);
            group.addSpan(span);
            spannableString.setSpan(span, index, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        group.init();
        return group;
    }


}
