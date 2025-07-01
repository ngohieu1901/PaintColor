package com.paintcolor.drawing.paint.presentations.components.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import com.paintcolor.drawing.paint.R

class GradientTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    private val framePaint = Paint()
    private val contentPaint = Paint()
    private val textPaint = Paint()
    private var _text = ""
    var text: String
        get() = _text
        set(value) {
            _text = value
            invalidate()
        }
    private var _textSize = 16f
    var textSize: Float
        get() = _textSize
        set(value) {
            _textSize = value
            textPaint.textSize = textSize
            invalidate()
        }
    private var _typeface = ResourcesCompat.getFont(context, R.font.inter_regular)
    var typeface: Typeface?
        get() = _typeface
        set(value) {
            _typeface = value
            textPaint.typeface = typeface
            invalidate()
        }

    @ColorInt
    private var _textColor = Color.WHITE
    var textColor: Int
        get() = _textColor
        set(@ColorInt value) {
            _textColor = value
            textPaint.color = textColor
            invalidate()
        }

    private var _radiusCorner = 0f
    var radiusCorner: Float
        get() = _radiusCorner
        set(value) {
            _radiusCorner = value
            invalidate()
        }

    @ColorInt
    private var _startColor = Color.parseColor("#00B1FF")
    var startColor: Int
        get() = _startColor
        set(@ColorInt value) {
            _startColor = value
            contentPaint.shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                intArrayOf(startColor, endColor),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
            invalidate()
        }

    @ColorInt
    private var _endColor = Color.parseColor("#3389FF")
    var endColor: Int
        get() = _endColor
        set(@ColorInt value) {
            _endColor = value
            contentPaint.shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                intArrayOf(startColor, endColor),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
            invalidate()
        }

    init {
        obtainStyledAttributes(attrs, defStyleAttr)
    }

    private fun obtainStyledAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TextViewGradient,
            defStyleAttr,
            0
        )
        try {
            with(typedArray) {
                endColor = getColor(R.styleable.TextViewGradient_endColor, endColor)
                startColor = getColor(R.styleable.TextViewGradient_startColor, startColor)
                radiusCorner = getDimension(R.styleable.TextViewGradient_radiusCorner, radiusCorner)
                textColor = getColor(R.styleable.TextViewGradient_textColor, textColor)
                textSize = getDimension(R.styleable.TextViewGradient_textSize, textSize)
                val textFont = getResourceId(R.styleable.TextViewGradient_textFont, -1)
                if (textFont != -1) {
                    typeface = ResourcesCompat.getFont(context, textFont)
                }
                text = getString(R.styleable.TextViewGradient_text) ?: ""
            }
        } catch (e: Exception) {
            // ignored
        } finally {
            typedArray.recycle()
        }
        framePaint.apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        contentPaint.apply {
            shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                intArrayOf(startColor, endColor),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        textPaint.apply {
            color = this@GradientTextView.textColor
            textSize = this@GradientTextView.textSize
            textAlign = Paint.Align.CENTER
//            typeface = this@TextViewGradient.typeface
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw frame
        val frameRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(frameRect, radiusCorner, radiusCorner, framePaint)

        // Draw content
        val contentRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(contentRect, radiusCorner, radiusCorner, contentPaint)


        // Draw text
        val textX = width.toFloat() / 2
        val textY = height.toFloat() / 2 - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, textX, textY, textPaint)
    }
}
