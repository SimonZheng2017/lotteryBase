package photoselector.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.facebook.drawee.drawable.DrawableUtils;

import photoselector.util.CommonUtils;

/**
 * Created by Administrator on 2016/8/18.
 */
public class ImageLoadingDrawable extends Drawable {


    //文字的画笔
    private Paint mTextPaint;
    // 画圆环的画笔
    private Paint mRingPaint;
    // 圆环颜色
    private int mRingColor;
    // 半径
    private float mRadius;
    // 圆环半径
    private float mRingRadius;
    // 圆环宽度
    private float mStrokeWidth;
    // 圆心x坐标
    private int mXCenter;
    // 圆心y坐标
    private int mYCenter;
    // 总进度
    private int mTotalProgress = 10000;
    // 当前进度
    private int mProgress;

    private Context context;

    public ImageLoadingDrawable(Context context) {
        this.context = context;
        initAttrs();
    }

    private void initAttrs() {
        mRadius = dip2px(15);
        mStrokeWidth = dip2px(1);
        mRingColor = 0xFFFFFFFF;
        mRingRadius = mRadius + mStrokeWidth / 2;
        initVariable();
    }

    private void initVariable() {

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mStrokeWidth);


        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(mStrokeWidth);
        mTextPaint.setTextSize(sp2px(10));
        mRingPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void draw(Canvas canvas) {
        drawBar(canvas, mProgress, mRingPaint);

        String testString = (mProgress / 100) + "%";
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(testString, 0, testString.length(), bounds);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        int baseline = (CommonUtils.getScreenHeightPixels(context) - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(testString, CommonUtils.getScreenWidthPixels(context) / 2 - bounds.width() / 2, baseline, mTextPaint);

    }

    private void drawBar(Canvas canvas, int level, Paint paint) {
        if (level > 0) {
//            Rect bound = getBounds();
//            mXCenter = bound.centerX();
//            mYCenter = bound.centerY();
            mXCenter = CommonUtils.getScreenWidthPixels(context) / 2;
            mYCenter = CommonUtils.getScreenHeightPixels(context) / 2;
            RectF oval = new RectF();
            oval.left = (mXCenter - mRingRadius);
            oval.top = (mYCenter - mRingRadius);
            oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
            oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
            canvas.drawArc(oval, -90, ((float) level / mTotalProgress) * 360, false, paint); //
        }
    }

    @Override
    protected boolean onLevelChange(int level) {
        mProgress = level;
        if (level > 0 && level <= 10000) {
            invalidateSelf();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mRingPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mRingPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return DrawableUtils.getOpacityFromColor(this.mRingPaint.getColor());
    }


    public int dip2px(float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public int sp2px(float var1) {
        float var2 = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (var1 * var2 + 0.5F);
    }

    public synchronized int getProgress() {
        return mProgress;
    }
}
