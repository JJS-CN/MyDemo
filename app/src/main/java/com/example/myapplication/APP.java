package com.example.myapplication;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Class:
 * Other:
 * Create by jsji on  2021/1/29.
 */
public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
