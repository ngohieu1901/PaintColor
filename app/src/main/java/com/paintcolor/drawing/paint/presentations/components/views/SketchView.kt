package com.paintcolor.drawing.paint.presentations.components.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

class SketchView : View {

    private var shouldCalculateTransaction = true

    private val paint = Paint()

    private var _sketchBitmap: Bitmap? = null
    private var _isSketchLocked: Boolean = false
    private var shouldCenter = true

    private var mScaleFactor = 1f
    private var mFocusX = 0f
    private var mFocusY = 0f
    private var mLastRotation = 0f

    private var mScaleDetector: ScaleGestureDetector
    private var mGestureDetector: GestureDetector
    private var mRotateDetector: RotationGestureDetector

    private val mMatrix = Matrix()

    constructor(context: Context) : super(context) {
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mGestureDetector = GestureDetector(context, GestureListener())
        mRotateDetector = RotationGestureDetector(this, RotateListener())
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mGestureDetector = GestureDetector(context, GestureListener())
        mRotateDetector = RotationGestureDetector(this, RotateListener())
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mGestureDetector = GestureDetector(context, GestureListener())
        mRotateDetector = RotationGestureDetector(this, RotateListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (_isSketchLocked) {
            return false
        }
        mScaleDetector.onTouchEvent(event)
        mGestureDetector.onTouchEvent(event)
        mRotateDetector.onTouchEvent(event)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        _sketchBitmap?.let {
            canvas.drawBitmap(it, mMatrix, paint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (_sketchBitmap != null && shouldCenter) {
            calculateTransactionToCenterBitmap()
            shouldCenter = false
        }
    }

    fun setAlpha(alpha: Int) {
        paint.alpha = (alpha * 255 / 100f).toInt()
        invalidate()
    }

    fun getSketchBitmap(): Bitmap? {
        return _sketchBitmap
    }

    fun setSketchBitmap(bitmap: Bitmap) {
        _sketchBitmap = bitmap
        post {
            if (shouldCenter) {
                calculateTransactionToCenterBitmap()
                shouldCenter = false
                invalidate()
            }
        }
    }

    fun setSketchLocked(isLocked: Boolean) {
        _isSketchLocked = isLocked
    }

    private fun calculateTransactionToCenterBitmap() {
        // Calculate initial translation to center the bitmap
        val centerX = width / 2f
        val centerY = height / 2f
        val bitmapWidth = _sketchBitmap!!.width
        val bitmapHeight = _sketchBitmap!!.height
        val bitmapCenterX = bitmapWidth / 2f
        val bitmapCenterY = bitmapHeight / 2f
        mMatrix.reset()
        mMatrix.postTranslate(centerX - bitmapCenterX, centerY - bitmapCenterY)
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mFocusX = detector.focusX
            mFocusY = detector.focusY
            mScaleFactor *= detector.scaleFactor
            if (mScaleFactor <= 0.05f) {
                mScaleFactor = 0.05f
                return true
            }
            if (mScaleFactor >= 5f) {
                mScaleFactor = 5f
                return true
            }
            mMatrix.postScale(detector.scaleFactor, detector.scaleFactor, detector.focusX, detector.focusY)
            invalidate()
            return true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            mMatrix.postTranslate(-distanceX, -distanceY)
            invalidate()
            return true
        }
    }

    private inner class RotateListener : RotationGestureDetector.OnRotationGestureListener {
        override fun onRotation(rotationDetector: RotationGestureDetector?) {
            mMatrix.postRotate((rotationDetector?.angle ?: 0f) - mLastRotation, mFocusX, mFocusY)
            mLastRotation = rotationDetector?.angle ?: 0f
        }
    }
}