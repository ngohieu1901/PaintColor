package com.paintcolor.drawing.paint.presentations.components.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.paintcolor.drawing.paint.R

class SizeSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var minValue = 0

    // Track
    private val paintTrack = Paint(Paint.ANTI_ALIAS_FLAG)
    private var trackPath = Path()

    // Thumb
    private var thumbDrawable: Drawable =
        ContextCompat.getDrawable(context, R.drawable.img_thumb)!!
    private var thumbSize = dpToPx(32f)

    // Min/Max indicator
    private val minRadius = dpToPx(2.5f)
    private val maxRadius = dpToPx(12f)

    // Nền (fill)
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.color_ECEEF2)
        style = Paint.Style.FILL
    }

    // Viền (stroke)
    private val strokeWidth = dpToPx(1f)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.color_CFCFCF)
        style = Paint.Style.STROKE
        this.strokeWidth = strokeWidth
    }

    private var progress = 50
    private var maxSize = 100

    var onSizeChanged: ((Int) -> Unit)? = null

    init {
        paintTrack.shader = LinearGradient(
            startX, 0f, endX, 0f,
            ContextCompat.getColor(context, R.color.color_F2F2F2),
            ContextCompat.getColor(context, R.color.color_F2F2F2),
            Shader.TileMode.CLAMP
        )
    }
    private val startX get() = minRadius
    private val endX get() = width - maxRadius


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f

        val path = Path().apply {
            moveTo(startX, centerY + minRadius)
            lineTo(startX, centerY - minRadius)
            lineTo(endX, centerY - maxRadius)
            lineTo(endX, centerY + maxRadius)
            close()
        }


        canvas.drawPath(path, paintTrack)

        canvas.drawCircle(startX, centerY, minRadius, fillPaint)
        canvas.drawCircle(startX, centerY, minRadius - strokeWidth / 2, strokePaint)

        canvas.drawCircle(endX, centerY, maxRadius, fillPaint)
        canvas.drawCircle(endX, centerY, maxRadius - strokeWidth / 2, strokePaint)

        val usableWidth = endX - startX
        val ratio = progress / maxSize.toFloat()
        val thumbX = startX + ratio * usableWidth
        val thumbY = centerY

        val minThumbSize = dpToPx(5f)
        val maxThumbSize = dpToPx(24f)
        val currentThumbSize = minThumbSize + (maxThumbSize - minThumbSize) * ratio
        val half = currentThumbSize / 2

        thumbDrawable.setBounds(
            (thumbX - half).toInt(),
            (thumbY - half).toInt(),
            (thumbX + half).toInt(),
            (thumbY + half).toInt()
        )
        thumbDrawable.draw(canvas)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val startX = minRadius * 2
                val endX = width - maxRadius * 2
                val usableWidth = endX - startX
                val touchX = event.x.coerceIn(startX, endX)
                val range = maxSize - minValue
                val newProgress = (minValue + ((touchX - startX) / usableWidth) * range).toInt()

                if (newProgress != progress) {
                    progress = newProgress
                    onSizeChanged?.invoke(progress)
                    invalidate()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setProgress(value: Int) {
        progress = value.coerceIn(minValue, maxSize)
        invalidate()
    }

    fun setMinValue(value: Int) {
        minValue = value
        progress = progress.coerceIn(minValue, maxSize)
        invalidate()
    }

    fun getProgress(): Int = progress

    private fun dpToPx(dp: Float): Float =
        dp * resources.displayMetrics.density
}
