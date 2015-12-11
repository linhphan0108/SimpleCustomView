package com.linhphan.simplecustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by linhphan on 12/11/15.
 */
public class MyPieChart extends ViewGroup {

    private boolean mShowText;
    private int mLabelPosition;

    private Paint mTextPaint;
    private Paint mPiePaint;
    private Paint mShadowPaint;

    private float mTextSize;
    private float mTextWidth;

    private RectF mPieBounds;
    private RectF mShadowBounds;

    private String mLabelText = "pie charts";
    private float mLabelX;
    private float mLabelY;

    private ArrayList<MyItem> mItems;

    //================= getters and setters ========================================================
    public boolean isShowText() {
        return mShowText;
    }

    public void setShowText(boolean showText) {
        this.mShowText = showText;
        invalidate();
        requestLayout();
    }

    public MyPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray arr = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyPieChart, 0, 0);
        try {
        } finally {
            arr.recycle();
        }
        initPaint();
    }

    //============ methods =========================================================================
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xPad = getPaddingLeft() + getPaddingRight();
        float yPad = getPaddingTop() + getPaddingBottom();

        //account for the label
        if (mShowText) {
            mTextWidth += xPad;
            float ww = w - xPad;
            float hh = h - yPad;

            //figure out how big we can make the pie
            float diameter = Math.min(ww, hh);
            mPieBounds = new RectF(
                    0.0f,
                    0.0f,
                    diameter,
                    diameter);

            mShadowBounds = new RectF(
                    mPieBounds.left + 10f,
                    mPieBounds.top + 10f,
                    mPieBounds.right + 10f,
                    mPieBounds.bottom + 10f
            );

            mLabelX = mPieBounds.right/2;
            mLabelY = mPieBounds.bottom;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(getClass().getName(), "on draw is called");

        //draw the shadow
        canvas.drawOval(mShadowBounds, mShadowPaint);

        // Draw the label text
        if (isShowText()) {
            canvas.drawText(mLabelText, mLabelX, mLabelY, mTextPaint);
        }
    }

    private void initPaint() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLUE);
        if (mTextSize == 0) {
            mTextSize = mTextPaint.getTextSize();
        } else {
            mTextPaint.setTextSize(mTextSize);
        }

        mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPiePaint.setColor(Color.BLUE);
        mPiePaint.setStyle(Paint.Style.FILL);
        mPiePaint.setTextSize(mTextSize);

        mShadowPaint = new Paint(0);
        mShadowPaint.setColor(0xff101010);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));

    }

    public class MyPieView extends View {

        // Used for SDK < 11
        private float mRotation = 0;
        private Matrix mTransform = new Matrix();
        private PointF mPivot = new PointF();
        private RectF mBounds;

        //================= getters and setters ====================================================
        public void setPivot(float pX, float pY){
            mPivot.x = pX;
            mPivot.y = pY;
            if (Build.VERSION.SDK_INT >= 11){
                setPivotX(pX);
                setPivotY(pY);
            }else{
                invalidate();
            }
        }

        public MyPieView(Context context) {
            super(context);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            mBounds = new RectF(0, 0, w, h);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (Build.VERSION.SDK_INT < 11) {
                mTransform.set(canvas.getMatrix());
                mTransform.preRotate(mRotation, mPivot.x, mPivot.y);
                canvas.setMatrix(mTransform);
            }

            for (MyItem it : mItems){
                mPiePaint.setShader(it.mShader);
                // Draw the pie slices
                canvas.drawArc(
                        mPieBounds,
                        0,
                        300,
                        true,
                        mPiePaint
                );
            }
        }


        private void init(){
            mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPiePaint.setColor(Color.BLUE);
            mPiePaint.setStyle(Paint.Style.FILL);
//        mPiePaint.setTextSize(mTextSize);
        }

        public void rotateTo(float pieRotation){
            mRotation = pieRotation;
            if (Build.VERSION.SDK_INT >= 11) {
                setRotation(pieRotation);
            }else{
                invalidate();
            }
        }
    }
}
