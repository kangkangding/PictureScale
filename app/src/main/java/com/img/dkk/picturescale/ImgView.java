package com.img.dkk.picturescale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by  dingkangkang on 2019/9/26
 * email：851615943@qq.com
 */
public class ImgView extends View implements GestureDetector.OnGestureListener {

    private final Rect mRect;
    private final BitmapFactory.Options options;
    private final GestureDetector mGesture;
    private final Scroller mScroller;
    private int mImgWidth;
    private int mImgHeight;
    private int mViewWidth;
    private int mViewHeight;
    private float mScale;
    private BitmapRegionDecoder bitmapRegionDecoder;
    private Bitmap mBitmap;

    public ImgView(Context context) {
        this(context,null);
    }

    public ImgView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public ImgView(Context context, AttributeSet attrs,
        int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mRect = new Rect();

        options = new BitmapFactory.Options();

        mGesture = new GestureDetector(context,this);

        mScroller = new Scroller(context);

    }


    public void setImg(InputStream inputStream){

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(inputStream,null,options);

        mImgWidth = options.outWidth;
        mImgHeight = options.outHeight;

        options.inMutable = true;

        options.inPreferredConfig = Bitmap.Config.RGB_565;

        options.inJustDecodeBounds = true;

        try {
            bitmapRegionDecoder =
                BitmapRegionDecoder.newInstance(inputStream, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestLayout();
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        mRect.left = 0;
        mRect.top = 0;
        mRect.right = mViewWidth;

        mScale = mViewWidth / (float)mViewWidth;
        mRect.bottom = (int)(mViewHeight / mScale);

    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(bitmapRegionDecoder == null){
            return;
        }

        options.inBitmap = mBitmap;

        mBitmap = bitmapRegionDecoder.decodeRegion(mRect,options);

        Matrix matrix = new Matrix();
        matrix.setScale(mScale,mScale);

        canvas.drawBitmap(mBitmap,matrix,null);

    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        return mGesture.onTouchEvent(event);
    }

    @Override public boolean onDown(MotionEvent e) {
        if(!mScroller.isFinished()){
            mScroller.forceFinished(true);
        }

        return true;
    }

    @Override public void onShowPress(MotionEvent e) {



    }

    @Override public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mRect.offset(0, (int) distanceY);

        if(mRect.bottom > mImgHeight){
            mRect.bottom = mImgHeight;
            mRect.top = mImgHeight - (int) (mViewHeight/mScale);
        }

        if(mRect.top < 0){
            mRect.top = 0;
            mRect.bottom = (int) (mViewHeight/mScale);
        }
        invalidate();
        return false;
    }

    @Override public void onLongPress(MotionEvent e) {

    }

    //处理惯性问题
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        mScroller.fling(0,mRect.top,0, (int) -velocityY,0,0,0,mImgHeight - (int) (mViewHeight/mScale));
        return false;
    }

    @Override public void computeScroll() {
        if(mScroller.isFinished()){
            return;
        }

        if(mScroller.computeScrollOffset()){
            mRect.top = mScroller.getCurrY();
            mRect.bottom = mRect.top + (int)(mViewHeight/mScale);
            invalidate();
        }

    }
}
