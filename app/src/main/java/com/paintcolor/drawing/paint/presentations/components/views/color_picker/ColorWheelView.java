package com.paintcolor.drawing.paint.presentations.components.views.color_picker;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paintcolor.drawing.paint.presentations.components.views.color_picker.ColorObserver;
import com.paintcolor.drawing.paint.presentations.components.views.color_picker.Constants;
import com.paintcolor.drawing.paint.presentations.components.views.color_picker.Updatable;


/**
 * HSV color wheel
 */
public class ColorWheelView extends FrameLayout implements ColorObservable, Updatable {

    private final PointF currentPoint = new PointF();
    private final ColorObservableEmitter emitter = new ColorObservableEmitter();
    private final ThrottledTouchEventHandler handler = new ThrottledTouchEventHandler(this);
    private float radius;
    private float centerX;
    private float centerY;
    private float lastX;
    private float lastY;
    private float selectorRadiusPx;
    private int currentColor = Color.MAGENTA;
    private boolean onlyUpdateOnTouchEventUp;
    private ColorWheelSelector selector;
    private ColorWheelPalette palette;

    public ColorWheelView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ColorWheelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ColorWheelView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public ColorWheelView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        selectorRadiusPx = com.paintcolor.drawing.paint.presentations.components.views.color_picker.Constants.SELECTOR_RADIUS_DP * getResources().getDisplayMetrics().density;

        {
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            palette = new ColorWheelPalette(context);
            int padding = (int) selectorRadiusPx;
            palette.setPadding(padding, padding, padding, padding);
            addView(palette, layoutParams);
        }

        {
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            selector = new ColorWheelSelector(context);
            selector.setSelectorRadiusPx(selectorRadiusPx);
            addView(selector, layoutParams);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        width = height = Math.min(maxWidth, maxHeight);
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int netWidth = w - getPaddingLeft() - getPaddingRight();
        int netHeight = h - getPaddingTop() - getPaddingBottom();
        radius = Math.min(netWidth, netHeight) * 0.5f - selectorRadiusPx;
        if (radius < 0) return;
        centerX = netWidth * 0.5f;
        centerY = netHeight * 0.5f;
        setColor(currentColor, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                handler.onTouchEvent(event);
                return true;
            case MotionEvent.ACTION_UP:
                update(event);
                lastX = event.getX();
                lastY = event.getY();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void update(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        boolean isTouchUpEvent = event.getActionMasked() == MotionEvent.ACTION_UP;
        if (!onlyUpdateOnTouchEventUp || isTouchUpEvent) {
            emitter.onColor(getColorAtPoint(x, y), true, isTouchUpEvent);
        }
        updateSelector(x, y);
    }

    private int getColorAtPoint(float eventX, float eventY) {
        float x = eventX - centerX;
        float y = eventY - centerY;
        double r = Math.sqrt(x * x + y * y);
        float[] hsv = {0, 0, palette.getBrightness()};
        hsv[0] = (float) (Math.atan2(y, -x) / Math.PI * 180f) + 180;
        hsv[1] = Math.max(0f, Math.min(1f, (float) (r / radius)));
        return Color.HSVToColor(hsv);
    }

    public float getBrightness() {
        return palette.getBrightness();
    }

    public void setBrightness(float brightness) {
        palette.setBrightness(brightness);
        emitter.onColor(getColorAtPoint(lastX, lastY), true, false);
    }

    public void setOnlyUpdateOnTouchEventUp(boolean onlyUpdateOnTouchEventUp) {
        this.onlyUpdateOnTouchEventUp = onlyUpdateOnTouchEventUp;
    }

    public void setColor(int color, boolean shouldPropagate) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        float r = hsv[1] * radius;
        float radian = (float) (hsv[0] / 180f * Math.PI);
        updateSelector((float) (r * Math.cos(radian) + centerX), (float) (-r * Math.sin(radian) + centerY));
        currentColor = color;
        palette.setBrightness(hsv[2]);
        if (!onlyUpdateOnTouchEventUp) {
            emitter.onColor(color, false, shouldPropagate);
        }
    }

    private void updateSelector(float eventX, float eventY) {
        float x = eventX - centerX;
        float y = eventY - centerY;
        double r = Math.sqrt(x * x + y * y);
        if (r > radius) {
            x *= (float) (radius / r);
            y *= (float) (radius / r);
        }
        currentPoint.x = x + centerX;
        currentPoint.y = y + centerY;
        selector.setCurrentPoint(currentPoint);
    }

    @Override
    public void subscribe(com.paintcolor.drawing.paint.presentations.components.views.color_picker.ColorObserver observer) {
        emitter.subscribe(observer);
    }

    @Override
    public void unsubscribe(ColorObserver observer) {
        emitter.unsubscribe(observer);
    }

    @Override
    public int getColor() {
        return emitter.getColor();
    }
}
