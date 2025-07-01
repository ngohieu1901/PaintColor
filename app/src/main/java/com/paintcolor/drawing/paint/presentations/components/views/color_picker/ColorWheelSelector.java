package com.paintcolor.drawing.paint.presentations.components.views.color_picker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.paintcolor.drawing.paint.presentations.components.views.color_picker.Constants;


public class ColorWheelSelector extends View {

    private final Paint selectorPaint;
    private final Paint strokeSelectorPaint;
    private float selectorRadiusPx = com.paintcolor.drawing.paint.presentations.components.views.color_picker.Constants.SELECTOR_RADIUS_DP * 3;
    private PointF currentPoint = new PointF();

    public ColorWheelSelector(Context context) {
        this(context, null);
    }

    public ColorWheelSelector(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorWheelSelector(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        strokeSelectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeSelectorPaint.setColor(Color.parseColor("#EBEDEF"));
        strokeSelectorPaint.setStyle(Paint.Style.STROKE);
        strokeSelectorPaint.setStrokeWidth(dpToPx(2));
        selectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectorPaint.setColor(Color.WHITE);
        selectorPaint.setAlpha(204);
        selectorPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawLine(currentPoint.x - selectorRadiusPx, currentPoint.y,
//                currentPoint.x + selectorRadiusPx, currentPoint.y, selectorPaint);
//        canvas.drawLine(currentPoint.x, currentPoint.y - selectorRadiusPx,
//                currentPoint.x, currentPoint.y + selectorRadiusPx, selectorPaint);
        canvas.drawCircle(currentPoint.x, currentPoint.y, selectorRadiusPx * 0.5f, selectorPaint);
        canvas.drawCircle(currentPoint.x, currentPoint.y, selectorRadiusPx * 0.5f, strokeSelectorPaint);
    }

    public void setSelectorRadiusPx(float selectorRadiusPx) {
        this.selectorRadiusPx = selectorRadiusPx;
    }

    private int dpToPx(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                Resources.getSystem().getDisplayMetrics()
        );
    }

    public void setCurrentPoint(PointF currentPoint) {
        this.currentPoint = currentPoint;
        invalidate();
    }
}
