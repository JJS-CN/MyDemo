package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * Class: 实现类似CheckBox的点击切换颜色效果，可以理解为没有复选框的checkbox：
 * Other: 文字变色、背景变色、文字大小变化
 * Create by jsji on  2021/3/17.
 */
public class ATextView extends androidx.appcompat.widget.AppCompatTextView {
    public ATextView(Context context) {
        super(context);
    }

    public ATextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ATextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



}
