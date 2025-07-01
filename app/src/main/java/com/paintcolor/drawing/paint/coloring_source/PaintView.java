package com.paintcolor.drawing.paint.coloring_source;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.paintcolor.drawing.paint.R;
import com.paintcolor.drawing.paint.coloring_source.Image;
import com.paintcolor.drawing.paint.coloring_source.UndoRedoListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import timber.log.Timber;

public class PaintView extends View {
    private static final float TOUCH_TOLERANCE = 4;
    private static final float MAX_SCALE_FACTOR = 4.0f;
    private static final float MIN_SCALE_FACTOR = 1.0f;
    private float mPosX = 0;
    private float mPosY = 0;
    private float mLastTouchX;
    private float mLastTouchY;

    // New fields for moving the view
    private boolean mIsDragging = false;
    private float mStartDragX;
    private float mStartDragY;
    private float mDragOffsetX;
    private float mDragOffsetY;

    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private Paint eraserPaint;

    private Stack<com.paintcolor.drawing.paint.coloring_source.Image> mUndoImages = new Stack<>();
    private Stack<com.paintcolor.drawing.paint.coloring_source.Image> mRedoneImages = new Stack<>();
    private int mBrushColor;
    private float mStrokeWidth;
    private int mEraserColor;
    private float mEraserWidth;
    private int mOldBrushColor;
    private int mFillColor;
    private float mOldStrokeWidth;
    private Bitmap mBitmap;
    private final Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private Canvas mCanvas;
    private int mBackgroundColor, mAlpha;
    private boolean mEraseMode = false, mColorFillMode = false;
    private int mWidth, mHeight;
    public StyleTools brushStyle = StyleTools.PENCIL;
    private UndoRedoListener undoRedoListener;
    private boolean mIsScaling = false;

    private float mScaleFactor = 1.0f;
    public ScaleGestureDetector mScaleDetector;
    private StyleTools currentStyle;
    private int limitSize = 1;

    public void clearAllStack(){
        mUndoImages.clear();
        mRedoneImages.clear();
    }

    public List<com.paintcolor.drawing.paint.coloring_source.Image> getUndoStack() {
        return new ArrayList<>(mUndoImages);
    }

    public List<com.paintcolor.drawing.paint.coloring_source.Image> getRedoStack() {
        return new ArrayList<>(mRedoneImages);
    }

    public void restoreState(Bitmap baseBitmap, List<com.paintcolor.drawing.paint.coloring_source.Image> undo, List<com.paintcolor.drawing.paint.coloring_source.Image> redo) {
        this.mBitmap = baseBitmap.copy(Bitmap.Config.ARGB_8888, true);
        this.mCanvas = new Canvas(this.mBitmap);

        this.mUndoImages.clear();
        this.mUndoImages.addAll(undo);

        this.mRedoneImages.clear();
        this.mRedoneImages.addAll(redo);

        notifyUndoRedoChanged();
        invalidate();
    }

