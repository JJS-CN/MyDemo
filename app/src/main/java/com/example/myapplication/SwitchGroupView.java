package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: 实现类似Switch开关的效果，不要求动画，只要处理点击切换效果。
 * Other: 因为Switch只能设置文本，在某些样式上处理不了，所以需要能放入一个viewGroup
 * java代码不关心是怎么切换的，所以对应逻辑应该进行封装，切换的颜色应该由xml控制
 * Create by jsji on  2021/3/23.
 */
public class SwitchGroupView extends LinearLayout implements View.OnClickListener {


    public SwitchGroupView(Context context) {
        super(context);
    }

    @Override
    public void onClick(View v) {

    }
}
