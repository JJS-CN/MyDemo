package com.example.deni;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathMeasure;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ConvertUtils;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Class:
 * Other:
 * Create by jsji on  2021/1/28.
 */
public class SignInLinesView extends View {
    private static final String TAG = "SignInLinesView";
    private Paint mPaint;//画笔
    private TextPaint mTextPaint;//文字画笔
    private int progressBackColor = 0xFFFFEAB1;
    private int progressfoceColor = 0xFFFE8400;
    private int pointColor = 0xFFFFFFFF;
    private int progressWidthSize;
    private int lineSpaceSize = 0;//线条间隔高度
    private Path mPath;
    private PathMeasure measure = new PathMeasure();
    private float pathMaxLength;//路径的最大长度
    private float pathVerticalLength;//路径竖向方向的长度
    private float pathHorizontalLength;//路径横向方向的长度
    private List<PosEntity> pathPos = new ArrayList<>();//用于存放计算好的点
    private List<ExtEntity> extList = new ArrayList<>();//用于存放解析后的数据
    private int bottomTextColor = 0xFFAAAAAA;
    private float bottomTextSize = ConvertUtils.sp2px(10);
    private int topTextSignColor = 0xFFFE8400;
    private int topTextNormalColor = 0xFF7C432A;
    private float topTextSize = ConvertUtils.sp2px(14);
    private Bitmap bitmapZuan;
    private Bitmap bitmapBao;
    private Bitmap bitmapBaoBig;
    private Bitmap bitmapTip;
    private DashPathEffect dashPathEffect;
    private DashPathEffect progressDash;
    private int endSignPosition = -1;
    private float animValue;
    private float animPathLength;


    public SignInLinesView(Context context) {
        this(context, null);
    }

    public SignInLinesView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignInLinesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(1);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        dashPathEffect = new DashPathEffect(new float[]{ConvertUtils.dp2px(2), ConvertUtils.dp2px(4)}, 0);