    public PaintView(Context context) {
        super(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setUpDrawing(brushStyle);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void init(int height, int width) {
        mHeight = height;
        mWidth = width;
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(mBackgroundColor);
        mBrushColor = getResources().getColor(R.color.black);
        mUndoImages.push(new com.paintcolor.drawing.paint.coloring_source.Image(mBitmap));
    }

    public void setBitmap(Bitmap bitmap) {
        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mBitmap = mutableBitmap;
        mCanvas = new Canvas(mutableBitmap);
        mPath.reset();
        mUndoImages.push(new com.paintcolor.drawing.paint.coloring_source.Image(mBitmap));
        invalidate();
        if (undoRedoListener != null) {
            notifyUndoRedoChanged();
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmapCanRdo(Bitmap bitmap, boolean clearStacks) {
        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mBitmap = mutableBitmap;
        mCanvas = new Canvas(mutableBitmap);
        if (clearStacks) {
            mUndoImages.clear();
            mRedoneImages.clear();
            notifyUndoRedoChanged();
        }
        mPath.reset();
        invalidate();
    }

    @NonNull
    public Bitmap createBitmap() throws OutOfMemoryError {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float mFocusX;
        private float mFocusY;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mIsScaling = true;  // Bắt đầu zoom
            mFocusX = detector.getFocusX();
            mFocusY = detector.getFocusY();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mIsScaling = false; // Kết thúc zoom
            super.onScaleEnd(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            mScaleFactor *= scaleFactor;
            mScaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(mScaleFactor, MAX_SCALE_FACTOR));

            float focusDeltaX = (mFocusX - mPosX) * (1 - scaleFactor);
            float focusDeltaY = (mFocusY - mPosY) * (1 - scaleFactor);

            mPosX += focusDeltaX;
            mPosY += focusDeltaY;

            // Clamp
            mPosX = Math.min(mPosX, getWidth() * (mScaleFactor - 1));
            mPosY = Math.min(mPosY, getHeight() * (mScaleFactor - 1));
            mPosX = Math.max(mPosX, -getWidth() * (mScaleFactor - 1));
            mPosY = Math.max(mPosY, -getHeight() * (mScaleFactor - 1));

            invalidate();
            return true;
        }
    }

    public void setColor(int color, int alpha) {
        mBrushColor = (color & 0x00FFFFFF) | (alpha << 24);
        mOldBrushColor = mBrushColor;
    }

    public int getColor() {
        return mBrushColor;
    }

    public void setStrokeWidth(float width) {
        mStrokeWidth = width;
        mOldStrokeWidth = width;
        settingBrushStyle(currentStyle);
        invalidate();
    }

    public void setColorFillMode(boolean mode) {
        mColorFillMode = mode;
    }

    public void setFillColor(int color, int alpha) {
        mFillColor = (color & 0x00FFFFFF) | (alpha << 24);
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        invalidate();
    }

    public void undo() {
        if (!mUndoImages.isEmpty()) {
            mRedoneImages.push(mUndoImages.pop());
            if (!mUndoImages.isEmpty()) {
                mBitmap = mUndoImages.peek().bitmap.copy(Bitmap.Config.ARGB_8888, true);
                mCanvas.setBitmap(mBitmap);
                invalidate();
            }
            notifyUndoRedoChanged();
        }
    }


    public void redo() {
        if (!mRedoneImages.isEmpty()) {
            mUndoImages.push(mRedoneImages.pop());
            mBitmap = mUndoImages.peek().bitmap.copy(Bitmap.Config.ARGB_8888, true);
            mCanvas.setBitmap(mBitmap);
            invalidate();
            notifyUndoRedoChanged();
        }
    }

    public void setUndoRedoListener(UndoRedoListener listener) {
        this.undoRedoListener = listener;
        notifyUndoRedoChanged();
    }

    public boolean canUndo() {
        return mUndoImages.size() > limitSize;
    }

    public boolean canRedo() {
        return !mRedoneImages.isEmpty();
    }

    public void setLimitSize(int limitSize) {
        this.limitSize = limitSize;
    }

    private void notifyUndoRedoChanged() {
        Log.d("Check_stack", "notify_\nlistCanUndo: " + mUndoImages + "\nlistCanRedo: " + mRedoneImages + "\n--------------------------");

        if (undoRedoListener != null) {
            boolean canUndo = mUndoImages.size() > limitSize;
            boolean canRedo = !mRedoneImages.isEmpty();
            undoRedoListener.onStackChanged(canUndo, canRedo);
        }
    }

    public Bitmap save() {
        return mBitmap;
    }

    public void clearCanvas() {
        mBitmap.eraseColor(mBackgroundColor);
        mUndoImages.clear();
        mRedoneImages.clear();
        mUndoImages.push(new com.paintcolor.drawing.paint.coloring_source.Image(mBitmap));
        invalidate();
        notifyUndoRedoChanged();
    }

    public void clearCanvasEdit() {
        mBitmap.eraseColor(mBackgroundColor);
        mUndoImages.clear();
        mRedoneImages.clear();
        mUndoImages.push(new com.paintcolor.drawing.paint.coloring_source.Image(mBitmap));
        invalidate();
        notifyUndoRedoChanged();
    }

    public void erase() {
        mEraseMode = true;
        mEraserColor = mBackgroundColor;
        mEraserWidth = mStrokeWidth;
        Log.d("Check_erase", "erase: " + mEraserWidth);

    }

    public void setEraserWidth(float eraserWidth) {
        mEraserWidth = eraserWidth;
        Log.d("Check_erase", "setEraserWidth: " + mEraserWidth);
    }

    public void setEraseMode(boolean eraseMode) {
        mEraseMode = eraseMode;
    }

    public Bitmap floodFill(Bitmap image, Point node, int targetColor, int replacementColor, int tolerance) {
        if (isColorSimilar(targetColor, replacementColor, tolerance)) return image;

        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);

        Queue<Point> queue = new LinkedList<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int x = p.x;
            int y = p.y;

            if (x < 0 || x >= width || y < 0 || y >= height) continue;

            int index = y * width + x;
            if (!isColorSimilar(pixels[index], targetColor, tolerance)) continue;

            // Quét sang trái
            int left = x;
            while (left >= 0 && isColorSimilar(pixels[y * width + left], targetColor, tolerance)) {
                left--;
            }
            left++;

            boolean spanUp = false, spanDown = false;
            for (int i = left; i < width && isColorSimilar(pixels[y * width + i], targetColor, tolerance); i++) {
                pixels[y * width + i] = replacementColor;

                if (y > 0) {
                    if (!spanUp && isColorSimilar(pixels[(y - 1) * width + i], targetColor, tolerance)) {
                        queue.add(new Point(i, y - 1));
                        spanUp = true;
                    } else if (spanUp && !isColorSimilar(pixels[(y - 1) * width + i], targetColor, tolerance)) {
                        spanUp = false;
                    }
                }

                if (y < height - 1) {
                    if (!spanDown && isColorSimilar(pixels[(y + 1) * width + i], targetColor, tolerance)) {
                        queue.add(new Point(i, y + 1));
                        spanDown = true;
                    } else if (spanDown && !isColorSimilar(pixels[(y + 1) * width + i], targetColor, tolerance)) {
                        spanDown = false;
                    }
                }
            }
        }

        image.setPixels(pixels, 0, width, 0, 0, width, height);
        return image;
    }

    private boolean isColorSimilar(int color1, int color2, int tolerance) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int diffA = Math.abs(a1 - a2);
        int diffR = Math.abs(r1 - r2);
        int diffG = Math.abs(g1 - g2);
        int diffB = Math.abs(b1 - b2);

        int totalDiff = diffA + diffR + diffG + diffB;

        return totalDiff <= tolerance;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();

        // Apply the current scale factor and position of the canvas
        canvas.scale(mScaleFactor, mScaleFactor, mPosX, mPosY);
        canvas.translate(mPosX, mPosY);

        if (mEraseMode) {
            mBrushColor = mEraserColor;
            mStrokeWidth = mEraserWidth;
        } else {
            mBrushColor = mOldBrushColor;
            mStrokeWidth = mOldStrokeWidth;
        }
        Timber.d("mBrushColorXXX: %d", mBrushColor);
        mPaint.setColor(mBrushColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);

        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void touchStart(float x, float y) {
        if (mColorFillMode) {
            Bitmap filledBitmap = floodFill(mBitmap,
                    new Point((int) x, (int) y),
                    mBitmap.getPixel((int) x, (int) y), mFillColor,50);
            setBitmap(filledBitmap);
            return;
        }

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (mColorFillMode)
            return;

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }

    }

