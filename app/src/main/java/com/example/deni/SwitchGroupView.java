package com.example.deni;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * Class:
 * Other: 分为固定背景、底部滑块，多个item的select属性切换，滑块需要执行动画效果
 * Create by jsji on  2021/3/4.
 */
public class SwitchGroupView extends LinearLayout {
    private int selectIndex;

    public SwitchGroupView(Context context) {
        super(context);
        init();
    }

    public SwitchGroupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwitchGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            //重新计算滑块宽度与偏移量
        }
    }

    private void init() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(childCount);
            childAt.setOnClickListener(itemClick);
            if (i == selectIndex) {
                updateSelect(childAt);
            }
        }
    }

    private void updateSelect(View view) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(childCount);
            setChildSelect(childAt, view == childAt);
            if (view == childAt) {
                if (selectIndex != i) {
                    selectIndex = i;
                    startAnim();
                }
            }
        }
    }

    private void startAnim() {
        //执行滑块滚动动画
    }

    private void setChildSelect(View childView, boolean select) {
        if (childView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) childView;
            for (int i = 0; i < group.getChildCount(); i++) {
                View childAt = group.getChildAt(i);
                setChildSelect(childAt, select);
            }
        } else {
            childView.setSelected(select);
        }
    }

    private OnClickListener itemClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            updateSelect(v);
        }
    };


}