        lineSpaceSize = ConvertUtils.dp2px(114);
        progressWidthSize = ConvertUtils.dp2px(4);
        bitmapZuan = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_boon_zuan);
        bitmapBao = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_boon_bao);
        bitmapBaoBig = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_boon_bao_big);
        bitmapTip = BitmapFactory.decodeResource(context.getResources(), R.mipmap.img_boon_tips_bg);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                day++;
                if (day >= 30) {
                    day = 0;
                }
                parse();
            }
        });
        parse();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        if (w > ConvertUtils.dp2px(288)) {
            w = ConvertUtils.dp2px(288);
        }
        final int h = w * 235 / 312;
        Log.e(TAG, "onMeasure: " + w + "  " + h);
        setMeasuredDimension(w, h);

        int oneLineY = h * 90 / 232;
        int twoLineY = h * 210 / 232;
        mPath = new Path();
        mPath.moveTo(0 + progressWidthSize, oneLineY);
        mPath.lineTo(w - progressWidthSize, oneLineY);
        mPath.lineTo(w - progressWidthSize, twoLineY);
        mPath.lineTo(0 + progressWidthSize, twoLineY);
        measure.setPath(mPath, false);
        pathMaxLength = measure.getLength();
        int pathW = w - progressWidthSize - progressWidthSize;
        int pathH = twoLineY - oneLineY;
        pathVerticalLength = pathMaxLength * pathH / (pathW * 2 + pathH);
        pathHorizontalLength = (pathMaxLength - pathVerticalLength) / 2;

        //大小改变时重新计算位置点
        pathPos.clear();
        float topLength = (pathHorizontalLength - progressWidthSize * 2) / 6;
        float leftPadding = topLength / 2 + progressWidthSize;


        //底部单份
        float bottomW = (pathHorizontalLength - topLength - leftPadding - progressWidthSize) / 3;
        //底部边距
        float bottomRightPadding = bottomW / 2 + pathHorizontalLength + pathVerticalLength + progressWidthSize;
        for (int i = 0; i < 10; i++) {
            float[] pos = new float[2];//临时存放path路径上的点
            float distance;
            if (i < 6) {
                distance = leftPadding + i * topLength;
                measure.getPosTan(distance, pos, null);
            } else if (i == 9) {
                //最后一个位置需要单独调整
                distance = pathMaxLength - leftPadding;
                measure.getPosTan(distance, pos, null);
            } else {
                distance = bottomRightPadding + (i - 6) * bottomW;
                measure.getPosTan(distance, pos, null);
            }
            pathPos.add(new PosEntity(pos[0], pos[1], distance));
        }
        updateProgressDash();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(progressBackColor);
        mPaint.setStrokeWidth(progressWidthSize * 2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        canvas.drawPath(mPath, mPaint);

        //绘制已签到进度-考虑动画不停渐变
        mPaint.setPathEffect(progressDash);
        mPaint.setColor(progressfoceColor);
        canvas.drawPath(mPath, mPaint);

        mPaint.setPathEffect(null);
        //绘制进度条上的点
        mPaint.setColor(pointColor);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (int i = 0; i < pathPos.size(); i++) {
            mPaint.setPathEffect(null);
            ExtEntity ext = extList.get(i);
            PosEntity pos = pathPos.get(i);
            //绘制条上的点
            canvas.drawCircle(pos.x, pos.y, progressWidthSize / 2, mPaint);
            //绘制底部文本
            mTextPaint.setTextSize(bottomTextSize);
            mTextPaint.setColor(ext.bottomTextColor);
            canvas.drawText(ext.title, pos.x, pos.y + Math.abs(mTextPaint.getFontMetrics().ascent) + progressWidthSize * 2, mTextPaint);
            //绘制顶部文本
            mTextPaint.setTextSize(topTextSize);
            mTextPaint.setColor(ext.topTextColor);
            float topBaseLine = pos.y - Math.abs(mTextPaint.getFontMetrics().descent) - Math.abs(mTextPaint.getFontMetrics().leading) - progressWidthSize * 1.5f;
            canvas.drawText(ext.point + "", pos.x, topBaseLine, mTextPaint);
            //绘制金币或者其他图案
            float textTop = topBaseLine - Math.abs(mTextPaint.getFontMetrics().ascent);
            Bitmap bmpIoon;
            if (i < pathPos.size() - 1) {
                bmpIoon = ext.isYunzuan ? bitmapZuan : bitmapBao;
            } else {
                bmpIoon = ext.isYunzuan ? bitmapZuan : bitmapBaoBig;
            }
            canvas.drawBitmap(bmpIoon, pos.x - bmpIoon.getWidth() / 2, textTop - bmpIoon.getHeight(), mPaint);

            //如果需要，绘制明日可领文案
            if (ext.needNextRecive) {
                canvas.drawBitmap(bitmapTip, pos.x - bitmapTip.getWidth() / 2, textTop - bitmapZuan.getHeight() - bitmapTip.getHeight(), mPaint);
            }

            //如果需要，绘制后几个点之间的点点点
            if (i >= 7) {
                //获取上一个节点进行画虚线
                PosEntity lastPos = pathPos.get(i - 1);
                mPaint.setPathEffect(this.dashPathEffect);
                canvas.drawLine(lastPos.x - progressWidthSize * 3, lastPos.y, pos.x + progressWidthSize * 3, pos.y, mPaint);
            }
        }

        /*float topLength = pathHorizontalLength / 6;
        float leftPadding = topLength / 2;


        //底部单份
        float bottomW = (pathHorizontalLength - topLength - leftPadding) / 3;
        //底部边距
        float bottomRightPadding = bottomW / 2 + pathHorizontalLength + pathVerticalLength;
        for (int i = 0; i < 10; i++) {
            if (i < 6) {
                measure.getPosTan(leftPadding + i * topLength, pathPos, null);
                canvas.drawCircle(pathPos[0], pathPos[1], progressWidthSize / 2, mPaint);
            } else if (i == 9) {
                //最后一个位置需要单独调整
                float length = pathMaxLength - leftPadding;
                measure.getPosTan(length, pathPos, null);
                canvas.drawCircle(pathPos[0], pathPos[1], progressWidthSize / 2, mPaint);
            } else {
                measure.getPosTan(bottomRightPadding + (i - 6) * bottomW, pathPos, null);
                canvas.drawCircle(pathPos[0], pathPos[1], progressWidthSize / 2, mPaint);
            }

        }*/

    }

    int day = 0;

    public void parse() {
        List<Boolean> signs = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            signs.add(i <= day);
        }
        extList.clear();
        for (int i = 1; i <= signs.size(); i++) {
            boolean isSign = signs.get(i - 1);
            if (i <= 7 || i == 14 || i == 21 || i == 30) {
                if (isSign) {
                    boolean isEndSign = true;
                    if (i < signs.size()) {
                        isEndSign = !signs.get(i);
                    }
                    //签到内-需要额外判断后一个是否未签到
                    extList.add(new ExtEntity(i * 100, topTextSignColor, isEndSign ? topTextSignColor : bottomTextColor,
                            true, false, isEndSign ? "今日已签" : "第" + i + "天"));
                    endSignPosition = extList.size() - 1;
                } else {
                    //签到外
                    extList.add(new ExtEntity(i * 100, topTextNormalColor, bottomTextColor,
                            !(i == 3 || i == 7 || i == 14 || i == 21 || i == 30),
                            signs.get(i - 2), "第" + i + "天"));
                }
            }
        }
        updateProgressDash();
        startAnim();
        invalidate();
    }

    float fixedLength = pathMaxLength;//固定距离
    float animLength = 0;//动画距离

    private void updateProgressDash() {

        if (extList.size() > 0 && endSignPosition == extList.size() - 1) {
            //最后一个，不缩减
            fixedLength = 0;
            //截取上个节点位置执行动画
            animLength = pathMaxLength - pathPos.get(endSignPosition - 1).length;
        } else if (endSignPosition >= 0 && endSignPosition < pathPos.size()) {
            //缩减一部分
            //fixedLength = pathMaxLength - pathPos.get(endSignPosition).length;
            //截取上个节点位置执行动画
            if (endSignPosition == 0) {
                //第一个需要特殊处理
                fixedLength = pathMaxLength - pathPos.get(endSignPosition).length;
                animLength = pathPos.get(endSignPosition).length;
            } else {
                animLength = pathPos.get(endSignPosition).length - pathPos.get(endSignPosition - 1).length;
                fixedLength = pathMaxLength - pathPos.get(endSignPosition).length;
            }
        } else {
            //设置满缩减-不执行动画
            fixedLength = pathMaxLength;
        }
        Log.e(TAG, "updateProgressDash: " + fixedLength + "  " + animLength);
        resetDash();
    }

    private void resetDash() {
        progressDash = new DashPathEffect(new float[]{pathMaxLength, pathMaxLength},
                fixedLength + animLength * animValue);
    }

    private void startAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animValue = (float) animation.getAnimatedValue();
                resetDash();
                Log.e(TAG, "onAnimationUpdate: " + animValue);
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private class ExtEntity {
        public ExtEntity(int point, int topTextColor, int bottomTextColor, boolean isYunzuan, boolean needNextRecive, String title) {
            this.point = point;
            this.bottomTextColor = bottomTextColor;
            this.topTextColor = topTextColor;
            this.isYunzuan = isYunzuan;
            this.needNextRecive = needNextRecive;
            this.title = title;
        }

        public int point;
        public int topTextColor;
        public int bottomTextColor;
        public boolean isYunzuan;
        public boolean needNextRecive;
        public String title;
    }

    private class PosEntity {
        public PosEntity(float x, float y, float length) {
            this.x = x;
            this.y = y;
            this.length = length;
        }

        public float x;
        public float y;
        public float length;
    }
}