    private void touchUp() {
        if (mColorFillMode)
            return;

        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);

        mUndoImages.push(new Image(mBitmap));
        mPath = new Path();
        Log.d("Check_stack", "touchUp_\nlistCanUndo: " + mUndoImages + "\nlistCanRedo: " + mRedoneImages + "\n--------------------------");
        notifyUndoRedoChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        float x = (event.getX() - mPosX) / mScaleFactor;
        float y = (event.getY() - mPosY) / mScaleFactor;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mIsScaling) {
                    return true; // đang zoom, bỏ qua vẽ
                }

                mLastTouchX = event.getX();
                mLastTouchY = event.getY();
                mStartDragX = x;
                mStartDragY = y;
                touchStart(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if (mIsScaling) {
                    return true;
                }
                touchMove(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if (mIsScaling) {
                    mIsScaling = false;
                    return true;
                }
                touchUp();
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
                mIsDragging = false;
                mIsScaling = false;
                break;
        }

        mLastTouchX = event.getX();
        mLastTouchY = event.getY();

        return true;
    }

    public void settingBrushStyle(StyleTools styleTools) {
        try {
            currentStyle = styleTools;
            if (styleTools == StyleTools.ERASER) {
                if (eraserPaint == null) {
                    eraserPaint = new Paint();
                    eraserPaint.setAntiAlias(true);
                    eraserPaint.setDither(true);
                    eraserPaint.setStyle(Paint.Style.STROKE);
                    eraserPaint.setColor(Color.WHITE);
                    eraserPaint.setStrokeWidth(mStrokeWidth);
                    eraserPaint.setPathEffect(null);
                    eraserPaint.setMaskFilter(null);
                    eraserPaint.setShadowLayer(0, 0, 0, 0);
                    eraserPaint.setStrokeCap(Paint.Cap.ROUND);
                }
                mPaint = eraserPaint;
                return;
            }

            if (mPaint == null) {
                mPaint = new Paint();
            }

            resetPaint();

            switch (styleTools) {
                case FILL:
                    Log.d("settingBrushStyle", "FILL: " + mStrokeWidth);
                    break;
                case PENCIL:
                    Log.d("settingBrushStyle", "PENCIL: " + mStrokeWidth);
                    mPaint.setMaskFilter(null);
                    mPaint.setShadowLayer(0, 0, 0, 0x00000000);
                    mPaint.setPathEffect(new DiscretePathEffect(12f, 1.5f));
                    mPaint.setStrokeWidth(mStrokeWidth / 10);
                    mPaint.setStrokeCap(Paint.Cap.BUTT);
                    mPaint.setStyle(Paint.Style.STROKE);
                    break;

                case SPRAY:
                    Log.d("settingBrushStyle", "SPRAY: " + mStrokeWidth);
                    if (mStrokeWidth < 1) {
                        mStrokeWidth = 25f;
                    }
                    mPaint.setMaskFilter(new BlurMaskFilter(mStrokeWidth / 1.5f, BlurMaskFilter.Blur.NORMAL));
                    mPaint.setPathEffect(null);
                    mPaint.setStrokeWidth(mStrokeWidth);
                    mPaint.setStrokeCap(Paint.Cap.ROUND);
                    mPaint.setColor(Color.rgb(30, 30, 30));
                    mPaint.setShadowLayer(25f, 0f, 0f, Color.argb(10, 0, 0, 0));
                    mPaint.setStyle(Paint.Style.STROKE);
                    break;

                case BRUSH:
                    Log.d("settingBrushStyle", "BRUSH: " + mStrokeWidth);
                    mPaint.setShadowLayer(0, 0, 0, 0x00000000);
                    mPaint.setStrokeWidth(mStrokeWidth);
                    Path trianglePath = new Path();
                    trianglePath.moveTo(0, mStrokeWidth);
                    trianglePath.lineTo(mStrokeWidth / 2, 0);
                    trianglePath.lineTo(mStrokeWidth, mStrokeWidth);
                    trianglePath.close();
                    mPaint.setPathEffect(
                            new ComposePathEffect(
                                    new DiscretePathEffect(10f, 2f),
                                    new PathDashPathEffect(
                                            trianglePath,
                                            0.5f,
                                            0,
                                            PathDashPathEffect.Style.TRANSLATE
                                    )
                            )
                    );

                    mPaint.setStrokeCap(Paint.Cap.ROUND);
                    mPaint.setStyle(Paint.Style.STROKE);
                    break;
                case BIG_BRUSH:
                    Log.d("settingBrushStyle", "BIG_BRUSH: " + mStrokeWidth);
                    mPaint.setShadowLayer(0, 0, 0, 0x00000000);
                    mPaint.setStrokeWidth(mStrokeWidth);
                    float width = mStrokeWidth * 2;
                    float height = mStrokeWidth;
                    Path rectanglePath = new Path();
                    rectanglePath.addRect(0, 0, width, height, Path.Direction.CW);
                    mPaint.setPathEffect(new ComposePathEffect(
                            new DiscretePathEffect(10f, 2f),
                            new PathDashPathEffect(rectanglePath, 5f, 0, PathDashPathEffect.Style.TRANSLATE)
                    ));

                    mPaint.setStrokeCap(Paint.Cap.ROUND);
                    mPaint.setStyle(Paint.Style.STROKE);
                    break;

                case MARKER:
                    Log.d("settingBrushStyle", "MARKER: " + mStrokeWidth);
                    mPaint.setMaskFilter(null);
                    mPaint.setShadowLayer(0, 0, 0, 0x00000000);

                    Path squarePath = new Path();
                    squarePath.addRect(0, 0, mStrokeWidth, mStrokeWidth, Path.Direction.CW);

                    mPaint.setPathEffect(new PathDashPathEffect(
                            squarePath,
                            0.5f,
                            0,
                            PathDashPathEffect.Style.TRANSLATE // TRANSLATE để giữ hình vuông không bị xoay
                    ));

                    mPaint.setStrokeWidth(mStrokeWidth);
                    mPaint.setStrokeCap(Paint.Cap.SQUARE);
                    mPaint.setStyle(Paint.Style.STROKE);
                    break;

                case TECH_PEN:
                    Log.d("settingBrushStyle", "TECH_PEN: " + mStrokeWidth);
                    mPaint.setMaskFilter(null);
                    mPaint.setShadowLayer(0, 0, 0, 0x00000000);
                    mPaint.setPathEffect(new PathDashPathEffect(
                            createBrushStrokePath(mStrokeWidth),
                            10f,
                            0f,
                            PathDashPathEffect.Style.MORPH));
                    mPaint.setStrokeCap(Paint.Cap.ROUND);
                    mPaint.setStyle(Paint.Style.STROKE);
                    break;
            }
        } catch (Exception e) {
            Log.d("settingBrushStyle", "settingBrushStyle: ", e);
        }
    }

    private Path createBrushStrokePath(float strokeWidth) {
        Path path = new Path();
        path.moveTo(0, 0);

        float length = strokeWidth * 5f;
        float amplitude = 1f;
        Random random = new Random();

        for (float x = 0; x < length; x += 10f) {
            float controlY = (random.nextFloat() - 0.5f) * amplitude * 2;
            float endY = (random.nextFloat() - 0.5f) * amplitude * 2;
            path.quadTo(x + 10f, controlY, x + 20f, endY);
        }

        return path;
    }

    private void resetPaint() {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    public void setUpDrawing(StyleTools brushStyle) {
        mPaint = new Paint();
        mPath = new Path();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        settingBrushStyle(brushStyle);
    }


}