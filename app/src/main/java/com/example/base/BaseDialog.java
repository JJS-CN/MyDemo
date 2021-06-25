package com.example.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;


/**
 * 说明：
 * Created by jjs on 2019/9/13.
 */
public class BaseDialog extends DialogFragment implements View.OnTouchListener {
    protected int mLayoutId;
    private OnCustomListener mCustomListener;//复用度不高时使用这个回调来布局修改xml
    public BaseViewHolder mViewHolder;//从这里面拿控件
    private boolean isBottom;//是否居于底部
    private boolean mCancelable = true;//是否点击外部dismiss,包含返回键触发
    private float windowBackgroundAlpha = -1f;//0f为透明色  小于0将作用默认值
    private boolean dialogTransparent = true;//dialog本身xml布局位置是否透明
    private DialogInterface.OnDismissListener dismissListener;
    public boolean notShowNavigation = false;

    /**
     * 设置布局id
     */
    public BaseDialog setLayoutId(@LayoutRes int layoutId) {
        mLayoutId = layoutId;
        return this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mRootView = inflater.inflate(mLayoutId, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mViewHolder = new BaseViewHolder(mRootView);
        mRootView.setClickable(true);
        updateView();
        if (mCustomListener != null) {
            mCustomListener.onCustom(mViewHolder, this);
        }
        return mRootView;
    }

    @Override
    public void dismiss() {
        //super.dismiss();
        try {
            dismissAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (dismissListener != null) {
                dismissListener.onDismiss(this.getDialog());
            }
        } catch (Exception e) {
        }
    }

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    //继承方式使用时，在构造方法中初始化数据，initView中解析绑定数据
    //展示后需要更新UI时，需判断viewholder是否为空，因为调用show()到onCreateView需要一段时间
    protected void initView() {
    }

    //做一次非空判断，从走initview--所有处理都最好使用update触发
    public void updateView() {
        if (mViewHolder != null) {
            initView();
        }
    }

    //会在初始化时调用一次，可通过viewHolder拿到所有控件
    public BaseDialog setCustomListener(OnCustomListener customListener) {
        this.mCustomListener = customListener;
        return this;
    }

    public interface OnCustomListener {
        void onCustom(BaseViewHolder holder, BaseDialog dialog);
    }

    public void show(FragmentActivity activity) {
        try {
            //在每个add事务前增加一个remove事务，防止连续的add
            activity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        } catch (Exception e) {
            //同一实例使用不同的tag会异常,这里捕获一下
            e.printStackTrace();
        }
        try {
            super.show(activity.getSupportFragmentManager(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            int divierId = getDialog().getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
            View divider = getDialog().findViewById(divierId);
            if (divider != null) {
                divider.setBackgroundColor(Color.TRANSPARENT);
            }
        } catch (Exception e) {
        }
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        if (windowBackgroundAlpha >= 0f) {
            params.dimAmount = windowBackgroundAlpha;
            window.setAttributes(params);
        }
        if (dialogTransparent) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

    }

    public BaseDialog setWindowBackgroundAlpha(float windowBackgroundAlpha) {
        this.windowBackgroundAlpha = windowBackgroundAlpha;
        return this;
    }

    public BaseDialog hasTransparentForDialog(boolean dialogTransparent) {
        this.dialogTransparent = dialogTransparent;
        return this;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    public void hasBottomUP(boolean isBottom) {
        this.isBottom = isBottom;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        boolean isShow = this.getShowsDialog();
        this.setShowsDialog(false);
        super.onActivityCreated(savedInstanceState);
        this.setShowsDialog(isShow);

        View view = getView();
        if (view != null) {
            if (view.getParent() != null) {
                throw new IllegalStateException(
                        "DialogFragment can not be attached to a container view");
            }
            this.getDialog().setContentView(view);
        }
        final Activity activity = getActivity();
        if (activity != null) {
            this.getDialog().setOwnerActivity(activity);
        }
        this.getDialog().setCancelable(false);
        this.getDialog().getWindow().getDecorView().setOnTouchListener(this);
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (mCancelable && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE)) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        if (savedInstanceState != null) {
            Bundle dialogState = savedInstanceState.getBundle("android:savedDialogState");
            if (dialogState != null) {
                this.getDialog().onRestoreInstanceState(dialogState);
            }
        }
        /**
         * 项目中的CartView 当作普同fragment使用  不会创建dialog
         */
        if (notShowNavigation && getShowsDialog()) {
            //not focus 来避免瞬间弹出
            getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            //重新设置可获取焦点 避免弹不出键盘
            getDialog().getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_FULLSCREEN;
                        getDialog().getWindow().getDecorView().setSystemUiVisibility(uiOptions);
                    }
                    getDialog().getWindow().setFlags(~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                }
            });
        }
    }

    public void setCancelable(boolean mCancelable) {
        this.mCancelable = mCancelable;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mCancelable && getDialog().isShowing()) {
            dismiss();
            return true;
        }
        return false;
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    public static class BaseViewHolder {
        View itemView;

        public BaseViewHolder(View itemView) {
            this.itemView = itemView;
        }

        public <T extends View> T getView(int id) {
            return itemView.findViewById(id);
        }
    }
}
